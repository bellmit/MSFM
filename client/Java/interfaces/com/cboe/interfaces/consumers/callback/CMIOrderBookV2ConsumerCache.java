package com.cboe.interfaces.consumers.callback;

import com.cboe.idl.cmiCallbackV2.CMIOrderBookConsumer;
import com.cboe.interfaces.domain.SessionKeyWrapper;
import com.cboe.interfaces.presentation.product.SessionProductClass;

/**
 * Created by IntelliJ IDEA.
 * User: HallB
 * Date: Apr 4, 2003
 * Time: 8:13:25 AM
 * To change this template use Options | File Templates.
 */
public interface CMIOrderBookV2ConsumerCache extends CallbackConsumerCache
{
    public CMIOrderBookConsumer getBookDepthConsumer(SessionKeyWrapper key);
    public CMIOrderBookConsumer getBookDepthConsumer(SessionProductClass productClass);
    public CMIOrderBookConsumer getBookDepthConsumer(String sessionName, int classKey);
}
