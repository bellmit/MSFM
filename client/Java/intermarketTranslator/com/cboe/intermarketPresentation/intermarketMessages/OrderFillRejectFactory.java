/**
 * Copyright 2000-2002 (C) Chicago Board Options Exchange
 * Package: com.cboe.intermarketPresentation.intermarketMessages
 * User: torresl
 * Date: Jan 2, 2003 10:29:57 AM
 */
package com.cboe.intermarketPresentation.intermarketMessages;

import com.cboe.interfaces.intermarketPresentation.intermarketMessages.OrderFillReject;
import com.cboe.idl.cmiIntermarketMessages.OrderFillRejectStruct;

public class OrderFillRejectFactory
{
    public static OrderFillReject createOrderFillReject(OrderFillRejectStruct orderFillRejectStruct)
    {
        return new OrderFillRejectImpl(orderFillRejectStruct);
    }
}