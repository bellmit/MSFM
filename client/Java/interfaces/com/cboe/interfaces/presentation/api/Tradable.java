package com.cboe.interfaces.presentation.api;

import com.cboe.idl.cmiUtil.PriceStruct;

public interface Tradable
{
    public int getSize();
    public PriceStruct getPrice();
    public boolean isOrder();
    public String getKey();
    public boolean equals(Object otherObj);
    public int hashCode();
}
