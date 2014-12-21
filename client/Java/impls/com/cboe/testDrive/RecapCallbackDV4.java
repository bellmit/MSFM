//
// -----------------------------------------------------------------------------------
// Source file: RecapCallbackDV4.java
//
// PACKAGE: com.cboe.testDrive
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.testDrive;

import com.cboe.idl.cmiMarketData.RecapStructV4;
import com.cboe.idl.cmiMarketData.LastSaleStructV4;

public class RecapCallbackDV4 extends com.cboe.idl.cmiCallbackV4.CMIRecapConsumerPOA
{
    private CASMeter casMeter = null;
    private int bucket = 0;
    private MarketDataCounter counter;

    public RecapCallbackDV4(CASMeter cm, int bucket)
    {
        this.casMeter = cm;
        this.bucket = bucket;
    }

    public RecapCallbackDV4()
    {
    }

    public void addMessageCounter(MarketDataCounter counter)
    {
        this.counter = counter;
    }

    public void acceptRecap(RecapStructV4[] recap, int messageSequence, int queueDepth, short queueAction)
    {
        if(casMeter != null)
        {
            casMeter.incrementFillCount(bucket, recap.length);
        }
        if(counter == null) return;

        counter.acceptAllMessages(recap.length);
    }

    public void acceptLastSale(LastSaleStructV4[] recap, int messageSequence, int queueDepth, short queueAction)
    {
        if(casMeter != null)
        {
            casMeter.incrementFillCount(bucket, recap.length);
        }
        if(counter == null) return;

        counter.acceptAllMessages(recap.length);
    }
}
