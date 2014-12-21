package com.cboe.interfaces.application.inprocess;

import com.cboe.idl.cmiProduct.ProductStruct;
import com.cboe.idl.cmiQuote.RFQStruct;

/**
 * @author Jing Chen
 */
public interface RFQConsumer
{
    public void acceptRFQ(RFQStruct rfq, ProductStruct product, int queueDepth);
}
