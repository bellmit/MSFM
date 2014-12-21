package com.cboe.application.session;

import com.cboe.interfaces.application.SessionManagerV9Home;
import com.cboe.interfaces.application.SessionManagerV9;
import com.cboe.idl.user.SessionProfileUserStructV2;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.util.ExceptionBuilder;
import com.cboe.idl.cmiCallback.CMIUserSessionAdmin;

public class SessionManagerV9HomeImpl extends SessionManagerV8HomeImpl implements SessionManagerV9Home
{

    public SessionManagerV9HomeImpl()
    {
        super();
    }

    public SessionManagerV9 createSessionManagerV9(SessionProfileUserStructV2 validUser, String sessionId,
                                                   int sessionKey, CMIUserSessionAdmin clientListener,
                                                   short sessionType, boolean gmdTextMessaging)
            throws DataValidationException, SystemException
    {

        // new session manager to be created here
        SessionManagerV9Impl userSessionManagerV9 = null;
        SessionManagerV9Interceptor wrappedSessionManagerV9 = null;
        // session manager new or existing
        SessionManagerV9 rawSessionManager = null;

        synchronized(this)
        {
            rawSessionManager = (SessionManagerV9) findSessionForListenerAndUser(validUser.userInfo.userId, clientListener);
            if(rawSessionManager == null)
            {
                Log.debug(this, "Creating a new session for user " + validUser.userInfo.userId);
                // create a new instance of BOject
                userSessionManagerV9 = new SessionManagerV9Impl();
                userSessionManagerV9.create(String.valueOf(userSessionManagerV9.hashCode()));

                // add BObject to the container
                addToContainer(userSessionManagerV9);
                // add the session to cached collection
                addSession(clientListener, userSessionManagerV9, validUser);
                // now session manager is created
                rawSessionManager = userSessionManagerV9;
            } else
                Log.alarm("Found duplicated CMIUserSessionAdmin callback for user " + validUser.userInfo.userId);
        }
        try
        {	// if it is new before, initialize session manager
            if(userSessionManagerV9 != null)
            {
                userSessionManagerV9.initialize(validUser, sessionId, sessionKey, isLazyIntialization(), clientListener,
                                                sessionType, gmdTextMessaging, true);
            }
            // create BOInterceptor
            wrappedSessionManagerV9 = (SessionManagerV9Interceptor) createInterceptor((com.cboe.infrastructureServices.foundationFramework.BObject) rawSessionManager);
            wrappedSessionManagerV9.setSessionManager(rawSessionManager);

            if(getInstrumentationEnablementProperty())
            {
                wrappedSessionManagerV9.startInstrumentation(getInstrumentationProperty());
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
                throw ExceptionBuilder.systemException("Could not initialize V9 user session for user " + validUser.userInfo.userId, 1);
            }
        }

        return wrappedSessionManagerV9;
    }

}