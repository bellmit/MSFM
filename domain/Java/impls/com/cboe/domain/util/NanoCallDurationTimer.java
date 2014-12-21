package com.cboe.domain.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

/**
 * Minimally-invasive, nanosecond-scale call timing mechanism.
 * 
 * @author sinclair
 */
public final class NanoCallDurationTimer
{
    private static ConcurrentHashMap<String,NanoCallDurationTimer> timers = new ConcurrentHashMap<String, NanoCallDurationTimer>();
    
    /**
     * Create a call timer that logs info messages periodically.
     * @param p_name - unique name for this timer
     * @param p_loggingPrefix - (may be null) additional string to include in the log messages
     * @param p_pollingIntervalMillis - polling frequency, in millis.  Typically many seconds long.
     * @return The new call timer, or null if a timer by that name already exists.
     */
    public static NanoCallDurationTimer createAndInitializeLoggingCallTimer(
            String p_name, String p_loggingPrefix, int p_pollingIntervalMillis)
    {
        final NanoCallDurationTimer callTimer = new NanoCallDurationTimer(p_name);
        if (timers.putIfAbsent(p_name, callTimer) != null)
        {
            return null; // already created: we'll just discard the new callTimer obj.
        }
        callTimer.addLoggingCollectorListener(p_loggingPrefix);
        callTimer.startCollectorTask(p_pollingIntervalMillis);
        return callTimer;
    }
    
    public static NanoCallDurationTimer getCallTimer(String p_name)
    {
        return timers.get(p_name);
    }
    
    /**
     * Cancel and remove timer from static maps.
     * @param p_name
     * @return true if removed, false if not found
     */
    public static boolean cancelAndRemoveTimer(String p_name)
    {
        NanoCallDurationTimer timer = timers.remove(p_name);
        if (timer != null)
        {
            timer.stopCollectorTask();
        }
        return timer != null;
    }
    
    public final class LocalCollector
    {
        long currCallStartNS;
        long longestCallNS;
        long numCalls;
        Thread owner;
        long totalCallTimesNS;

        LocalCollector(final Thread p_owner)
        {
            this.owner = p_owner;
        }

        /**
         * Collect local data, adding it to the outer-cass intervalXxx fields.
         * There is no synchronization on the intervalXxx fields since it is
         * expected that it is only the single timer task which will be calling
         * these methods.
         * 
         */
        private synchronized void collectAndReset()
        {
            if (longestCallNS > intervalLongestDurationNS)
            {
                intervalLongestDurationNS = longestCallNS;
            }
            intervalNumCalls += numCalls;
            intervalTotalDurationNS += totalCallTimesNS;
            reset();
        }
        
        private synchronized void reset()
        {
            totalCallTimesNS = 0;
            numCalls = 0;
            longestCallNS = 0;
        }

        // (synchronized should only contend with collect(), which should be infrequent)
        public synchronized void exit()
        {
            if (currCallStartNS == 0)
            {
                Log.alarm("Invalid call to NanoCallDurationTimer("+name+")$LocalCollector.endCall() - no corresponding startCall()");
            }
            final long durationNS = System.nanoTime() - currCallStartNS;
            if (durationNS < 0)
            {
                Log.alarm("NanoCallDurationTimer("+name+")$LocalCollector.endCall() - negative call duration?!? (ignored) (duration="+durationNS+")");
                return;
            }
            numCalls++;
            totalCallTimesNS += durationNS;
            if (durationNS > longestCallNS)
            {
                longestCallNS = durationNS;
            }
            currCallStartNS = 0;
        }

        public void enter()
        {
            currCallStartNS = System.nanoTime();
        }
    }

    public interface NanoTimerListener
    {
        public void overallUpdate(NanoCallDurationTimer p_nanoTimer, long p_callCount, 
                long p_averageDurationNS, long p_highWatermarkDurationNS);

        public void intervalUpdate(NanoCallDurationTimer p_nanoTimer, long p_intervalStartMillis, long p_callCount, 
                long p_averageDurationNS, long p_highWatermarkDurationNS);
    }

    private Timer collectionTaskTimer = null;
    private final ArrayList<NanoTimerListener> listeners = new ArrayList<NanoTimerListener>();
    private final ArrayList<LocalCollector> localCollectors = new ArrayList<LocalCollector>(128);

    private final ThreadLocal<SimpleDateFormat> dateFormatterTL = new ThreadLocal<SimpleDateFormat>()
    {
        @Override
        public SimpleDateFormat initialValue()
        {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        }
    };
    
    private final ThreadLocal<LocalCollector> localCollectorTL = new ThreadLocal<LocalCollector>()
    {
        @Override
        public LocalCollector initialValue()
        {
            final LocalCollector newObj = new LocalCollector(Thread.currentThread());
            synchronized (localCollectors)
            {
                localCollectors.add(newObj);
            }
            return newObj;
        }
    };


    private boolean ignoreIntervalsWithNoCalls = true;
    private boolean started;
    
    long intervalStartMillis;
    long intervalNumCalls;
    long intervalTotalDurationNS;
//    long intervalTotalSquaredDurationNS; // stdDev = sqrt[ sum(D^2) - (sum(D)*sum(D)/n) / (n-1)  
    long intervalLongestDurationNS;

    private long startedTimeMillis;
    
    private long totalCallDurationNS;
    private long totalCalls;
    
    public long getStartedTimeMillis()
    {
        return startedTimeMillis;
    }

    private long longestDurationNS;

    private final String name;

    public synchronized void addListener(final NanoTimerListener p_listener)
    {
        if (!listeners.contains(p_listener))
        {
            listeners.add(p_listener);
        }
    }

    synchronized void collect()
    {
        final long nowMillis = System.currentTimeMillis();
        
        // update this.intervalXxx fields:
        final int numCollectors = this.localCollectors.size();
        for (int i=0; i < numCollectors; i++)
        {
            this.localCollectors.get(i).collectAndReset(); // (this call updates this.intervalXxx fields)
        }
        final long intervalAvg;
        if (this.intervalNumCalls == 0)
        {
            if (this.ignoreIntervalsWithNoCalls)
            {
                intervalStartMillis = System.currentTimeMillis();
                return;
            }
            intervalAvg = 0;
        }
        else
        {
            intervalAvg = this.intervalTotalDurationNS / this.intervalNumCalls;
        }
        
        // update this.totalXxx fields:
        if (this.intervalLongestDurationNS > this.longestDurationNS)
        {
            this.longestDurationNS = this.intervalLongestDurationNS;
        }
        this.totalCalls += this.intervalNumCalls;
        this.totalCallDurationNS += this.intervalTotalDurationNS;
        final long totalAvg = (totalCalls==0) ? 0L : (this.totalCallDurationNS / this.totalCalls);
        
        // report findings:
        final int numListeners = this.listeners.size();
        for (int i=0; i < numListeners; i++)
        {
            final NanoTimerListener listener = this.listeners.get(i);
            listener.intervalUpdate(this, intervalStartMillis,  
                    intervalNumCalls, intervalAvg, intervalLongestDurationNS);
            listener.overallUpdate(this, totalCalls, totalAvg, longestDurationNS);
        }
        
        // reset interval fields:
        intervalStartMillis = System.currentTimeMillis();
        intervalLongestDurationNS = 0;
        intervalNumCalls = 0;
        intervalTotalDurationNS = 0;
    }
    
    public String getName()
    {
        return name;
    }

    public NanoCallDurationTimer(String p_name)
    {
        this.name = p_name;
    }
    
    /**
     * Equivalent to getThreadLocal().enter()
     */
    public void enter()
    {
        getThreadLocal().enter();
    }

    /**
     * Equivalent to getThreadLocal().exit()
     */
    public void exit()
    {
        getThreadLocal().exit();
    }

    public LocalCollector getThreadLocal()
    {
        return localCollectorTL.get();
    }

    public void setIgnoreIntervalsWithNoCalls(final boolean p_value)
    {
        ignoreIntervalsWithNoCalls = p_value;
    }
    
    public boolean isIgnoringIntervalsWithNoCalls()
    {
        return ignoreIntervalsWithNoCalls;
    }
    
    /**
     * Convenience method to add a logging listener.
     * @param p_prefix
     */
    public void addLoggingCollectorListener(String p_prefix)
    {
        final String prefix = p_prefix==null ? "" : (" "+p_prefix);
        addListener(new NanoTimerListener() {
            public void overallUpdate(NanoCallDurationTimer p_nanoTimer, 
                    long p_callCount, long p_averageDurationNS, long p_highWatermarkDurationNS)
            {
                Log.information(prefix+" NanoCallDurationTimer("+name+") - (TOTAL) callCount="+p_callCount
                        +", avgDuration="+formatNS(p_averageDurationNS)
                        +", longestDuration="+formatNS(p_highWatermarkDurationNS)
                        +", time='"+formatTime(System.currentTimeMillis())+"'");
            }

            public void intervalUpdate(NanoCallDurationTimer p_nanoTimer, long p_intervalStartMillis,
                    long p_callCount, long p_averageDurationNS, long p_highWatermarkDurationNS)
            {
                Log.information(prefix+" NanoCallDurationTimer("+name+") - (INTERVAL) callCount="+p_callCount
                        +", avgDuration="+formatNS(p_averageDurationNS)
                        +", longestDuration="+formatNS(p_highWatermarkDurationNS)
                        +", intervalMillis="+(System.currentTimeMillis()-p_intervalStartMillis)
                        + " ms, fromTime='"+formatTime(p_intervalStartMillis));
            }
            private String formatNS(long p_ns)
            {
                return Double.toString(p_ns/1000000d)+" ms";
            }
        });
    }
    
    private String formatTime(long p_currentTimeMillis)
    {
        return dateFormatterTL.get().format(new Date(p_currentTimeMillis));
    }

    public synchronized boolean startCollectorTask(final int p_intervalMillis)
    {
        if (started)
        {
            return false; // only one collector task at a time, please.
        }
        
        // Because the timer may have been cancelled, don't trust it: just create a new one. 
        this.collectionTaskTimer = new Timer("NanoTimerTask("+name+")");
        this.collectionTaskTimer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                collect();
            }
        }, p_intervalMillis, p_intervalMillis);
        started = true;
        
        // reset:
        startedTimeMillis = System.currentTimeMillis();
        this.longestDurationNS = 0;
        this.totalCallDurationNS = 0;
        this.totalCalls = 0;
        final int numCollectors = this.localCollectors.size();
        for (int i=0; i < numCollectors; i++)
        {
            this.localCollectors.get(i).reset();
        }
        Log.information("NanoCallDurationTimer("+name+"): started collector task with pollingInterval="+p_intervalMillis+" ms");
        return true;
    }

    public synchronized boolean stopCollectorTask()
    {
        if (!started)
        {
            return false;
        }
        this.collectionTaskTimer.cancel();
        started = false;
        Log.information("NanoCallDurationTimer("+name+"): cancelled collector task");
        return true;
    }
}
