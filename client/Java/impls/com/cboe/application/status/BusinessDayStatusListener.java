package com.cboe.application.status;

import com.cboe.idl.session.BusinessDayStruct;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.event.EventChannelAdapterFactory;
import com.cboe.util.event.EventChannelListener;

/**
 * @author Jing Chen
 */
public class BusinessDayStatusListener implements EventChannelListener
{
    protected AppServerStatusManagerImpl statusManager;

    public BusinessDayStatusListener(AppServerStatusManagerImpl statusManager)
    {
        this.statusManager = statusManager;
        initialize();
    }

    public void initialize()
    {
        ChannelKey channelKey = new ChannelKey(ChannelType.BUSINESS_DAY, Integer.valueOf(0));
        EventChannelAdapterFactory.find().addChannelListener(this, this, channelKey);
    }

    public void channelUpdate(ChannelEvent event)
    {
        ChannelKey key = (ChannelKey) event.getChannel();
        if(key.channelType==ChannelType.BUSINESS_DAY)
        {
            statusManager.acceptBusinessDayEvent((BusinessDayStruct)event.getEventData());
        }
        else
        {
            Log.alarm("BusinessDayStatusListener received unknown event type:"+key.channelType);
        }
    }
}
