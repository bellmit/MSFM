package com.cboe.application.supplier;

import com.cboe.domain.supplier.BaseSupplier;
import com.cboe.application.supplier.proxy.TickerCollectorProxy;
/**
 * TickerCollectorSupplier extends the ChannelAdapter framework to provide a multithreaded
 * multichanneled event dispatcher functionality to the CAS callback supplier.
 *
 * It is important to note that the channel key can be any hashable object.
 *
 * @author Keith A. Korecky
 */

public class TickerCollectorSupplier extends BaseSupplier
{
    public String getListenerClassName()
    {
        return TickerCollectorProxy.class.getName();
    }
}
