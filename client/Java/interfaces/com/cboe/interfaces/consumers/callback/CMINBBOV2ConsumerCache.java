package com.cboe.interfaces.consumers.callback;

import com.cboe.idl.cmiCallbackV2.CMINBBOConsumer;
import com.cboe.interfaces.domain.SessionKeyWrapper;
import com.cboe.interfaces.presentation.product.SessionProductClass;

/**
 * Created by IntelliJ IDEA.
 * User: HallB
 * Date: Apr 4, 2003
 * Time: 8:12:32 AM
 * To change this template use Options | File Templates.
 */
public interface CMINBBOV2ConsumerCache extends CallbackConsumerCache
{
    public CMINBBOConsumer getNBBOConsumer(SessionKeyWrapper key);
    public CMINBBOConsumer getNBBOConsumer(SessionProductClass productClass);
    public CMINBBOConsumer getNBBOConsumer(String sessionName, int classKey);
}
