package com.cboe.application.cas;

import com.cboe.application.shared.POANameHelper;
import com.cboe.application.shared.RemoteConnectionFactory;
import com.cboe.application.shared.ServicesHelper;
import com.cboe.application.heartBeatConsumer.HeartBeatConsumerFactory;
import com.cboe.delegates.application.UserSessionManagerDelegate;
import com.cboe.delegates.application.UserSessionManagerV3Delegate;
import com.cboe.domain.startup.ClientRoutingBOHome;
import com.cboe.domain.util.TimeServiceWrapper;
import com.cboe.exceptions.*;
import com.cboe.idl.cmi.UserSessionManagerHelper;
import com.cboe.idl.cmiAdmin.HeartBeatStruct;
import com.cboe.idl.cmiCallback.CMIUserSessionAdmin;
import com.cboe.idl.cmiCallback.CMIUserSessionAdminHelper;
import com.cboe.idl.cmiConstants.LoginSessionTypes;
import com.cboe.idl.cmiConstants.UserRoles;
import com.cboe.idl.cmiErrorCodes.AuthenticationCodes;
import com.cboe.idl.cmiErrorCodes.DataValidationCodes;
import com.cboe.idl.cmiUser.UserLogonStruct;
import com.cboe.idl.cmiV3.UserSessionManagerV3Helper;
import com.cboe.idl.user.SessionProfileUserStructV2;
import com.cboe.infrastructureServices.foundationFramework.BOHome;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.SessionManager;
import com.cboe.interfaces.callbackServices.HeartBeatCallbackService;
import com.cboe.util.ExceptionBuilder;

public class UserAccessBaseImpl extends BObject
{
    char sessionMode;
    int heartbeatTimeout;
    String cmiVersion;

    public UserAccessBaseImpl(char sessionMode, int heartbeatTimeout, String cmiVersion) {
        super();
        this.sessionMode = sessionMode;
        this.heartbeatTimeout = heartbeatTimeout;
        this.cmiVersion = cmiVersion;
    }

    protected CMIUserSessionAdmin checkValidListener(CMIUserSessionAdmin cmiListener)
        throws DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "Callback object being tested = " + cmiListener);
        }
        if (cmiListener == null) {
            throw ExceptionBuilder.dataValidationException("Need a valid callback", DataValidationCodes.MISSING_LISTENER);
        }

        CMIUserSessionAdmin clientListener =
                        CMIUserSessionAdminHelper.narrow((org.omg.CORBA.Object) RemoteConnectionFactory.find().setRoundTripTimeout(cmiListener, heartbeatTimeout));

        try
        {
            HeartBeatStruct heartBeatStruct = new HeartBeatStruct();
            heartBeatStruct.pulseInterval = 1;
            heartBeatStruct.currentDate = TimeServiceWrapper.toDateStruct();
            heartBeatStruct.requestID = "Initial callback test";
            heartBeatStruct.currentTime = TimeServiceWrapper.toTimeStruct();
            RemoteConnectionFactory.find().cleanupConnection(clientListener);
            String hostname = RemoteConnectionFactory.find().getHostname(clientListener);
            StringBuilder sb = new StringBuilder(100);
            if (Character.isDigit(hostname.charAt(0)))
            {
                sb.append("Found IP address (").append(hostname).append(") instead of hostname.");
                Log.alarm(this, sb.toString());
                sb.setLength(0);
            }
            sb.append(hostname).append(':').append(RemoteConnectionFactory.find().getPort(clientListener));
            String hostPort = sb.toString();
            sb.setLength(0);
            sb.append("Testing heartBeat on callback object ").append(hostPort);
            Log.information(this, sb.toString());
            clientListener.acceptHeartBeat(heartBeatStruct);
            return clientListener;
        }
        catch (Exception e)
        {
            Log.exception(this, e);
            throw ExceptionBuilder.dataValidationException("Could not communicate with supplied callback", DataValidationCodes.MISSING_LISTENER);
        }
    }

    protected void checkLogonStruct(UserLogonStruct logonStruct)
        throws CommunicationException, DataValidationException
    {
        UserLogonHelper.checkCMIVersion(logonStruct, cmiVersion);
        UserLogonHelper.checkLoginMode(logonStruct, sessionMode);
    }

    protected SessionManager createUserSessionManager(UserLogonStruct logonStruct,
                                                      String sessionId,
                                                      int sessionKey,
                                                      CMIUserSessionAdmin clientListener,
                                                      boolean gmdTextMessaging,
                                                      short sessionType)
        throws DataValidationException, CommunicationException, SystemException, NotFoundException, AuthorizationException
    {
        SessionManager sessionManager;
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
            Log.debug(this, "Creating CAS session for user " + logonStruct.userId);
        }
        sessionManager = ServicesHelper.createSessionManager(sessionProfileUser, sessionId, sessionKey, clientListener, sessionType, gmdTextMessaging);

        // SPOW
        registerSPOWHeartBeatCallback();
        
        StringBuilder successful = new StringBuilder(logonStruct.userId.length()+35);
        successful.append("CAS login successful for user ").append(logonStruct.userId);
        Log.information(this, successful.toString());
        
        
        return sessionManager;
    }

    protected void registerSPOWHeartBeatCallback()
    {
        if (Log.isDebugOn()) {
            Log.debug(this, "registerSPOWHeartBeatCallback() entry");
        }
        if (!ClientRoutingBOHome.clientIsRemote())
        {
            if (Log.isDebugOn())
            {
                Log.debug(this, "No heartbeat callback to register, since local client has no Frontend");
            }
            return;
        }
        HeartBeatCallbackService heartBeatCallbackService = ServicesHelper.getHeartBeatCallbackService();
        String orbName;
        try {
            orbName = FoundationFramework.getInstance().getConfigService().getProperty("Process.name()");
        } catch (com.cboe.infrastructureServices.systemsManagementService.NoSuchPropertyException nsp) {
            orbName = System.getProperty("ORB.OrbName");
            Log.alarm(this, "NoSuchPropertyException getting Process.name -substituting ORB.OrbName:" + orbName);
        }
        
        try {
            heartBeatCallbackService.registerHeartBeatCallback(orbName,  HeartBeatConsumerFactory.getHeartBeatConsumerCallback(), orbName);
            if (Log.isDebugOn()) {
                Log.debug(this, "registerSPOWHeartBeatCallback(" + orbName + ") complete");
            }
        }
        catch (SystemException ex) {
            Log.exception(this, ex.details.message, ex);
        }
        catch (DataValidationException ex) {
            Log.exception(this, ex.details.message, ex);
        }
        catch (CommunicationException ex) {
            Log.exception(this, ex.details.message, ex);
        }
        catch (AuthorizationException ex) {
            Log.exception(this, ex.details.message, ex);
        }
    }


    protected com.cboe.idl.cmiV3.UserSessionManagerV3 logonV3( UserLogonStruct logonStruct, short sessionType, CMIUserSessionAdmin cmiListener, boolean gmdTextMessaging )
        throws  SystemException, CommunicationException,
                AuthorizationException, AuthenticationException,
                DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "Logging on user = " + logonStruct.userId + " login mode = " + logonStruct.loginMode
                    + " sessionType = " + sessionType + " gmdTextMessage = " + gmdTextMessaging);
    }

        int sessionKey      = -1;
        String sessionId    = null;
        CMIUserSessionAdmin clientListener = null;
        SessionManager      sessionManager = null;
        boolean sessionCreated = false;
        UserLogonHelper.checkLogoutInProgressPreLogin(logonStruct.userId);
        UserLogonHelper.checkValidCASState(true);
        clientListener = checkValidListener(cmiListener);
        checkLogonStruct(logonStruct);
        sessionId = UserLogonHelper.verifyPassword(logonStruct);
        sessionKey = UserLogonHelper.registerWithSMS(logonStruct, sessionId);
        try {
            sessionManager = createUserSessionManager(logonStruct, sessionId, sessionKey, clientListener,gmdTextMessaging, sessionType);
            sessionCreated = true;
            return bindUserSessionV3(sessionManager);
        }
        catch (SystemException e)
        {
            Log.exception(this, "user:" + logonStruct.userId,  e);
            UserLogonHelper.cleanupSession(clientListener, sessionKey);

            throw e;
        }
        catch (CommunicationException e)
        {
            Log.exception(this, "user:" + logonStruct.userId,  e);
            UserLogonHelper.cleanupSession(clientListener, sessionKey);
            throw e;
        }
        catch (AuthorizationException e)
        {
            Log.exception(this, "user:" + logonStruct.userId,  e);
            UserLogonHelper.cleanupSession(clientListener, sessionKey);
            throw e;
        }
        catch (DataValidationException e)
        {
            Log.exception(this, "user:" + logonStruct.userId,  e);
            UserLogonHelper.cleanupSession(clientListener, sessionKey);
            throw e;
        }
        catch ( org.omg.CORBA.NO_PERMISSION e )
        {
            Log.exception(this, "user:" + logonStruct.userId,  e);
            UserLogonHelper.cleanupSession(clientListener, sessionKey);
            throw ExceptionBuilder.authenticationException("User not authorized to login", AuthenticationCodes.UNKNOWN_USER);
        }
        catch (NotFoundException e)
        {
            Log.exception(this, "user:" + logonStruct.userId, e);
            UserLogonHelper.cleanupSession(clientListener, sessionKey);
            throw ExceptionBuilder.authenticationException("User not found", AuthenticationCodes.UNKNOWN_USER);
        }
        catch (Exception e)
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

    protected com.cboe.idl.cmi.UserSessionManager logonV1( UserLogonStruct logonStruct, short sessionType, CMIUserSessionAdmin cmiListener, boolean gmdTextMessaging )
        throws  SystemException, CommunicationException,
                AuthorizationException, AuthenticationException,
                DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "Logging on user = " + logonStruct.userId + " login mode = " + logonStruct.loginMode
                    + " sessionType = " + sessionType + " gmdTextMessage = " + gmdTextMessaging);
        }

        int sessionKey      = -1;
        String sessionId    = null;
        CMIUserSessionAdmin clientListener = null;
        SessionManager      sessionManager = null;
        boolean sessionCreated = false;
        UserLogonHelper.checkLogoutInProgressPreLogin(logonStruct.userId);
        UserLogonHelper.checkValidCASState(true);
        clientListener = checkValidListener(cmiListener);
        checkLogonStruct(logonStruct);
        sessionId = UserLogonHelper.verifyPassword(logonStruct);
        sessionKey = UserLogonHelper.registerWithSMS(logonStruct, sessionId);
        try {
            sessionManager = createUserSessionManager(logonStruct, sessionId, sessionKey, clientListener,gmdTextMessaging, sessionType);
            sessionCreated = true;
            return bindUserSession(sessionManager);
        }
        catch (SystemException e)
        {
            Log.exception(this, "user:" + logonStruct.userId,  e);
            UserLogonHelper.cleanupSession(clientListener, sessionKey);

            throw e;
        }
        catch (CommunicationException e)
        {
            Log.exception(this, "user:" + logonStruct.userId,  e);
            UserLogonHelper.cleanupSession(clientListener, sessionKey);
            throw e;
        }
        catch (AuthorizationException e)
        {
            Log.exception(this, "user:" + logonStruct.userId,  e);
            UserLogonHelper.cleanupSession(clientListener, sessionKey);
            throw e;
        }
        catch (DataValidationException e)
        {
            Log.exception(this, "user:" + logonStruct.userId,  e);
            UserLogonHelper.cleanupSession(clientListener, sessionKey);
            throw e;
        }
        catch ( org.omg.CORBA.NO_PERMISSION e )
        {
            Log.exception(this, "user:" + logonStruct.userId,  e);
            UserLogonHelper.cleanupSession(clientListener, sessionKey);
            throw ExceptionBuilder.authenticationException("User not authorized to login", AuthenticationCodes.UNKNOWN_USER);
        }
        catch (NotFoundException e)
        {
            Log.exception(this, "user:" + logonStruct.userId, e);
            UserLogonHelper.cleanupSession(clientListener, sessionKey);
            throw ExceptionBuilder.authenticationException("User not found", AuthenticationCodes.UNKNOWN_USER);
        }
        catch (Exception e)
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

    protected com.cboe.idl.cmiV3.UserSessionManagerV3 bindUserSessionV3(SessionManager sessionManager)
                throws  SystemException, CommunicationException,
                AuthorizationException, AuthenticationException,
                DataValidationException
    {
        UserSessionManagerV3Delegate delegate = new UserSessionManagerV3Delegate(sessionManager);
        String poaName = POANameHelper.getPOAName((BOHome)ServicesHelper.getSessionManagerHome());
        org.omg.CORBA.Object obj = (org.omg.CORBA.Object) RemoteConnectionFactory.find().
            register_object(delegate, poaName);
        com.cboe.idl.cmiV3.UserSessionManagerV3 corbaObj = UserSessionManagerV3Helper.narrow(obj);
        sessionManager.setRemoteDelegate(corbaObj);
        return corbaObj;
    }
    protected com.cboe.idl.cmi.UserSessionManager bindUserSession(SessionManager sessionManager)
                throws  SystemException, CommunicationException,
                AuthorizationException, AuthenticationException,
                DataValidationException
    {
        UserSessionManagerDelegate delegate = new UserSessionManagerDelegate(sessionManager);
        String poaName = POANameHelper.getPOAName((BOHome)ServicesHelper.getSessionManagerHome());
        org.omg.CORBA.Object obj = (org.omg.CORBA.Object) RemoteConnectionFactory.find().
            register_object(delegate, poaName);
        com.cboe.idl.cmi.UserSessionManager corbaObj = UserSessionManagerHelper.narrow(obj);
        sessionManager.setRemoteDelegate(corbaObj);
        return corbaObj;
    }
}
