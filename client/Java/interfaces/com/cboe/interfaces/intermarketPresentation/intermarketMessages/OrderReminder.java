//
// -----------------------------------------------------------------------------------
// Source file: OrderReminder.java
//
// PACKAGE: com.cboe.interfaces.intermarketPresentation.intermarketMessages;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2002 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.intermarketPresentation.intermarketMessages;

import com.cboe.idl.cmiIntermarketMessages.OrderReminderStruct;
import com.cboe.interfaces.domain.dateTime.DateTime;
import com.cboe.interfaces.presentation.order.OrderId;

public interface OrderReminder
{
    /**
     * Gets the underlying struct
     * @return OrderReminderStruct
     * @deprecated Should not use underlying, but exposed interface.
     */
    public OrderReminderStruct getStruct();

    public OrderId getReminderId();
    public String getReminderReason();
    public DateTime getTimeSent();
}