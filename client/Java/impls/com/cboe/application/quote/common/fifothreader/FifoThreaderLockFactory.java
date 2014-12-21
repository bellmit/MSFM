package com.cboe.application.quote.common.fifothreader;

/**
 * FifoThreaderFactory.java
 *
 * @author Dmitry Volpyansky
 *
 */

import com.cboe.client.util.collections.*;
import com.cboe.client.util.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.*;

public class FifoThreaderLockFactory
{
    protected static final SessionKeyObjectMap sessionKeyObjectMap;
    protected static final int                 fifoThreaderType;
    protected static volatile boolean          wasMapModified;

    public static final String FIFO_THREADER_TYPE_NONE                          = "None";
    public static final int    FIFO_THREADER_TYPE_NONE_VAL                      = 0;
    public static final String FIFO_THREADER_TYPE_EQUAL_PRIORITY                = "EqualPriority";
    public static final int    FIFO_THREADER_TYPE_EQUAL_PRIORITY_VAL            = 1;
    public static final String FIFO_THREADER_TYPE_CANCEL_IS_HIGHER_PRIORITY     = "CancelIsHigherPriority";
    public static final int    FIFO_THREADER_TYPE_CANCEL_IS_HIGHER_PRIORITY_VAL = 2;

    public static final String PROPERTY_FIFO_THREADER_TYPE                      = "FifoThreaderType";
    public static final String PROPERTY_FIFO_THREADER_DEBUG_SLEEP_TIME          = "FifoThreaderDebugSleepTime";

    static
    {
        sessionKeyObjectMap = new SessionKeyObjectMap.SessionKeyObjectMapMT(); // the map itself is thread-safe

        String s = System.getProperty(PROPERTY_FIFO_THREADER_TYPE);

        if (FIFO_THREADER_TYPE_CANCEL_IS_HIGHER_PRIORITY.equalsIgnoreCase(s))
        {
            fifoThreaderType = FIFO_THREADER_TYPE_CANCEL_IS_HIGHER_PRIORITY_VAL;
        }
        else if (FIFO_THREADER_TYPE_EQUAL_PRIORITY.equalsIgnoreCase(s))
        {
            fifoThreaderType = FIFO_THREADER_TYPE_EQUAL_PRIORITY_VAL;
        }
        else
        {
            fifoThreaderType = FIFO_THREADER_TYPE_NONE_VAL;
        }

        if (isFifoThreadingOn())
        {
            s = System.getProperty(PROPERTY_FIFO_THREADER_DEBUG_SLEEP_TIME);
            if (s != null)
            {
                do
                {
                    int sleepSeconds;
                    try
                    {
                        sleepSeconds = Integer.parseInt(s);
                    }
                    catch (Exception ex)
                    {
                        Log.information("Periodic Debug Dumps for " + PROPERTY_FIFO_THREADER_TYPE + " are turned off: invalid -D" + PROPERTY_FIFO_THREADER_DEBUG_SLEEP_TIME + " value: " + s);
                        break;
                    }

                    if (sleepSeconds == 0)
                    {
                        StringBuilder dumpoff = new StringBuilder(PROPERTY_FIFO_THREADER_TYPE.length()+PROPERTY_FIFO_THREADER_DEBUG_SLEEP_TIME.length()+s.length()+55);
                        dumpoff.append("Periodic Debug Dumps for ").append(PROPERTY_FIFO_THREADER_TYPE)
                               .append(" are turned off: -D").append(PROPERTY_FIFO_THREADER_DEBUG_SLEEP_TIME)
                               .append(" value: ").append(s);
                        Log.information(dumpoff.toString());
                        break;
                    }

                    if (sleepSeconds < 14 || sleepSeconds > 60*8)
                    {
                        sleepSeconds = 300;
                    }

                    final SessionKeyObjectMap.SessionKeyObjectMapVisitorIF visitor = new SessionKeyObjectMap.SessionKeyObjectMapVisitorIF()
                    {
                        String sessionName = "";

                        public void setSessionName(String sessionName)
                        {
                            this.sessionName = sessionName;
                        }

                        public int visit(int key, Object value)
                        {
                            String val = value.toString();
                            StringBuilder sb = new StringBuilder(sessionName.length()+val.length()+35);
                            sb.append("FifoThreaderLock[").append(sessionName)
                              .append(':').append(key).append(':').append(val).append(']');
                            Log.information(sb.toString());
                            return SessionKeyObjectMap.SessionKeyObjectMapVisitorIF.CONTINUE;
                        }
                    };

                    final int fifoThreaderDebugSleepSeconds = sleepSeconds;
                    Thread thread = new Thread("FifoThreaderTypeDumpThread")
                    {
                        public void run()
                        {
                            StringBuilder dumpson = new StringBuilder(PROPERTY_FIFO_THREADER_TYPE.length()+80);
                            dumpson.append("Periodic Debug Dumps for ").append(PROPERTY_FIFO_THREADER_TYPE)
                                   .append(" are turned on to write to System.out every: ").append(fifoThreaderDebugSleepSeconds)
                                   .append(" seconds");
                            Log.information(dumpson.toString());
                            dumpson = null;  // Allow this to be garbage collected

                            while (true)
                            {
                                ThreadHelper.sleepSeconds(fifoThreaderDebugSleepSeconds);
                                if (wasMapModified)
                                {
                                    sessionKeyObjectMap.acceptKeyValueVisitor(visitor);
                                    wasMapModified = false;
                                }
                            }
                        }
                    };

                    thread.setDaemon(true);
                    thread.start();
                }
                while (false);
            }
        }
    }

    public static final boolean isFifoThreadingOn()
    {
        return fifoThreaderType != FIFO_THREADER_TYPE_NONE_VAL;
    }

    public static FifoThreaderLock acquireAcceptClassKeyLock(String userID, String sessionName, int classKey, long timestamp)
    {
        FifoThreaderLock fifoThreaderLock = findFifoThreader(userID, sessionName, classKey);

        fifoThreaderLock.acquireAcceptClassLock();

        return fifoThreaderLock;
    }

    public static void releaseAcceptClassKeyLock(FifoThreaderLock fifoThreaderLock)
    {
        releaseFifoThreader(fifoThreaderLock);
    }

    public static FifoThreaderLock acquireCancelClassKeyLock(String userID, String sessionName, int classKey, long timestamp)
    {
        FifoThreaderLock fifoThreaderLock = findFifoThreader(userID, sessionName, classKey);

        fifoThreaderLock.acquireCancelClassLock();

        return fifoThreaderLock;
    }

    public static void releaseCancelClassKeyLock(FifoThreaderLock fifoThreaderLock)
    {
        releaseFifoThreader(fifoThreaderLock);
    }

    public static FifoThreaderLock acquireAcceptProductKeyLock(String userID, String sessionName, int classKey, int productKey, long timestamp)
    {
        FifoThreaderLock fifoThreaderLock = findFifoThreader(userID, sessionName, classKey);

        fifoThreaderLock.acquireAcceptProductLock();

        return fifoThreaderLock;
    }

    public static void releaseAcceptProductKeyLock(FifoThreaderLock fifoThreaderLock)
    {
        releaseFifoThreader(fifoThreaderLock);
    }

    public static FifoThreaderLock acquireCancelProductKeyLock(String userID, String sessionName, int classKey, int productKey, long timestamp)
    {
        FifoThreaderLock fifoThreaderLock = findFifoThreader(userID, sessionName, classKey);

        fifoThreaderLock.acquireCancelProductLock();

        return fifoThreaderLock;
    }

    public static void releaseCancelProductKeyLock(FifoThreaderLock fifoThreaderLock)
    {
        releaseFifoThreader(fifoThreaderLock);
    }

    protected static void releaseFifoThreader(FifoThreaderLock fifoThreaderLock)
    {
        fifoThreaderLock.releaseLock();

        wasMapModified = true;
    }

    protected static FifoThreaderLock findFifoThreader(String userID, String sessionName, int classKey)
    {
        StringBuilder sb = new StringBuilder(userID.length()+sessionName.length()+1);
        sb.append(userID).append(':').append(sessionName);
        String userSession = sb.toString();

        // see if the lock was already created (this is thread-safe)
        FifoThreaderLock fifoThreaderLock = (FifoThreaderLock) sessionKeyObjectMap.getValueForKey(userSession, classKey);
        if (fifoThreaderLock == null)
        {
            fifoThreaderLock = createThreaderLock();

            // now, put the lock into the map ONLY IT WASN'T ALREADY THERE (again, this is thread-safe)
            sessionKeyObjectMap.putKeyValue(userSession, classKey, fifoThreaderLock, sessionKeyObjectMap.UpdateIfNullPolicy);
        }

        wasMapModified = true;

        // do another get because of possible thread contention where two threads in this method
        // both had a null fifoThreaderLock above and each created one -- only one put would have
        // succeeded; that's the object we need to return, and the additional one will be garbage
        // collected.
        return (FifoThreaderLock) sessionKeyObjectMap.getValueForKey(userSession, classKey);
    }

    protected static FifoThreaderLock createThreaderLock()
    {
        switch (fifoThreaderType)
        {
            case FIFO_THREADER_TYPE_CANCEL_IS_HIGHER_PRIORITY_VAL:
                return FifoThreaderLock.createCancelIsHigherPriorityFifoThreaderLock();
            case FIFO_THREADER_TYPE_EQUAL_PRIORITY_VAL:
                return FifoThreaderLock.createEqualPriorityFifoThreaderLock();
            default: // for future
                return FifoThreaderLock.createEqualPriorityFifoThreaderLock();
        }
    }
}
