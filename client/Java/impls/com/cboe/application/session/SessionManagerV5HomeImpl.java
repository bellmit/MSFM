package com.cboe.application.session;

import com.cboe.interfaces.application.SessionManagerV5Home;
import com.cboe.interfaces.application.SessionManagerV5;
import com.cboe.idl.user.SessionProfileUserStructV2;
import com.cboe.idl.cmiCallback.CMIUserSessionAdmin;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.util.ExceptionBuilder;

/**
 * Created by IntelliJ IDEA.
 * User: beniwalv
 * Date: Nov 1, 2007
 */

public class SessionManagerV5HomeImpl extends SessionManagerHomeImpl implements SessionManagerV5Home{

    public SessionManagerV5HomeImpl()
    {
        super();
    }

    public SessionManagerV5 createSessionManagerV5(SessionProfileUserStructV2 validUser, String sessionId,
                                                   int sessionKey, CMIUserSessionAdmin clientListener,
                                                   short sessionType, boolean gmdTextMessaging)
            throws DataValidationException, SystemException {

        // new session manager to be created here
        SessionManagerV5Impl userSessionManagerV5 = null;
        SessionManagerV5Interceptor wrappedSessionManagerV5 = null;
        // session manager new or existing
        SessionManagerV5 rawSessionManager = null;
        synchronized(this)
        {
            rawSessionManager = (SessionManagerV5) findSessionForListenerAndUser(validUser.userInfo.userId, clientListener);
            if(rawSessionManager == null)
            {
                if (Log.isDebugOn())
                {
                    Log.debug(this, "Creating a new session for user " + validUser.userInfo.userId);
                }
                // create a new instance of BOject
                userSessionManagerV5 = new SessionManagerV5Impl();
                userSessionManagerV5.create(String.valueOf(userSessionManagerV5.hashCode()));

                // add BObject to the container
                addToContainer(userSessionManagerV5);
                // add the session to cached collection
                addSession(clientListener, userSessionManagerV5, validUser);
                // now session manager is created
                rawSessionManager = userSessionManagerV5;
            } else
            	Log.alarm("Found duplicated CMIUserSessionAdmin callback for user " + validUser.userInfo.userId);
        }
        try
        {	// if it is new before, initialize session manager
            if(userSessionManagerV5 != null)
            {
                userSessionManagerV5.initialize(validUser, sessionId, sessionKey, isLazyIntialization(), clientListener,
                                              sessionType, gmdTextMessaging, true);
            }
            // create BOInterceptor
            wrappedSessionManagerV5 = (SessionManagerV5Interceptor) createInterceptor((BObject) rawSessionManager);
            wrappedSessionManagerV5.setSessionManager(rawSessionManager);

            if(getInstrumentationEnablementProperty())
            {
                wrappedSessionManagerV5.startInstrumentation(getInstrumentationProperty());
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

        return wrappedSessionManagerV5;  
    }
}
