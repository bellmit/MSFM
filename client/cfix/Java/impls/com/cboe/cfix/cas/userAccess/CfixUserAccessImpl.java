package com.cboe.cfix.cas.userAccess;


import com.cboe.application.cas.UserLogonHelper;
import com.cboe.application.shared.ServicesHelper;
import com.cboe.cfix.cas.shared.CfixServicesHelper;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiErrorCodes.AuthenticationCodes;
import com.cboe.idl.cmiUser.UserLogonStruct;
import com.cboe.idl.user.SessionProfileUserStructV2;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.cfix.CfixSessionManager;
import com.cboe.interfaces.cfix.CfixUserAccess;
import com.cboe.interfaces.cfix.CfixUserSessionAdminConsumer;
import com.cboe.util.ExceptionBuilder;

public class CfixUserAccessImpl extends BObject implements CfixUserAccess
{
    char sessionMode;

    /**
    * SBTAccessImpl constructor comment.
    */
    public CfixUserAccessImpl(char sessionMode) {
        super();
        this.sessionMode = sessionMode;
    }

    public CfixSessionManager logon( UserLogonStruct logonStruct, short sessionType, CfixUserSessionAdminConsumer clientListener, boolean gmdTextMessaging )
        throws  SystemException, CommunicationException,
            AuthorizationException, AuthenticationException,
            DataValidationException//,TransactionFailedException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "Logging on user = " + logonStruct.userId + " login mode = " + logonStruct.loginMode
                    + " sessionType = " + sessionType + " gmdTextMessage = " + gmdTextMessaging);
        }
        int sessionKey = -1;
        String sessionId = null;
        CfixSessionManager sessionManager = null;
        boolean sessionCreated = false;
        UserLogonHelper.checkLogoutInProgressPreLogin(logonStruct.userId);
        UserLogonHelper.checkValidCASState(true);
        UserLogonHelper.checkLoginMode(logonStruct,sessionMode);
        sessionId = UserLogonHelper.verifyPassword(logonStruct);

        try {
            if (Log.isDebugOn())
            {
                Log.debug(this, "Getting user settings for " + logonStruct.userId + " from the user service");
            }
            // Create a LoginStruct and then validate the user with the UserService.
            SessionProfileUserStructV2 user = ServicesHelper.getUserService().getSessionProfileUserInformationV2(logonStruct.userId);

            // Create the APIManager object passing it the ValidUserStruct for the client
            // that was returned from the call to the UserService.
            // Each user connecting to a single CAS will have their own APIManager object.
            if (Log.isDebugOn())
            {
                Log.debug(this, "Creating CFIX CAS session for user " + logonStruct.userId);
            }
            sessionManager = CfixServicesHelper.createCfixSessionManager(user, sessionId, clientListener, sessionType, gmdTextMessaging);
            sessionCreated = true;
            StringBuilder successful = new StringBuilder(45);
            successful.append("CFIX CAS login successful for user ").append(logonStruct.userId);
            Log.notification(this, successful.toString());

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
        }

    }

}
