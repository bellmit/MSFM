//
// -----------------------------------------------------------------------------------
// Source file: CallbackV4ConsumerCacheFactory.java
//
// PACKAGE: com.cboe.interfaces.consumers.callback
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.consumers.callback;

/**
 * This provides an interface to get the cache of consumers for each type of V4 market data. 
 */
public interface CallbackV4ConsumerCacheFactory
{
    public CMICurrentMarketV4ConsumerCache getCurrentMarketConsumerCache();
    public CMIRecapV4ConsumerCache getRecapConsumerCache();
    public CMITickerV4ConsumerCache getTickerConsumerCache();
    public CMINBBOV4ConsumerCache getNBBOConsumerCache();
}