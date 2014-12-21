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
 * This is the implementation of the CMITickerConsumer callback object which
 * receives  market ticker data from a callback supplier on the CAS and publishes it
 * on a designated event channel.
 *
 * @author Keith A. Korecky
 * @version 06/29/1999
 */

public class TickerConsumerImpl implements TickerConsumer
{
	private int tickerCount;
    public final static int LOG_COUNT = 100;

    private EventChannelAdapter eventChannel = null;

    /**
	 * TickerConsumerImpl constructor.
	 *
	 * @author Keith A. Korecky
	 *
	 * @param channelType the channel type to publish on.
	 * @param eventChannel the event channel to publish to.
	 */
    public TickerConsumerImpl(EventChannelAdapter eventChannel)
    {
        super();

        this.eventChannel = eventChannel;
    }

    /**
	 * The callback method used by the CAS to publish  market ticker data.
	 *
	 * @author Keith A. Korecky
	 *
	 * @param Ticker the  market ticker data to publish to all subscribed listeners
	 */
    public void acceptTicker(TickerStruct[] ticker)
    {
        ChannelKey key = null;
        ChannelEvent event = null;

        for (int i = 0; i < ticker.length; i++)
        {
            key = new ChannelKey(ChannelType.CB_TICKER, new SessionKeyContainer(ticker[i].sessionName, ticker[i].productKeys.productKey));
            event = EventChannelAdapterFactory.find().getChannelEvent(this, key, ticker[i]);
            eventChannel.dispatch(event);

            this.tickerCount++;
            if(GUILoggerHome.find().isDebugOn() && this.tickerCount % LOG_COUNT == 0 )
            {
                String item = ticker[i].sessionName + "."+ ticker[i].productKeys.productKey;
                GUILoggerHome.find().debug(this.getClass().getName() + ".acceptTicker() Count for "+item+" at time t="
                    + System.currentTimeMillis() + "ms  ",
                    GUILoggerBusinessProperty.COMMON,String.valueOf(this.tickerCount));
            }
        }
    }
}
