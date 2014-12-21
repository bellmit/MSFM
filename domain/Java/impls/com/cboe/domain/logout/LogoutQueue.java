package com.cboe.domain.logout;
/**
 * @author Jing Chen
 */
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import java.util.HashMap;

public class LogoutQueue
{
    private HashMap logoutQueue;
    private boolean waitForEmptyLogoutQueue;
    private long logoutQueueTimeout;   // timeout in milliseconds
    private static final int MS_TO_NS = 1000000;
    private static final int MAX_FRAMES_TO_REPORT = 5;

    public LogoutQueue(long timeout)
    {
        logoutQueue = new HashMap();
        waitForEmptyLogoutQueue = false;
        this.logoutQueueTimeout = timeout;
    }

    public synchronized void addLogout(Object source)
    {
        logoutQueue.put(source, source);
    }

    public synchronized void removeLogout(Object source)
    {
        logoutQueue.remove(source);
        if (logoutQueue.isEmpty())
        {
            waitForEmptyLogoutQueue = false;
            notifyAll();
        }
    }

    public synchronized void setWaitForEmptyLogoutQueueFlag(boolean waitFlag)
    {
        waitForEmptyLogoutQueue = true;
    }

    public synchronized int getQueueSize()
    {
        return logoutQueue.size();
    }

    public synchronized boolean waitForLogoutComplete()
    {
        boolean logoutWait = false;
        long waitedTooLong = System.nanoTime() + logoutQueueTimeout * MS_TO_NS;
        while (waitForEmptyLogoutQueue && !logoutQueue.isEmpty())
        {
            try
            {
                wait(logoutQueueTimeout);
                logoutWait = true;
                if (waitedTooLong > 0 && System.nanoTime() > waitedTooLong)
                {
                    waitedTooLong = 0; // only write alarm message one time
                    String thisClass = this.getClass().getName();
                    boolean report = false;
                    int frames = 0;
                    StringBuilder s = new StringBuilder();
                    StackTraceElement[] stack = Thread.currentThread().getStackTrace();
                    for (StackTraceElement ste : stack)
                    {
                        if (ste.getClassName().equals(thisClass))
                        {
                            // We ignored the first few frames, which belong to
                            // the getStackTrace() call.
                            report = true;
                        }
                        if (report)
                        {
                            if (frames >= MAX_FRAMES_TO_REPORT)
                            {
                                // We won't report further frames.
                                s.append("...");
                    /*for*/     break;
                            }
                            if (frames++ > 0)
                            {
                                s.append(',');
                            }
                            s.append(ste.getClassName()).append('.')
                             .append(ste.getMethodName()).append(':')
                             .append(ste.getLineNumber());
                        }
                    } // for
                    Log.alarm("Waited too long for LogoutQueue. Get thread dump and notify Client support."
                        + " threadId:" + Thread.currentThread().getId()
                        + " threadName:" + Thread.currentThread().getName()
                        + " stack:" + s.toString());
                }
            }
            catch (InterruptedException e)
            {

            }
        }
        return logoutWait;
    }
}
