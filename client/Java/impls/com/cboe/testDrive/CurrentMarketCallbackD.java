package com.cboe.testDrive;

import com.cboe.idl.cmi.*;
import com.cboe.idl.cmiCallback.*;
import com.cboe.application.test.*;
import com.cboe.testDrive.*;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.idl.cmiMarketData.CurrentMarketStruct;
import com.cboe.interfaces.callback.CurrentMarketConsumer;

public class CurrentMarketCallbackD extends com.cboe.idl.cmiCallback.CMICurrentMarketConsumerPOA
{
    private CASMeter casMeter;
    private int bucket;
    private MarketDataCounter counter;

    public CurrentMarketCallbackD(CASMeter cm, int bucket)
    {
        this.casMeter = cm;
        this.bucket = bucket;
    }

    public void addMessageCounter(MarketDataCounter counter)
    {
        this.counter = counter;
    }

    public void acceptCurrentMarket(CurrentMarketStruct[] currentMarket)
    {
        try {
            casMeter.incrementFillCount(bucket, currentMarket.length);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        if (counter == null) return;

        for (int i=0; i < currentMarket.length; i++)
        {
            counter.acceptMessage(currentMarket[i].productKeys.classKey);
        }
    }
}
