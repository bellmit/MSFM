package com.cboe.testDrive;

import com.cboe.idl.cmi.*;
import com.cboe.idl.cmiCallback.*;
import com.cboe.idl.cmiMarketData.*;
import com.cboe.application.test.*;
import com.cboe.testDrive.*;
import com.cboe.infrastructureServices.foundationFramework.*;

public class NBBOCallbackDV2 extends com.cboe.idl.cmiCallbackV2.CMINBBOConsumerPOA
{
    private CASMeter casMeter = null;
    private int bucket = 0;
    private MarketDataCounter counter;

    public NBBOCallbackDV2(CASMeter cm, int bucket)
    {
        this.casMeter = cm;
        this.bucket = bucket;
    }

    public NBBOCallbackDV2()
    {
    }

    public void addMessageCounter(MarketDataCounter counter)
    {
        this.counter = counter;
    }

    public void acceptNBBO(NBBOStruct[] struct, int queueDepth, short queueAction)
    {
        if (casMeter != null)
        {
            casMeter.incrementFillCount(bucket, struct.length);
        }
        if (counter != null ) //&& struct.length > 0)
        {
            counter.acceptAllMessages(struct.length);
//            counter.acceptMessage(struct[0].productKeys.classKey);
        }
    }
}
