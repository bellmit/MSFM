package com.cboe.presentation.product;

import com.cboe.interfaces.presentation.product.FutureProduct;
import com.cboe.idl.cmiProduct.ProductStruct;

class InactiveSessionFutureProductImpl extends InactiveSessionProductImpl implements FutureProduct
{
    public InactiveSessionFutureProductImpl(String sessionName, String inactiveSessionName, ProductStruct productStruct)
    {
        super(sessionName, inactiveSessionName, productStruct);
    }
}
