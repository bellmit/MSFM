//
// -----------------------------------------------------------------------------------
// Source file: RecapV2Cache.java
//
// PACKAGE: com.cboe.interfaces.presentation.api.marketDataCache
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.api.marketDataCache;

import com.cboe.idl.cmiMarketData.RecapStruct;

/**
 * This cache will listen to the IEC and store RecapStruct data received for its subscribed classes.
 */
public interface RecapV2Cache extends SessionMarketDataCache<RecapStruct>
{
}