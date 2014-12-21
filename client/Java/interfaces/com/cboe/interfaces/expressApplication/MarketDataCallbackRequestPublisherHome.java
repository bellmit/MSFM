//
// -----------------------------------------------------------------------------------
// Source file: MarketDataCallbackRequestPublisherHome.java
//
// PACKAGE: com.cboe.interfaces.expressApplication
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.expressApplication;

public interface MarketDataCallbackRequestPublisherHome
{
    public final static String HOME_NAME = "MarketDataCallbackRequestPublisherHome";

    public MarketDataCallbackRequestPublisher create()
            throws Exception;

    public MarketDataCallbackRequestPublisher find()
            throws Exception;
}