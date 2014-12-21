//
// ------------------------------------------------------------------------
// FILE: CommonStyles.java
//
// PACKAGE: com.cboe.interfaces.presentation.common.formatters
//
// ------------------------------------------------------------------------
// Copyright (c) 1999-2003 The Chicago Board Options Exchange.  All Rights Reserved.
//
// ------------------------------------------------------------------------
//
package com.cboe.interfaces.presentation.common.formatters;

public interface CommonFormatStrategyStyles
{
    public static final String BRIEF_STYLE_NAME = "Brief Style";
    public static final String BRIEF_STYLE_DESCRIPTION = "One Line Summary";
    public static final String FULL_STYLE_NAME = "Full Style";
    public static final String FULL_STYLE_DESCRIPTION = "Full alert information";
    public static final String BRIEF_STYLE_DELIMITER = " ";
    public static final String FULL_STYLE_DELIMITER = "\n";

    boolean isBrief(String styleName);
    String getDelimiterForStyle(String style);
}
