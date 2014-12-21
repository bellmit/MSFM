package com.cboe.cfix.cas.sessionManager;

/**
 * @author Jing Chen
 */

import java.util.*;

import com.cboe.application.supplier.*;
import com.cboe.domain.startup.*;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiConstants.*;
import com.cboe.idl.cmiUser.*;
import com.cboe.idl.user.SessionProfileUserStructV2;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.*;
import com.cboe.interfaces.application.*;
import com.cboe.interfaces.cfix.*;
import com.cboe.util.*;
import com.cboe.util.channel.*;

public class CfixSessionManagerHomeImpl extends ClientBOHome implements CfixSessionManagerHome, SessionManagerCleanupHome, UserSessionQueryHome
{
    private Hashtable sessionsByListener;
    private Hashtable sessionsByUser;
    private Hashtable listenersBySession;
    private static String LAZY_INITIALIZATION = "useLazyInitialization";
    private boolean userLazyInitialize = false;

    /**
     * SessionManagerHomeImpl constructor comment.
     */
    public CfixSessionManagerHomeImpl()
    {
        super();
        sessionsByListener = new Hashtable();
        listenersBySession = new Hashtable();
        sessionsByUser = new Hashtable();
    }

    private boolean primaryExists(SessionProfileUserStructV2 validUser)
    {
        Vector sessions = (Vector)sessionsByUser.get(validUser.userInfo.userId);
        CfixSessionManager session;
        short loginType;
        if (sessions != null) {
            for (int i = 0; i < sessions.size(); i++) {
                session = (CfixSessionManager)sessions.elementAt(i);
                try {
                    loginType = session.getLoginType();
                    if (loginType == LoginSessionTypes.PRIMARY) {
                        return true;
                    }
                } catch (Exception e) {
                    Log.exception(this, e);
                }
            }
        }
        return false;
    }

    public synchronized CfixSessionManager createCfixSession(SessionProfileUserStructV2 validUser, String sessionId, CfixUserSessionAdminConsumer clientListener, short sessionType, boolean gmdTextMessaging)
        throws DataValidationException, SystemException
    {
        CfixSessionManagerImpl cfixUserSessionManager = null;
        boolean primaryExists = primaryExists(validUser);

        CfixSessionManagerInterceptor wrappedSessionManager = null;
        CfixSessionManager rawSessionManager = findSessionForListenerAndUser(validUser.userInfo.userId, clientListener);
        try
        {

            if ( rawSessionManager == null )
            {
                if (Log.isDebugOn())
                {
                    Log.debug(this, "Creating a new session for user " + validUser.userInfo.userId);
                }

                cfixUserSessionManager = new CfixSessionManagerImpl();
                cfixUserSessionManager.create( String.valueOf( cfixUserSessionManager.hashCode() ) );
                //add the bo to the container.
                addToContainer( cfixUserSessionManager );
                addSession(clientListener, cfixUserSessionManager, validUser);
                cfixUserSessionManager.initialize( validUser, sessionId, isLazyIntialization(), clientListener, sessionType, gmdTextMessaging );
                rawSessionManager = cfixUserSessionManager;
            }

            wrappedSessionManager = (CfixSessionManagerInterceptor)createInterceptor( (BObject) rawSessionManager );
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

    public synchronized void remove(CfixSessionManager session, String userId)
    {
        try {
            if (Log.isDebugOn())
            {
                Log.debug(this, "*******Removing cfix session for user " + session);
            }

            Vector sessions = (Vector)sessionsByUser.remove(userId);
            if ( sessions != null ) {
                sessions.remove(session);
                if (Log.isDebugOn())
                {
                    Log.debug(this, "******User " + userId + " has " + sessions.size() + " remaining cfix session(s) established");
                }
            };

            CfixUserSessionAdminConsumer clientListener = (CfixUserSessionAdminConsumer)listenersBySession.remove(session);
            if ( clientListener != null ) {
                sessionsByListener.remove(clientListener);
            }
        } catch (Exception e) {
            Log.exception(this, "Error removing cfix session", e);
        }
    }

    protected CfixSessionManager findSessionForListenerAndUser(String userId, CfixUserSessionAdminConsumer clientListener)
    {
        CfixSessionManager session = (CfixSessionManager)sessionsByListener.get(clientListener);
        try {
            if (session != null && !session.getValidUser().userId.equals(userId)) {
                session = null;
            }
        } catch (Exception e) {
            Log.exception(this, "Failed attempting to locate cfix session", e);
            session = null;
        }

        return session;
    }

    protected void addSession(CfixUserSessionAdminConsumer clientListener, CfixSessionManagerImpl sessionManager, SessionProfileUserStructV2 validUser)
    {
        try {
            sessionsByListener.put(clientListener, sessionManager);
            listenersBySession.put(sessionManager, clientListener);
            Vector sessions = (Vector)sessionsByUser.get(validUser.userInfo.userId);
            if ( sessions == null ) {
                sessions = new Vector();
            }
            sessions.add(sessionManager);
            sessionsByUser.put(validUser.userInfo.userId, sessions);
        } catch (Exception e) {
            Log.exception(this, "Failed to add the cfix session", e);
        }
    }
    public void clientShutdown()
    {
        logoffAllUsers(false);
    }

    public void logoffAllUsers(boolean componentFailure)
    {
        Enumeration sessionsEnum = sessionsByListener.elements();

		CfixSessionManagerImpl session = null;
		while(sessionsEnum.hasMoreElements())
		{
		    session = (CfixSessionManagerImpl)sessionsEnum.nextElement();
		    notifySessionForShutdown(session, componentFailure);
        }
    }

    public void logoffUserSession(Object clientListener)
    {
        CfixSessionManagerImpl sessionManager = (CfixSessionManagerImpl)sessionsByListener.get(clientListener);
        if(sessionManager != null)
        {
            notifySessionForShutdown(sessionManager, false);
        }
    }

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

    public synchronized Map getActiveSessions()
    {
        return (Map)sessionsByListener.clone();
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

    private void notifySessionForShutdown(SessionManager session, boolean componentFailure)
    {
        try
        {

            UserSessionAdminSupplier adminSupplier = UserSessionAdminSupplierFactory.find(session);
            ChannelKey key = new ChannelKey(ChannelType.CB_LOGOUT, session.getValidUser().userId);

            ChannelEvent event = null;
            if (componentFailure) {
                event = adminSupplier.getChannelEvent(this, key, "(CFIX)critical components unreachable due to process failure.");
            } else {
                event = adminSupplier.getChannelEvent(this, key, "Session shutdown.");
            }
            adminSupplier.dispatch(event);

            Log.alarm(this, "Force user to logout due to the shutdown of the session home for user: " + session.getValidUser().userId);
            session.logout();
        }
        catch(Exception e)
        {
            Log.exception(this, e);
        }
    }

}
