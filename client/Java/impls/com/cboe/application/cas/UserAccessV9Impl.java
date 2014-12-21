package com.cboe.application.cas;

import com.cboe.interfaces.application.UserAccessV9;
import com.cboe.interfaces.application.SessionManagerV9;
import com.cboe.idl.cmiUser.UserLogonStruct;
import com.cboe.idl.cmiCallback.CMIUserSessionAdmin;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.AuthenticationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.util.ExceptionBuilder;

public class UserAccessV9Impl extends UserAccessBaseImpl implements UserAccessV9
{

    public UserAccessV9Impl(char sessionMode, int heartbeatTimeout, String cmiVersion)
    {
        super(sessionMode, heartbeatTimeout, cmiVersion);
    }

    public com.cboe.idl.cmiV9.UserSessionManagerV9 logon(UserLogonStruct logonStruct, short sessionType,
                                      CMIUserSessionAdmin cmiListener, boolean gmdTextMessaging)
                 throws  SystemException, CommunicationException, AuthorizationException,
                         AuthenticationException, DataValidationException, NotFoundException
    {
        Log.debug(this, "Logging on user = " + logonStruct.userId + " login mode = " + logonStruct.loginMode
                + " sessionType = " + sessionType + " gmdTextMessage = " + gmdTextMessaging);

        int sessionKey;
        String sessionId;
        CMIUserSessionAdmin clientListener;
        SessionManagerV9 sessionManager;

        boolean sessionCreated = false;
        UserLogonHelper.checkLogoutInProgressPreLogin(logonStruct.userId);
        // check if CAS is initialized and business day is started
        UserLogonHelper.checkValidCASState(true);
        // check heartbeat callback
        clientListener = checkValidListener(cmiListener);
        // check cmi version and login mode
        checkLogonStruct(logonStruct);
        // verify user password
        sessionId = UserLogonHelper.verifyPassword(logonStruct);
        sessionKey = UserLogonHelper.registerWithSMS(logonStruct, sessionId);
        try
        {
            sessionManager = createUserSessionManagerV9(logonStruct, sessionId, sessionKey,
                    clientListener, gmdTextMessaging, sessionType);
            sessionCreated = true;
            return bindUserSession(sessionManager);
        }
        catch(SystemException e)
        {
            Log.exception(this, "user:" + logonStruct.userId, e);
            UserLogonHelper.cleanupSession(clientListener, sessionKey);
            throw e;
        }
        catch(CommunicationException e)
        {
            Log.exception(this, "user:" + logonStruct.userId, e);
            UserLogonHelper.cleanupSession(clientListener, sessionKey);
            throw e;
        }
        catch(AuthorizationException e)
        {
            Log.exception(this, "user:" + logonStruct.userId, e);
            UserLogonHelper.cleanupSession(clientListener, sessionKey);
            throw e;
        }
        catch(DataValidationException e)
        {
            Log.exception(this, "user:" + logonStruct.userId, e);
            UserLogonHelper.cleanupSession(clientListener, sessionKey);
            throw e;
        }
        catch(org.omg.CORBA.NO_PERMISSION e)
        {
            Log.exception(this, "user:" + logonStruct.userId, e);
            UserLogonHelper.cleanupSession(clientListener, sessionKey);
            throw ExceptionBuilder.authenticationException("User not authorized to login",
                                                           com.cboe.idl.cmiErrorCodes.AuthenticationCodes.UNKNOWN_USER);
        }
        catch(NotFoundException e)
        {
            Log.exception(this, "user:" + logonStruct.userId, e);
            UserLogonHelper.cleanupSession(clientListener, sessionKey);
            throw ExceptionBuilder.authenticationException("User not found", com.cboe.idl.cmiErrorCodes.AuthenticationCodes.UNKNOWN_USER);
        }
        catch(Exception e)
        {
            Log.exception(this, "user:" + logonStruct.userId, e);
            UserLogonHelper.cleanupSession(clientListener, sessionKey);
            throw ExceptionBuilder.systemException("Fatal exception during logon", 0);
        }
        finally
        {
            UserLogonHelper.checkLogoutInProgressPostLogin(logonStruct.userId, clientListener, sessionKey, sessionCreated);
        }

    }

    protected SessionManagerV9 createUserSessionManagerV9(UserLogonStruct logonStruct, String sessionId,
                                                          int sessionKey, CMIUserSessionAdmin clientListener,
                                                          boolean gmdTextMessaging, short sessionType)
                throws SystemException, CommunicationException, AuthorizationException,
                       NotFoundException, DataValidationException
    {

        Log.debug(this, "Getting user settings for " + logonStruct.userId + " from the user service");
        com.cboe.idl.user.SessionProfileUserStructV2 sessionProfileUser = com.cboe.application.shared.ServicesHelper.getUserService().getSessionProfileUserInformationV2(logonStruct.userId);

        //OMT users are not permitted to login as secondary session login types
        if(sessionProfileUser.userInfo.role == com.cboe.idl.cmiConstants.UserRoles.BOOTH_OMT || sessionProfileUser.userInfo.role == com.cboe.idl.cmiConstants.UserRoles.CROWD_OMT || sessionProfileUser.userInfo.role == com.cboe.idl.cmiConstants.UserRoles.DISPLAY_OMT || sessionProfileUser.userInfo.role == com.cboe.idl.cmiConstants.UserRoles.HELP_DESK_OMT)
        {
            if(sessionType == com.cboe.idl.cmiConstants.LoginSessionTypes.SECONDARY)
            {
                throw ExceptionBuilder.authorizationException("OMT users are not permitted to login using secondary login types", 0);
            }
        }

        Log.debug(this, "Creating CAS TMS session for user " + logonStruct.userId);
        SessionManagerV9 sessionManager = com.cboe.application.shared.ServicesHelper.createSessionManagerV9(sessionProfileUser, sessionId, sessionKey,
                                                                                  clientListener, sessionType, gmdTextMessaging);
        // SPOW
        registerSPOWHeartBeatCallback();

        Log.information(this, "CAS login successful for user " + logonStruct.userId);
        return sessionManager;

    }

    protected com.cboe.idl.cmiV9.UserSessionManagerV9 bindUserSession(SessionManagerV9 sessionManager)
            throws  SystemException, CommunicationException, AuthorizationException, AuthenticationException, DataValidationException
    {
        com.cboe.delegates.application.UserSessionManagerV9Delegate delegate = new com.cboe.delegates.application.UserSessionManagerV9Delegate(sessionManager);
        // Get the existing POA name
        String poaName = com.cboe.application.shared.POANameHelper.getPOAName((com.cboe.infrastructureServices.foundationFramework.BOHome) com.cboe.application.shared.ServicesHelper.getSessionManagerHome());
        // Connect to POA and activate CORBA servant
        org.omg.CORBA.Object obj = (org.omg.CORBA.Object) com.cboe.application.shared.RemoteConnectionFactory.find().register_object(delegate, poaName);
        // Register with RemoteSessionManagerHome
        com.cboe.idl.cmiV9.UserSessionManagerV9 corbaObj = com.cboe.idl.cmiV9.UserSessionManagerV9Helper.narrow(obj);
        sessionManager.setRemoteDelegate(corbaObj);
        return corbaObj;
    }
}