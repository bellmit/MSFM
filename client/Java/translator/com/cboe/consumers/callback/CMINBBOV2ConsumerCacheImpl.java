package com.cboe.consumers.callback;

import com.cboe.interfaces.consumers.callback.CMINBBOV2ConsumerCache;
import com.cboe.interfaces.domain.SessionKeyWrapper;
import com.cboe.interfaces.presentation.product.SessionProductClass;
import com.cboe.util.event.EventChannelAdapter;
import com.cboe.idl.cmiCallbackV2.CMINBBOConsumer;
import com.cboe.domain.util.SessionKeyContainer;

/**
 * Created by IntelliJ IDEA.
 * User: HallB
 * Date: Apr 4, 2003
 * Time: 8:23:22 AM
 * To change this template use Options | File Templates.
 */
public class CMINBBOV2ConsumerCacheImpl extends AbstractCallbackConsumerCache implements CMINBBOV2ConsumerCache
{
    public CMINBBOV2ConsumerCacheImpl(EventChannelAdapter eventChannel)
    {
        super(eventChannel);
    }

    public CMINBBOConsumer getNBBOConsumer(SessionKeyWrapper key)
    {
        return (CMINBBOConsumer)getCallbackConsumer(key);
    }

    public CMINBBOConsumer getNBBOConsumer(SessionProductClass productClass)
    {
        return this.getNBBOConsumer(productClass.getSessionKeyWrapper());
    }

    public CMINBBOConsumer getNBBOConsumer(String sessionName, int classKey)
    {
        return this.getNBBOConsumer(new SessionKeyContainer(sessionName, classKey));
    }

    protected org.omg.CORBA.Object createNewCallbackConsumer()
    {
        return NBBOV2ConsumerFactory.create(getEventChannel());
    }
}
