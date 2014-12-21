package com.cboe.presentation.product;

import com.cboe.idl.cmiProduct.PendingNameStruct;
import com.cboe.interfaces.presentation.product.PendingNameContainer;

public class PendingNameFactory
{
    public static PendingNameContainer create(PendingNameStruct name) {
        return new PendingNameContainerImpl(name);
    }
}