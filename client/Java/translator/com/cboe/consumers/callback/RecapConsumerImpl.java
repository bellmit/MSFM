package com.cboe.consumers.callback;

import com.cboe.application.shared.*;
import com.cboe.interfaces.callback.*;
import com.cboe.idl.cmiMarketData.*;
import com.cboe.util.*;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.event.*;
import com.cboe.domain.util.SessionKeyContainer;

import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;

/**
 * This is the implementation of the CMIRecapConsumer callback object which
 * receives  market recap data from a callback supplier on the CAS and publishes it
 * on a designated event channel.
 *
 * @author Keith A. Korecky
 * @version 06/29/1999
 */

public class RecapConsumerImpl implements RecapConsumer
{
	private int recapCount;
    public final static int LOG_COUNT = 100;

    private EventChannelAdapter eventChannel = null;

    /**
	 * RecapConsumerImpl constructor.
	 *
	 * @author Keith A. Korecky
	 *
	 * @param channelType the channel type to publish on.
	 * @param eventChannel the event channel to publish to.
	 */
    public RecapConsumerImpl(EventChannelAdapter eventChannel)
    {
        super();

        this.eventChannel = eventChannel;
    }

    /**
	 * The callback method used by the CAS to publish  market recap data.
	 *
	 * @author Keith A. Korecky
	 *
	 * @param recap the  market recap data to publish to all subscribed listeners
	 */
    public void acceptRecap(RecapStruct[] recap)
    {
        ChannelKey key = null;
        ChannelEvent event = null;

        for (int i = 0; i < recap.length; i++)
        {
            key = new ChannelKey(ChannelType.CB_RECAP_BY_CLASS, new SessionKeyContainer(recap[i].sessionName, recap[i].productKeys.classKey));
            event = EventChannelAdapterFactory.find().getChannelEvent(this, key, recap[i]);
            eventChannel.dispatch(event);

            key = new ChannelKey(ChannelType.CB_RECAP_BY_PRODUCT, new SessionKeyContainer(recap[i].sessionName, recap[i].productKeys.productKey));
            event = EventChannelAdapterFactory.find().getChannelEvent(this, key, recap[i]);
            eventChannel.dispatch(event);

            this.recapCount++;
            if(GUILoggerHome.find().isDebugOn() && this.recapCount % LOG_COUNT == 0 )
            {
                String item = recap[i].sessionName + "."+ recap[i].productKeys.productKey;
                GUILoggerHome.find().debug(this.getClass().getName() + ".acceptRecap() Count for "+item+" at time t="
                    + System.currentTimeMillis() + "ms  ",
                    GUILoggerBusinessProperty.COMMON,String.valueOf(this.recapCount));
            }
        }
    }
}
