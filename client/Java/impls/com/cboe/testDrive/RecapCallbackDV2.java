package com.cboe.testDrive;

import com.cboe.idl.cmi.*;
import com.cboe.idl.cmiCallbackV2.*;
import com.cboe.idl.cmiMarketData.*;
import com.cboe.application.test.*;
import com.cboe.testDrive.*;
import com.cboe.infrastructureServices.foundationFramework.*;


public class RecapCallbackDV2 extends com.cboe.idl.cmiCallbackV2.CMIRecapConsumerPOA
{
    private CASMeter casMeter = null;
    private int bucket =0;
    private MarketDataCounter counter;

    public RecapCallbackDV2(CASMeter cm, int bucket)
    {
        this.casMeter = cm;
        this.bucket = bucket;
    }

    public RecapCallbackDV2()
    {
    }

    public void addMessageCounter(MarketDataCounter counter)
    {
        this.counter = counter;
    }

    public synchronized void acceptRecap(RecapStruct[] recap, int queueDepth, short queueAction)
    {
        if (casMeter != null)
        {
            casMeter.incrementFillCount(bucket, recap.length);
        }
        if (counter == null) return;

        counter.acceptAllMessages(recap.length);
/*        for (int i=0; i < recap.length; i++)
        {
            counter.acceptMessage(recap[i].productKeys.classKey);
        }   */
    }
}
