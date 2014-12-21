package com.cboe.consumers.eventChannel;

import com.cboe.domain.util.RemoteMarketDataSubscriptionInfoContainer;
import com.cboe.idl.util.RoutingParameterStruct;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.events.RemoteCASRecoveryConsumer;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.event.EventChannelAdapter;
import com.cboe.util.event.EventChannelAdapterFactory;


public class RemoteCASRecoveryConsumerIECImpl extends BObject implements RemoteCASRecoveryConsumer
{
    private EventChannelAdapter internalEventChannel;
    private static final Integer INT_0 = 0;

    public RemoteCASRecoveryConsumerIECImpl()
    {
        internalEventChannel = EventChannelAdapterFactory.find();
    }

    public void acceptMarketDataRecoveryForGroup(int groupKey)
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "Consume/IEC Publish acceptMarketDataRecoveryForGroup" +
                    "; groupKey= " + groupKey + ".");
        }
        ChannelKey channelKey = new ChannelKey(ChannelType.MDCAS_RECOVERY, INT_0);
        ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, channelKey, Integer.valueOf(groupKey));
        internalEventChannel.dispatch(event);
    }

    public void acceptMDXRecoveryForGroup(int mdxGroupKey)
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "Consume/IEC Publish acceptMDXRecoveryForGroup" +
                            "; groupKey= " + mdxGroupKey + ".");
        }
        ChannelKey channelKey = new ChannelKey(ChannelType.EXPRESS_CAS_RECOVERY, INT_0);
        ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, channelKey, Integer.valueOf(mdxGroupKey));
        internalEventChannel.dispatch(event);
    }
}
