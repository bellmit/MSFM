//
// ------------------------------------------------------------------------
// FILE: OrderBookUpdateV2ConsumerImpl.java
//
// PACKAGE: com.cboe.consumers.callback
//
// ------------------------------------------------------------------------
// Copyright (c) 1999-2003 The Chicago Board Options Exchange.  All Rights Reserved.
//
// ------------------------------------------------------------------------
//
package com.cboe.consumers.callback;

import com.cboe.interfaces.callback.OrderBookUpdateV2Consumer;
import com.cboe.util.event.*;
import com.cboe.idl.cmiMarketData.BookDepthUpdateStruct;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;

public class OrderBookUpdateV2ConsumerImpl implements OrderBookUpdateV2Consumer
{
    public static final int LOG_COUNT = 100;
    private EventChannelAdapter eventChannel;
    protected int count;

    public OrderBookUpdateV2ConsumerImpl(EventChannelAdapter eventChannel)
    {
        super();
        this.eventChannel = eventChannel;
        this.count = 0 ;
    }

    public void acceptBookDepthUpdate(BookDepthUpdateStruct[] bookDepthUpdate, int queueDepth, short queueAction)
    {
//        ChannelKey key = null;
//        ChannelEvent event = null;

        for (int i=0; i<bookDepthUpdate.length; i++)
        {
//            key = new ChannelKey(ChannelType.CB_BOOK_DEPTH_UPDATE_BY_PRODUCT , new SessionKeyContainer(bookDepthUpdate[i].sessionName, bookDepthUpdate[i].productKeys.productKey));
//            event = EventChannelAdapterFactory.find().getChannelEvent(this, key, bookDepthUpdate);
//            eventChannel.dispatch(event);
//
//            key = new ChannelKey(ChannelType.CB_BOOK_DEPTH_UPDATE_BY_PRODUCT, new SessionKeyContainer(bookDepthUpdate[i].sessionName, bookDepthUpdate[i].productKeys.classKey));
//            event = EventChannelAdapterFactory.find().getChannelEvent(this, key, bookDepthUpdate);
//            eventChannel.dispatch(event);

            this.count++;
            if(GUILoggerHome.find().isDebugOn() && this.count % LOG_COUNT == 0 )
            {
                String item = bookDepthUpdate[i].sessionName + "."+ bookDepthUpdate[i].productKeys.productKey;
                GUILoggerHome.find().debug(this.getClass().getName() + ".acceptBookDepthUpdate()-V2 Count for "+item+" ",
                                           GUILoggerBusinessProperty.COMMON,String.valueOf(this.count));
            }
        }
    }
}
