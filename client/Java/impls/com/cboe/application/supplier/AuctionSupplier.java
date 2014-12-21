package com.cboe.application.supplier;

import com.cboe.application.supplier.proxy.AuctionConsumerProxy;
import com.cboe.domain.instrumentedChannel.supplier.InstrumentedBaseSupplier;

/** Dispatch Auction events to the CAS callback Internal Event Channel.
 * @see com.cboe.util.channel.ChannelAdapter
 */
public class AuctionSupplier extends InstrumentedBaseSupplier
{
    public String getListenerClassName()
    {
        return AuctionConsumerProxy.class.getName();
    }
}
