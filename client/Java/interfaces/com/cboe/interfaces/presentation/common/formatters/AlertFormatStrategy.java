//
// ------------------------------------------------------------------------
// FILE: AlertFormatStrategy.java
// 
// PACKAGE: com.cboe.interfaces.presentation.common.formatters
// 
// ------------------------------------------------------------------------
// Copyright (c) 1999-2003 The Chicago Board Options Exchange.  All Rights Reserved.
// 
// ------------------------------------------------------------------------
//
package com.cboe.interfaces.presentation.common.formatters;

import com.cboe.idl.cmiIntermarketMessages.AlertStruct;
import com.cboe.interfaces.intermarketPresentation.intermarketMessages.Alert;

public interface AlertFormatStrategy extends FormatStrategy, CommonFormatStrategyStyles
{
    String format(AlertStruct alertStruct);
    String format(AlertStruct alertStruct, String style);
    String format(Alert alert);
    String format(Alert alert, String style);
}
