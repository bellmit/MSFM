package com.cboe.application.tmsSession;

import com.cboe.application.session.SessionManagerHomeImpl;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.idl.cmiCallback.CMIUserSessionAdmin;
import com.cboe.idl.user.SessionProfileUserStructV2;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.SessionManagerTMS;
import com.cboe.interfaces.application.SessionManagerTMSHome;
import com.cboe.util.ExceptionBuilder;

public class SessionManagerTMSHomeImpl 
			extends SessionManagerHomeImpl 
			implements SessionManagerTMSHome {

	public SessionManagerTMS createSessionManagerTMS(SessionProfileUserStructV2 validUser, String sessionId, int sessionKey, CMIUserSessionAdmin clientListener, short sessionType, boolean gmdTextMessaging) 
			throws DataValidationException, 
				   SystemException {
		// new session manager to be created here
        SessionManagerTMSImpl userSessionManager = null;
        SessionManagerTMSInterceptor wrappedSessionManager = null;
        // session manager new or existing 
        SessionManagerTMS rawSessionManager = null;
        synchronized(this)
        {
            rawSessionManager = (SessionManagerTMS) findSessionForListenerAndUser(validUser.userInfo.userId, clientListener);
            if(rawSessionManager == null)
            {
                if (Log.isDebugOn())
                {
                    Log.debug(this, "Creating a new session for user " + validUser.userInfo.userId);
                }
                // create a new instance of BOject
                userSessionManager = new SessionManagerTMSImpl();
                userSessionManager.create(String.valueOf(userSessionManager.hashCode()));

                // add BObject to the container
                addToContainer(userSessionManager);
                // add the session to cached collection
                addSession(clientListener, userSessionManager, validUser);
                // now session manager is created 
                rawSessionManager = userSessionManager;
            } else
            	Log.alarm(this, "Found duplicated CMIUserSessionAdmin callback for user " + validUser.userInfo.userId);
        }
        try
        {	// if it is new before, initialize session manager
            if(userSessionManager != null)
            {
                userSessionManager.initialize(validUser, sessionId, sessionKey, isLazyIntialization(), clientListener,
                                              sessionType, gmdTextMessaging, true);
            }
            // create BOInterceptor
            wrappedSessionManager = (SessionManagerTMSInterceptor) createInterceptor((BObject) rawSessionManager);
            wrappedSessionManager.setSessionManager(rawSessionManager);
            
            if(getInstrumentationEnablementProperty())
            {
                wrappedSessionManager.startInstrumentation(getInstrumentationProperty());
            }
        }
        catch(Exception ex)
        {
            Log.exception(this, ex);
            if(ex instanceof DataValidationException)
            {
                throw (DataValidationException) ex;
            }
            else
            {
                throw ExceptionBuilder.systemException("Could not initialize TMS user session for user "+validUser.userInfo.userId, 1);
            }
        }
        return wrappedSessionManager;
	}
	
}
