package com.cboe.interfaces.consumers.callback;

import com.cboe.idl.cmiCallback.CMINBBOConsumer;
import com.cboe.interfaces.domain.SessionKeyWrapper;
import com.cboe.interfaces.presentation.product.SessionProductClass;

/**
 * Created by IntelliJ IDEA.
 * User: Davisson
 * Date: Mar 5, 2003
 * Time: 3:41:49 PM
 * To change this template use Options | File Templates.
 */
public interface CMINBBOConsumerCache extends CallbackConsumerCache
{
    public CMINBBOConsumer getNBBOConsumer(SessionKeyWrapper key);
    public CMINBBOConsumer getNBBOConsumer(SessionProductClass productClass);
    public CMINBBOConsumer getNBBOConsumer(String sessionName, int classKey);
}
