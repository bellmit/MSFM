package com.cboe.consumers.internalPresentation;

import com.cboe.consumers.callback.CMIRecapConsumerCacheImpl;
import com.cboe.util.event.EventChannelAdapter;

/**
 * Created by IntelliJ IDEA.
 * User: Davisson
 * Date: Mar 3, 2003
 * Time: 3:17:32 PM
 * To change this template use Options | File Templates.
 */
public class AdminCMIRecapConsumerCacheImpl extends CMIRecapConsumerCacheImpl
{
    public AdminCMIRecapConsumerCacheImpl(EventChannelAdapter eventChannel)
    {
        super(eventChannel);
    }

    /**
     * Override CMIRecapConsumerCacheImpl.createNewCallbackConsumer() to create the internalPresentation
     * implementation of CMIRecapConsumer.  This impl will be added to the cache.
     *
     * @return org.omg.CORBA.Object
     */
    protected org.omg.CORBA.Object createNewCallbackConsumer()
    {
        return RecapConsumerFactory.create(getEventChannel());
    }
}
