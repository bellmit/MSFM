package com.cboe.presentation.api.orderBook;

import com.cboe.interfaces.presentation.api.Tradable;

import com.cboe.idl.cmiUtil.PriceStruct;
import com.cboe.domain.util.PriceFactory;
import com.cboe.presentation.common.comparators.AskPriceComparator;
import com.cboe.presentation.common.formatters.DisplayPriceFactory;

public class TradableImpl implements Tradable
{
    private int size;
    private PriceStruct price;
    private boolean isOrder;
    private String key;
    private int keyHash;
    private String displayValue;
    private static AskPriceComparator tradablePriceComparator;

    private TradableImpl()
    {}

    public TradableImpl(int size, PriceStruct price, boolean isOrder, String key)
    {
        this.size = size;
        this.price = price;
        this.isOrder = isOrder;
        this.key = key;
        this.keyHash = this.key.hashCode();
        this.displayValue = null;
    }

    public void setSize(int size)
    {
        this.size = size;
    }

    public int getSize()
    {
        return size;
    }

    public PriceStruct getPrice()
    {
        return price;
    }

    public boolean isOrder()
    {
        return isOrder;
    }

    public String getKey()
    {
        return key;
    }

    public boolean equals(Object obj)
    {
        boolean result = false;

        if (compare(obj) == 0)
        {
            result = true;
        }

        return result;
    }

    public int hashcode()
    {
        return this.keyHash;
    }

    public int compare(Object obj)
    {
        int result = 0;
        tradablePriceComparator = new AskPriceComparator();
        if (obj != null && obj instanceof TradableImpl)
        {
            Tradable tradable = (Tradable)obj;
            result = tradablePriceComparator.compare(this, tradable);
        }
        return result;
    }

    public String toString()
    {
        if ( displayValue == null )
        {
            StringBuffer buffer = new StringBuffer();
            buffer.append(isOrder?"Order":"Quote");
            buffer.append(' ').append(key);
            buffer.append(' ').append(size);
            buffer.append('@').append(DisplayPriceFactory.create(price));
            buffer.append(" hashcode:").append(keyHash);
            displayValue = buffer.toString();
        }

        return displayValue;
    }
}
