package com.cboe.test.application.cmiScript;

import com.cboe.idl.cmiCallback.CMIRFQConsumerPOA;
import com.cboe.idl.cmiQuote.RFQStruct;

public class RFQConsumer extends CMIRFQConsumerPOA
{
    public void acceptRFQ(RFQStruct rfq)
    {
        Log.message("RFQConsumer.acceptRFQ " + Struct.toString(rfq));
    }
}
