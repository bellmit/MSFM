package com.cboe.interfaces.application.inprocess;

import com.cboe.idl.cmiQuote.LockNotificationStruct;
import com.cboe.idl.cmiProduct.ProductStruct;

/**
 * @author Jing Chen
 */
public interface LockedQuoteStatusConsumer
{
    public void acceptQuoteLockedReport(LockNotificationStruct lockNotificationStructs, ProductStruct product, int queueDepth);
}
