//
// -----------------------------------------------------------------------------------
// Source file: TickerCallbackDV4.java
//
// PACKAGE: com.cboe.testDrive
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.testDrive;

import com.cboe.idl.cmiMarketData.TickerStructV4;

public class TickerCallbackDV4 extends com.cboe.idl.cmiCallbackV4.CMITickerConsumerPOA
{
    private CASMeter casMeter = null;
    private int bucket = 0;
    private MarketDataCounter counter;

    public TickerCallbackDV4(CASMeter cm, int bucket)
    {
        this.casMeter = cm;
        this.bucket = bucket;
    }

    public TickerCallbackDV4()
    {
    }

    public void addMessageCounter(MarketDataCounter counter)
    {
        this.counter = counter;
    }

    public void acceptTicker(TickerStructV4[] structs, int messageSequence, int queueDepth, short queueAction)
    {
        if(casMeter != null)
        {
            casMeter.incrementFillCount(bucket);
        }
        if(counter == null) return;

        counter.acceptAllMessages(structs.length);
    }
}
