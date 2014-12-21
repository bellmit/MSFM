package com.cboe.presentation.product;

import com.cboe.idl.cmiUtil.DateStruct;
import com.cboe.idl.cmiProduct.PendingAdjustmentStruct;
import com.cboe.interfaces.presentation.product.PendingNameContainer;
import com.cboe.interfaces.presentation.product.ProductAdjustmentContainer;

public class ProductAdjustmentContainerImpl implements ProductAdjustmentContainer
{
    private PendingAdjustmentStruct adjustment;
    private PendingNameContainer[] pendingProducts;

    public ProductAdjustmentContainerImpl(PendingAdjustmentStruct adj) {
        adjustment = adj;
        pendingProducts = new PendingNameContainer[adj.productsPending.length];
        for (int i = 0 ; i < adj.productsPending.length ; i++) {
            pendingProducts[i] = PendingNameFactory.create(adj.productsPending[i]);
        }
    }

    public int getClassKey() {
        return adjustment.classKey;
    }

    public DateStruct getEffectiveDate() {
        return adjustment.effectiveDate;
    }

    public DateStruct getSubmittedDate() {
        return adjustment.submittedDate;
    }

    public short getType() {
        return adjustment.type;
    }

    public boolean getActive() {
        return adjustment.active;
    }

    public PendingNameContainer[] getProductsPending() {
        return pendingProducts;
    }
}