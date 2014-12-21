package com.cboe.test.application.cmiScript;

import com.cboe.idl.cmiCallback.CMIProductStatusConsumerPOA;
import com.cboe.idl.cmiSession.ProductStateStruct;
import com.cboe.idl.cmiSession.SessionProductStruct;

public class ProductStatusConsumer extends CMIProductStatusConsumerPOA
{
    public void updateProduct(SessionProductStruct updatedProduct)
    {
        Log.message("ProductStatusConsumer.updateProduct "
                + Struct.toString(updatedProduct));
    }
    
    public void acceptProductState(ProductStateStruct productState[])
    {
        Log.message("ProductStatusConsumer.acceptProductState "
                + Struct.toString(productState));
    }
}
