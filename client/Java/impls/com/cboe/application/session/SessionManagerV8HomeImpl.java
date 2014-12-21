package com.cboe.application.session;

import com.cboe.interfaces.application.SessionManagerV8Home;
import com.cboe.interfaces.application.SessionManagerV8;
import com.cboe.idl.user.SessionProfileUserStructV2;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.util.ExceptionBuilder;
import com.cboe.idl.cmiCallback.CMIUserSessionAdmin;

public class SessionManagerV8HomeImpl extends SessionManagerV7HomeImpl implements SessionManagerV8Home
{

    public SessionManagerV8HomeImpl()
    {
        super();
    }

    public SessionManagerV8 createSessionManagerV8(SessionProfileUserStructV2 validUser, String sessionId,
                                                   int sessionKey, CMIUserSessionAdmin clientListener,
                                                   short sessionType, boolean gmdTextMessaging)
            throws DataValidationException, SystemException
    {

        // new session manager to be created here
        SessionManagerV8Impl userSessionManagerV8 = null;
        SessionManagerV8Interceptor wrappedSessionManagerV8 = null;
        // session manager new or existing
        com.cboe.interfaces.application.SessionManagerV8 rawSessionManager = null;

        synchronized(this)
        {
            rawSessionManager = (com.cboe.interfaces.application.SessionManagerV8) findSessionForListenerAndUser(validUser.userInfo.userId, clientListener);
            if(rawSessionManager == null)
            {
                if (Log.isDebugOn())
                {
                    Log.debug(this, "Creating a new session for user " + validUser.userInfo.userId);
                }
                // create a new instance of BOject
                userSessionManagerV8 = new SessionManagerV8Impl();
                userSessionManagerV8.create(String.valueOf(userSessionManagerV8.hashCode()));

                // add BObject to the container
                addToContainer(userSessionManagerV8);
                // add the session to cached collection
                addSession(clientListener, userSessionManagerV8, validUser);
                // now session manager is created
                rawSessionManager = userSessionManagerV8;
            } else
                Log.alarm("Found duplicated CMIUserSessionAdmin callback for user " + validUser.userInfo.userId);
        }
        try
        {	// if it is new before, initialize session manager
            if(userSessionManagerV8 != null)
            {
                userSessionManagerV8.initialize(validUser, sessionId, sessionKey, isLazyIntialization(), clientListener,
                                              sessionType, gmdTextMessaging, true);
            }
            // create BOInterceptor
            wrappedSessionManagerV8 = (com.cboe.application.session.SessionManagerV8Interceptor) createInterceptor((com.cboe.infrastructureServices.foundationFramework.BObject) rawSessionManager);
            wrappedSessionManagerV8.setSessionManager(rawSessionManager);

            if(getInstrumentationEnablementProperty())
            {
                wrappedSessionManagerV8.startInstrumentation(getInstrumentationProperty());
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
                throw ExceptionBuilder.systemException("Could not initialize V8 user session for user " + validUser.userInfo.userId, 1);
            }
        }

        return wrappedSessionManagerV8;
    }
}
