package com.cboe.application.cas;

import com.cboe.application.shared.ServicesHelper;
import com.cboe.domain.logout.LogoutQueueFactory;
import com.cboe.domain.startup.StartupHelper;
import com.cboe.exceptions.AuthenticationException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.TransactionFailedException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.idl.cmi.Version;
import com.cboe.idl.cmiConstants.LoginSessionModes;
import com.cboe.idl.cmiErrorCodes.AuthenticationCodes;
import com.cboe.idl.cmiErrorCodes.AuthorizationCodes;
import com.cboe.idl.cmiErrorCodes.CommunicationFailureCodes;
import com.cboe.idl.cmiErrorCodes.DataValidationCodes;
import com.cboe.idl.cmiErrorCodes.TransactionFailedCodes;
import com.cboe.idl.cmiUser.UserLogonStruct;
import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.securityService.SecurityService;
import com.cboe.infrastructureServices.sessionManagementService.SessionManagementService;
import com.cboe.infrastructureServices.sessionManagementService.SessionManagementServiceV2;
import com.cboe.interfaces.application.AppServerStatusManager;
import com.cboe.util.ExceptionBuilder;

public class UserLogonHelper
{
    private static FoundationFramework ff = FoundationFramework.getInstance();
    private static SessionManagementService sms = ff.getSessionManagementService();
    private static SecurityService security = ff.getSecurityService();

    public static void checkValidCASState(boolean checkBusinessDay)
            throws CommunicationException, DataValidationException
    {
        AppServerStatusManager appServerStatusManager = ServicesHelper.getAppServerStatusManager();
        if (!StartupHelper.getStartupStatus().equals(StartupHelper.READY))
        {
            throw ExceptionBuilder.communicationException("CAS has not completed starting up", 0);
        }
        if (!appServerStatusManager.isSystemReady())
        {
            throw ExceptionBuilder.communicationException("CAS to Exchange Connection Lost. Try again later.", CommunicationFailureCodes.LOST_CONNECTION);
        }
        if (checkBusinessDay)
        {
            if (!appServerStatusManager.isBusinessDayStarted())
            {
                throw ExceptionBuilder.dataValidationException("Business day has not begun. No trading is allowed.", DataValidationCodes.BUSINESS_DAY_NOT_STARTED);
            }
        }
    }

    public static void checkCMIVersion(UserLogonStruct logonStruct, String cmiVersion)
            throws DataValidationException
    {
        float userVersion = Float.parseFloat(logonStruct.version);
        float currentCmiVersion = Float.parseFloat(Version.CMI_VERSION);
        float compCmiVersion = Float.parseFloat(cmiVersion);
        if (Log.isDebugOn())
        {
            Log.debug("userVersion:" + userVersion + " currentCmiVersion:" + currentCmiVersion + " compCmiVersion:" + compCmiVersion);
        }
        if (userVersion != currentCmiVersion)
        {
            if ((userVersion < compCmiVersion) || (userVersion > currentCmiVersion))
            {
                throw ExceptionBuilder.dataValidationException("Version mismatch.  The CMi client must be compatible with version " + Version.CMI_VERSION, +DataValidationCodes.INVALID_VERSION);
            }
        }
    }

    public static String verifyPassword(UserLogonStruct logonStruct)
            throws AuthenticationException
    {
        String sessionId = null;
        try
        {
            // Get sessionId from the Security Service
            if (Log.isDebugOn())
            {
                Log.debug("UserLogonHelper --> Authenticating user " + logonStruct.userId + " with the security service");
            }
            sessionId = security.authenticateWithPassword(logonStruct.userId, logonStruct.password);
            return sessionId;
        }
        catch (org.omg.CORBA.NO_PERMISSION e)
        {
            Log.exception("UserLogonHelper --> user:" + logonStruct.userId, e);
            throw ExceptionBuilder.authenticationException("Authentication with security service failed.  Invalid user or password.", AuthenticationCodes.INCORRECT_PASSWORD);

        }
    }

    public static int registerWithSMS(UserLogonStruct logonStruct, String sessionId)
            throws DataValidationException, CommunicationException, SystemException, AuthorizationException
    {
        int sessionKey = -1;
        try
        {
            if (Log.isDebugOn())
            {
                Log.debug("UserLogonHelper --> Authenticating user " + logonStruct.userId + " with the session management service");
            }
            // Attempt to validate the user with SMS and establish a SMS Session
            sessionKey = sms.createSession(logonStruct.userId, sessionId);
            return sessionKey;
        }
        catch (TransactionFailedException tfe)
        {    //this catch will be removed when logon() will raise TransactionFailedException
            Log.exception("UserLogonHelper --> user:" + logonStruct.userId, tfe);                 //in com.cboe.idl.cmi.UserAccess
            throw ExceptionBuilder.systemException("Authentication with session management service failed", TransactionFailedCodes.CREATE_FAILED);
        }
        catch (AuthorizationException e)
        {
            Log.exception("UserLogonHelper --> SMS Authorization Failure for User:" + logonStruct.userId, e);
            throw e;
        }

    }

    public static int registerParSession(String userId, String password)
            throws AuthenticationException, SystemException, AuthorizationException, CommunicationException, DataValidationException
    {

        SessionManagementServiceV2 smsV2 = ff.getSessionManagementServiceV2();

        if (Log.isDebugOn())
        {
            Log.debug("UserLogonHelper --> Authenticating user " + userId + " with the security service :" + smsV2.getSMSComponent());
        }
        try
        {
            int idx = userId.indexOf(":");
            if (idx >0)
            {
                String parUserID = userId.substring(0, idx);
                security.authenticateWithPassword(parUserID, password);
                return smsV2.createSession(userId, smsV2.getSMSComponent(),"ParSession", false);
            }
            else
            {
                throw ExceptionBuilder.dataValidationException("Invalid userId format." + userId, DataValidationCodes.INVALID_ID);
            }
        }
        catch (org.omg.CORBA.NO_PERMISSION e)
        {
            Log.exception("UserLogonHelper --> user:" + userId, e);
            throw ExceptionBuilder.authenticationException("Authentication with security service failed.  Invalid user or password.", AuthenticationCodes.INCORRECT_PASSWORD);

        }
        catch (TransactionFailedException tfe)
        {    //this catch will be removed when logon() will raise TransactionFailedException
            Log.exception("UserLogonHelper --> user:" + userId, tfe);                 //in com.cboe.idl.cmi.UserAccess
            throw ExceptionBuilder.systemException("Authentication with session management service failed", TransactionFailedCodes.CREATE_FAILED);
        }

    }

    public static int unRegisterParSession(String userId)
            throws AuthenticationException, SystemException, AuthorizationException, CommunicationException, DataValidationException, NotFoundException
    {


        if (Log.isDebugOn())
        {
            Log.debug("UserLogonHelper --> Closing SMS session for  user " + userId );
        }
        try
        {
            int idx = userId.indexOf(":");
            if (idx >0)
            {
                int sessionId = sms.getSessionForUser(userId);
                sms.closeSession(sessionId);
                return sessionId;
            }
            else
            {
                throw ExceptionBuilder.dataValidationException("Invalid Par  userId format." + userId, DataValidationCodes.INVALID_ID);
            }
        }
        catch (org.omg.CORBA.NO_PERMISSION e)
        {
            Log.exception("UserLogonHelper --> user:" + userId, e);
            throw ExceptionBuilder.authenticationException("Authentication with security service failed.  Invalid user or password.", AuthenticationCodes.INCORRECT_PASSWORD);

        }
        catch (TransactionFailedException tfe)
        {    //this catch will be removed when logon() will raise TransactionFailedException
            Log.exception("UserLogonHelper --> user:" + userId, tfe);                 //in com.cboe.idl.cmi.UserAccess
            throw ExceptionBuilder.systemException("Authentication with session management service failed", TransactionFailedCodes.CREATE_FAILED);
        }

    }

    public static void cleanupSession(Object clientListener, int sessionKey)
    {
        try
        {
            ServicesHelper.getSessionManagerCleanupHome().logoffUserSession(clientListener);
        }
        catch (Exception e)
        {
            Log.exception("UserLogonHelper --> internal session cleanUp failuer for listener:" + clientListener, e);
        }
        try
        {
            if (sessionKey != -1)
            {
                sms.leaveSession(sessionKey);
            }
        }
        catch (Exception e)
        {
            Log.exception("UserLogonHelper --> SMS cleanUp failure for sessionKey:" + sessionKey, e);
        }
    }

    public static String getMode(char sessionMode)
    {
        switch (sessionMode)
        {
            case LoginSessionModes.STAND_ALONE_TEST:
                return "STANDALONE TEST";
            case LoginSessionModes.NETWORK_TEST:
                return "NETWORK TEST";
            case LoginSessionModes.PRODUCTION:
                return "PRODUCTION";

            default:
                return "UNKNOWN LOGIN MODE";
        }
    }

    public static boolean waitForLogoutComplete(String userId)
    {
        return LogoutQueueFactory.find(userId).waitForLogoutComplete();
    }

    public static void checkLogoutInProgressPreLogin(String userId)
            throws AuthorizationException
    {
        checkLogoutInProgress(userId);
    }

    public static void checkLogoutInProgress(String userId)
            throws AuthorizationException
    {
        if (waitForLogoutComplete(userId))
        {
            throw ExceptionBuilder.authorizationException("Login rejected due to pending logouts for user:" + userId, AuthorizationCodes.NOT_PERMITTED);
        }
    }

    public static void checkLogoutInProgressPostLogin(String userId, Object clientListener, int sessionKey, boolean cleanupSession)
            throws AuthorizationException
    {
        if (waitForLogoutComplete(userId) && cleanupSession)
        {
            cleanupSession(clientListener, sessionKey);   // clean up this session.
            waitForLogoutComplete(userId);
            throw ExceptionBuilder.authorizationException("Login rejected due to pending logouts for user:" + userId, AuthorizationCodes.NOT_PERMITTED);
        }
    }


    public static void checkLoginMode(UserLogonStruct logonStruct, char sessionMode)
            throws DataValidationException
    {
        if (sessionMode != logonStruct.loginMode)
        {
            throw ExceptionBuilder.dataValidationException("Session mode mismatch. The client should be in session mode: " + getMode(sessionMode), DataValidationCodes.INVALID_LOGIN_MODE);
        }
    }

}
