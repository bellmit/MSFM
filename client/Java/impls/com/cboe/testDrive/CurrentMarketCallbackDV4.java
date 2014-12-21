//
// -----------------------------------------------------------------------------------
// Source file: CurrentMarketCallbackDV4.java
//
// PACKAGE: com.cboe.testDrive
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.testDrive;

import com.cboe.idl.cmiMarketData.CurrentMarketStructV4;

public class CurrentMarketCallbackDV4 extends com.cboe.idl.cmiCallbackV4.CMICurrentMarketConsumerPOA
{
    private CASMeter bestMarketMeter = null;
    private CASMeter bestPublicMarketMeter = null;
    private int bucket = 0;
    private MarketDataCounter bestMarketCounter;
    private MarketDataCounter bestPublicMarketCounter;

    public CurrentMarketCallbackDV4(CASMeter bestMarketMeter, CASMeter bestPublicMarketMeter, int bucket)
    {
        this.bestMarketMeter = bestMarketMeter;
        this.bestPublicMarketMeter = bestPublicMarketMeter;
        this.bucket = bucket;
    }

    public CurrentMarketCallbackDV4()
    {
    }

    public void addMessageCounter(MarketDataCounter bestMarketCounter, MarketDataCounter bestPublicMarketCounter)
    {
        this.bestMarketCounter = bestMarketCounter;
        this.bestPublicMarketCounter = bestPublicMarketCounter;
    }

    public void acceptCurrentMarket(CurrentMarketStructV4[] bestMarkets, CurrentMarketStructV4[] bestPublicMarkets,
                                    int messageSequence, int queueDepth, short queueAction)
    {
        if(bestMarketMeter != null)
        {
            bestMarketMeter.incrementFillCount(bucket, bestMarkets.length);
            bestPublicMarketMeter.incrementFillCount(bucket, bestPublicMarkets.length);
        }

        if(bestMarketCounter!= null)
        {
            bestMarketCounter.acceptAllMessages(bestMarkets.length);
        }

        if(bestPublicMarketCounter!= null )
        {
            bestPublicMarketCounter.acceptAllMessages(bestPublicMarkets.length);
        }
    }
}
