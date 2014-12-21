package com.cboe.testDrive;

import com.cboe.idl.cmi.*;
import com.cboe.idl.cmiCallback.*;
import com.cboe.idl.cmiMarketData.*;
import com.cboe.application.test.*;
import com.cboe.testDrive.*;
import com.cboe.infrastructureServices.foundationFramework.*;

public class BestBookCallbackD extends com.cboe.idl.cmiCallback.CMIOrderBookConsumerPOA
{
    private CASMeter casMeter;
    private int bucket;
    private MarketDataCounter counter;

    public BestBookCallbackD(CASMeter cm, int bucket)
    {
        this.casMeter = cm;
        this.bucket = bucket;
    }

    public void addMessageCounter(MarketDataCounter counter)
    {
        this.counter = counter;
    }

    public void acceptBookDepth(BookDepthStruct struct)
    {
        try{
            casMeter.incrementFillCount(bucket);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        if (counter != null)
        {
            counter.acceptMessage(struct.productKeys.classKey);
        }
    }
}