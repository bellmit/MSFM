package com.cboe.consumers.callback;

import com.cboe.application.shared.*;
import com.cboe.interfaces.callback.*;
import com.cboe.idl.cmiQuote.*;
import com.cboe.util.*;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.event.*;
import com.cboe.domain.util.SessionKeyContainer;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;
/**
 * This is the implementation of the CMIRFQConsumer callback object which
 * receives request for quote data from a callback supplier on the CAS and publishes it
 * on a designated event channel.
 *
 * @author Derek T. Chambers-Boucher
 * @version 03/16/1999
 */

public class RFQConsumerImpl implements RFQConsumer
{
    public static final int LOG_COUNT = 100;
    private EventChannelAdapter eventChannel = null;
    protected int count;

    /**
     * RFQConsumerImpl constructor.
     *
     * @author Derek T. Chambers-Boucher
     *
     * @param channelType the channel type to publish on.
     * @param eventChannel the event channel to publish to.
     */
    public RFQConsumerImpl(EventChannelAdapter eventChannel)
    {
        super();

        this.eventChannel = eventChannel;
        this.count = 0;
    }

    /**
     * The callback method used by the CAS to publish request for quote data.
     *
     * @author Derek T. Chambers-Boucher
     *
     * @param rfq the request for quote data to publish to all subscribed listeners
     */
    public void acceptRFQ(RFQStruct rfq)
    {
        // keyed by classKey
        ChannelKey key = new ChannelKey(ChannelType.RFQ, rfq.sessionName);
        ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, key, rfq);
        eventChannel.dispatch(event);

        this.count++;
        if(GUILoggerHome.find().isDebugOn() && this.count % LOG_COUNT == 0 )
        {
            String item = rfq.sessionName + "."+ rfq.productKeys.productKey;
            GUILoggerHome.find().debug(this.getClass().getName() + ".acceptRFQ() Count for "+item+" ",
                                   GUILoggerBusinessProperty.COMMON,String.valueOf(this.count));
        }

    }
}
