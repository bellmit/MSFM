//
// ------------------------------------------------------------------------
// FILE: AlertHeaderFormatStrategy.java
// 
// PACKAGE: com.cboe.interfaces.presentation.common.formatters
// 
// ------------------------------------------------------------------------
// Copyright (c) 1999-2003 The Chicago Board Options Exchange.  All Rights Reserved.
// 
// ------------------------------------------------------------------------
//
package com.cboe.interfaces.presentation.common.formatters;

import com.cboe.idl.cmiIntermarketMessages.AlertHdrStruct;
import com.cboe.interfaces.intermarketPresentation.intermarketMessages.AlertHeader;

public interface AlertHeaderFormatStrategy extends FormatStrategy, CommonFormatStrategyStyles
{
    String format(AlertHdrStruct alertHeaderStruct);
    String format(AlertHdrStruct alertHeaderStruct, String style);
    String format(AlertHeader alertHeader);
    String format(AlertHeader alertHeader, String style);
}
