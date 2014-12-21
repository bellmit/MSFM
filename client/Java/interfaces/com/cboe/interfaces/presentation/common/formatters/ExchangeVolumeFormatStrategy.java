//
// ------------------------------------------------------------------------
// FILE: ExchangeVolumeFormatStrategy.java
// 
// PACKAGE: com.cboe.interfaces.presentation.common.formatters
// 
// ------------------------------------------------------------------------
// Copyright (c) 1999-2003 The Chicago Board Options Exchange.  All Rights Reserved.
// 
// ------------------------------------------------------------------------
//
package com.cboe.interfaces.presentation.common.formatters;

import com.cboe.interfaces.presentation.marketData.ExchangeVolume;
import com.cboe.idl.cmiMarketData.ExchangeVolumeStruct;

public interface ExchangeVolumeFormatStrategy extends FormatStrategy
{
    public static final String BRIEF_STYLE = "Brief Style";
    public static final String BRIEF_DESCRIPTION = "Single line";
    public static final String FULL_STYLE = "Full Style";
    public static final String FULL_DESCRIPTION = "Multiple lines";
    public static final String BID_STYLE = "Bid Style";
    public static final String BID_DESCRIPTION = "Bid Portion of bid/ask";
    public static final String ASK_STYLE = "Ask Style";
    public static final String ASK_DESCRIPTION = "Ask portion of bid/ask";
    
    String format(ExchangeVolume exchangeVolume);
    String format(ExchangeVolume exchangeVolume, String style);
    String format(ExchangeVolumeStruct exchangeVolumeStruct);
    String format(ExchangeVolumeStruct exchangeVolumeStruct, String style);
    String format(ExchangeVolume[] exchangeVolumes);
    String format(ExchangeVolume[] exchangeVolumes, String style);
    String format(ExchangeVolumeStruct[] exchangeVolumeStructs);
    String format(ExchangeVolumeStruct[] exchangeVolumeStructs, String style);
}
