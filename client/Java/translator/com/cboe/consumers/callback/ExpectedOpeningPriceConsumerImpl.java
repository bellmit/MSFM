package com.cboe.consumers.callback;

import com.cboe.interfaces.callback.*;
import com.cboe.idl.cmiMarketData.*;
import com.cboe.util.*;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.event.*;
import com.cboe.domain.util.SessionKeyContainer;

import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;
/**
 * This is the implementation of the CMIExpectedOpeningPrice callback object which
 * receives opening prive data from a callback supplier on the CAS and publishes it
 * on a designated event channel.
 *
 * @author Derek T. Chambers-Boucher
 * @version 06/07/1999
 */

public class ExpectedOpeningPriceConsumerImpl implements ExpectedOpeningPriceConsumer
{
    public static final int LOG_COUNT = 100;
    private EventChannelAdapter eventChannel = null;
    protected int count;

    /**
     * ExpectedOpeningPriceConsumerImpl constructor.
     *
     * @author Derek T. Chambers-Boucher
     *
     * @param channelType the channel type to publish on.
     * @param eventChannel the event channel to publish to.
     */
    public ExpectedOpeningPriceConsumerImpl(EventChannelAdapter eventChannel)
    {
        super();

        this.eventChannel = eventChannel;
        this.count = 0;
    }

    /**
     * The callback method used by the CAS to publish expected opening price.
     *
     * @author Derek T. Chambers-Boucher
     *
     * @param expectedOpeningPrice the expected opening price data to publish to all subscribed listeners
     */
    public void acceptExpectedOpeningPrice(ExpectedOpeningPriceStruct expectedOpeningPrice)
    {
//GUILoggerHome.find().information("Opening Price in consumer*********",expectedOpeningPrice);
    ChannelKey key = new ChannelKey(ChannelType.CB_EXPECTED_OPENING_PRICE, new SessionKeyContainer(expectedOpeningPrice.sessionName, expectedOpeningPrice.productKeys.classKey));
        ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, key, expectedOpeningPrice);
        eventChannel.dispatch(event);

    key = new ChannelKey(ChannelType.CB_EXPECTED_OPENING_PRICE_BY_PRODUCT, new SessionKeyContainer(expectedOpeningPrice.sessionName, expectedOpeningPrice.productKeys.productKey));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, expectedOpeningPrice);
        eventChannel.dispatch(event);

        this.count++;
        if(GUILoggerHome.find().isDebugOn() && this.count % LOG_COUNT == 0 )
        {
            String item = expectedOpeningPrice.sessionName + "."+ expectedOpeningPrice.productKeys.productKey;
            GUILoggerHome.find().debug(this.getClass().getName() + ".acceptExpectedOpeningPrrice() Count for "+item+" ",
                                   GUILoggerBusinessProperty.COMMON,String.valueOf(this.count));
        }

    }
}
