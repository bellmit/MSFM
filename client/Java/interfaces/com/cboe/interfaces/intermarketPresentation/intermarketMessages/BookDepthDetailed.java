//
// ------------------------------------------------------------------------
// FILE: BookDepthDetailed.java
// 
// PACKAGE: com.cboe.interfaces.intermarketPresentation.intermarketMessages
// 
// ------------------------------------------------------------------------
// Copyright (c) 1999-2003 The Chicago Board Options Exchange.  All Rights Reserved.
// 
// ------------------------------------------------------------------------
//
package com.cboe.interfaces.intermarketPresentation.intermarketMessages;

import com.cboe.interfaces.presentation.product.ProductKeys;
import com.cboe.idl.cmiIntermarketMessages.BookDepthDetailedStruct;

public interface BookDepthDetailed
{
    ProductKeys getProductKeys();
    String getSessionName();
    int getTransactionSequenceNumber();
    OrderBookDetailPrice[] getBuyOrdersAtDifferentPrice();
    OrderBookDetailPrice[] getSellOrdersAtDifferentPrice();

    BookDepthDetailedStruct toStruct();
}
