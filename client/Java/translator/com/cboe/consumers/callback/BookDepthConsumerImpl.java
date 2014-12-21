package com.cboe.consumers.callback;

import com.cboe.application.shared.*;
import com.cboe.interfaces.callback.*;
import com.cboe.idl.cmiMarketData.*;
import com.cboe.util.*;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.event.*;
import com.cboe.domain.util.SessionKeyContainer;

/**
 * This is the implementation of the CMIOrderBookConsumer callback object which
 * receives book depth data from a callback supplier on the CAS and publishes it
 * on a designated event channel.
 *
 * @author William Wei
 */

public class BookDepthConsumerImpl implements OrderBookConsumer
{
    private int channelType = 0;
    private EventChannelAdapter eventChannel = null;

    /**
     * BookDepthConsumerImpl constructor.
     *
     * @param eventChannel the event channel to publish to.
     */
    public BookDepthConsumerImpl(EventChannelAdapter eventChannel)
    {
        super();

        this.eventChannel = eventChannel;
    }

    /**
     * The callback method used by the CAS to publish product book depth data.
     *
     * @param bookDepth the book depth data to publish to all subscribed listeners
     */
    public void acceptBookDepth(BookDepthStruct bookDepth)
    {
        ChannelKey key = null;
        ChannelEvent event = null;

        key = new ChannelKey(ChannelType.CB_BOOK_DEPTH_BY_PRODUCT, new SessionKeyContainer(bookDepth.sessionName, bookDepth.productKeys.productKey));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, bookDepth);
        eventChannel.dispatch(event);

        key = new ChannelKey(ChannelType.CB_BOOK_DEPTH_BY_PRODUCT, bookDepth.sessionName);
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, bookDepth);
        eventChannel.dispatch(event);
    }
}
