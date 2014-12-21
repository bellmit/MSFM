package com.cboe.application.supplier;

import com.cboe.application.supplier.proxy.QuoteNotificationConsumerProxy;
import com.cboe.domain.instrumentedChannel.supplier.InstrumentedBaseSupplier;
/**
 * CurentMarketSupplier extends the ChannelAdapter framework to provide a multithreaded
 * multichanneled event dispatcher functionality to the CAS callback supplier.
 *
 * It is important to note that the channel key can be any hashable object.
 *
 * @author William Wei
 * @version 06/28/1999
 */

public class QuoteNotificationSupplier extends InstrumentedBaseSupplier
{
    public String getListenerClassName()
    {
        return QuoteNotificationConsumerProxy.class.getName();
    }
}
