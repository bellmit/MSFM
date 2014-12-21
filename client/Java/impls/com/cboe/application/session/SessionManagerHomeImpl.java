package com.cboe.application.session;

import com.cboe.application.shared.ServicesHelper;
import com.cboe.application.supplier.UserSessionAdminSupplier;
import com.cboe.application.supplier.UserSessionAdminSupplierFactory;
import com.cboe.domain.startup.ClientBOHome;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.idl.cmiCallback.CMIUserSessionAdmin;
import com.cboe.idl.user.SessionProfileUserStructV2;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.*;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.ExceptionBuilder;
import com.cboe.util.channel.ChannelEvent;

import java.util.*;
/**
 * This class controls the creation of all users SessionManager objects
 * and implements the CBEO Home Pattern.
 *
 * @author Derek T. Chambers-Boucher
 * @author Connie Feng
 * @version 06/26/1999
 *
 */

public class SessionManagerHomeImpl extends ClientBOHome
        implements SessionManagerHome, SessionManagerCleanupHome, RemoteSessionManagerHome, UserSessionQueryHome
{
    private Hashtable sessionsByListener;
    private Hashtable sessionsByUser;
    private Hashtable listenersBySession;
    private Hashtable remoteSessions;
    private static String LAZY_INITIALIZATION = "useLazyInitialization";
    private static final SessionManager[] EMPTY_SessionManager_ARRAY = new SessionManager[0];
    private boolean userLazyInitialize = false;
    /**
     * SessionManagerHomeImpl constructor comment.
     */
    public SessionManagerHomeImpl()
    {
        super();
        sessionsByListener = new Hashtable();
        listenersBySession = new Hashtable();
        sessionsByUser = new Hashtable();
        remoteSessions = new Hashtable();
    }

    /**
     * This method creates a new SessionManager object given the ValidUserStruct.
     *
     * @param validUser the ValidUserStruct representing the user requesting the APIManager.
     * @return the SessionManager for the requesting user.
     *
     */
    public SessionManager create(SessionProfileUserStructV2 validUser, String sessionId, int sessionKey, CMIUserSessionAdmin clientListener, short sessionType, boolean gmdTextMessaging)
        throws DataValidationException, SystemException
    {
        SessionManagerImpl userSessionManager = null;
        SessionManagerInterceptor wrappedSessionManager = null;
        SessionManager rawSessionManager = null;
        synchronized(this)
        {
            rawSessionManager = findSessionForListenerAndUser(validUser.userInfo.userId, clientListener);
            if ( rawSessionManager == null )
            {
                if (Log.isDebugOn())
                {
                    Log.debug(this, "Creating a new session for user " + validUser.userInfo.userId);
                }

                userSessionManager = new SessionManagerImpl();
                userSessionManager.create( String.valueOf( userSessionManager.hashCode() ) );

                //add the bo to the container.
                addToContainer( userSessionManager );
                addSession(clientListener, userSessionManager, validUser);

                rawSessionManager = userSessionManager;
            }
        }
        try
        {
            if(userSessionManager != null)
            {
                userSessionManager.initialize( validUser, sessionId, sessionKey, isLazyIntialization(), clientListener, sessionType, gmdTextMessaging, true );
            }
            wrappedSessionManager = (SessionManagerInterceptor)createInterceptor( (BObject) rawSessionManager );
            wrappedSessionManager.setSessionManager(rawSessionManager);
            if(getInstrumentationEnablementProperty())
            {
                wrappedSessionManager.startInstrumentation(getInstrumentationProperty());
            }
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

    /**
    * Removes an instance of the session.
    *
    * @param userId the valid user information
    * @param session the session to be removed in association with the user
    */
    public synchronized void remove(SessionManager session, String userId)
    {
        try {
            if (Log.isDebugOn())
            {
                Log.debug(this, "*******Removing session for user " + session);
            }

       //     Vector sessions = (Vector)sessionsByUser.get(session.getValidUser().userId);
            Vector sessions = (Vector)sessionsByUser.get(userId);
            if ( sessions != null ) {
                sessions.remove(session);
                if (Log.isDebugOn())
                {
                    Log.debug(this, "******User " + userId + " has " + sessions.size() + " remaining session(s) established");
                }
                if(sessions.size() == 0)
                {
                    sessionsByUser.remove(userId);
                }
            };

            String ior = (String)listenersBySession.remove(session);
            if ( ior != null ) {
                sessionsByListener.remove(ior);
            }
        } catch (Exception e) {
            Log.exception(this, "Error removing session", e);
        }
    }

    /**
    * Finds an instance of the session manager service.
    *
    * @return reference to session manager service
    *
    */
    public SessionManager[] find(String userId)

    {
        Vector sessions = (Vector)sessionsByUser.get(userId);
        if ( sessions != null ) {
            return (SessionManager[]) sessions.toArray(EMPTY_SessionManager_ARRAY);
        }
        else {
            return EMPTY_SessionManager_ARRAY;
        }
    }

    public String getUserSessionIor(SessionManager sessionManager)
    {
        return (String)listenersBySession.get(sessionManager);
    }

    /**
    * shuts down the home.  All users will be logged out
    *
    */
    public void clientShutdown()
    {
        logoffAllUsers(false);
    }

    public void logoffAllUsers(boolean componentFailure)
    {
        Enumeration sessionsEnum = sessionsByListener.elements();

        SessionManagerImpl session = null;
        while(sessionsEnum.hasMoreElements())
        {
            session = (SessionManagerImpl)sessionsEnum.nextElement();
            notifySessionForShutdown(session, componentFailure, false);
        }
    }

    public void logoffUserSession(Object clientListener)
    {
        CMIUserSessionAdmin listener = (CMIUserSessionAdmin) clientListener;
        String ior = ServicesHelper.getIORMaker().object_to_string(listener);
        SessionManagerImpl sessionManager = (SessionManagerImpl)sessionsByListener.get(ior);
        if(sessionManager != null)
        {
            notifySessionForShutdown(sessionManager, false, true);
        }
    }

    /**
    * Notifies the user for the shutdown.
    *
    */
    private void notifySessionForShutdown(SessionManagerImpl session, boolean componentFailure, boolean internalCleanupOnly)
    {
        String smgr = session.toString();
        StringBuilder sb = new StringBuilder(smgr.length()+80);
        sb.append("notify session for shutdown.  session:").append(session).append(" componentFailure:").append(componentFailure);
        Log.information(this, sb.toString());
        try
        {
            SessionProfileUserStructV2 validUser = session.getValidSessionProfileUserV2();

            UserSessionAdminSupplier adminSupplier = UserSessionAdminSupplierFactory.find(session);
            ChannelKey key = new ChannelKey(ChannelType.CB_LOGOUT, validUser.userInfo.userId);

            ChannelEvent event = null;
            if (componentFailure) {
                 event = adminSupplier.getChannelEvent(this, key, "(CAS)critical components unreachable due to process failure.");
            } else {
                 event = adminSupplier.getChannelEvent(this, key, "Session shutdown.");
            }
            adminSupplier.dispatch(event);

            sb.setLength(0);
            sb.append("Force user to logout due to the shutdown of the session home for user: ").append(validUser.userInfo.userId);
            Log.alarm(this, sb.toString());
            if(internalCleanupOnly)
            {
                session.publishLogout();
            }
            else
            {
                session.logout();
            }
        }
        catch(Exception e)
        {
            Log.exception(this, e);
        }
    }

    /**
    * Finds an instance of the session manager service that has been created.
    * @param userId the valid user information
    * @return reference to session manager service
    *
    */
    protected SessionManager findSessionForListenerAndUser(String userId, CMIUserSessionAdmin clientListener)
    {
        SessionManager session = null;
        String ior = ServicesHelper.getIORMaker().object_to_string(clientListener);
        StringBuilder sb = new StringBuilder(userId.length()+ior.length()+35);
        sb.append("User = ").append(userId).append(" IOR of callback object = ").append(ior);
        Log.information(this, sb.toString());
        if (ior != null) {
            session = (SessionManager)sessionsByListener.get(ior);
        }

        try {
            if (session != null)
            {
                sb.setLength(0);
                sb.append("Found session for the same heartbeat object. session:").append(session);
                Log.alarm(this, sb.toString());
                if(!session.getValidSessionProfileUser().userId.equals(userId))
                {
                    session = null;
                }
            }
        } catch (Exception e) {
            Log.exception(this, "failed attempting to locate session", e);
            session = null;
        }

        return session;
    }

    /**
    * Finds an instance of the session manager service that has been created.
    * @return reference to session manager service
    *
    */
    public SessionManager findRemoteSession(String userId, com.cboe.idl.cmi.UserSessionManager corbaSession )
    {
        SessionManager session = null;
        String ior = ServicesHelper.getIORMaker().object_to_string(corbaSession);

        if (Log.isDebugOn())
        {
            Log.debug(this, "->findRemoteSession, ior of UserSessionManager " + ior + " "+ corbaSession);
        }

        if (ior != null) {
            session = (SessionManager)remoteSessions.get(ior);
            if (Log.isDebugOn())
            {
                Log.debug(this, "->findRemoteSession, " + session);
            }
        }
        try {
            if (session != null && !session.getValidSessionProfileUser().userId.equals(userId)) {
                if (Log.isDebugOn())
                {
                    Log.debug(this, session.getValidSessionProfileUser().userId);
                }
                session = null;
            }
        } catch (Exception e) {
            Log.exception(this, "failed attempting to locate session", e);
            session = null;
        }

        return session;
    }
    /**
    * Adds an instance of the session manager service to the cached collection.
    * @param validUser the valid user information
    * @param sessionManager the session manager object
    */
    protected void addSession(CMIUserSessionAdmin clientListener, SessionManagerImpl sessionManager, SessionProfileUserStructV2 validUser)
    {
        try {
            // get IOR of client listener which we will use to uniquely identify a session
            String ior = ServicesHelper.getIORMaker().object_to_string(clientListener);
            sessionsByListener.put(ior, sessionManager);
            listenersBySession.put(sessionManager, ior);
            Vector sessions = (Vector)sessionsByUser.get(validUser.userInfo.userId);
            if ( sessions == null ) {
                sessions = new Vector();
            }
            sessions.add(sessionManager);
            sessionsByUser.put(validUser.userInfo.userId, sessions);
        } catch (Exception e) {
            Log.exception(this, "Failed to add the session", e);
        }
    }

    /**
     * Returns value of "session manager lazy initialization" property.
     *
     * @return <code>true</code> if lazy initialization, default to false
     *
     */
    public boolean isLazyIntialization()
    {
        return userLazyInitialize;
    }

    public void clientStart()
        throws Exception
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "SMA Type = " + this.getSmaType());
        }

        String retnVal = getProperty(LAZY_INITIALIZATION, "false");
        userLazyInitialize = (retnVal.equals("true"));
    }

    public Map getActiveSessions()
    {
        synchronized(sessionsByListener)
        {
            return (Map)sessionsByListener.clone();
        }
    }

    public void addRemoteSession(SessionManager sessionManager, com.cboe.idl.cmi.UserSessionManager corbaSession)
    {

        String ior = ServicesHelper.getIORMaker().object_to_string(corbaSession);
        if (Log.isDebugOn())
        {
            Log.debug(this, "->addRemoteSession, ior of UserSessionManager " + ior + " "+ corbaSession);
        }
        remoteSessions.put(ior, sessionManager);
    }

    public void removeRemoteSession(com.cboe.idl.cmi.UserSessionManager corbaSession)
    {
        String ior = null;;
        try
        {
            ior = ServicesHelper.getIORMaker().object_to_string(corbaSession);
            remoteSessions.remove(ior);
        }
        catch(Exception e)
        {
            Log.exception(this, e);
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
} //EOF
