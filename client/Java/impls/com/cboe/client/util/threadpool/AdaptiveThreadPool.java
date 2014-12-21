package com.cboe.client.util.threadpool;

/**
 * AdaptiveThreadPool.java
 *
 * @author Dmitry Volpyansky
 *
 */

/**
 * An automatically growable/shrinkable Thread Pool
 *
 */

import java.util.*;

import com.cboe.infrastructureServices.foundationFramework.utilities.*;
import com.cboe.client.util.*;
import com.cboe.client.util.queue.*;

public class AdaptiveThreadPool implements Runnable
{
    protected final  DoublePriorityEventChannelIF threadPoolEventChannel = new DoublePriorityEventChannel();
    protected        String                       threadPoolName         = "adaptiveThreadPool";
    protected final  ThreadGroup                  threadPoolThreadGroup;
    protected final  Object                       lock = new Object();
    protected        int                          threadID;
    protected final  Latch                        growShrinkPoolLatch = new Latch();
    protected        Thread                       harvesterThread;
    protected        int                          totalExecutingThreads;
    protected        int                          totalStartedThreads;
    protected        int                          totalPendingThreads;
    protected        int                          startPoolSize;
    protected        int                          warmPoolSize;
    protected        int                          minimumPoolSize;
    protected        int                          maximumPoolSize = 50;
    protected        int                          shrinkSleepTime = 5000;
    protected        int                          idleThreadRemovalSeconds = 3000;
    protected        int                          debugFlags;
    protected        int                          highWatermark;
    protected        boolean                      stopping;
    protected static AdaptiveThreadPool           defaultThreadPool;
    protected        boolean                      poolIsAtMaximumSize;

    public static final String PROPERTY_MINIMUM_POOL_SIZE           = "adaptiveThreadPool.minimumPoolSize";
    public static final String PROPERTY_MAXIMUM_POOL_SIZE           = "adaptiveThreadPool.maximumPoolSize";
    public static final String PROPERTY_START_POOL_SIZE             = "adaptiveThreadPool.startPoolSize";
    public static final String PROPERTY_WARM_POOL_SIZE              = "adaptiveThreadPool.warmPoolSize";
    public static final String PROPERTY_CHECK_FOR_SHRINK_SLEEP_TIME = "adaptiveThreadPool.shrinkSleepTime";
    public static final String PROPERTY_IDLE_THREAD_REMOVAL_SECONDS = "adaptiveThreadPool.idleThreadRemovalSeconds";
    public static final String PROPERTY_DEBUG_FLAGS                 = "adaptiveThreadPool.debugFlags";

    public static final int DEBUG_DEQUEUE = 1;
    public static final int DEBUG_ENQUEUE = 2;
    public static final int DEBUG_MANAGE  = 3;

    protected final static AdaptiveThreadPoolRequest terminateYourself = new AdaptiveThreadPoolRequest(new Runnable() {public void run(){}}, "TERMINATE_IDLE_THREAD");

    public interface AdaptiveThreadPoolInstrumentationIF
    {
        public int getCurrentlyExecutingThreads();
        public int getStartedThreads();
        public int getPendingThreads();
        public int getStartedThreadsHighWatermark();
        public int getPendingTaskCount();
        public int getPendingTaskCountHighWatermark();
    }

    public static class AdaptiveThreadPoolInstrumentation implements AdaptiveThreadPoolInstrumentationIF
    {
        public int executingThreads;
        public int startedThreads;
        public int pendingThreads;
        public int highWatermark;
        public int queuedSize;
        public int queuedHighWatermark;

        public int getCurrentlyExecutingThreads()     {return executingThreads;}
        public int getStartedThreads()                {return startedThreads;}
        public int getPendingThreads()                {return pendingThreads;}
        public int getStartedThreadsHighWatermark()   {return highWatermark;}
        public int getPendingTaskCount()              {return queuedSize;}
        public int getPendingTaskCountHighWatermark() {return queuedHighWatermark;}
    }

    private static class AdaptiveThreadPoolRequest
    {
       public Runnable runnable;
       public String   name;

       public AdaptiveThreadPoolRequest(Runnable runnable, String name)
       {
           this.runnable = runnable;
           this.name     = name;
       }

       public String toString()
       {
           String r = runnable.toString();
           StringBuilder result = new StringBuilder(name.length()+r.length()+30);
           result.append("AdaptiveThreadPoolRequest[").append(name).append(",").append(r).append("]");
           return result.toString();
       }
    }

    public AdaptiveThreadPool()
    {
        ThreadGroup topGroup = Thread.currentThread().getThreadGroup();

        while (topGroup.getParent() != null)
        {
            topGroup = topGroup.getParent();
        }

        threadPoolThreadGroup = new ThreadGroup(topGroup, "adaptiveThreadPool");

        threadPoolThreadGroup.setDaemon(true);
    }

    public AdaptiveThreadPool(String propertyPrefix, Properties properties)
    {
        this();
        StringBuilder sb = new StringBuilder(100);

        if (propertyPrefix == null)
        {
            propertyPrefix = "";
        }
        else if (!propertyPrefix.endsWith("."))
        {
            sb.append(propertyPrefix).append('.');
            propertyPrefix = sb.toString();
        }

        int integer;

        sb.setLength(0);
        sb.append(propertyPrefix).append(PROPERTY_MINIMUM_POOL_SIZE);
        integer = IntegerHelper.parseInt(properties.getProperty(sb.toString(), "0"));
        if (integer != IntegerHelper.INVALID_VALUE)
        {
            minimumPoolSize = integer;
        }

        sb.setLength(0);
        sb.append(propertyPrefix).append(PROPERTY_MAXIMUM_POOL_SIZE);
        integer = IntegerHelper.parseInt(properties.getProperty(sb.toString(), "5"));
        if (integer != IntegerHelper.INVALID_VALUE)
        {
            maximumPoolSize = integer;
        }

        sb.setLength(0);
        sb.append(propertyPrefix).append(PROPERTY_WARM_POOL_SIZE);
        integer = IntegerHelper.parseInt(properties.getProperty(sb.toString(), "0"));
        if (integer != IntegerHelper.INVALID_VALUE)
        {
            warmPoolSize = integer;
        }

        sb.setLength(0);
        sb.append(propertyPrefix).append(PROPERTY_START_POOL_SIZE);
        integer = IntegerHelper.parseInt(properties.getProperty(sb.toString(), "0"));
        if (integer != IntegerHelper.INVALID_VALUE)
        {
            startPoolSize = integer;
        }

        sb.setLength(0);
        sb.append(propertyPrefix).append(PROPERTY_CHECK_FOR_SHRINK_SLEEP_TIME);
        integer = IntegerHelper.parseInt(properties.getProperty(sb.toString(), "5000"));
        if (integer != IntegerHelper.INVALID_VALUE)
        {
            shrinkSleepTime = integer;
        }

        sb.setLength(0);
        sb.append(propertyPrefix).append(PROPERTY_IDLE_THREAD_REMOVAL_SECONDS);
        integer = IntegerHelper.parseInt(properties.getProperty(sb.toString(), "30"));
        if (integer != IntegerHelper.INVALID_VALUE)
        {
            idleThreadRemovalSeconds = integer;
        }

        sb.setLength(0);
        sb.append(propertyPrefix).append(PROPERTY_DEBUG_FLAGS);
        integer = IntegerHelper.parseInt(properties.getProperty(sb.toString(), "0"));
        if (integer != IntegerHelper.INVALID_VALUE)
        {
            debugFlags = integer;
        }
    }

    public int setDebugFlags(int debugFlags)
    {
        int oldDebugFlags = this.debugFlags;

        this.debugFlags = debugFlags;

        return oldDebugFlags;
    }

    public int getDebugFlags()
    {
        return debugFlags;
    }

    public static AdaptiveThreadPool createThreadPool()
    {
        return new AdaptiveThreadPool();
    }

    public static AdaptiveThreadPool createThreadPool(String propertyPrefix, Properties properties)
    {
        return new AdaptiveThreadPool(propertyPrefix, properties);
    }

    public static AdaptiveThreadPool getDefaultThreadPool()
    {
        if (defaultThreadPool == null)
        {
            synchronized(AdaptiveThreadPool.class)
            {
                if (defaultThreadPool == null)
                {
                    defaultThreadPool = new AdaptiveThreadPool();

                    defaultThreadPool.threadPoolThreadGroup.setDaemon(false);

                    defaultThreadPool.setName("defaultAdaptiveThreadPool");
                    defaultThreadPool.setWarmPoolSize(2);
                    defaultThreadPool.setStartPoolSize(4);
                    defaultThreadPool.setMinimumPoolSize(4);
                    defaultThreadPool.setMaximumPoolSize(100);
                    defaultThreadPool.startPool();
                }
            }
        }

        return defaultThreadPool;
    }

    public AdaptiveThreadPoolInstrumentationIF getAdaptiveThreadPoolInstrumentation()
    {
        AdaptiveThreadPoolInstrumentation adaptiveThreadPoolInstrumentation = new AdaptiveThreadPoolInstrumentation();

        SinglePriorityEventChannelIF.EventChannelInstrumentationIF eventChannelInstrumentation = threadPoolEventChannel.getEventChannelInstrumentation();

        synchronized(lock)
        {
            adaptiveThreadPoolInstrumentation.executingThreads    = totalExecutingThreads;
            adaptiveThreadPoolInstrumentation.startedThreads      = totalStartedThreads;
            adaptiveThreadPoolInstrumentation.pendingThreads      = totalPendingThreads;
            adaptiveThreadPoolInstrumentation.highWatermark       = highWatermark;
            adaptiveThreadPoolInstrumentation.queuedSize          = eventChannelInstrumentation.currentSize();
            adaptiveThreadPoolInstrumentation.queuedHighWatermark = eventChannelInstrumentation.highWaterMark();
        }

        return adaptiveThreadPoolInstrumentation;
    }

    public int getExecutingThreads()
    {
        synchronized(lock)
        {
            return totalExecutingThreads;
        }
    }

    public int getCurrentPoolSize()
    {
        synchronized(lock)
        {
            return totalPendingThreads + totalStartedThreads;
        }
    }

    public int getHighWatermark()
    {
        return highWatermark;
    }

    public String getName()
    {
        return threadPoolName;
    }

    public void setName(String name)
    {
        threadPoolName = name;

        if (harvesterThread != null)
        {
            StringBuilder sb = new StringBuilder(threadPoolName.length()+20);
            sb.append("[").append(threadPoolName).append(":harvesterThread]");
            harvesterThread.setName(sb.toString());
        }
    }

    public int getWarmPoolSize()
    {
        return warmPoolSize;
    }

    public void setWarmPoolSize(int warmPoolSize)
    {
        this.warmPoolSize = warmPoolSize;
    }

    public int getStartPoolSize()
    {
        return startPoolSize;
    }

    public void setStartPoolSize(int startPoolSize)
    {
        this.startPoolSize = startPoolSize;
    }

    public int getMinimumPoolSize()
    {
        return minimumPoolSize;
    }

    public void setMinimumPoolSize(int minimumPoolSize)
    {
        this.minimumPoolSize = minimumPoolSize;
    }

    public int getMaximumPoolSize()
    {
        return maximumPoolSize;
    }

    public void setMaximumPoolSize(int maximumPoolSize)
    {
        this.maximumPoolSize = maximumPoolSize;
    }

    public int getShrinkSleepTime()
    {
        return shrinkSleepTime;
    }

    public void setShrinkSleepTime(int shrinkSleepTime)
    {
        this.shrinkSleepTime = shrinkSleepTime;
    }

    public int getIdleThreadRemovalSeconds()
    {
        return idleThreadRemovalSeconds;
    }

    public void setIdleThreadRemovalSeconds(int idleThreadRemovalSeconds)
    {
        this.idleThreadRemovalSeconds = idleThreadRemovalSeconds;
    }

    public ThreadGroup getThreadGroup()
    {
        return threadPoolThreadGroup;
    }

    public synchronized void startPool()
    {
        if (harvesterThread == null)
        {
            StringBuilder name = new StringBuilder(threadPoolName.length()+20);
            name.append("[").append(threadPoolName).append(":harvesterThread]");
            harvesterThread = new Thread(threadPoolThreadGroup, this);
            harvesterThread.setName(name.toString());
            harvesterThread.setDaemon(true);
            harvesterThread.start();
        }
    }

    public boolean isDefaultThreadPool()
    {
        return this == defaultThreadPool;
    }

    public synchronized void stopPool()
    {
        if (BitHelper.isBitMaskSet(debugFlags, DEBUG_MANAGE)) {Log.information("adaptiveThreadPool(" + threadPoolName + ").THREAD stopPool()");}

        if (!stopping && !isDefaultThreadPool() && harvesterThread != null)
        {
            stopping = true;

            if (BitHelper.isBitMaskSet(debugFlags, DEBUG_MANAGE)) {Log.information("adaptiveThreadPool(" + threadPoolName + ").THREAD terminating at most " + maximumPoolSize + " threads");}

            for (int i = 0; i < maximumPoolSize; i++)
            {
                threadPoolEventChannel.enqueueHighPriorityFront(terminateYourself);
            }
        }

        growShrinkPoolLatch.release();
    }

    public boolean execute(Runnable runnable)
    {
        return execute(runnable, "");
    }

    public boolean execute(Runnable runnable, String name)
    {
        if (runnable == null || stopping)
        {
            try
            {
                if (runnable == null)
                {
                    throw new Exception("adaptiveThreadPool(" + threadPoolName + ") can't enqueue NULL runnable");
                }

                throw new Exception("adaptiveThreadPool(" + threadPoolName + ") can't enqueue because it is stopping: (" + runnable + ", " + name + ")");
            }
            catch (Exception ex)
            {
                Log.exception(ex);
            }

            return false;
        }

        if (BitHelper.isBitMaskSet(debugFlags, DEBUG_ENQUEUE)) {Log.information("adaptiveThreadPool(" + threadPoolName + ").THREAD enqueuing(" + runnable + ", " + name + ")");}

        threadPoolEventChannel.enqueue(new AdaptiveThreadPoolRequest(runnable, name));

        if (!poolIsAtMaximumSize)
        {
            growShrinkPoolLatch.release();
        }

        return true;
    }

    public void run() // this thread will grow/shrink its worker pool
    {
        int waitingToRun;
        int pendingThreads;
        int executingThreads;
        int availableThreads;
        int startedThreads;
        int neededThreads;
        int createdThreads;
        int i;
        int lastThreadInstantiationTime;

        if (startPoolSize < minimumPoolSize)
        {
            i = minimumPoolSize;
        }
        else
        {
            i = startPoolSize;
        }

        highWatermark       = i;
        totalPendingThreads = i;

        for (; !stopping && i > 0; i--)
        {
            startAdaptiveThreadPoolThread();
        }

        lastThreadInstantiationTime = DateHelper.currentTimeInSeconds();

        while (!stopping)
        {
            try
            {
                synchronized(lock)
                {
                    startedThreads   = totalStartedThreads;
                    pendingThreads   = totalPendingThreads;
                    executingThreads = totalExecutingThreads;
                }

                createdThreads   = pendingThreads + startedThreads;
                availableThreads = createdThreads - executingThreads;

                waitingToRun = threadPoolEventChannel.size();

                if (waitingToRun > 0)
                {
                    if (waitingToRun > availableThreads && availableThreads < maximumPoolSize) // check if we need more threads because of a large queue
                    {
                        neededThreads = waitingToRun + warmPoolSize - availableThreads;

                        if (createdThreads + neededThreads > maximumPoolSize)
                        {
                            neededThreads = maximumPoolSize - createdThreads;
                        }

                        if (highWatermark < maximumPoolSize && createdThreads + neededThreads > highWatermark)
                        {
                            highWatermark = createdThreads + neededThreads;
                        }

                        if (!stopping)
                        {
                            synchronized(lock)
                            {
                                totalPendingThreads += neededThreads;

                                startedThreads   = totalStartedThreads;
                                pendingThreads   = totalPendingThreads;
                            }

                            if (startedThreads + pendingThreads >= maximumPoolSize)
                            {
                                poolIsAtMaximumSize = true;
                            }
                            else
                            {
                                poolIsAtMaximumSize = false;
                            }

                            for (i = 0; i < neededThreads; i++)
                            {
                                startAdaptiveThreadPoolThread();
                            }

                            lastThreadInstantiationTime = DateHelper.currentTimeInSeconds();
                        }
                    }
                }
                else
                {
                    if (DateHelper.currentTimeInSeconds() - lastThreadInstantiationTime > idleThreadRemovalSeconds)
                    {
                        if (!stopping && availableThreads > warmPoolSize) // check if we need to terminate some threads (only if we've been available for X seconds)
                        {
                            neededThreads = availableThreads - warmPoolSize;

                            if (createdThreads - neededThreads < minimumPoolSize)
                            {
                                neededThreads = minimumPoolSize - (createdThreads - neededThreads);
                            }

                            if (BitHelper.isBitMaskSet(debugFlags, DEBUG_MANAGE)) {Log.information("adaptiveThreadPool(" + threadPoolName + ").THREAD terminating " + neededThreads + " threads");}

                            for (i = 0; i < neededThreads; i++)
                            {
                                threadPoolEventChannel.enqueue(terminateYourself);
                            }

                            if (startedThreads + pendingThreads - neededThreads < maximumPoolSize)
                            {
                                poolIsAtMaximumSize = false;
                            }
                        }
                    }
                }

                if (!stopping)
                {
                    growShrinkPoolLatch.acquireAndReset(shrinkSleepTime);
                }
            }
            catch (Throwable ex)
            {

            }
        }
    }

    protected void startAdaptiveThreadPoolThread()
    {
        if (stopping)
        {
            return;
        }

        StringBuilder sb = new StringBuilder(threadPoolName.length()+40);
        sb.append("[").append(threadPoolName).append("{adaptiveThreadPoolWorker(").append(++threadID).append(")}]");
        Thread thread = new Thread(threadPoolThreadGroup, sb.toString())
        {
            public void run()
            {
                synchronized(lock)
                {
                   --totalPendingThreads;
                   ++totalStartedThreads;
                }

                String                    workerThreadName = this.getName();
                StringBuilder             tname = new StringBuilder(75);
                AdaptiveThreadPoolRequest threadPoolRequest;

                if (BitHelper.isBitMaskSet(debugFlags, DEBUG_MANAGE)) {Log.information("adaptiveThreadPool(" + threadPoolName + ").THREAD(" + workerThreadName + ").started thread");}

                while (!stopping)
                {
                    try
                    {
                        threadPoolRequest = null;

                        if (BitHelper.isBitMaskSet(debugFlags, DEBUG_DEQUEUE)) {Log.information("adaptiveThreadPool(" + threadPoolName + ").THREAD(" + workerThreadName + ").dequeuing()");}

                        try
                        {
                             threadPoolRequest = (AdaptiveThreadPoolRequest) threadPoolEventChannel.dequeue();
                        }
                        catch (Exception ex)
                        {
                            try
                            {
                                Log.exception(workerThreadName + " dequeue", ex);
                            }
                            catch (Throwable th2)
                            {

                            }

                            continue;
                        }
                        catch (Throwable th)
                        {
                            try
                            {
                                Log.information(workerThreadName + " Exception dequeue " + ExceptionHelper.getStackTrace(th));
                            }
                            catch (Throwable th2)
                            {

                            }

                            continue;
                        }

                        if (BitHelper.isBitMaskSet(debugFlags, DEBUG_DEQUEUE)) {Log.information("adaptiveThreadPool(" + threadPoolName + ").THREAD(" + workerThreadName + ").dequeued(" + threadPoolRequest + ")");}

                        if (threadPoolRequest == terminateYourself)
                        {
                            break;
                        }

                        if (threadPoolRequest == null)
                        {
                           continue;
                        }

                        if (threadPoolRequest.runnable != null)
                        {
                            synchronized(lock)
                            {
                                ++totalExecutingThreads;
                            }

                            if (BitHelper.isBitMaskSet(debugFlags, DEBUG_DEQUEUE)) {Log.information("adaptiveThreadPool(" + threadPoolName + ").THREAD(" + workerThreadName + ").dequeued(" + threadPoolRequest.runnable + ", " + threadPoolRequest.name + ")");}

                            try
                            {
                                tname.setLength(0);
                                tname.append("[").append(threadPoolName).append("{").append(threadPoolRequest.name).append("}]");
                                this.setName(tname.toString());

                                threadPoolRequest.runnable.run();
                            }
                            catch (Exception ex)
                            {
                                try
                                {
                                    Log.exception(workerThreadName + " dequeue", ex);
                                }
                                catch (Throwable th2)
                                {

                                }

                                continue;
                            }
                            catch (Throwable th)
                            {
                                try
                                {
                                    Log.information(workerThreadName + " Exception dequeue " + ExceptionHelper.getStackTrace(th));
                                }
                                catch (Throwable th2)
                                {

                                }

                                continue;
                            }

                            finally
                            {
                                synchronized(lock)
                                {
                                    --totalExecutingThreads;
                                }

                                this.setName(workerThreadName);
                            }
                        }
                    }
                    catch (Exception ex)
                    {

                    }
                }

                synchronized(lock)
                {
                   --totalStartedThreads;
                }

                threadPoolRequest = null;

                if (BitHelper.isBitMaskSet(debugFlags, DEBUG_MANAGE)) {Log.information("adaptiveThreadPool(" + threadPoolName + ").THREAD(" + workerThreadName + ").ended thread");}
            }
        };

        thread.setDaemon(false);

        if (BitHelper.isBitMaskSet(debugFlags, DEBUG_MANAGE)) {Log.information("adaptiveThreadPool(" + threadPoolName + ").THREAD Starting");}

        thread.start();
    }

/*
    public static void main(String[] args)
    {
        AdaptiveThreadPool pool = new AdaptiveThreadPool();

        pool.setMaximumPoolSize(4);
        pool.setMinimumPoolSize(2);
        pool.setStartPoolSize(3);
        pool.setShrinkSleepTime(10000);
        pool.setIdleThreadRemovalSeconds(10);

        Runnable runA = new Runnable()
        {
            public void run()
            {
                System.out.println("A");
                ThreadHelper.sleepSeconds(10000);
            }
        };

        Runnable runB = new Runnable()
        {
            public void run()
            {
                System.out.println("B");
                ThreadHelper.sleepSeconds(10000);
            }
        };

        Runnable runC = new Runnable()
        {
            public void run()
            {
                System.out.println("C");
                ThreadHelper.sleepSeconds(10000);
            }
        };

        Runnable runD = new Runnable()
        {
            public void run()
            {
                System.out.println("D");
                ThreadHelper.sleepSeconds(10000);
            }
        };

        pool.setDebugFlags(255);

        pool.startPool();

        pool.execute(runA, "A");
        pool.execute(runB, "B");
        pool.execute(runC, "C");
        pool.execute(runD, "D");

        ThreadHelper.sleepSeconds(50000);
    }
*/
}
