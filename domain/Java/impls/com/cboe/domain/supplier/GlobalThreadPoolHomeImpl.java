package com.cboe.domain.supplier;

import java.util.*;
import com.cboe.util.*;
import com.cboe.interfaces.domain.*;
import com.cboe.infrastructureServices.foundationFramework.*;

import com.cboe.infrastructureServices.foundationFramework.exceptionHandling.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.*;
import com.cboe.infrastructureServices.systemsManagementService.*;
import com.cboe.domain.startup.ClientBOHome;

/**
 *  GlobalThreadPoolHomeImpl
 *
 *  This home created a singleton ThreadPool
 *  Author: Connie Feng
 */
public class GlobalThreadPoolHomeImpl extends ClientBOHome implements GlobalThreadPoolHome {

    public final static String THREAD_POOL       = "GlobalThreadPool";
    public final static String THREAD_POOL_SIZE  = "globalThreadPoolSize";

    // Default ThreadPool size - used only if it does not get defined in the
    // XML file for that property (SMA)
    private             int threadPoolSize;

    private ThreadPool threadPool;

    public GlobalThreadPoolHomeImpl() {
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
     *  @return ThreadPool
     *
     */
    public ThreadPool create()
    {
        if ( threadPool == null )
        {
            threadPool = new ThreadPool(threadPoolSize, THREAD_POOL);
        }
        return (threadPool);
    }

    /**
     *  Return ThreadPool
	 */
    public ThreadPool find()
    {
        return create();
    }
}
