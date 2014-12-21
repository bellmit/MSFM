package com.cboe.testDrive;

import com.cboe.testDrive.*;
import com.cboe.idl.cmiMarketData.CurrentMarketStruct;

public class CurrentMarketCallbackDV3 extends com.cboe.idl.cmiCallbackV3.CMICurrentMarketConsumerPOA
{
    private CASMeter bestMarketMeter = null;
    private CASMeter bestPublicMarketMeter = null;
    private int bucket = 0;
    private MarketDataCounter bestMarketCounter;
    private MarketDataCounter bestPublicMarketCounter;

    public CurrentMarketCallbackDV3(CASMeter bestMarketMeter, CASMeter bestPublicMarketMeter, int bucket)
    {
        this.bestMarketMeter = bestMarketMeter;
        this.bestPublicMarketMeter = bestPublicMarketMeter;
        this.bucket = bucket;
    }

    public CurrentMarketCallbackDV3()
    {
    }

    public void addMessageCounter(MarketDataCounter bestMarketCounter, MarketDataCounter bestPublicMarketCounter)
    {
        this.bestMarketCounter = bestMarketCounter;
        this.bestPublicMarketCounter = bestPublicMarketCounter;
    }

    public void acceptCurrentMarket(CurrentMarketStruct[] bestMarkets, CurrentMarketStruct[] bestPublicMarkets, int queueDepth, short queueAction)
    {
        if (bestMarketMeter != null)         // && bestPublicMarketMeter != null
        {
            bestMarketMeter.incrementFillCount(bucket, bestMarkets.length);
            bestPublicMarketMeter.incrementFillCount(bucket, bestPublicMarkets.length);
        }

        if (bestMarketCounter!= null ) //&& bestMarkets.length > 0)
        {
            bestMarketCounter.acceptAllMessages(bestMarkets.length);
/*            for (int i=0; i < bestMarkets.length; i++)
            {
                bestMarketCounter.acceptMessage(bestMarkets[i].productKeys.classKey);
            }      */
        }

        if (bestPublicMarketCounter!= null ) //&& bestPublicMarkets.length > 0)
        {
            bestPublicMarketCounter.acceptAllMessages(bestPublicMarkets.length);
/*            for (int i=0; i < bestMarkets.length; i++)
            {
                bestPublicMarketCounter.acceptMessage(bestPublicMarkets[i].productKeys.classKey);
            }  */
        }
    }
}
