package com.cboe.consumers.callback;

import com.cboe.interfaces.presentation.product.SessionProductClass;
import com.cboe.interfaces.domain.SessionKeyWrapper;
import com.cboe.util.event.EventChannelAdapter;
import com.cboe.idl.cmiCallback.CMICurrentMarketConsumer;
import com.cboe.consumers.callback.CurrentMarketConsumerFactory;
import com.cboe.domain.util.SessionKeyContainer;
import com.cboe.interfaces.consumers.callback.CMICurrentMarketConsumerCache;

/**
 * This provides an interface to a cache of CMICurrentMarketConsumers.  It requires an EventChannelAdapter for the
 * consumers to dispatch their events on.  Whenever a consumer is requested for a Session/ProductClass that isn't already
 * in the cache, a new one is created by createNewCallbackConsumer() and then added to the cache.
 *
 * Created by IntelliJ IDEA.
 * User: Davisson
 * Date: Feb 25, 2003
 * Time: 2:07:26 PM
 */
public class CMICurrentMarketConsumerCacheImpl extends AbstractCallbackConsumerCache implements CMICurrentMarketConsumerCache
{
    /**
     * If eventChannel is null an IllegalArgumentException will be thrown.
     *
     * @param eventChannel
     */
    public CMICurrentMarketConsumerCacheImpl(EventChannelAdapter eventChannel)
    {
        super(eventChannel);
    }

    /**
     * This will never throw a ClassCastException because the implementation of createNewCallbackConsumer() always
     * returns a CMICurrentMarketConsumer.
     *
     * @param key
     * @return CMICurrentMarketConsumer
     */
    public CMICurrentMarketConsumer getCurrentMarketConsumer(SessionKeyWrapper key)
    {
        return (CMICurrentMarketConsumer)getCallbackConsumer(key);
    }

    public CMICurrentMarketConsumer getCurrentMarketConsumer(SessionProductClass productClass)
    {
        return getCurrentMarketConsumer(productClass.getSessionKeyWrapper());
    }

    public CMICurrentMarketConsumer getCurrentMarketConsumer(String sessionName, int classKey)
    {
        return getCurrentMarketConsumer(new SessionKeyContainer(sessionName, classKey));
    }

    /**
     * Creates a new CMICurrentMarketConsumer.    This object will be added to the cache.
     *
     * @return org.omg.CORBA.Object
     */
    protected org.omg.CORBA.Object createNewCallbackConsumer()
    {
        return CurrentMarketConsumerFactory.create(getEventChannel());
    }
}
