package com.cboe.consumers.internalPresentation;

import com.cboe.consumers.callback.CMINBBOConsumerCacheImpl;
import com.cboe.util.event.EventChannelAdapter;

/**
 * Created by IntelliJ IDEA.
 * User: Davisson
 * Date: Mar 5, 2003
 * Time: 4:48:07 PM
 * To change this template use Options | File Templates.
 */
public class AdminCMINBBOConsumerCacheImpl extends CMINBBOConsumerCacheImpl
{
    public AdminCMINBBOConsumerCacheImpl(EventChannelAdapter eventChannel)
    {
        super(eventChannel);
    }

    /**
     * Override createNewCallbackConsumer() to return an internalPresentation implementation of CMINBBOConsumer.
     *
     * @return org.omg.CORBA.Object
     */
    protected org.omg.CORBA.Object createNewCallbackConsumer()
    {
        return NBBOConsumerFactory.create(getEventChannel());
    }
}
