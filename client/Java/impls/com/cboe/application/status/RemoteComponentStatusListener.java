package com.cboe.application.status;

import com.cboe.util.channel.ChannelListener;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.event.EventChannelAdapterFactory;
import com.cboe.util.event.EventChannelListener;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.exceptions.*;
import com.cboe.interfaces.application.RemoteComponentStatusCollector;

public class RemoteComponentStatusListener implements EventChannelListener
{
    protected RemoteComponentStatusCollector statusManager;

    public RemoteComponentStatusListener(RemoteComponentStatusCollector statusManager)
    {
        this.statusManager = statusManager;
        subscribeRemoteComponentStatus();
    }

    protected void subscribeRemoteComponentStatus()
    {
        Integer zero = 0;
        ChannelKey channelKey = new ChannelKey(ChannelType.CB_COMPONENT_UP, zero);
        EventChannelAdapterFactory.find().addChannelListener(this, this, channelKey);

        channelKey = new ChannelKey(ChannelType.CB_COMPONENT_DOWN, zero);
        EventChannelAdapterFactory.find().addChannelListener(this, this, channelKey);
    }

    private synchronized void acceptRemoteComponentIsMaster(String componentName)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        StringBuilder master = new StringBuilder(componentName.length()+40);
        master.append("received remote component:").append(componentName).append(" is master event.");
        Log.notification(master.toString());
        statusManager.acceptRemoteComponentStatusUp();
    }

    protected synchronized void acceptRemoteComponentsFailed(String componentName)
    {
        StringBuilder down = new StringBuilder(componentName.length()+40);
        down.append("received remote component:").append(componentName).append(" down event.");
        Log.notification(down.toString());
        statusManager.acceptRemoteComponentStatusDown();
    }

    public void channelUpdate(ChannelEvent event)
    {
        ChannelKey key = (ChannelKey) event.getChannel();
        try{
            switch(key.channelType)
            {
                case ChannelType.CB_COMPONENT_UP:
                    acceptRemoteComponentIsMaster((String)event.getEventData());
                    break;
                case ChannelType.CB_COMPONENT_DOWN:
                    acceptRemoteComponentsFailed((String)event.getEventData());
                    break;
                default:
                    Log.alarm("unknown channel type:"+key.channelType);
            }
        }
        catch(Exception e)
        {
            Log.exception(e);
        }
    }
}
