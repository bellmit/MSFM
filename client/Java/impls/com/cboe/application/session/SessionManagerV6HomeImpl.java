package com.cboe.application.session;

import com.cboe.interfaces.application.SessionManagerV6Home;
import com.cboe.interfaces.application.SessionManagerV6;
import com.cboe.idl.user.SessionProfileUserStructV2;
import com.cboe.idl.cmiCallback.CMIUserSessionAdmin;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.util.ExceptionBuilder;

/**
 * Created by IntelliJ IDEA.
 * User: josephg
 * Date: March 2, 2009
 */

public class SessionManagerV6HomeImpl extends SessionManagerV5HomeImpl implements SessionManagerV6Home{

    public SessionManagerV6HomeImpl()
    {
        super();
    }

    public SessionManagerV6 createSessionManagerV6(SessionProfileUserStructV2 validUser, String sessionId,
                                                   int sessionKey, CMIUserSessionAdmin clientListener,
                                                   short sessionType, boolean gmdTextMessaging)
            throws DataValidationException, SystemException {

        // new session manager to be created here
        SessionManagerV6Impl userSessionManagerV6 = null;
        SessionManagerV6Interceptor wrappedSessionManagerV6 = null;
        // session manager new or existing
        SessionManagerV6 rawSessionManager = null;

        synchronized(this)
        {
            rawSessionManager = (SessionManagerV6) findSessionForListenerAndUser(validUser.userInfo.userId, clientListener);
            if(rawSessionManager == null)
            {
                if (Log.isDebugOn())
                {
                    Log.debug(this, "Creating a new session for user " + validUser.userInfo.userId);
                }
                // create a new instance of BObject
                userSessionManagerV6 = new SessionManagerV6Impl();
                userSessionManagerV6.create(String.valueOf(userSessionManagerV6.hashCode()));

                // add BObject to the container
                addToContainer(userSessionManagerV6);
                // add the session to cached collection
                addSession(clientListener, userSessionManagerV6, validUser);
                // now session manager is created
                rawSessionManager = userSessionManagerV6;
            } else
            	Log.alarm("Found duplicated CMIUserSessionAdmin callback for user " + validUser.userInfo.userId);
        }
        try
        {	// if it is new before, initialize session manager
            if(userSessionManagerV6 != null)
            {
                userSessionManagerV6.initialize(validUser, sessionId, sessionKey, isLazyIntialization(), clientListener,
                                              sessionType, gmdTextMessaging, true);
            }
            // create BOInterceptor
            wrappedSessionManagerV6 = (SessionManagerV6Interceptor) createInterceptor((BObject) rawSessionManager);
            wrappedSessionManagerV6.setSessionManager(rawSessionManager);

            if(getInstrumentationEnablementProperty())
            {
                wrappedSessionManagerV6.startInstrumentation(getInstrumentationProperty());
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
                throw ExceptionBuilder.systemException("Could not initialize V5 user session for user "+validUser.userInfo.userId, 1);
            }
        }

        return wrappedSessionManagerV6;
    }
}
