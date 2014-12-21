package com.cboe.application.status;

import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.event.EventChannelAdapterFactory;
import com.cboe.util.event.EventChannelListener;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.exceptions.*;
import com.cboe.interfaces.application.LocalComponentStatusCollector;

public class LocalComponentStatusListener implements EventChannelListener
{
    LocalComponentStatusCollector statusManager;
    public LocalComponentStatusListener(LocalComponentStatusCollector manager)
    {
        statusManager = manager;
        subscribeCASStatus();
    }

    protected void subscribeCASStatus()
    {
        Integer zero = 0;
        ChannelKey channelKey = new ChannelKey(ChannelType.CB_CAS_UP, zero);
        EventChannelAdapterFactory.find().addChannelListener(this, this, channelKey);

        channelKey = new ChannelKey(ChannelType.CB_CAS_DOWN, zero);
        EventChannelAdapterFactory.find().addChannelListener(this, this, channelKey);
    }

    protected synchronized void acceptCASUpStatus(String componentName)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        StringBuilder up = new StringBuilder(componentName.length()+40);
        up.append("received local component:").append(componentName).append(" up event.");
        Log.notification(up.toString());
        statusManager.acceptLocalComponentStatusUp();
    }

    protected synchronized void acceptCASDownStatus(String componentName)
    {
        StringBuilder down = new StringBuilder(componentName.length()+40);
        down.append("received local component:").append(componentName).append(" down event.");
        Log.notification(down.toString());
        statusManager.acceptLocalComponentStatusDown();
    }

    public void channelUpdate(ChannelEvent event)
    {
        ChannelKey key = (ChannelKey) event.getChannel();
        try {
            switch (key.channelType)
            {
                case ChannelType.CB_CAS_UP:
                    acceptCASUpStatus((String)event.getEventData());
                    break;
                case ChannelType.CB_CAS_DOWN:
                    acceptCASDownStatus((String)event.getEventData());
                    break;
                default:
                    Log.alarm("Unknown event type:"+key.channelType);
            }
        }
        catch (Exception e)
        {
            Log.exception(e);
        }

    }

}
