package com.cboe.interfaces.presentation.product;

import com.cboe.idl.cmiProduct.*;

public interface PendingNameContainer
{
    public short getAction();
    public Product getProduct();
    public ProductNameStruct getProductNameStruct();
}