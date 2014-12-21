package com.cboe.interfaces.presentation.product;

import com.cboe.idl.cmiUtil.*;

public interface ProductAdjustmentContainer
{
    public int getClassKey();
    public DateStruct getEffectiveDate();
    public DateStruct getSubmittedDate();
    public short getType();
    public boolean getActive();
    public PendingNameContainer[] getProductsPending();
}