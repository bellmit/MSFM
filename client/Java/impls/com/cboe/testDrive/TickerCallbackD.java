package com.cboe.testDrive;

import com.cboe.idl.cmi.*;
import com.cboe.idl.cmiCallback.*;
import com.cboe.idl.cmiMarketData.*;
import com.cboe.application.test.*;
import com.cboe.testDrive.*;
import com.cboe.infrastructureServices.foundationFramework.*;

public class TickerCallbackD extends com.cboe.idl.cmiCallback.CMITickerConsumerPOA
{
    private CASMeter casMeter;
    private int bucket;
    private MarketDataCounter counter;

    public TickerCallbackD(CASMeter cm, int bucket)
    {
        this.casMeter = cm;
        this.bucket = bucket;
    }

    public void addMessageCounter(MarketDataCounter counter)
    {
        this.counter = counter;
    }

    public void acceptTicker(TickerStruct[] structs)
    {
        try{
            casMeter.incrementFillCount(bucket);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        if (counter == null) return;

        for (int i=0; i < structs.length; i++)
        {
            counter.acceptMessage(structs[i].productKeys.classKey);
        }

    }
}
