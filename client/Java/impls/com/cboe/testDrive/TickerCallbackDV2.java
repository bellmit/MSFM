package com.cboe.testDrive;

import com.cboe.idl.cmi.*;
import com.cboe.idl.cmiCallbackV2.*;
import com.cboe.idl.cmiMarketData.*;
import com.cboe.application.test.*;
import com.cboe.testDrive.*;
import com.cboe.infrastructureServices.foundationFramework.*;

public class TickerCallbackDV2 extends com.cboe.idl.cmiCallbackV2.CMITickerConsumerPOA
{
    private CASMeter casMeter = null;
    private int bucket = 0;
    private MarketDataCounter counter;

    public TickerCallbackDV2(CASMeter cm, int bucket)
    {
        this.casMeter = cm;
        this.bucket = bucket;
    }

    public TickerCallbackDV2()
    {
    }

    public void addMessageCounter(MarketDataCounter counter)
    {
        this.counter = counter;
    }

    public void acceptTicker(TickerStruct[] structs, int queueDepth, short queueAction)
    {
        if (casMeter != null)
        {
            casMeter.incrementFillCount(bucket);
        }

        if (counter == null) return;

        counter.acceptAllMessages(structs.length);
/*        for (int i=0; i < structs.length; i++)
        {
            counter.acceptMessage(structs[i].productKeys.classKey);
        }   */

    }
}
