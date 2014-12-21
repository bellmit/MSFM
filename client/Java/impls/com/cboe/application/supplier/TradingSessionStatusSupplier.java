package com.cboe.application.supplier;

import com.cboe.application.supplier.proxy.TradingSessionStatusConsumerProxy;
import com.cboe.domain.instrumentedChannel.supplier.InstrumentedBaseSupplier;
/**
 * TradingSessionStatusSupplier extends the ChannelAdapter framework to provide a multithreaded
 * multichanneled event dispatcher functionality to the CAS callback supplier.
 *
 * It is important to note that the channel key can be any hashable object.
 *
 * @author Keith A. Korecky
 * @version 06/29/1999
 */

public class TradingSessionStatusSupplier extends InstrumentedBaseSupplier
{
    public String getListenerClassName()
    {
        return TradingSessionStatusConsumerProxy.class.getName();
    }
}
