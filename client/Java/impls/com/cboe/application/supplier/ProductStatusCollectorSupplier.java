package com.cboe.application.supplier;

import com.cboe.application.supplier.proxy.ProductStatusCollectorProxy;
import com.cboe.domain.instrumentedChannel.supplier.InstrumentedBaseSupplier;

/**
 * QuoteStatusSupplier extends the ChannelAdapter framework to provide a multithreaded
 * multichanneled event dispatcher functionality to the CAS callback supplier.
 *
 * It is important to note that the channel key can be any hashable object.
 *
 * @author Derek T. Chambers-Boucher
 * @version 04/20/1999
 */

public class ProductStatusCollectorSupplier extends InstrumentedBaseSupplier
{
    public String getListenerClassName()
    {
        return ProductStatusCollectorProxy.class.getName();
    }
}
