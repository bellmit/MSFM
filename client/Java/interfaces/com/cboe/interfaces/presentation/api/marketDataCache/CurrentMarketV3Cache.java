//
// -----------------------------------------------------------------------------------
// Source file: CurrentMarketV3Cache.java
//
// PACKAGE: com.cboe.interfaces.presentation.api.marketDataCache
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.api.marketDataCache;

import com.cboe.interfaces.domain.CurrentMarketProductContainer;

/**
 * This cache will listen to the IEC and store CurrentMarketProductContainer data received for its subscribed classes.
 */
public interface CurrentMarketV3Cache extends
        SessionMarketDataCache<CurrentMarketProductContainer>
{
}