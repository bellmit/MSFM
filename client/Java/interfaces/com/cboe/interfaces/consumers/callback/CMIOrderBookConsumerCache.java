package com.cboe.interfaces.consumers.callback;

import com.cboe.idl.cmiCallback.CMIOrderBookConsumer;
import com.cboe.interfaces.domain.SessionKeyWrapper;
import com.cboe.interfaces.presentation.product.SessionProductClass;

/**
 * Created by IntelliJ IDEA.
 * User: Davisson
 * Date: Mar 5, 2003
 * Time: 4:03:38 PM
 * To change this template use Options | File Templates.
 */
public interface CMIOrderBookConsumerCache extends CallbackConsumerCache
{
    public CMIOrderBookConsumer getBookDepthConsumer(SessionKeyWrapper key);
    public CMIOrderBookConsumer getBookDepthConsumer(SessionProductClass productClass);
    public CMIOrderBookConsumer getBookDepthConsumer(String sessionName, int classKey);
}
