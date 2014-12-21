package com.cboe.domain.instrumentedChannel.supplier;

import com.cboe.domain.instrumentedChannel.InstrumentedThreadPool;
import com.cboe.domain.startup.ClientBOHome;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.domain.InstrumentedGlobalThreadPoolHome;
import com.cboe.util.ThreadPool;

/**
 *  GlobalThreadPoolHomeImpl
 *
 *  This home created a singleton ThreadPool
 *  @author Jing Chen
 */
public class GlobalThreadPoolHomeImpl extends ClientBOHome implements InstrumentedGlobalThreadPoolHome {

    public final static String THREAD_POOL       = "InstrumentedGlobalThreadPool";
    public final static String THREAD_POOL_SIZE  = "globalThreadPoolSize";

    // Default ThreadPool size - used only if it does not get defined in the
    // XML file for that property (SMA)
    private int threadPoolSize;

    private ThreadPool threadPool;

    public GlobalThreadPoolHomeImpl()
    {
        super();
        threadPool = null;
    }

    public void clientInitialize()
        throws Exception
    {
        threadPoolSize = Integer.parseInt(getProperty(THREAD_POOL_SIZE));
        Log.debug(this, "SMA Type = " + this.getSmaType());
    }

    /**
     *  Create a new ThreadPool
     */
    public ThreadPool create()
    {
        if ( threadPool == null )
        {
            threadPool = new InstrumentedThreadPool(threadPoolSize, THREAD_POOL);
        }
        return (threadPool);
    }

    public ThreadPool find()
    {
        return create();
    }
}
