package com.cboe.consumers.callback;

import com.cboe.interfaces.consumers.callback.CMIRecapConsumerCache;
import com.cboe.interfaces.domain.SessionKeyWrapper;
import com.cboe.interfaces.presentation.product.SessionProductClass;
import com.cboe.idl.cmiCallback.CMIRecapConsumer;
import com.cboe.util.event.EventChannelAdapter;
import com.cboe.domain.util.SessionKeyContainer;
import org.omg.CORBA.Object;

/**
 * Created by IntelliJ IDEA.
 * User: Davisson
 * Date: Mar 3, 2003
 * Time: 2:46:28 PM
 * To change this template use Options | File Templates.
 */
public class CMIRecapConsumerCacheImpl extends AbstractCallbackConsumerCache implements CMIRecapConsumerCache
{
    public CMIRecapConsumerCacheImpl(EventChannelAdapter eventChannel)
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
        return RecapConsumerFactory.create(getEventChannel());
    }
}
