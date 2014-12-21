//
// ------------------------------------------------------------------------
// FILE: CancelRequestFormatStrategy.java
// 
// PACKAGE: com.cboe.interfaces.presentation.common.formatters
// 
// ------------------------------------------------------------------------
// Copyright (c) 1999-2003 The Chicago Board Options Exchange.  All Rights Reserved.
// 
// ------------------------------------------------------------------------
//
package com.cboe.interfaces.presentation.common.formatters;

import com.cboe.interfaces.presentation.order.CancelRequest;
import com.cboe.idl.cmiOrder.CancelRequestStruct;

public interface CancelRequestFormatStrategy
{
    public static final String FULL_STYLE_NAME = "Full Style";
    public static final String BRIEF_STYLE_NAME = "Brief Style";
    public static final String FULL_STYLE_DESCRIPTION = "Full Style Description";
    public static final String BRIEF_STYLE_DESCRIPTION = "Brief Style Description";

    String format(CancelRequestStruct cancelRequestStruct);
    String format(CancelRequestStruct cancelRequestStruct, String style);
    String format(CancelRequest cancelRequest);
    String format(CancelRequest cancelRequest, String style);
}
