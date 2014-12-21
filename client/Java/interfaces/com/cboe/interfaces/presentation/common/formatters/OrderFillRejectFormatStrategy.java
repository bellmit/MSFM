//
// ------------------------------------------------------------------------
// Source file: OrderFillRejectFormatStrategy.java
//
// PACKAGE: com.cboe.interfaces.presentation.common.formatters
//
// ------------------------------------------------------------------------
// Copyright (c) 1999-2003 The Chicago Board Options Exchange.  All Rights Reserved.
//
// ------------------------------------------------------------------------

package com.cboe.interfaces.presentation.common.formatters;

import com.cboe.idl.cmiIntermarketMessages.OrderFillRejectStruct;
import com.cboe.interfaces.intermarketPresentation.intermarketMessages.OrderFillReject;

public interface OrderFillRejectFormatStrategy extends CommonFormatStrategyStyles
{
    String format(OrderFillRejectStruct orderFillRejectStruct);
    String format(OrderFillRejectStruct orderFillRejectStruct, String style);
    String format(OrderFillReject orderFillReject);
    String format(OrderFillReject orderFillReject, String style);
}
