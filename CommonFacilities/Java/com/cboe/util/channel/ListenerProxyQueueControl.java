package com.cboe.util.channel;

public interface ListenerProxyQueueControl
{
    public int getQueueSize();

    public int getMaxQueueSize();

    /** Empty the queue of all ChannelEvent objects. Call release() on each
     * object except the last in the queue.
     *
     * @return The last ChannelEvent object in the queue,
     *     or null if queue was empty.
     */
    public ChannelEvent flushQueue();
}
