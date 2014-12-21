package com.cboe.interfaces.consumers.callback;

import com.cboe.idl.cmiCallbackV2.CMIRecapConsumer;
import com.cboe.interfaces.domain.SessionKeyWrapper;
import com.cboe.interfaces.presentation.product.SessionProductClass;

/**
 * Created by IntelliJ IDEA.
 * User: HallB
 * Date: Apr 4, 2003
 * Time: 8:15:17 AM
 * To change this template use Options | File Templates.
 */
public interface CMIRecapV2ConsumerCache extends CallbackConsumerCache
{
    public CMIRecapConsumer getRecapConsumer(SessionKeyWrapper key);
    public CMIRecapConsumer getRecapConsumer(SessionProductClass productClass);
    public CMIRecapConsumer getRecapConsumer(String sessionName, int classKey);
}
