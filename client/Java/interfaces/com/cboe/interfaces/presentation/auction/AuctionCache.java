// -----------------------------------------------------------------------------------
// Source file: AuctionCache
//
// PACKAGE: com.cboe.presentation.test.other
// 
// Created: Aug 27, 2004 3:28:42 PM
// -----------------------------------------------------------------------------------
// Copyright (c) 2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.auction;

import com.cboe.util.event.EventChannelListener;
import com.cboe.idl.cmiOrder.AuctionStruct;

public interface AuctionCache extends EventChannelListener
{
    public Auction addAuction(AuctionStruct auction);
    public void removeAuction(Auction auction);

    public Auction[] getAuctionForClass(int classKey);
    public Auction[] getAuctionForClassAndType(int classKey, short auctionType);
    public Auction[] getAuctionForProduct(int productKey);
    public Auction[] getAllAuctions();

    public boolean doAuctionsExist();
    public int getAuctionCount();
}
