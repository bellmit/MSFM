package com.cboe.application.supplier;

import java.util.*;

import com.cboe.util.*;
import com.cboe.interfaces.application.*;
import com.cboe.interfaces.domain.session.*;

import com.cboe.infrastructureServices.foundationFramework.utilities.*;

import com.cboe.domain.startup.ClientBOHome;
import com.cboe.domain.instrumentedChannel.InstrumentedThreadPool;
import com.cboe.domain.util.InstrumentorNameHelper;
import org.omg.CORBA.UserException;

/**
 *  UserSessionThreadPoolHomeImpl
 *
 *  This home acts as a cache of ThreadPools for a given session.
 *  ThreadPools are cached based on session.
 *  Author: Connie Feng
 */
public class UserSessionThreadPoolHomeImpl extends ClientBOHome implements UserSessionThreadPoolHome {

    public final static String THREAD_POOL       = "UserSessionThreadPool";
    public final static String THREAD_POOL_SIZE  = "UserSessionThreadPoolSize";
    public final static String USER_ROLE_OVERRIDE = "UserRoleOverride";
    public final static String THREAD_POOL_SIZE_OVERRIDE = "ThreadPoolSizeOverride";
    public static final String TOKENIZER_DELIMITERS = ",; ";
    public static final String ENABLE_OVERRIDE = "UserSessionThreadPoolOverrideEnabled";
    
    

    // Default ThreadPool size - used only if it does not get defined in the
    // XML file for that property (SMA)
    private             int threadPoolSize;
    private             boolean overrideEnabled = false;

    // map of user ids to rate monitors
    private HashMap threadPoolMap = null;
    private HashMap<Integer, Integer> userRoleThreadPoolOverride;

    public UserSessionThreadPoolHomeImpl() {
        super();

        // map sesion to threadpool
        threadPoolMap = new HashMap();
        userRoleThreadPoolOverride = new HashMap<Integer, Integer>();
    }

    public void clientInitialize()
        throws Exception
    {
        threadPoolSize = Integer.parseInt(getProperty(THREAD_POOL_SIZE));
        if (Log.isDebugOn())
        {
            Log.debug(this, "User Session ThreadPool Size::" + threadPoolSize );
        }
        String str = System.getProperty(ENABLE_OVERRIDE, "false");
        if(str.equalsIgnoreCase("true")) {
            overrideEnabled = true;
            if (Log.isDebugOn())
            {
                Log.debug(this, "User Session Thread Pool Override is enabled.");
            }
        }
        else {
            if (Log.isDebugOn())
            {
                Log.debug(this, "User Session Thread Pool Override is disenabled.");
            }
            return;
        }
        
        String userRoleOverride = getProperty(USER_ROLE_OVERRIDE);
        String threadPoolOverride = getProperty(THREAD_POOL_SIZE_OVERRIDE);
        if (Log.isDebugOn())
        {
            Log.debug(this, "User Role Thread Pool Override::" +
                            userRoleOverride + "/" +
                            threadPoolOverride);
        }
        if(userRoleOverride.length() > 0 && threadPoolOverride.length() > 0)
        {
            StringTokenizer urorTokenizer = new StringTokenizer(userRoleOverride, TOKENIZER_DELIMITERS, false);
            StringTokenizer tporTokenizer = new StringTokenizer(threadPoolOverride, TOKENIZER_DELIMITERS, false);
            while(urorTokenizer.hasMoreTokens())
            {
                String userRole = urorTokenizer.nextToken();
                if(tporTokenizer.hasMoreTokens())
                {               
                   Integer key = Integer.valueOf(userRole.charAt(0));
                   Integer val = Integer.valueOf(tporTokenizer.nextToken());
                   userRoleThreadPoolOverride.put(key,val);
                }
            } 
        }
        
    }
    /**
     *  Create a new ThreadPool for a given session
     *  @param session  Session Manager to create a thread pool for
     *  @return com.cboe.util.ThreadPool
     *
     */
    public synchronized ThreadPool create(BaseSessionManager session)
    {
        ThreadPool threadPool = getThreadPool(session);

        if ( threadPool == null && session != null)
        {
            try
            {
                int tpSize = threadPoolSize;
                Integer key = Integer.valueOf(session.getValidSessionProfileUserV2().userInfo.role);
                if(overrideEnabled && userRoleThreadPoolOverride.containsKey(key)) {
                    tpSize = userRoleThreadPoolOverride.get(key).intValue();
                }
                threadPool = new InstrumentedThreadPool(tpSize,
                        InstrumentorNameHelper.createInstrumentorName(new String[]{session.getInstrumentorName(), THREAD_POOL}, this));
                threadPool.setQuickShutdown(true);
            }
            catch(UserException e)
            {
                Log.exception(e);
            }
            updateThreadPool(threadPool, session);
        }

        return threadPool;
    }

    /**
     *  Return ThreadPool given a specific Session
     *
     *  @param session the session manager to create the pool for
     *  @return com.cboe.util.ThreadPool
     */
    public ThreadPool find(BaseSessionManager session)
    {
        return create(session);
    }

    private ThreadPool getThreadPool(BaseSessionManager session)
    {
        ThreadPool threadPool =  (ThreadPool)threadPoolMap.get(session);
        return threadPool;
    }

    /**
     *  Synchronized update of a cache with a given session and thread pool
     *
     *  @param threadPool cache to be updated
     *  @param session hash key
     */
    private void updateThreadPool(ThreadPool threadPool, BaseSessionManager session)
    {
        threadPoolMap.put(session, threadPool);
    }

    /**
     *  removes ThreadPool given a specific Session
     *
     *  @param session the session manager to Remove
     *  @return none
     */
    public synchronized void remove(BaseSessionManager session)
    {
        getThreadPool(session).shutdown();
        threadPoolMap.remove(session);
    }

    public void clientStart()
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "SMA Type = " + this.getSmaType());
        }
    }
}
