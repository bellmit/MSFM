package com.cboe.testDrive;

import com.cboe.idl.cmi.*;
import com.cboe.idl.cmiCallback.*;
import com.cboe.idl.cmiMarketData.*;
import com.cboe.application.test.*;
import com.cboe.testDrive.*;
import com.cboe.infrastructureServices.foundationFramework.*;

public class RecapCallbackD extends com.cboe.idl.cmiCallback.CMIRecapConsumerPOA
{
    private CASMeter casMeter;
    private int bucket;
    private MarketDataCounter counter;

    public RecapCallbackD(CASMeter cm, int bucket)
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
            casMeter.incrementFillCount(bucket);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


    }
    public void acceptRecap(RecapStruct[] recap)
    {
        try{
            casMeter.incrementFillCount(bucket, recap.length);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        if (counter == null) return;

        for (int i=0; i < recap.length; i++)
        {
            counter.acceptMessage(recap[i].productKeys.classKey);
        }
    }
}
