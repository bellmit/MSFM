package com.cboe.presentation.api.orderBook;

import java.util.*;

import com.cboe.exceptions.*;
import com.cboe.interfaces.presentation.api.Tradable;
import com.cboe.idl.cmiUtil.PriceStruct;


public class TradableFactory
{
    private static Tradable tradable = null;

    public TradableFactory()
    {
        super();
    }

    public static Tradable create(int qty, PriceStruct price, boolean isOrder, String key)
    {
        tradable = new TradableImpl(qty, price, isOrder, key);

        return tradable;
    }
}