//
// ------------------------------------------------------------------------
// FILE: OrderBook.java
// 
// PACKAGE: com.cboe.interfaces.intermarketPresentation.intermarketMessages
// 
// ------------------------------------------------------------------------
// Copyright (c) 1999-2003 The Chicago Board Options Exchange.  All Rights Reserved.
// 
// ------------------------------------------------------------------------
//
package com.cboe.interfaces.intermarketPresentation.intermarketMessages;

import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.domain.dateTime.DateTime;
import com.cboe.interfaces.presentation.order.OrderContingency;
import com.cboe.interfaces.presentation.order.OrderId;
import com.cboe.interfaces.presentation.product.Product;
import com.cboe.interfaces.presentation.product.ProductClass;
import com.cboe.idl.cmiIntermarketMessages.OrderBookStruct;

public interface OrderBook
{
    OrderId getOrderId();
    int getOriginalQuantity();
    int getRemainingQuantity();
    int getClassKey();
    int getProductKey();
    short getProductType();
    char getSide();
    Price getPrice();
    char getTimeInForce();
    DateTime getReceivedTime();
    OrderContingency getContingency();
    char getOrderOriginType();
    short getState();
    short getOrderNBBOProtectionType();
    String getOptionalData();
    char getTradableType();

    Product getProduct();
    ProductClass getProductClass();
    OrderBookStruct toStruct();
}
