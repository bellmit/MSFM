package com.cboe.interfaces.consumers.callback;

import com.cboe.idl.cmiCallback.CMICurrentMarketConsumer;
import com.cboe.interfaces.domain.SessionKeyWrapper;
import com.cboe.interfaces.presentation.product.SessionProductClass;

/**
 * Created by IntelliJ IDEA.
 * User: Davisson
 * Date: Feb 27, 2003
 * Time: 10:45:58 AM
 * To change this template use Options | File Templates.
 */
public interface CMICurrentMarketConsumerCache extends CallbackConsumerCache
{
    public CMICurrentMarketConsumer getCurrentMarketConsumer(SessionKeyWrapper key);
    public CMICurrentMarketConsumer getCurrentMarketConsumer(SessionProductClass productClass);
    public CMICurrentMarketConsumer getCurrentMarketConsumer(String sessionName, int classKey);
}
