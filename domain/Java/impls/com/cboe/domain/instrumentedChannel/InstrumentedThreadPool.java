package com.cboe.domain.instrumentedChannel;

import com.cboe.instrumentationService.instrumentors.ThreadPoolInstrumentor;
import com.cboe.instrumentationService.factories.InstrumentorAlreadyCreatedException;
import com.cboe.domain.instrumentorExtension.ThreadPoolInstrumentorExtensionFactory;
import com.cboe.domain.instrumentorExtension.ThreadPoolInstrumentorExtension;
import com.cboe.util.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

import java.util.Enumeration;
/**
 * @author Jing Chen
 */
public class InstrumentedThreadPool extends ThreadPool
{
    protected ThreadPoolInstrumentorExtension threadPoolInstrumentorExtension;
    protected boolean isReadyForInstrumentation = false;

    public InstrumentedThreadPool(int numThreads, String name)
    {
        super(numThreads, name);
        // Start instrumentation after all threads are created.
        isReadyForInstrumentation = true;
    }

    private ThreadPoolInstrumentorExtension getInstrumentor()
    {
        if(threadPoolInstrumentorExtension == null)
        {
            try
            {
            	// Here threadNumber should be equal to thread pool size, otherwise 
            	// instrumentation data will be bad.
                threadPoolInstrumentorExtension = ThreadPoolInstrumentorExtensionFactory.createThreadPoolInstrumentor(poolName, threadNumber, null, true);
            }
            catch(InstrumentorAlreadyCreatedException e)
            {
                // do not want to propagate this exception up.
                //It indicates the instrumentation data will be bad from this point on for this queue.
                Log.exception(e);
                threadPoolInstrumentorExtension = ThreadPoolInstrumentorExtensionFactory.find(poolName);
            }
        }
        return threadPoolInstrumentorExtension;
    }

    public ThreadPoolInstrumentorExtension getThreadPoolInstrumentorExtension()
    {
        return threadPoolInstrumentorExtension;
    }

    public synchronized void addIdleThread(Thread thread)
    {
        super.addIdleThread(thread);
        // start instrumentation only after all threads are created.
        if(isReadyForInstrumentation)
        	getInstrumentor().setCurrentlyExecutingThreads(threadNumber-idleSet.size());
    }

    public synchronized void removeIdleThread(Thread thread)
    {
        super.removeIdleThread(thread);
        // start instrumentation only after all threads are created. 
        if(isReadyForInstrumentation)
        	getInstrumentor().setCurrentlyExecutingThreads(threadNumber-idleSet.size());
    }

    public int getIdleThreadCount()
    {
        return idleSet.size();
    }
// shutdown thread run.
    public void run()
    {
        super.run();
        ThreadPoolInstrumentorExtensionFactory.removeThreadPoolInstrumentor(threadPoolInstrumentorExtension.getName());
    }
}
