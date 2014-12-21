//
// ------------------------------------------------------------------------
// FILE: OrderBookV2ConsumerImpl.java
//
// PACKAGE: com.cboe.consumers.callback
//
// ------------------------------------------------------------------------
// Copyright (c) 1999-2003 The Chicago Board Options Exchange.  All Rights Reserved.
//
// ------------------------------------------------------------------------
//
package com.cboe.consumers.callback;

import com.cboe.interfaces.callback.OrderBookV2Consumer;
import com.cboe.util.event.*;
import com.cboe.util.*;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.idl.cmiMarketData.BookDepthStruct;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;

import com.cboe.domain.util.SessionKeyContainer;

public class OrderBookV2ConsumerImpl implements OrderBookV2Consumer
{
    public static final int LOG_COUNT = 100;
    private EventChannelAdapter eventChannel;
    protected int count;

    public OrderBookV2ConsumerImpl(EventChannelAdapter eventChannel)
    {
        super();
        this.eventChannel = eventChannel;
        this.count = 0 ;
    }

    public void acceptBookDepth(BookDepthStruct[] bookDepth, int queueDepth, short queueAction)
    {
        ChannelKey key = null;
        ChannelEvent event = null;

        for (int i=0; i<bookDepth.length; i++)
        {
            key = new ChannelKey(ChannelType.CB_BOOK_DEPTH_BY_PRODUCT_V2, new SessionKeyContainer(bookDepth[i].sessionName, bookDepth[i].productKeys.productKey));
            event = EventChannelAdapterFactory.find().getChannelEvent(this, key, bookDepth);
            eventChannel.dispatch(event);

            key = new ChannelKey(ChannelType.CB_BOOK_DEPTH_BY_CLASS_V2,new SessionKeyContainer(bookDepth[i].sessionName, bookDepth[i].productKeys.classKey));
            event = EventChannelAdapterFactory.find().getChannelEvent(this, key, bookDepth);
            eventChannel.dispatch(event);

            this.count++;
            if(GUILoggerHome.find().isDebugOn() && this.count % LOG_COUNT == 0 )
            {
                String item = bookDepth[i].sessionName + "."+ bookDepth[i].productKeys.productKey;
                GUILoggerHome.find().debug(this.getClass().getName() + ".acceptBookDepth()-V2 Count for "+item+" ",
                                           GUILoggerBusinessProperty.COMMON,String.valueOf(this.count));
            }
        }
    }
}
