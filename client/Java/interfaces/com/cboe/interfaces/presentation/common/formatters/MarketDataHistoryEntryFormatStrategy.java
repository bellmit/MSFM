// -----------------------------------------------------------------------------------
// Source file: MarketDataHistoryEntryFormatStrategy
//
// PACKAGE: com.cboe.interfaces.presentation.common.formatters
// 
// Created: Jul 20, 2004 2:36:13 PM
// -----------------------------------------------------------------------------------
// Copyright (c) 2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.common.formatters;

public interface MarketDataHistoryEntryFormatStrategy extends FormatStrategy
{
    final public static String BRIEF="BRIEF";
    final public static String FULL="FULL";
    final public static String BRIEF_DESC="Single Character Symbol";
    final public static String FULL_DESC="Full Text Name";
    
    public String format(short entryType);
    public String format(short entryType, String styleName);

} // -- end of interface MarketDataHistoryEntryFormatStrategy
