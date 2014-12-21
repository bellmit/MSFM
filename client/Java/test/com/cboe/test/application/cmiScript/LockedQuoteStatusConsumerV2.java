package com.cboe.test.application.cmiScript;

import com.cboe.idl.cmiCallbackV2.CMILockedQuoteStatusConsumerPOA;
import com.cboe.idl.cmiQuote.LockNotificationStruct;

public class LockedQuoteStatusConsumerV2 extends CMILockedQuoteStatusConsumerPOA
{
    public void acceptQuoteLockedReport(LockNotificationStruct lnseq[], int queueDepth)
    {
        Log.message("LockedQuoteStatusConsumer.acceptQuoteStatus "
                + Struct.toString(lnseq) + " queueDepth:" + queueDepth);
    }
}
