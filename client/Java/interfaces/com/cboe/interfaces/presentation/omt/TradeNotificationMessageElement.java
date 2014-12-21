//
// -----------------------------------------------------------------------------------
// Source file: TradeNotificationMessageElement.java
//
// PACKAGE: com.cboe.interfaces.presentation.omt
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.omt;

import com.cboe.idl.cmiTradeNotification.TradeNotificationStruct;

public interface TradeNotificationMessageElement extends InfoMessageElement
{
    TradeNotificationStruct getTradeNotificationStruct();
}