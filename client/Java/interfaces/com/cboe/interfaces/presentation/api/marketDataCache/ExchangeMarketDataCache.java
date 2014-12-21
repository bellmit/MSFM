//
// -----------------------------------------------------------------------------------
// Source file: ExchangeMarketDataCache.java
//
// PACKAGE: com.cboe.interfaces.presentation.api.marketDataCache
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.api.marketDataCache;

import java.util.*;

import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;

/**
 * This caches Market Data for multiple exchanges (e.g., V4 (MDX) Market Data)).
 */
public interface ExchangeMarketDataCache<T> extends MarketDataCache
{
    /**
     * Returns a Map where the key is the exchange and the values are market data objects of generic type T.
     * @param productKey
     * @return Map of market data
     * @throws SystemException
     * @throws CommunicationException
     * @throws AuthorizationException
     * @throws DataValidationException
     * @throws NotFoundException
     */
    Map<String, T> getMarketDataForProduct(int productKey) throws SystemException, CommunicationException,
            AuthorizationException, DataValidationException, NotFoundException;
}