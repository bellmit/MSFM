//
// -----------------------------------------------------------------------------------
// Source file: UserAccessV4Impl.java
//
// PACKAGE: com.cboe.expressApplication.cas
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.expressApplication.cas;

import com.cboe.idl.cmiUser.UserLogonStruct;
import com.cboe.idl.cmiCallback.CMIUserSessionAdmin;
import com.cboe.idl.cmiConstants.LoginSessionTypes;
import com.cboe.idl.cmiConstants.UserRoles;
import com.cboe.idl.cmiErrorCodes.AuthenticationCodes;
import com.cboe.idl.user.SessionProfileUserStructV2;
import com.cboe.idl.cmiV4.UserSessionManagerV4;
import com.cboe.idl.cmiV4.UserSessionManagerV4Helper;

import com.cboe.exceptions.*;

import com.cboe.interfaces.expressApplication.UserAccessV4;
import com.cboe.interfaces.expressApplication.SessionManagerV4;
import com.cboe.interfaces.callbackServices.HeartBeatCallbackService;

import com.cboe.util.ExceptionBuilder;

import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.foundationFramework.BOHome;
import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.application.shared.POANameHelper;
import com.cboe.application.shared.ServicesHelper;
import com.cboe.application.shared.RemoteConnectionFactory;
import com.cboe.application.cas.UserAccessBaseImpl;
import com.cboe.application.cas.UserLogonHelper;
import com.cboe.application.heartBeatConsumer.HeartBeatConsumerFactory;
import com.cboe.delegates.expressApplication.UserSessionV4ManagerDelegate;

public class UserAccessV4Impl extends UserAccessBaseImpl implements UserAccessV4
{
    public UserAccessV4Impl(char sessionMode, int heartbeatTimeout, String cmiVersion)
    {
        super(sessionMode, heartbeatTimeout, cmiVersion);
    }

    public UserSessionManagerV4 logon(UserLogonStruct logonStruct, short sessionType,
                                      CMIUserSessionAdmin cmiListener,
                                      boolean gmdTextMessaging)
            throws SystemException, CommunicationException,
                   AuthorizationException, AuthenticationException,
                   DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "Logging on user = " + logonStruct.userId + " login mode = " + logonStruct.loginMode
                            + " sessionType = " + sessionType + " gmdTextMessage = " + gmdTextMessaging);
        }

        boolean sessionCreated = false;
        UserLogonHelper.checkLogoutInProgressPreLogin(logonStruct.userId);
        UserLogonHelper.checkValidCASState(true);
        CMIUserSessionAdmin clientListener = checkValidListener(cmiListener);
        checkLogonStruct(logonStruct);
        String sessionId = UserLogonHelper.verifyPassword(logonStruct);
        int sessionKey = UserLogonHelper.registerWithSMS(logonStruct, sessionId);
        try
        {
            SessionManagerV4 sessionManager = createUserSessionManagerV4(logonStruct, sessionId, sessionKey,
                                                                              clientListener, gmdTextMessaging,
                                                                              sessionType);
            sessionCreated = true;
            return bindUserSessionV4(sessionManager);
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
                                                           AuthenticationCodes.UNKNOWN_USER);
        }
        catch(NotFoundException e)
        {
            Log.exception(this, "user:" + logonStruct.userId, e);
            UserLogonHelper.cleanupSession(clientListener, sessionKey);
            throw ExceptionBuilder.authenticationException("User not found", AuthenticationCodes.UNKNOWN_USER);
        }
        catch(Exception e)
        {
            Log.exception(this, "user:" + logonStruct.userId, e);
            UserLogonHelper.cleanupSession(clientListener, sessionKey);
            throw ExceptionBuilder.systemException("Fatal exception during logon", 0);
        }
        finally
        {
            UserLogonHelper.checkLogoutInProgressPostLogin(logonStruct.userId, clientListener, sessionKey,
                                                           sessionCreated);
        }
    }

    protected SessionManagerV4 createUserSessionManagerV4(UserLogonStruct logonStruct,
                                                                    String sessionId,
                                                                    int sessionKey,
                                                                    CMIUserSessionAdmin clientListener,
                                                                    boolean gmdTextMessaging,
                                                                    short sessionType)
            throws DataValidationException, CommunicationException, SystemException, NotFoundException,
                   AuthorizationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "Getting user settings for " + logonStruct.userId + " from the user service");
        }
        SessionProfileUserStructV2 sessionProfileUser = ServicesHelper.getUserService().getSessionProfileUserInformationV2(logonStruct.userId);
        
        // OMT users are not permitted to login as secondary session login types
        if(sessionProfileUser.userInfo.role == UserRoles.BOOTH_OMT || sessionProfileUser.userInfo.role == UserRoles.CROWD_OMT || sessionProfileUser.userInfo.role == UserRoles.DISPLAY_OMT || sessionProfileUser.userInfo.role == UserRoles.HELP_DESK_OMT)
        {
        	if(sessionType == LoginSessionTypes.SECONDARY)
        	{
        		throw ExceptionBuilder.authorizationException("OMT users are not permitted to login using secondary login types", 0);
        	}
        }
        
        if (Log.isDebugOn())
        {
            Log.debug(this, "Creating CAS V4 session for user " + logonStruct.userId);
        }
        SessionManagerV4 sessionManager = ServicesHelper.createSessionManagerV4(sessionProfileUser, sessionId, sessionKey,
                                                                                          clientListener, sessionType, gmdTextMessaging);

        // SPOW
        registerSPOWHeartBeatCallback();
        
        StringBuilder successful = new StringBuilder(logonStruct.userId.length()+30);
        successful.append("CAS login successful for user ").append(logonStruct.userId);
        Log.information(this, successful.toString());
        return sessionManager;
    }

    protected UserSessionManagerV4 bindUserSessionV4(SessionManagerV4 sessionManager)
            throws SystemException, CommunicationException,
                   AuthorizationException, AuthenticationException,
                   DataValidationException
    {
        UserSessionV4ManagerDelegate delegate = new UserSessionV4ManagerDelegate(sessionManager);
        String poaName = POANameHelper.getPOAName((BOHome) ServicesHelper.getSessionManagerHome());
        org.omg.CORBA.Object obj = (org.omg.CORBA.Object) RemoteConnectionFactory.find().register_object(delegate, poaName);
        UserSessionManagerV4 corbaObj = UserSessionManagerV4Helper.narrow(obj);
        sessionManager.setRemoteDelegate(corbaObj);
        return corbaObj;
    }
}
