package com.cboe.application.inprocess.consumer.proxy;

import com.cboe.util.event.EventChannelListener;
import com.cboe.infrastructureServices.foundationFramework.BObject;

/**
 * @author Jing Chen
 */
public abstract class BaseConsumerProxy extends BObject implements EventChannelListener
{
    protected Object hashKey;
    public int hashCode()
    {
        return hashKey.hashCode();
    }

    public void setHashKey(Object hashKey)
    {
        this.hashKey = hashKey;
    }

    public boolean equals(Object obj)
    {
        // check the equivalence of the IOR strings.
        if (obj instanceof BaseConsumerProxy)
        {
            return hashKey.equals(((BaseConsumerProxy)obj).getHashKey());
        }
        else
        {
            return false;
        }
    }

    protected Object getHashKey()
    {
        return hashKey;
    }
}
