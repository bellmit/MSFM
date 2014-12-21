package com.cboe.consumers.callback;

import com.cboe.interfaces.consumers.callback.CMINBBOConsumerCache;
import com.cboe.interfaces.domain.SessionKeyWrapper;
import com.cboe.interfaces.presentation.product.SessionProductClass;
import com.cboe.idl.cmiCallback.CMINBBOConsumer;
import com.cboe.util.event.EventChannelAdapter;
import com.cboe.domain.util.SessionKeyContainer;

/**
 * Created by IntelliJ IDEA.
 * User: Davisson
 * Date: Mar 5, 2003
 * Time: 3:43:08 PM
 * To change this template use Options | File Templates.
 */
public class CMINBBOConsumerCacheImpl extends AbstractCallbackConsumerCache implements CMINBBOConsumerCache
{
    public CMINBBOConsumerCacheImpl(EventChannelAdapter eventChannel)
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
        return NBBOConsumerFactory.create(getEventChannel());
    }
}
