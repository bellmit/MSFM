package com.cboe.interfaces.consumers.callback;

import com.cboe.idl.cmiCallbackV2.CMICurrentMarketConsumer;
import com.cboe.interfaces.domain.SessionKeyWrapper;
import com.cboe.interfaces.presentation.product.SessionProductClass;

/**
 * Created by IntelliJ IDEA.
 * User: HallB
 * Date: Apr 4, 2003
 * Time: 8:09:46 AM
 * To change this template use Options | File Templates.
 */
public interface CMICurrentMarketV2ConsumerCache extends CallbackConsumerCache
{
    public CMICurrentMarketConsumer getCurrentMarketConsumer(SessionKeyWrapper key);
    public CMICurrentMarketConsumer getCurrentMarketConsumer(SessionProductClass productClass);
    public CMICurrentMarketConsumer getCurrentMarketConsumer(String sessionName, int classKey);
}
