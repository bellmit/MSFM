package com.cboe.consumers.callback;

import com.cboe.interfaces.consumers.callback.CMIOrderBookConsumerCache;
import com.cboe.interfaces.presentation.product.SessionProductClass;
import com.cboe.interfaces.domain.SessionKeyWrapper;
import com.cboe.idl.cmiCallback.CMIOrderBookConsumer;
import com.cboe.util.event.EventChannelAdapter;
import com.cboe.domain.util.SessionKeyContainer;

/**
 * Created by IntelliJ IDEA.
 * User: Davisson
 * Date: Mar 5, 2003
 * Time: 4:07:51 PM
 * To change this template use Options | File Templates.
 */
public class CMIOrderBookConsumerCacheImpl extends AbstractCallbackConsumerCache implements CMIOrderBookConsumerCache
{
    public CMIOrderBookConsumerCacheImpl(EventChannelAdapter eventChannel)
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
        return BookDepthConsumerFactory.create(getEventChannel());
    }
}
