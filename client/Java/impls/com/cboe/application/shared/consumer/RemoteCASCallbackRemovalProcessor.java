package com.cboe.application.shared.consumer;

import com.cboe.interfaces.domain.session.CallbackDeregistrationInfo;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.RemoteCASCallbackRemovalCollector;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.event.EventChannelListener;

public class RemoteCASCallbackRemovalProcessor implements EventChannelListener
{
    private RemoteCASCallbackRemovalCollector parent;

    public void setParent(RemoteCASCallbackRemovalCollector parent)
    {
        this.parent = parent;
    }

    public RemoteCASCallbackRemovalCollector getParent()
    {
        return parent;
    }

    public void channelUpdate(ChannelEvent event)
    {
        ChannelKey channelKey = (ChannelKey) event.getChannel();
        if (channelKey.channelType == ChannelType.MDCAS_CALLBACK_REMOVAL && parent != null)
        {
            parent.acceptRemoteCASCallbackRemoval((CallbackDeregistrationInfo) event.getEventData());
        }
        else
        {
            if (Log.isDebugOn())
            {
                Log.debug("RemoteCASCallbackRemovalProcessor -> Wrong Channel : " + channelKey.channelType);
            }
        }
    }
}
