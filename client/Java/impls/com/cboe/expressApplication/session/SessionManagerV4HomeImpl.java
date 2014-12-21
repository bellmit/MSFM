//
// -----------------------------------------------------------------------------------
// Source file: SessionManagerV4HomeImpl.java
//
// PACKAGE: com.cboe.expressApplication.session
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.expressApplication.session;

import com.cboe.idl.user.SessionProfileUserStructV2;
import com.cboe.idl.cmiCallback.CMIUserSessionAdmin;

import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;

import com.cboe.interfaces.expressApplication.SessionManagerV4Home;
import com.cboe.interfaces.expressApplication.SessionManagerV4;
import com.cboe.interfaces.application.SessionManagerCleanupHome;
import com.cboe.interfaces.application.RemoteSessionManagerHome;
import com.cboe.interfaces.application.UserSessionQueryHome;

import com.cboe.util.ExceptionBuilder;

import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.application.session.SessionManagerHomeImpl;

/**
 * Extending SessionManagerHomeImpl to reuse its tracking/caching of sessions and listeners.
 */
public class SessionManagerV4HomeImpl extends SessionManagerHomeImpl
        implements SessionManagerV4Home, SessionManagerCleanupHome, RemoteSessionManagerHome, UserSessionQueryHome
{
    public SessionManagerV4HomeImpl()
    {
        super();
    }

    /**
     * This method creates a new SessionManagerV4 object given the user and session information.
     * @return the SessionManagerV4 for the requesting user.
     */
    public SessionManagerV4 createSessionManagerV4(SessionProfileUserStructV2 validUser, String sessionId,
                                                        int sessionKey,
                                                        CMIUserSessionAdmin clientListener, short sessionType,
                                                        boolean gmdTextMessaging)
            throws DataValidationException, SystemException
    {
        SessionManagerV4Impl userSessionManager = null;
        SessionManagerV4Interceptor wrappedSessionManager;
        SessionManagerV4 rawSessionManager;
        synchronized(this)
        {
            rawSessionManager = (SessionManagerV4)findSessionForListenerAndUser(validUser.userInfo.userId, clientListener);
            if(rawSessionManager == null)
            {
                if (Log.isDebugOn())
                {
                    Log.debug(this, "Creating a new session for user " + validUser.userInfo.userId);
                }

                userSessionManager = new SessionManagerV4Impl();
                userSessionManager.create(String.valueOf(userSessionManager.hashCode()));

                //add the bo to the container.
                addToContainer(userSessionManager);
                addSession(clientListener, userSessionManager, validUser);

                rawSessionManager = userSessionManager;
            }
        }
        try
        {
            if(userSessionManager != null)
            {
                userSessionManager.initialize(validUser, sessionId, sessionKey, isLazyIntialization(), clientListener,
                                              sessionType, gmdTextMessaging, true);
            }
            wrappedSessionManager = (SessionManagerV4Interceptor) createInterceptor((BObject) rawSessionManager);
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
                throw ExceptionBuilder.systemException("Could not initialize user session for user "+validUser.userInfo.userId, 1);
            }
        }
        return wrappedSessionManager;
    }

    /**
     * Removes an instance of the session.
     * @param userId  the valid user information
     * @param session the session to be removed in association with the user
     */
    public synchronized void remove(SessionManagerV4 session, String userId)
    {
        // the maps of sessions are maintained in super class
        if (Log.isDebugOn())
        {
            Log.debug(this, "*******Removing session for user " + session);
        }
        super.remove(session, userId);
    }
}
