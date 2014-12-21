package com.cboe.consumers.callback;

import com.cboe.interfaces.consumers.callback.CMIOrderBookV2ConsumerCache;
import com.cboe.interfaces.domain.SessionKeyWrapper;
import com.cboe.interfaces.presentation.product.SessionProductClass;
import com.cboe.util.event.EventChannelAdapter;
import com.cboe.idl.cmiCallbackV2.CMIOrderBookConsumer;
import com.cboe.domain.util.SessionKeyContainer;

/**
 * Created by IntelliJ IDEA.
 * User: HallB
 * Date: Apr 4, 2003
 * Time: 8:28:40 AM
 * To change this template use Options | File Templates.
 */
public class CMIOrderBookV2ConsumerCacheImpl extends AbstractCallbackConsumerCache implements CMIOrderBookV2ConsumerCache
{
    public CMIOrderBookV2ConsumerCacheImpl(EventChannelAdapter eventChannel)
    {
        super(eventChannel);
    }

    public CMIOrderBookConsumer getBookDepthConsumer(SessionKeyWrapper key)
    {
        return (CMIOrderBookConsumer)getCallbackConsumer(key);
    }

    public CMIOrderBookConsumer getBookDepthConsumer(String sessionName, int classKey)
    {
        return getBookDepthConsumer(new SessionKeyContainer(sessionName, classKey));
    }

    public CMIOrderBookConsumer getBookDepthConsumer(SessionProductClass productClass)
    {
        return getBookDepthConsumer(productClass.getSessionKeyWrapper());
    }

    protected org.omg.CORBA.Object createNewCallbackConsumer()
    {
        return OrderBookV2ConsumerFactory.create(getEventChannel());
    }
}
