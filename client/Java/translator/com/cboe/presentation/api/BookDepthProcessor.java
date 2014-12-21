//
// -----------------------------------------------------------------------------------
// Source file: BookDepthProcessor.java
//
// PACKAGE: com.cboe.presentation.api;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2001 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.api;


import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.event.EventChannelListener;
import com.cboe.util.event.EventChannelAdapterFactory;
import com.cboe.util.ChannelType;
import com.cboe.util.ChannelKey;

import com.cboe.idl.cmiMarketData.BookDepthStruct;

import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;

public class BookDepthProcessor implements EventChannelListener 
{

    private BookDepthCache bookDepthCache = null;
    private String sessionName;

    public BookDepthProcessor (String sessionName) 
    {
        bookDepthCache = BookDepthCacheFactory.find(sessionName);
    }

    public void subscribeForBookDepthEvents(String sessionName) 
    {
        ChannelKey channelKey;

        channelKey = new ChannelKey(ChannelType.CB_BOOK_DEPTH_BY_PRODUCT, sessionName);
        EventChannelAdapterFactory.find().addChannelListener(this, this, channelKey);

        channelKey = new ChannelKey(ChannelType.CB_BOOK_DEPTH_BY_PRODUCT_V2, sessionName);
        EventChannelAdapterFactory.find().addChannelListener(this, this, channelKey);
    }

    public void channelUpdate(ChannelEvent event) 
    {
        int channelType = ((ChannelKey)event.getChannel()).channelType;
        Object eventData = event.getEventData();
        switch(channelType)
        {
            case ChannelType.CB_BOOK_DEPTH_BY_PRODUCT:
                 BookDepthStruct bookDepth = (BookDepthStruct)eventData;
                 if ( GUILoggerHome.find().isDebugOn() ) {
                     GUILoggerHome.find().debug("BookDepthProcessor (translator) calling channelUpdate", GUILoggerBusinessProperty.PRODUCT_QUERY,
                                "book cache for productKey = " + bookDepth.productKeys.productKey);
                 }
                 bookDepthCache.addBookDepth(bookDepth);
                 break;
            case ChannelType.CB_BOOK_DEPTH_BY_PRODUCT_V2:
                 BookDepthStruct[] bookDepthArray = (BookDepthStruct[])eventData;
                 for (int i=0;i<bookDepthArray.length;i++)
                 {
                     if ( GUILoggerHome.find().isDebugOn() ) 
                     {
                         GUILoggerHome.find().debug("BookDepthProcessor (translator) calling channelUpdate", GUILoggerBusinessProperty.PRODUCT_QUERY,
                                    "book cache for productKey = " + bookDepthArray[i].productKeys.productKey);
                     }
                     bookDepthCache.addBookDepth(bookDepthArray[i]);
                 }
                 break;
            default:
        }
    }
}
