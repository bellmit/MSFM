package com.cboe.remoteApplication.session;

import com.cboe.domain.startup.ClientBOHome;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.remoteApplication.RemoteCASSessionManager;
import com.cboe.interfaces.remoteApplication.RemoteCASSessionManagerHome;
import com.cboe.interfaces.application.UserSessionQueryHome;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.SystemException;

import java.util.*;

/**
 * @author Jing Chen
 */
public class RemoteCASSessionManagerHomeImpl extends ClientBOHome
        implements RemoteCASSessionManagerHome, UserSessionQueryHome
{
    //private Hashtable sessionsByUser;
    private Hashtable sessionsByUserIor;

    public RemoteCASSessionManagerHomeImpl()
    {
        sessionsByUserIor = new Hashtable(11);
    }

    public synchronized RemoteCASSessionManager find(String userSessionIor, String userId, String casOrigin)
    {
        return create(userSessionIor, userId, casOrigin);
    }

    public RemoteCASSessionManager create(String userSessionIor, String userId, String casOrigin)
    {
        RemoteCASSessionManagerImpl remoteCASSessionManager = (RemoteCASSessionManagerImpl)(sessionsByUserIor.get(userSessionIor));
        if (remoteCASSessionManager == null)
        {
            remoteCASSessionManager = new RemoteCASSessionManagerImpl(userSessionIor);
            addToContainer(remoteCASSessionManager);
            remoteCASSessionManager.initialize(userId, casOrigin);
            sessionsByUserIor.put(userSessionIor, remoteCASSessionManager);
        }
        return remoteCASSessionManager;
    }

    public Map getActiveSessions()
    {
        synchronized(sessionsByUserIor)
        {
            return (Map)sessionsByUserIor.clone();
        }
    }

    public void remove(String userIor)
    {
        sessionsByUserIor.remove(userIor);
    }

    public void clientShutdown()
    {
        logoffAllUsers();
    }

    public void logoffAllUsers()
    {
        Enumeration sessionsEnum = sessionsByUserIor.elements();

        RemoteCASSessionManagerImpl session = null;
        while(sessionsEnum.hasMoreElements())
        {
            session = (RemoteCASSessionManagerImpl)sessionsEnum.nextElement();
            try
            {
                if (Log.isDebugOn())
                {
                    Log.debug(this, "*******Removing session for user " + session.getUserId());
                }
                session.publishLogout();
                sessionsByUserIor.remove(session);
            }
            catch (Exception e)
            {
                Log.exception(this, "Error removing session", e);
            }
        }
    }
    
    public String[] getLoggedInUserIds() throws SystemException, CommunicationException, AuthorizationException
    {
        int numberOfUsers = sessionsByUserIor.size();
        Set users = new HashSet(Math.max((int) (numberOfUsers/.75f) + 1, 16));
        
        synchronized(sessionsByUserIor) //don't want a concurrent modification exception
        {
            Enumeration sessionEnum = sessionsByUserIor.elements();
            while(sessionEnum.hasMoreElements())
            {
                RemoteCASSessionManagerImpl session = (RemoteCASSessionManagerImpl) sessionEnum.nextElement();
                String userId =  session.getUserId();
                users.add(userId);
            }
        }

        String[] userList = new String[users.size()];
        users.toArray(userList);
        
        return userList;
    }
}
