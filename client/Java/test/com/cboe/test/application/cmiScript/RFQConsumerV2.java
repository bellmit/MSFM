package com.cboe.test.application.cmiScript;

import com.cboe.idl.cmiCallbackV2.CMIRFQConsumerPOA;
import com.cboe.idl.cmiQuote.RFQStruct;

public class RFQConsumerV2 extends CMIRFQConsumerPOA
{
    public void acceptRFQ(RFQStruct rfqseq[], int queueDepth, short queueAction)
    {
        Log.message("RFQConsumerV2.acceptRFQ " + Struct.toString(rfqseq)
         + " queueDepth:" + queueDepth + " queueAction:" + queueAction);
    }
}
