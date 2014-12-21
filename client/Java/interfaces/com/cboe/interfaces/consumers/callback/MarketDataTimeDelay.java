//
// -----------------------------------------------------------------------------------
// Source file: MarketDataTimeDelay.java
//
// PACKAGE: com.cboe.interfaces.consumers.callback
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.consumers.callback;

public interface MarketDataTimeDelay extends AcceptTimeDelay
{
    public static final String CURRENT_MARKET_CONSUMER_DELAY_PROPERTY_NAME = "CurrentMarketConsumerDelayMillis";
    public static final String NBBO_CONSUMER_DELAY_PROPERTY_NAME = "NBBOConsumerDelayMillis";
    public static final String EOP_CONSUMER_DELAY_PROPERTY_NAME = "EOPConsumerDelayMillis";
    public static final String RECAP_CONSUMER_DELAY_PROPERTY_NAME = "RecapConsumerDelayMillis";
    public static final String TICKER_CONSUMER_DELAY_PROPERTY_NAME = "TickerConsumerDelayMillis";
}