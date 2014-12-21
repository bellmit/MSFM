package com.cboe.consumers.internalPresentation;

import com.cboe.util.event.EventChannelAdapter;
import com.cboe.consumers.internalPresentation.CurrentMarketConsumerFactory;
import com.cboe.consumers.callback.CMICurrentMarketConsumerCacheImpl;

/**
 * Created by IntelliJ IDEA.
 * User: Davisson
 * Date: Feb 26, 2003
 * Time: 3:00:16 PM
 * To change this template use Options | File Templates.
 */
public class AdminCMICurrentMarketConsumerCacheImpl extends CMICurrentMarketConsumerCacheImpl
{
    /**
     * If eventChannel is null an IllegalArgumentException will be thrown.
     *
     * @param eventChannel
     */
    public AdminCMICurrentMarketConsumerCacheImpl(EventChannelAdapter eventChannel)
    {
        super(eventChannel);
    }

    /**
     * Override CMICurrentMarketConsumerCacheImpl.createNewCallbackConsumer() to create the internalPresentation
     * implementation of CMICurrentMarketConsumer.  This impl will be added to the cache.
     *
     * @return
     */
    protected org.omg.CORBA.Object createNewCallbackConsumer()
    {
        return CurrentMarketConsumerFactory.create(getEventChannel());
    }
}
