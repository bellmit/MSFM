// -----------------------------------------------------------------------------------
// Source file: AuctionFactory
//
// PACKAGE: com.cboe.presentation.auction
// 
// Created: Sep 9, 2004 2:37:29 PM
// -----------------------------------------------------------------------------------
// Copyright (c) 2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.auction;

import com.cboe.interfaces.presentation.auction.Auction;
import com.cboe.idl.cmiOrder.AuctionStruct;

public abstract class AuctionFactory
{
    public static Auction create(AuctionStruct struct)
    {
        if (struct == null)
        {
            throw new IllegalArgumentException("AuctionStruct cannot be null");
        }

        return new AuctionImpl(struct);
    }
}
