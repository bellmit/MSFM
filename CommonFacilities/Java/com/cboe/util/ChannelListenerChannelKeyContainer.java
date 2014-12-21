package com.cboe.util;

import com.cboe.util.channel.*;
/**
* This class wraps the event service ConsumerFilter to be a reference count object
* the reference count is initialized to be 1 when created based on the consumer filter
* @author Connie Feng
*/

public class ChannelListenerChannelKeyContainer
{
    private ChannelListener listener;
    private ChannelKey channelKey;
    private int referenceCount;
    private Object lock;

    /**
    * Constructor
    * @param consumerFilter the event service consumer filter
    * to be wrapped up
    */
    public ChannelListenerChannelKeyContainer(ChannelListener listener, ChannelKey channelKey)
    {
        this.listener = listener;
        this.channelKey = channelKey;
        lock = new Object();

        referenceCount = 1;
    }

    /**
    * Increases the reference count
    * @return the current reference count after the increase
    */
    public int increase()
    {
        synchronized(lock)
        {
            referenceCount++;
        }
        return referenceCount;
    }

    /**
    * Decreases the reference count
    * @return the current reference count after the decrease
    */
    public int decrease()
    {
        synchronized(lock)
        {
            referenceCount--;
        }

        return referenceCount;
    }

    /**
    * Retrieves the reference count
    * @return the current reference count
    */
    public int getReferenceCount()
    {
        return referenceCount;
    }

    /**
    * Retrieves the ChannelKey object
    * @return the ChannelKey object
    */
    public ChannelKey getChannelKey()
    {
        return channelKey;
    }

    public void releaseAll()
    {
        referenceCount = 0;
    }
    /**
    * Retrieves the listener object
    * @return the ChannelListener object
    */
    public ChannelListener getChannelListener()
    {
        return listener;
    }
}//EOF
