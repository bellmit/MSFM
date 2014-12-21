// -----------------------------------------------------------------------------------
// Source file: AuctionCacheImpl
//
// PACKAGE: com.cboe.presentation.test.other
// 
// Created: Aug 27, 2004 3:38:10 PM
// -----------------------------------------------------------------------------------
// Copyright (c) 2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.auction;

import java.util.Map;
import java.util.HashMap;

import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;
import com.cboe.presentation.api.TraderAPIImpl;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.event.EventChannelAdapterFactory;
import com.cboe.idl.cmiOrder.AuctionStruct;
import com.cboe.interfaces.presentation.auction.AuctionCache;
import com.cboe.interfaces.presentation.auction.Auction;
import com.cboe.interfaces.presentation.util.CBOEId;

public class AuctionCacheImpl implements AuctionCache
{
    protected Map auctionByClass;             // key - ClassKey   Value - Map -> (AuctionId, Auction)
    protected Map auctionByProduct;           // Key - ProductKey Value - Map -> (AuctionId, Auction)
    protected Map auctionByAuctionId;         // Key - AuctionId  Value - Auction
    protected Map auctionByClassAndType;      // key - ClassKey + AuctionType Value - Map -> (AuctionId, Auction)

    public AuctionCacheImpl()
    {
        auctionByClass        = new HashMap();
        auctionByProduct      = new HashMap();
        auctionByAuctionId    = new HashMap();
        auctionByClassAndType = new HashMap();
    }

    public synchronized Auction addAuction(AuctionStruct struct)
    {
        Auction auction =  AuctionFactory.create(struct);

        addAuctionByClass(auction);
        addAuctionByProduct(auction);
        addAuctionByAuctionId(auction);
        addAuctionByClassAndType(auction);

        return auction;
    }

    public synchronized void removeAuction(Auction auction)
    {
        removeAuctionByClass(auction);
        removeAuctionByProduct(auction);
        removeAuctionByAuctionId(auction);
        removeAuctionByClassAndType(auction);
    }

    public synchronized Auction[] getAuctionForClass(int classKey)
    {
        Map auctionsMap = (Map)auctionByClass.get(new Integer(classKey));
        if (auctionsMap != null)
        {
            Auction[] auctions = new Auction[auctionsMap.size()];
            return (Auction[]) auctionsMap.values().toArray(auctions);
        }

        return null;
    }

    public synchronized Auction[] getAuctionForClassAndType(int classKey, short auctionType)
    {
        String key = String.valueOf(classKey) + String.valueOf(auctionType);
        Map auctionsMap = (Map)auctionByClassAndType.get(key);

        if (auctionsMap != null)
        {
            Auction[] auctions = new Auction[auctionsMap.size()];
            return (Auction[]) auctionsMap.values().toArray(auctions);
        }

        return null;
    }

    public synchronized Auction[] getAuctionForProduct(int productKey)
    {
        Map auctionsMap = (Map)auctionByProduct.get(new Integer(productKey));
        if (auctionsMap != null)
        {
            Auction[] auctions = new Auction[auctionsMap.size()];
            return (Auction[]) auctionsMap.values().toArray(auctions);
        }

        return null;
    }

    public synchronized Auction[] getAllAuctions()
    {
        Auction[] auctionArray = new Auction[auctionByAuctionId.size()];
        return (Auction[])auctionByAuctionId.values().toArray(auctionArray);
    }

    public synchronized Auction getAuctionForAuctionId(CBOEId id)
    {
        return  (Auction) auctionByAuctionId.get(id);
    }

    public boolean doAuctionsExist()
    {
        return (getAuctionCount() > 0);
    }

    public synchronized int getAuctionCount()
    {
        return (auctionByAuctionId != null) ? auctionByAuctionId.size() : 0;
    }

    public void channelUpdate(ChannelEvent event)
    {
        if(GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.AUCTION))
        {
            GUILoggerHome.find().debug(TraderAPIImpl.TRANSLATOR_NAME + ":" + getClass().getName() +".channelUpdate",
                                       GUILoggerBusinessProperty.AUCTION, event);
        }

        try
        {
            ChannelKey channel = (ChannelKey)event.getChannel();
            if (channel.channelType == ChannelType.AUCTION)
            {
                AuctionStruct auction = (AuctionStruct)event.getEventData();
                dispatch(addAuction(auction));
            }
        }
        catch (Exception e)
        {
            GUILoggerHome.find().exception(getClass().getName() + ".channelUpdate" , e);
        }
    }

    protected void addAuctionByAuctionId(Auction auction)
    {
        auctionByAuctionId.put(auction.getAuctionId(), auction);
    }

    protected void addAuctionByProduct(Auction auction)
    {
        Map auctionMap = (Map)auctionByProduct.get(auction.getProductKey());
        if (auctionMap == null)
        {
            auctionMap = new HashMap();
            auctionByProduct.put(auction.getProductKey(), auctionMap);
        }

        auctionMap.put(auction.getAuctionId(), auction);
    }

    protected void addAuctionByClass(Auction auction)
    {
        Map auctionsMap = (Map)auctionByClass.get(auction.getClassKey());
        if (auctionsMap == null)
        {
            auctionsMap = new HashMap();
            auctionByClass.put(auction.getClassKey(), auctionsMap);
        }

        auctionsMap.put(auction.getAuctionId(), auction);
    }

    protected void addAuctionByClassAndType(Auction auction)
    {
        String key = auction.getClassKey() + Short.toString(auction.getAuctionType());

        Map auctionsMap = (Map)auctionByClassAndType.get(key);
        if (auctionsMap == null)
        {
            auctionsMap = new HashMap();
            auctionByClassAndType.put(key, auctionsMap);
        }

        auctionsMap.put(auction.getAuctionId(), auction);
    }

    protected void removeAuctionByClassAndType(Auction auction)
    {
        String key = auction.getClassKey() + Short.toString(auction.getAuctionType());

        Map auctionsMap = (Map)auctionByClassAndType.get(key);
        if (auctionsMap != null)
        {
            auctionsMap.remove(auction.getAuctionId());
        }
    }

    protected void removeAuctionByClass(Auction auction)
    {
        Map auctionsMap = (Map)auctionByClass.get(auction.getClassKey());
        if (auctionsMap != null)
        {
            auctionsMap.remove(auction.getAuctionId());
        }
    }

    protected void removeAuctionByProduct(Auction auction)
    {
        Map auctionsMap = (Map)auctionByProduct.get(auction.getProductKey());
        if (auctionsMap != null)
        {
            auctionsMap.remove(auction.getAuctionId());
        }
    }

    protected void removeAuctionByAuctionId(Auction auction)
    {
        auctionByAuctionId.remove(auction.getAuctionId());
    }

    protected void dispatch(Auction auction)
    {
        // publish update event on the channel
        if (auction != null)
        {
            ChannelKey channelKey = new ChannelKey(ChannelType.CB_AUCTION,
                                                   new AuctionTypeContainer(auction.getSessionName(),
                                                                            auction.getClassKey().intValue(),
                                                                            auction.getAuctionType()));

            ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, channelKey, auction);
            EventChannelAdapterFactory.find().dispatch(event);

            channelKey = new ChannelKey(ChannelType.CB_AUCTION, auction.getSessionName());
            event = EventChannelAdapterFactory.find().getChannelEvent(this, channelKey, auction);
            EventChannelAdapterFactory.find().dispatch(event);

            channelKey = new ChannelKey(ChannelType.CB_AUCTION, auction.getAuctionId());
            event = EventChannelAdapterFactory.find().getChannelEvent(this, channelKey, auction);
            EventChannelAdapterFactory.find().dispatch(event);
        }
    }

}
