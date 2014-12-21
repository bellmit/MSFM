package com.cboe.application.supplier;

import com.cboe.application.supplier.proxy.StrategyStatusConsumerProxy;
import com.cboe.domain.instrumentedChannel.supplier.InstrumentedBaseSupplier;
/**
 * StrategyStatusSupplier extends the ChannelAdapter framework to provide a multithreaded
 * multichanneled event dispatcher functionality to the CAS callback supplier.
 *
 * It is important to note that the channel key can be any hashable object.
 *
 * @author Derek T. Chambers-Boucher
 * @version 04/20/1999
 */

public class StrategyStatusSupplier extends InstrumentedBaseSupplier
{
    public String getListenerClassName()
    {
        return StrategyStatusConsumerProxy.class.getName();
    }
}
