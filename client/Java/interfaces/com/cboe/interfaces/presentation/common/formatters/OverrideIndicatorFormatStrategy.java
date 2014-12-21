// -----------------------------------------------------------------------------------
// Source file: OverrideIndicatorFormatStrategy
//
// PACKAGE: com.cboe.interfaces.presentation.common.formatters
// 
// Created: Jul 21, 2004 1:30:57 PM
// -----------------------------------------------------------------------------------
// Copyright (c) 2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.common.formatters;

public interface OverrideIndicatorFormatStrategy
{
    final public static String BRIEF="BRIEF";
    final public static String FULL="FULL";
    final public static String BRIEF_DESC="Single Character Indicator";
    final public static String FULL_DESC="Override Indicator  Name";

    public String format(char indicator);
    public String format(char indicator, String styleName);

} // -- end of interface OverrideIndicatorFormatStrategy
