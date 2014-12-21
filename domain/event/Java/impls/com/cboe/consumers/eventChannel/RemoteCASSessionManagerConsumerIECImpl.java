package com.cboe.consumers.eventChannel;

import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.events.RemoteCASSessionManagerConsumer;
import com.cboe.util.event.EventChannelAdapter;
import com.cboe.util.event.EventChannelAdapterFactory;
import com.cboe.util.ChannelKey;
import com.cboe.util.channel.ChannelEvent;


public class RemoteCASSessionManagerConsumerIECImpl extends BObject implements RemoteCASSessionManagerConsumer
{
    private EventChannelAdapter internalEventChannel;

    public RemoteCASSessionManagerConsumerIECImpl()
    {
        internalEventChannel = EventChannelAdapterFactory.find();
    }

    public void logout(String casOrigin, String userSessionIOR, String userId)
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "Consume/IEC Publish logout" +
                    "; casOrigin= " + casOrigin +
                    "; userId=" + userId + ".");
        }

        ChannelKey channelKey = new ChannelKey(ChannelKey.MDCAS_LOGOUT, userSessionIOR);
        ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, channelKey, userId);
        internalEventChannel.dispatch(event);
    }
}
