package com.cboe.presentation.product;

import com.cboe.interfaces.presentation.product.ProductAdjustmentContainer;
import com.cboe.idl.cmiProduct.PendingAdjustmentStruct;

public class ProductAdjustmentFactory
{
    public static ProductAdjustmentContainer create(PendingAdjustmentStruct adjustment) {
        return new ProductAdjustmentContainerImpl(adjustment);
    }
}