package com.cboe.consumers.internalPresentation;

import com.cboe.util.event.EventChannelAdapter;
import com.cboe.consumers.callback.CMIOrderBookConsumerCacheImpl;

/**
 * Created by IntelliJ IDEA.
 * User: Davisson
 * Date: Mar 5, 2003
 * Time: 4:49:58 PM
 * To change this template use Options | File Templates.
 */
public class AdminCMIOrderBookConsumerCacheImpl extends CMIOrderBookConsumerCacheImpl
{
    public AdminCMIOrderBookConsumerCacheImpl(EventChannelAdapter eventChannel)
    {
        super(eventChannel);
    }

    /**
     * Override createNewCallbackConsumer() to return an internalPresentation implementation of CMIOrderBookConsumer.
     *
     * @return org.omg.CORBA.Object
     */
    protected org.omg.CORBA.Object createNewCallbackConsumer()
    {
        return BookDepthConsumerFactory.create(getEventChannel());
    }
}
