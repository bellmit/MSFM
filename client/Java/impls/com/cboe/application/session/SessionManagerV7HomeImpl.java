package com.cboe.application.session;

import com.cboe.interfaces.application.SessionManagerV7Home;
import com.cboe.interfaces.application.SessionManagerV7;
import com.cboe.idl.user.SessionProfileUserStructV2;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.util.ExceptionBuilder;
import com.cboe.idl.cmiCallback.CMIUserSessionAdmin;

public class SessionManagerV7HomeImpl extends SessionManagerV6HomeImpl implements SessionManagerV7Home
{

    public SessionManagerV7HomeImpl()
    {
        super();
    }

    public SessionManagerV7 createSessionManagerV7(SessionProfileUserStructV2 validUser, String sessionId,
                                                   int sessionKey, CMIUserSessionAdmin clientListener,
                                                   short sessionType, boolean gmdTextMessaging)
            throws DataValidationException, SystemException
    {

        // new session manager to be created here
        SessionManagerV7Impl userSessionManagerV7 = null;
        SessionManagerV7Interceptor wrappedSessionManagerV7 = null;
        // session manager new or existing
        com.cboe.interfaces.application.SessionManagerV7 rawSessionManager = null;

        synchronized(this)
        {
            rawSessionManager = (com.cboe.interfaces.application.SessionManagerV7) findSessionForListenerAndUser(validUser.userInfo.userId, clientListener);
            if(rawSessionManager == null)
            {
                if (Log.isDebugOn())
                {
                    Log.debug(this, "Creating a new session for user " + validUser.userInfo.userId);
                }
                // create a new instance of BObject
                userSessionManagerV7 = new SessionManagerV7Impl();
                userSessionManagerV7.create(String.valueOf(userSessionManagerV7.hashCode()));

                // add BObject to the container
                addToContainer(userSessionManagerV7);
                // add the session to cached collection
                addSession(clientListener, userSessionManagerV7, validUser);
                // now session manager is created
                rawSessionManager = userSessionManagerV7;
            } else
                Log.alarm("Found duplicated CMIUserSessionAdmin callback for user " + validUser.userInfo.userId);
        }
        try
        {	// if it is new before, initialize session manager
            if(userSessionManagerV7 != null)
            {
                userSessionManagerV7.initialize(validUser, sessionId, sessionKey, isLazyIntialization(), clientListener,
                                              sessionType, gmdTextMessaging, true);
            }
            // create BOInterceptor
            wrappedSessionManagerV7 = (com.cboe.application.session.SessionManagerV7Interceptor) createInterceptor((com.cboe.infrastructureServices.foundationFramework.BObject) rawSessionManager);
            wrappedSessionManagerV7.setSessionManager(rawSessionManager);

            if(getInstrumentationEnablementProperty())
            {
                wrappedSessionManagerV7.startInstrumentation(getInstrumentationProperty());
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
                throw ExceptionBuilder.systemException("Could not initialize V7 user session for user " + validUser.userInfo.userId, 1);
            }
        }

        return wrappedSessionManagerV7;
    }
}
