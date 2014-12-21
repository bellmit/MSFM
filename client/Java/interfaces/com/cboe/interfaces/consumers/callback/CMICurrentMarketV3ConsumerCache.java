package com.cboe.interfaces.consumers.callback;

import com.cboe.idl.cmiCallbackV3.CMICurrentMarketConsumer;
import com.cboe.interfaces.domain.SessionKeyWrapper;
import com.cboe.interfaces.presentation.product.SessionProductClass;

/**
 * Created by IntelliJ IDEA.
 * User: kyriakop
 * Date: Mar 24, 2004
 */
public interface CMICurrentMarketV3ConsumerCache extends CallbackConsumerCache
{
    public CMICurrentMarketConsumer getCurrentMarketConsumer(SessionKeyWrapper key);
    public CMICurrentMarketConsumer getCurrentMarketConsumer(SessionProductClass productClass);
    public CMICurrentMarketConsumer getCurrentMarketConsumer(String sessionName, int classKey);
}
