package com.cboe.application.inprocess.userAccess;

import com.cboe.application.inprocess.shared.InProcessServicesHelper;
import com.cboe.application.shared.ServicesHelper;
import com.cboe.application.cas.UserLogonHelper;
import com.cboe.application.heartBeatConsumer.HeartBeatConsumerFactory;
import com.cboe.domain.startup.ClientRoutingBOHome;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiErrorCodes.*;
import com.cboe.idl.cmiUser.UserLogonStruct;
import com.cboe.idl.user.SessionProfileUserStructV2;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.inprocess.InProcessSessionManager;
import com.cboe.interfaces.application.inprocess.UserAccess;
import com.cboe.interfaces.application.inprocess.UserSessionAdminConsumer;
import com.cboe.interfaces.callbackServices.HeartBeatCallbackService;
import com.cboe.util.ExceptionBuilder;

public class UserAccessImpl extends BObject implements UserAccess
{
    char sessionMode;

    public UserAccessImpl(char sessionMode) {
        super();
        this.sessionMode = sessionMode;
    }

    public InProcessSessionManager logon( UserLogonStruct logonStruct, UserSessionAdminConsumer clientListener)
        throws  SystemException, CommunicationException,
            AuthorizationException, AuthenticationException,
            DataValidationException//,TransactionFailedException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "Logging on user = " + logonStruct.userId + " login mode = " + logonStruct.loginMode);
        }
        int sessionKey = -1;
        String sessionId = null;
        InProcessSessionManager sessionManager = null;
        // don't allow CAS logins if the CAS isn't completely started up
        UserLogonHelper.checkLogoutInProgressPreLogin(logonStruct.userId);
        UserLogonHelper.checkValidCASState(true);
        UserLogonHelper.checkLoginMode(logonStruct, sessionMode);
        boolean sessionCreated = false;

        sessionId = UserLogonHelper.verifyPassword(logonStruct);
        sessionKey = UserLogonHelper.registerWithSMS(logonStruct, sessionId);

        try {
            if (Log.isDebugOn())
            {
                Log.debug(this, "Getting user settings for " + logonStruct.userId + " from the user service");
            }
            // Create a LoginStruct and then validate the user with the UserService.
            SessionProfileUserStructV2 sessionProfileUser = ServicesHelper.getUserService().getSessionProfileUserInformationV2(logonStruct.userId);
            // Create the APIManager object passing it the ValidUserStruct for the client
            // that was returned from the call to the UserService.
            // Each user connecting to a single CAS will have their own APIManager object.
            if (Log.isDebugOn())
            {
                Log.debug(this, "Creating InProcess CAS session for user " + logonStruct.userId);
            }
            sessionManager = InProcessServicesHelper.createSessionManager(sessionProfileUser, sessionId, sessionKey, clientListener);
            StringBuilder successful = new StringBuilder(logonStruct.userId.length()+35);
            successful.append("CAS login successful for user ").append(logonStruct.userId);
            Log.notification(this, successful.toString());
            sessionCreated = true;
            return sessionManager;
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
            // SPOW
            registerSPOWHeartBeatCallback();
        }
    }

    private void registerSPOWHeartBeatCallback()
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

        HeartBeatCallbackService heartBeatCallbackService = InProcessServicesHelper.getHeartBeatCallbackService();
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
        catch (com.cboe.exceptions.SystemException ex) {
            Log.exception(this, ex.details.message, ex);
        }
        catch (com.cboe.exceptions.CommunicationException ex) {
            Log.exception(this, ex.details.message, ex);
        }
        catch (com.cboe.exceptions.AuthorizationException ex) {
            Log.exception(this, ex.details.message, ex);
        }
        catch (com.cboe.exceptions.DataValidationException ex) {
            Log.exception(this, ex.details.message, ex);
        }
    }
}
