// -----------------------------------------------------------------------------------
// Source file: AuctionSubscriptionResultFormatStrategy
//
// PACKAGE: com.cboe.interfaces.presentation.common.formatters
// 
// Created: Dec 22, 2004 1:35:45 PM
// -----------------------------------------------------------------------------------
// Copyright (c) 2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.common.formatters;

import com.cboe.idl.cmiOrder.AuctionSubscriptionResultStruct;

public interface AuctionSubscriptionResultFormatStrategy extends FormatStrategy
{
    public static final String FULL_SUBSCRIPTION_RESULT = "Full Subscription Result";

    public static final String FULL_SUBSCRIPTION_RESULT_DESCRIPTION = "Full Subscription Result Reason";

    /**
     * Defines a method for formatting Auction Subscription Result Struct using default style
     * @param result AuctionSubscriptionResultStruct to format
     * @return formatted string
     */
    public String format(AuctionSubscriptionResultStruct result);

    /**
     * Defines a method for formatting Auction Subscription Result Struct
     * @param result AuctionSubscriptionResultStruct to format
     * @param style to use
     * @return formatted string
     */
    public String format(AuctionSubscriptionResultStruct result, String style);

}
