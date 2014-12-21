//
// -----------------------------------------------------------------------------------
// Source file: OrderReminderFactory.java
//
// PACKAGE: com.cboe.intermarketPresentation.intermarketMessages;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.intermarketPresentation.intermarketMessages;

import com.cboe.idl.cmiIntermarketMessages.OrderReminderStruct;

import com.cboe.interfaces.intermarketPresentation.intermarketMessages.OrderReminder;

public class OrderReminderFactory
{
    public static OrderReminder createOrderReminder(OrderReminderStruct orderReminderStruct)
    {
        return new OrderReminderImpl(orderReminderStruct);
    }
}