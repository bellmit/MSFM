package com.cboe.interfaces.consumers.callback;

import com.cboe.idl.cmiCallback.CMIRecapConsumer;
import com.cboe.interfaces.domain.SessionKeyWrapper;
import com.cboe.interfaces.presentation.product.SessionProductClass;

/**
 * Created by IntelliJ IDEA.
 * User: Davisson
 * Date: Feb 27, 2003
 * Time: 10:45:58 AM
 * To change this template use Options | File Templates.
 */
public interface CMIRecapConsumerCache extends CallbackConsumerCache
{
    public CMIRecapConsumer getRecapConsumer(SessionKeyWrapper key);
    public CMIRecapConsumer getRecapConsumer(SessionProductClass productClass);
    public CMIRecapConsumer getRecapConsumer(String sessionName, int classKey);
}
