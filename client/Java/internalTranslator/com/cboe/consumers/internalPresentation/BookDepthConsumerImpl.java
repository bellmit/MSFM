package com.cboe.consumers.internalPresentation;

import com.cboe.idl.cmiCallback.*;
import com.cboe.idl.cmiProduct.*;
import com.cboe.idl.cmiMarketData.*;
import com.cboe.util.*;
import com.cboe.util.event.*;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.interfaces.internalCallback.*;
import com.cboe.domain.util.SessionKeyContainer;

/**
 * This is the implementation of the CMIOrderBookConsumer callback object which
 * receives book depth data from a callback supplier on the CAS and publishes it
 * on a designated event channel.
 *
 * @author William Wei
 * @version 12/03/2001
 */

public class BookDepthConsumerImpl extends com.cboe.consumers.callback.BookDepthConsumerImpl implements BookDepthConsumer
{
    private EventChannelAdapter eventChannel = null;

    public BookDepthConsumerImpl(EventChannelAdapter eventChannel)
    {
        super(eventChannel);
    }

    /**
     * The callback method used by the CAS to publish product book depth data.
     *
     * @param bookDepth the book depth data to publish to all subscribed listeners
     */
    public void acceptBookDepth(int [] groups, BookDepthStruct bookDepth)
    {
    ChannelKey key = null;
        ChannelEvent event = null;

        key = new ChannelKey(ChannelType.CB_BOOK_DEPTH_BY_PRODUCT, new SessionKeyContainer(bookDepth.sessionName, bookDepth.productKeys.productKey));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, bookDepth);
        eventChannel.dispatch(event);
    }
}
