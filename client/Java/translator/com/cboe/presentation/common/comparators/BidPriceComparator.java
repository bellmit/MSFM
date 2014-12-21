package com.cboe.presentation.common.comparators;

import java.util.*;
import com.cboe.domain.util.ValuedPrice;
import com.cboe.domain.util.ValuedPriceComparator;
import com.cboe.interfaces.presentation.api.Tradable;
import com.cboe.idl.cmiUtil.PriceStruct;
import com.cboe.idl.cmiConstants.PriceTypes;

public class BidPriceComparator implements Comparator
{
    private static AskPriceComparator askPriceComparator;

    public BidPriceComparator()
    {
        super();
        askPriceComparator = new AskPriceComparator();
    }

    /**
     * Compares two ValuedPrice.
     */
    public int compare(Object arg1In, Object arg2In)
    {
        Tradable t1 = (Tradable)arg1In;
        Tradable t2 = (Tradable)arg2In;
        ValuedPrice arg1 = null;
        ValuedPrice arg2 = null;
        int value = askPriceComparator.compareMarket(t1, t2);

        if(value != askPriceComparator.NO_MARKET_ORDERS)
        {
            return value;
        }
        else
        {
            return (askPriceComparator.compare(arg1In, arg2In) * (-1) );
        }
    }

    private ValuedPrice getValuedPrice(PriceStruct priceIn)
    {
        ValuedPrice price = new ValuedPrice(priceIn);
        return  price;
    }
}
