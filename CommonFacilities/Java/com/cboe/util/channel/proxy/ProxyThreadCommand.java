/**
 * The ChannelThreadCommand class contains the logic a worker thread will
 * execute to update a ChannelListener through its proxy object.
 *
 * @author Derek T. Chambers-Boucher
 * @version 04/18/1999
 */
package com.cboe.util.channel.proxy;

import com.cboe.util.*;
import com.cboe.util.channel.*;
import java.util.Enumeration;

public interface ProxyThreadCommand
{
    /**
     * Sets the reference of the threadPool that this thread command should
     * schedule itself with.
     *
     * @param threadPool the ThreadPool reference.
     */
    public void setThreadPool(ThreadPool threadPool);


    public void release();
    /**
     * Returns whether the thread pool has been set for this thread command
     */
    public boolean isThreadPool();

    /**
     * This method adds the passed ChannelEvent to the ChannelThreadCommand's
     * event queue.  This will assure serialized delivery of events to the associated
     * EventChannelListener.
     * @param event the ChannelEvent to send to the registered listener.
     */
    public void addEvent(ChannelEvent event);

    /**
     * This method contains the actual work the worker thread will carry out.
     */
    public void execute();

    public int getQueueSize();

    public int getMaxQueueSize();

    /** Empty the queue of ChannelEvent objects.
     *
     * @return ChannelEvent objects that were in the queue before this call.
     */
    public Enumeration flushQueue();
}
