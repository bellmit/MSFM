package com.cboe.application.supplier;

import com.cboe.domain.supplier.BaseSupplier;
import com.cboe.application.supplier.proxy.BookDepthCollectorProxy;
/**
 * BookDepthCollectorSupplier extends the ChannelAdapter framework to provide a multithreaded
 * multichanneled event dispatcher functionality to the CAS callback supplier.
 *
 * It is important to note that the channel key can be any hashable object.
 *
 * @author William Wei
 */

public class BookDepthCollectorSupplier extends BaseSupplier
{
    public String getListenerClassName()
    {
        return BookDepthCollectorProxy.class.getName();
    }
}
