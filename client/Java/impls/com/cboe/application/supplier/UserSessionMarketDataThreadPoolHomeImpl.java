package com.cboe.application.supplier;

import com.cboe.application.quote.common.UserQuoteServiceHomeImpl;
import com.cboe.domain.startup.ClientBOHome;
import com.cboe.domain.util.InstrumentorNameHelper;
import com.cboe.domain.instrumentedChannel.InstrumentedThreadPool;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.UserSessionMarketDataThreadPoolHome;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.util.ThreadPool;

import java.util.HashMap;
import java.util.StringTokenizer;

import org.omg.CORBA.UserException;
/**
 *  @author Jing Chen
 */
public class UserSessionMarketDataThreadPoolHomeImpl extends ClientBOHome implements UserSessionMarketDataThreadPoolHome {

    public final static String THREAD_POOL       = "UserSessionMarketDataThreadPool";
    public final static String THREAD_POOL_SIZE  = "UserSessionMarketDataThreadPoolSize";
    public final static String OVERLAY_THREAD_POOL       = "UserSessionOverlayMarketDataThreadPool";
    public final static String OVERLAY_THREAD_POOL_SIZE  = "UserSessionMarketDataOverlayThreadPoolSize";
    public final static String USER_ROLE_OVERRIDE = "UserRoleOverride";
    public final static String THREAD_POOL_SIZE_OVERRIDE = "ThreadPoolSizeOverride";
    public final static String OVERLAY_THREAD_POOL_SIZE_OVERRIDE = "OverlayThreadPoolSizeOverride";
    public static final String TOKENIZER_DELIMITERS = ",; ";
    public static final String ENABLE_OVERRIDE = "UserSessionMarketDataThreadPoolOverrideEnabled";

    // Default ThreadPool size - used only if it does not get defined in the
    // XML file for that property (SMA)
    private             int threadPoolSize;
    private             int overlayThreadPoolSize;

    private HashMap threadPoolMap = null;
    private HashMap overlayThreadPoolMap = null;
    private HashMap<Integer, Integer> userRoleThreadPoolOverride;
    private HashMap<Integer, Integer> userRoleOverlayThreadPoolOverride;
    private             boolean overrideEnabled = false;

    public UserSessionMarketDataThreadPoolHomeImpl() {
        super();

        // map sesion to threadpool
        threadPoolMap = new HashMap();
        overlayThreadPoolMap = new HashMap();
        userRoleThreadPoolOverride = new HashMap<Integer, Integer>();
        userRoleOverlayThreadPoolOverride = new HashMap<Integer, Integer>();
    }

    public void clientInitialize()
        throws Exception
    {
        threadPoolSize = Integer.parseInt(getProperty(THREAD_POOL_SIZE));
        if (Log.isDebugOn())
        {
            Log.debug(this, "User Session MarketData ThreadPool Size::" + threadPoolSize );
        }

        overlayThreadPoolSize = Integer.parseInt(getProperty(OVERLAY_THREAD_POOL_SIZE));
        if (Log.isDebugOn())
        {
            Log.debug(this, "User Session MarketData Overlay ThreadPool Size::" + threadPoolSize );
        }
        
        String str = System.getProperty(ENABLE_OVERRIDE, "false");
        if(str.equalsIgnoreCase("true")) {
            overrideEnabled = true;
            if (Log.isDebugOn())
            {
                Log.debug(this, "User Session Market Data Thread Pool Override is enabled.");
            }
        }
        else {
            if (Log.isDebugOn())
            {
                Log.debug(this, "User Session Market Data Thread Pool Override is disenabled.");
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

        String overlayThreadPoolOverride = getProperty(OVERLAY_THREAD_POOL_SIZE_OVERRIDE);
        if (Log.isDebugOn())
        {
            Log.debug(this, "User Role Overly Thread Pool Override::" +
                    userRoleOverride + "/" +
                    overlayThreadPoolOverride);
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
        
        if(userRoleOverride.length() > 0 && overlayThreadPoolOverride.length() > 0)
        {
            StringTokenizer urorTokenizer = new StringTokenizer(userRoleOverride, TOKENIZER_DELIMITERS, false);
            StringTokenizer tporTokenizer = new StringTokenizer(overlayThreadPoolOverride, TOKENIZER_DELIMITERS, false);
            while(urorTokenizer.hasMoreTokens())
            {
                String userRole = urorTokenizer.nextToken();
                if(tporTokenizer.hasMoreTokens())
                {               
                   Integer key = Integer.valueOf(userRole.charAt(0));
                   Integer val = Integer.valueOf(tporTokenizer.nextToken());
                   userRoleOverlayThreadPoolOverride.put(key,val);
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
                if(overrideEnabled ){
                	Integer key = Integer.valueOf(session.getValidSessionProfileUserV2().userInfo.role);
                	if (userRoleThreadPoolOverride.containsKey(key)) {
                		tpSize = userRoleThreadPoolOverride.get(key).intValue();
                    }
                	
                }	
                threadPool = new InstrumentedThreadPool(tpSize, InstrumentorNameHelper.createInstrumentorName(
                        new String[]{session.getInstrumentorName(),THREAD_POOL}, this));
                threadPool.setQuickShutdown(true);
                updateThreadPool(threadPool, session);
            }
            catch (UserException e)
            {
                Log.exception(e);
            }
        }

        return threadPool;
    }

    /**
     *  Return ThreadPool given a specific Session and threadpool name
     *
     *  @param session the session manager to create the pool for
     *  @return com.cboe.util.ThreadPool
     */
    public ThreadPool find(BaseSessionManager session)
    {
        return create(session);
    }

    public synchronized ThreadPool findOverlayThreadPool(BaseSessionManager session)
    {
        ThreadPool threadPool = getOverlayThreadPool(session);

        if ( threadPool == null && session != null)
        {
            try
            {
                int tpSize = overlayThreadPoolSize;
                
                if(overrideEnabled) {
                	Integer key = Integer.valueOf(session.getValidSessionProfileUserV2().userInfo.role);
                	if(userRoleOverlayThreadPoolOverride.containsKey(key)) {
                		tpSize = userRoleOverlayThreadPoolOverride.get(key).intValue();
                	}
                }
                threadPool = new InstrumentedThreadPool(tpSize, InstrumentorNameHelper.createInstrumentorName(
                        new String[]{session.getInstrumentorName(),OVERLAY_THREAD_POOL}, this));
                threadPool.setQuickShutdown(true);
                updateOverlayThreadPool(threadPool, session);
            }
            catch (UserException e)
            {
                Log.exception(e);
            }
        }

        return threadPool;
    }

    private ThreadPool getThreadPool(BaseSessionManager session)
    {
        ThreadPool threadPool;

        threadPool =  (ThreadPool)threadPoolMap.get(session);

        return threadPool;
    }

    private ThreadPool getOverlayThreadPool(BaseSessionManager session)
    {
        ThreadPool threadPool;

        threadPool =  (ThreadPool)overlayThreadPoolMap.get(session);

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

    private void updateOverlayThreadPool(ThreadPool threadPool, BaseSessionManager session)
    {
        overlayThreadPoolMap.put(session, threadPool);
    }

    /**
     *  removes ThreadPool given a specific Session
     *
     *  @param session the session manager to Remove
     */
    public void remove(BaseSessionManager session)
    {
        ThreadPool threadPool =  (ThreadPool)threadPoolMap.get(session);
        if(threadPool != null)
        {
            threadPool.shutdown();
            threadPoolMap.remove(session);
        }

        ThreadPool overlayThreadPool =  (ThreadPool)overlayThreadPoolMap.get(session);
        if(overlayThreadPool != null)
        {
            overlayThreadPool.shutdown();
            overlayThreadPoolMap.remove(session);
        }

    }

    public void clientStart()
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "SMA Type = " + this.getSmaType());
        }
    }
}
