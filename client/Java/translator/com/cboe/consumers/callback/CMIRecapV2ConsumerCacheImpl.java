package com.cboe.consumers.callback;

import com.cboe.interfaces.consumers.callback.CMIRecapV2ConsumerCache;
import com.cboe.interfaces.domain.SessionKeyWrapper;
import com.cboe.interfaces.presentation.product.SessionProductClass;
import com.cboe.util.event.EventChannelAdapter;
import com.cboe.idl.cmiCallbackV2.CMIRecapConsumer;
import com.cboe.domain.util.SessionKeyContainer;

/**
 * Created by IntelliJ IDEA.
 * User: HallB
 * Date: Apr 4, 2003
 * Time: 8:25:27 AM
 * To change this template use Options | File Templates.
 */
public class CMIRecapV2ConsumerCacheImpl extends AbstractCallbackConsumerCache implements CMIRecapV2ConsumerCache
{
    public CMIRecapV2ConsumerCacheImpl(EventChannelAdapter eventChannel)
    {
        super(eventChannel);
    }

    public CMIRecapConsumer getRecapConsumer(SessionKeyWrapper key)
    {
        return (CMIRecapConsumer)getCallbackConsumer(key);
    }

    public CMIRecapConsumer getRecapConsumer(SessionProductClass productClass)
    {
        return getRecapConsumer(productClass.getSessionKeyWrapper());
    }

    public CMIRecapConsumer getRecapConsumer(String sessionName, int classKey)
    {
        return getRecapConsumer(new SessionKeyContainer(sessionName, classKey));
    }

    protected org.omg.CORBA.Object createNewCallbackConsumer()
    {
        return RecapV2ConsumerFactory.create(getEventChannel());
    }
}
