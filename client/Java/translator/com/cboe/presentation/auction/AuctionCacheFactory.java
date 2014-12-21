// -----------------------------------------------------------------------------------
// Source file: AuctionCacheFactory
//
// PACKAGE: com.cboe.presentation.test.other
// 
// Created: Aug 30, 2004 11:00:51 AM
// -----------------------------------------------------------------------------------
// Copyright (c) 2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.auction;

import java.util.Map;
import java.util.HashMap;

import com.cboe.interfaces.presentation.auction.AuctionCache;
import com.cboe.util.ChannelType;
import com.cboe.util.ChannelKey;
import com.cboe.util.event.EventChannelAdapterFactory;

public abstract class AuctionCacheFactory
{
    private static Map sessions;

    private AuctionCacheFactory() { }

    private static void registerAuctionCache(String sessionName, AuctionCache auctionCache)
    {
        ChannelKey key = new ChannelKey(ChannelType.AUCTION, sessionName);
        EventChannelAdapterFactory.find().setDynamicChannels(true);
        EventChannelAdapterFactory.find().addChannelListener(auctionCache, auctionCache, key);
    }

    private static synchronized Map getSessions()
    {
        if (sessions == null)
        {
            sessions = new HashMap();
        }

        return sessions;
    }

    private static synchronized AuctionCache getAuctionCache(String sessionName)
    {
        Map sessions = getSessions();
        AuctionCache theAuctionCache = (AuctionCache) sessions.get(sessionName);
        if (theAuctionCache == null)
        {
            theAuctionCache = new AuctionCacheImpl();
            sessions.put(sessionName, theAuctionCache);
            registerAuctionCache(sessionName, theAuctionCache);
        }
        
        return theAuctionCache;
    }

    public static AuctionCache create(String sessionName)
    {
        return getAuctionCache(sessionName);
    }

    public static AuctionCache find(String sessionName)
    {
        return create(sessionName);
    }
}
