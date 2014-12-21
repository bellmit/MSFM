// -----------------------------------------------------------------------------------
// Source file: AuctionV3ConsumerImpl
//
// PACKAGE: com.cboe.consumers.callback
// 
// Created: Sep 9, 2004 4:06:39 PM
// -----------------------------------------------------------------------------------
// Copyright (c) 2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.consumers.callback;

import com.cboe.idl.cmiOrder.AuctionStruct;
import com.cboe.util.event.EventChannelAdapter;
import com.cboe.util.event.EventChannelAdapterFactory;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.ChannelType;
import com.cboe.util.ChannelKey;
import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.interfaces.callback.AuctionConsumer;

public class AuctionConsumerImpl implements AuctionConsumer
{
    private EventChannelAdapter eventChannel;

    public AuctionConsumerImpl(EventChannelAdapter eventChannel)
    {
        super();
        this.eventChannel = eventChannel;
    }

    public void acceptAuction(AuctionStruct auctionStruct)
    {
        if(GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.AUCTION))
        {
            String item = auctionStruct.sessionName + "." + auctionStruct.auctionId + "." + auctionStruct.classKey + "." + auctionStruct.productKey;
            GUILoggerHome.find().debug(this.getClass().getName() + ".acceptAuction() for "+ item + " ",
                                       GUILoggerBusinessProperty.AUCTION, auctionStruct);
        }

        ChannelKey key = new ChannelKey(ChannelType.AUCTION, auctionStruct.sessionName);
        ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, key, auctionStruct);
        eventChannel.dispatch(event);
    }
}
