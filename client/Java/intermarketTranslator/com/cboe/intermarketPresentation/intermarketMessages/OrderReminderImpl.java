//
// -----------------------------------------------------------------------------------
// Source file: OrderReminderImpl.java
//
// PACKAGE: com.cboe.intermarketPresentation.intermarketMessages;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.intermarketPresentation.intermarketMessages;

import com.cboe.interfaces.domain.dateTime.DateTime;
import com.cboe.interfaces.presentation.order.OrderId;
import com.cboe.interfaces.intermarketPresentation.intermarketMessages.OrderReminder;

import com.cboe.idl.cmiIntermarketMessages.OrderReminderStruct;
import com.cboe.presentation.common.dateTime.DateTimeImpl;
import com.cboe.presentation.order.OrderIdFactory;

class OrderReminderImpl implements OrderReminder
{
    protected OrderId               reminderId;
    protected String                reminderReason;
    protected DateTime              timeSent;
    protected OrderReminderStruct   orderReminderStruct;
    public OrderReminderImpl(OrderReminderStruct orderReminderStruct)
    {
        this.orderReminderStruct = orderReminderStruct;
        initialize();
    }

    private void initialize()
    {
        reminderId = OrderIdFactory.createOrderId(orderReminderStruct.reminderId);
        reminderReason = new String(orderReminderStruct.reminderReason);
        timeSent = new DateTimeImpl(orderReminderStruct.timeSent);
    }

    public OrderId getReminderId()
    {
        return reminderId;
    }

    public String getReminderReason()
    {
        return reminderReason;
    }

    public DateTime getTimeSent()
    {
        return timeSent;
    }

    /**
     * Gets the underlying struct
     * @return OrderReminderStruct
     * @deprecated Should not use underlying, but exposed interface.
     */
    public OrderReminderStruct getStruct()
    {
        return orderReminderStruct;
    }
}
