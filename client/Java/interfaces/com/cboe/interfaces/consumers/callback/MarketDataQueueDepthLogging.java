//
// ------------------------------------------------------------------------
// FILE: MarketDataQueueDepthLogging.java
// 
// PACKAGE: com.cboe.interfaces.consumers.callback
// 
// ------------------------------------------------------------------------
// Copyright (c) 1999-2003 The Chicago Board Options Exchange.  All Rights Reserved.
// 
// ------------------------------------------------------------------------
//
package com.cboe.interfaces.consumers.callback;

public interface MarketDataQueueDepthLogging extends QueueDepthLogging
{
    public static final String CURRENT_MARKET_LOG_QUEUE_DEPTH_THRESHOLD_PROPERTY_NAME = "CurrentMarketLogQueueDepthThreshold";
    public static final String NBBO_LOG_QUEUE_DEPTH_THRESHOLD_PROPERTY_NAME = "NBBOLogQueueDepthThreshold";
    public static final String EOP_LOG_QUEUE_DEPTH_THRESHOLD_PROPERTY_NAME = "EOPLogQueueDepthThreshold";
    public static final String RECAP_LOG_QUEUE_DEPTH_THRESHOLD_PROPERTY_NAME = "RecapLogQueueDepthThreshold";
    public static final String TICKER_LOG_QUEUE_DEPTH_THRESHOLD_PROPERTY_NAME = "TickerLogQueueDepthThreshold";
}
