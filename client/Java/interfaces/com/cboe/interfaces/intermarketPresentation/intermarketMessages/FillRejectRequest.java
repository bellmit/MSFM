//
// ------------------------------------------------------------------------
// FILE: FillRejectRequest.java
// 
// PACKAGE: com.cboe.interfaces.intermarketPresentation.intermarketMessages
// 
// ------------------------------------------------------------------------
// Copyright (c) 1999-2003 The Chicago Board Options Exchange.  All Rights Reserved.
// 
// ------------------------------------------------------------------------
//
package com.cboe.interfaces.intermarketPresentation.intermarketMessages;

import com.cboe.idl.cmiIntermarketMessages.FillRejectRequestStruct;
import com.cboe.interfaces.presentation.order.OrderId;
import com.cboe.interfaces.domain.Price;

public interface FillRejectRequest
{
    OrderId                 getOrderId();
    Short                   getRejectReason();
    String                  getExtensions();
    Integer                 getTradedQuantity();
    Price                   getTradePrice();
    String                  getAwayExchangeExecutionId();
    String                  getExecutionReceiptTime();

    void                    setOrderId(OrderId orderId);
    void                    setRejectReason(Short reason);
    void                    setExtensions(String extensions);
    void                    setTradedQuantity(Integer tradedQuantity);
    void                    setTradePrice(Price tradePrice);
    void                    setAwayExchangeExecutionId(String id);
    void                    setExecutionReceiptTime(String executionReceiptTime);

    FillRejectRequestStruct getStruct();
}
