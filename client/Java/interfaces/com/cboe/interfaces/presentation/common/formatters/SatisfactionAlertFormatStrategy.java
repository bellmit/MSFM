//
// ------------------------------------------------------------------------
// FILE: SatisfactionAlertFormatStrategy.java
// 
// PACKAGE: com.cboe.interfaces.presentation.common.formatters
// 
// ------------------------------------------------------------------------
// Copyright (c) 1999-2003 The Chicago Board Options Exchange.  All Rights Reserved.
// 
// ------------------------------------------------------------------------
//
package com.cboe.interfaces.presentation.common.formatters;

import com.cboe.idl.cmiIntermarketMessages.SatisfactionAlertStruct;
import com.cboe.interfaces.intermarketPresentation.intermarketMessages.SatisfactionAlert;

public interface SatisfactionAlertFormatStrategy extends FormatStrategy, CommonFormatStrategyStyles
{
    String format(SatisfactionAlertStruct satisfactionAlertStruct);
    String format(SatisfactionAlertStruct satisfactionAlertStruct, String style);
    String format(SatisfactionAlert satisfactionAlert);
    String format(SatisfactionAlert satisfactionAlert, String style);
}
