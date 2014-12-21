// -----------------------------------------------------------------------------------
// Source file: AuctionSubscriptionResultFormatter
//
// PACKAGE: com.cboe.presentation.common.formatters
// 
// Created: Dec 22, 2004 3:29:10 PM
// -----------------------------------------------------------------------------------
// Copyright (c) 2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------

package com.cboe.presentation.common.formatters;

import com.cboe.interfaces.presentation.common.formatters.AuctionSubscriptionResultFormatStrategy;
import com.cboe.interfaces.presentation.common.formatters.OperationResultFormatStrategy;
import com.cboe.idl.cmiOrder.AuctionSubscriptionResultStruct;

public class AuctionSubscriptionResultFormatter extends Formatter implements AuctionSubscriptionResultFormatStrategy
{
    protected static final String AUCTIONTYPE_TAG_STRING = "AuctionType";

    public AuctionSubscriptionResultFormatter()
    {
        super();

        addStyle(FULL_SUBSCRIPTION_RESULT, FULL_SUBSCRIPTION_RESULT_DESCRIPTION);
        setDefaultStyle(FULL_SUBSCRIPTION_RESULT);
    }

    /**
     * Defines a method for formatting Auction Subscription Result Struct using default style
     *
     * @param result AuctionSubscriptionResultStruct to format
     *
     * @return formatted string
     */
    public String format(AuctionSubscriptionResultStruct result)
    {
        return format(result, getDefaultStyle());
    }

    /**
     * Defines a method for formatting Auction Subscription Result Struct
     *
     * @param result AuctionSubscriptionResultStruct to format
     * @param style  to use
     *
     * @return formatted string
     */
    public String format(AuctionSubscriptionResultStruct result, String style)
    {
        validateStyle(style);
        if(style.equals(FULL_SUBSCRIPTION_RESULT))
        {
            return formatFullAuctionSubscriptionResult(result);
        }

        return "";
    }

    private String formatFullAuctionSubscriptionResult(AuctionSubscriptionResultStruct result)
    {
        StringBuffer buffer = new StringBuffer(100);
        OperationResultFormatStrategy operationResultFormatStrategy = FormatFactory.getOperationResultFormatStrategy();

        buffer.append(AUCTIONTYPE_TAG_STRING);
        buffer.append(": ");
        buffer.append(AuctionTypes.toString(result.auctionType));
        buffer.append(" ");
        buffer.append(operationResultFormatStrategy.format(result.subscriptionResult));

        return buffer.toString();
    }
}
