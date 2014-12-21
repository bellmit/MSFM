package com.cboe.consumers.eventChannel;

import com.cboe.infrastructureServices.eventService.*;

/**
* This class wraps the event service ConsumerFilter to be a reference count object
* the reference count is initialized to be 1 when created based on the consumer filter
* @author Connie Feng
*/

public class EventChannelFilter
{
    private ConsumerFilter consumerFilter;
    private int referenceCount;
    private Object lock;

    /**
    * Constructor
    * @param consumerFilter the event service consumer filter
    * to be wrapped up
    */
    public EventChannelFilter(ConsumerFilter consumerFilter)
    {
        this.consumerFilter = consumerFilter;
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
            if(referenceCount<0)
            {
                referenceCount=0;
            }
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
    * Retrieves the event service consumer filter that the object
    * is wrapping up.
    * @return the event service ConsumerFilter object
    */
    public ConsumerFilter getConsumerFilter()
    {
        return consumerFilter;
    }
}//EOF
