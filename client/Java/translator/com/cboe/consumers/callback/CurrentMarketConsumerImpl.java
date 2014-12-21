package com.cboe.consumers.callback;

import com.cboe.application.shared.*;
import com.cboe.interfaces.callback.*;
import com.cboe.idl.cmiMarketData.*;
import com.cboe.idl.infrastructureServices.loggingService.corba.Category;
import com.cboe.util.*;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.event.*;
import com.cboe.domain.util.SessionKeyContainer;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;

/**
 * This is the implementation of the CMICurrentMarketConsumer callback object which
 * receives market best data from a callback supplier on the CAS and publishes it
 * on a designated event channel.
 *
 * @author Derek T. Chambers-Boucher
 * @version 03/16/1999
 */

public class CurrentMarketConsumerImpl implements CurrentMarketConsumer
{
    public static final int LOG_COUNT = 100;
    private EventChannelAdapter eventChannel = null;
    private int channelType = 0;
    protected int count;

    /**
     * CurrentMarketConsumerImpl constructor.
     *
     * @author Derek T. Chambers-Boucher
     *
     * @param channelType the channel type to publish on.
     * @param eventChannel the event channel to publish to.
     */
    public CurrentMarketConsumerImpl(EventChannelAdapter eventChannel)
    {
        super();

        this.eventChannel = eventChannel;
        this.count = 0;
    }

    /**
     * The callback method used by the CAS to publish product market data.
     *
     * @author Derek T. Chambers-Boucher
     *
     * @param currentMarket the currnet market data to publish to all subscribed listeners
     */
    public void acceptCurrentMarket(CurrentMarketStruct[] currentMarket)
    {
        ChannelKey key = null;
        ChannelEvent event = null;

        for (int i = 0; i < currentMarket.length; i++)
        {
            key = new ChannelKey(ChannelType.CB_CURRENT_MARKET_BY_PRODUCT, new SessionKeyContainer(currentMarket[i].sessionName, currentMarket[i].productKeys.productKey));
            event = EventChannelAdapterFactory.find().getChannelEvent(this, key, currentMarket[i]);
            eventChannel.dispatch(event);

            key = new ChannelKey(ChannelType.CB_CURRENT_MARKET_BY_CLASS, new SessionKeyContainer(currentMarket[i].sessionName, currentMarket[i].productKeys.classKey));
            event = EventChannelAdapterFactory.find().getChannelEvent(this, key, currentMarket[i]);
            eventChannel.dispatch(event);

            this.count++;
            if(GUILoggerHome.find().isDebugOn() && this.count % LOG_COUNT == 0 )
            {
                String item = currentMarket[i].sessionName + "."+ currentMarket[i].productKeys.productKey;
                GUILoggerHome.find().debug(this.getClass().getName() + ".acceptCurentMarket() Count for "+item+" ",
                                       GUILoggerBusinessProperty.COMMON,String.valueOf(this.count));
            }
        }
    }
}
