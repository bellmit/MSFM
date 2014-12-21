package com.cboe.application.inprocess.session;

import com.cboe.application.session.SessionManagerImpl;
import com.cboe.domain.startup.ClientBOHome;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.idl.cmiUser.UserStruct;
import com.cboe.idl.user.SessionProfileUserStructV2;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.SessionManager;
import com.cboe.interfaces.application.SessionManagerCleanupHome;
import com.cboe.interfaces.application.UserSessionQueryHome;
import com.cboe.interfaces.application.inprocess.InProcessSessionManager;
import com.cboe.interfaces.application.inprocess.InProcessSessionManagerHome;
import com.cboe.interfaces.application.inprocess.UserSessionAdminConsumer;
import com.cboe.util.ExceptionBuilder;

import java.util.*;

/**
 * @author Jing Chen
 */
public class InProcessSessionManagerHomeImpl extends ClientBOHome implements InProcessSessionManagerHome, SessionManagerCleanupHome, UserSessionQueryHome
{
    private Hashtable sessionsByListener;
    private Hashtable sessionsByUser;
    private Hashtable listenersBySession;
    private static final SessionManager[] EMPTY_SessionManager_ARRAY = new SessionManager[0];

    /**
     * InProcessSessionManagerHomeImpl constructor comment.
     */
    public InProcessSessionManagerHomeImpl()
    {
        super();
        sessionsByListener = new Hashtable();
        listenersBySession = new Hashtable();
        sessionsByUser = new Hashtable();
    }

    public synchronized InProcessSessionManager createInProcessSession(SessionProfileUserStructV2 validUser, String sessionId, int sessionKey, UserSessionAdminConsumer clientListener)
        throws DataValidationException, SystemException
    {
        InProcessSessionManagerImpl userSessionManager = null;

        InProcessSessionManagerInterceptor wrappedSessionManager = null;
        SessionManager rawSessionManager = findSessionForListenerAndUser(validUser.userInfo.userId, clientListener);
        try
        {
            if ( rawSessionManager == null )
            {
                if (Log.isDebugOn())
                {
                    Log.debug(this, "Creating a new session for user " + validUser.userInfo.userId);
                }

                userSessionManager = new InProcessSessionManagerImpl();
                userSessionManager.create( String.valueOf( userSessionManager.hashCode() ) );
                //add the bo to the container.
                addToContainer( userSessionManager );
                addSession(clientListener, userSessionManager, validUser);
                userSessionManager.initialize(validUser, sessionId, sessionKey, clientListener);
                rawSessionManager = userSessionManager;
            }

            wrappedSessionManager = (InProcessSessionManagerInterceptor)createInterceptor( (BObject) rawSessionManager );
            wrappedSessionManager.setSessionManager(rawSessionManager);
        }
        catch (Exception ex)
        {
            Log.exception(this, ex);
            if (ex instanceof DataValidationException)
            {
                throw (DataValidationException)ex;
            }
            else
            {
                throw ExceptionBuilder.systemException("Could not initialize session", 1);
            }
        }

        return wrappedSessionManager;
    }

    public SessionManager find(UserStruct validUser)
    {
        return (SessionManager)sessionsByUser.get(validUser.userId);
    }

    public void clientShutdown()
    {
        logoffAllUsers(false);
    }

    public SessionManager[] find(String userId)
    {
        Vector sessions = (Vector)sessionsByUser.get(userId);
        if ( sessions != null )
        {
            return (SessionManager[]) sessions.toArray(EMPTY_SessionManager_ARRAY);
        }
        else
        {
            return EMPTY_SessionManager_ARRAY;
        }
    }

    public synchronized void logoffAllUsers(boolean componentFailure)
    {
        Enumeration sessionsEnum = sessionsByListener.elements();

        InProcessSessionManagerImpl session = null;
        while(sessionsEnum.hasMoreElements())
        {
            session = (InProcessSessionManagerImpl)sessionsEnum.nextElement();
            notifySessionForShutdown(session, componentFailure);
        }
    }

    public void logoffUserSession(Object clientListener)
    {
        InProcessSessionManagerImpl sessionManager = (InProcessSessionManagerImpl)sessionsByListener.get(clientListener);
        if(sessionManager != null)
        {
            notifySessionForShutdown(sessionManager, false);
        }
    }

    private void notifySessionForShutdown(InProcessSessionManagerImpl session, boolean componentFailure)
    {
        try
        {
            UserStruct validUser = session.getValidUser();
            UserSessionAdminConsumer sessionListener = (UserSessionAdminConsumer)listenersBySession.get(session);
            if (componentFailure) {
                   sessionListener.acceptLogout("(CAS)critical components unreachable due to process failure.");
            } else {
                sessionListener.acceptLogout("Session shutdown.");
            }
            StringBuilder forcelogout = new StringBuilder(validUser.userId.length()+75);
            forcelogout.append("Force user to logout due to the shutdown of the session home for user: ").append(validUser.userId);
            Log.alarm(this, forcelogout.toString());
            session.logout();
        }
        catch(Exception e)
        {
            Log.exception(this, e);
        }
    }

    private SessionManager findSessionForListenerAndUser(String userId, UserSessionAdminConsumer clientListener)
    {
        SessionManager session = null;
        if (clientListener != null) {
            session = (SessionManager)sessionsByListener.get(clientListener);
        }

        try {
            if (session != null && !session.getValidUser().userId.equals(userId)) {
                session = null;
            }
        } catch (Exception e) {
            Log.exception(this, "failed attempting to locate session", e);
            session = null;
        }

        return session;
    }

    private void addSession(UserSessionAdminConsumer clientListener, SessionManagerImpl sessionManager, SessionProfileUserStructV2 validUser)
    {
        try {
            sessionsByListener.put(clientListener, sessionManager);
            listenersBySession.put(sessionManager, clientListener);
            sessionsByUser.put(validUser.userInfo.userId, sessionManager);
        } catch (Exception e) {
            Log.exception(this, "Failed to add the session", e);
        }
    }

    public void clientStart()
        throws Exception
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "SMA Type = " + this.getSmaType());
        }
    }

    public Map getActiveSessions()
    {
        synchronized(sessionsByListener)
        {
            return (Map)sessionsByListener.clone();
        }
    }

    public synchronized void remove(InProcessSessionManager session, String userId)
    {
        try
        {
            if (Log.isDebugOn())
            {
                Log.debug(this, "*******Removing session for user " + session);
            }
            sessionsByUser.remove(userId);
            Object listener = listenersBySession.remove(session);
            if(listener != null)
            {
                sessionsByListener.remove(listener);
            }
        }
        catch (Exception e)
        {
            Log.exception(this, "Error removing session", e);
        }

    }

    public String[] getLoggedInUserIds()
    {
        // Thread safe because key set returned is a synchronized set which
        // uses the Hashtable object as the mutex.  The toArray() method
        // called below synchronizes on the Hashtable and even allocates a new
        // String array if the size of the array does not match the size of the
        // key set (which could occur if another thread changes the size of the set
        // after I allocate the String array and before toArray gets the lock.
        Set users = sessionsByUser.keySet();
        String[] userIds = new String[users.size()];
        users.toArray(userIds);
        return userIds;
    }
}
