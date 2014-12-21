// -----------------------------------------------------------------------------------
// Source file: AuctionV3ConsumerDelegate
//
// PACKAGE: callback
// 
// Created: Sep 13, 2004 11:44:00 AM
// -----------------------------------------------------------------------------------
// Copyright (c) 2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.delegates.callback;

import com.cboe.idl.cmiCallbackV3.POA_CMIAuctionConsumer_tie;
import com.cboe.interfaces.callback.AuctionV3Consumer;

public class AuctionV3ConsumerDelegate extends POA_CMIAuctionConsumer_tie
{
    public AuctionV3ConsumerDelegate(AuctionV3Consumer delegate)
    {
        super(delegate);
    }
}

