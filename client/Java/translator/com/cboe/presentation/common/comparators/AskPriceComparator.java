package com.cboe.presentation.common.comparators;

import java.util.*;
import com.cboe.domain.util.ValuedPrice;
import com.cboe.domain.util.ValuedPriceComparator;
import com.cboe.interfaces.presentation.api.Tradable;
import com.cboe.idl.cmiUtil.PriceStruct;
import com.cboe.idl.cmiConstants.PriceTypes;

public class AskPriceComparator implements Comparator
{
    private static ValuedPriceComparator valuedPriceComparator;
    public static final int NO_MARKET_ORDERS = -999;

    public AskPriceComparator()
    {
        super();
        valuedPriceComparator = new ValuedPriceComparator();
    }

    /**
     * Returns 1 is t1 is a Market order; -1 if t2 is; 0 if they both are, and -999 if neither are.
     * Convenience method for compare().
     * @param t1
     * @param t2
     * @return
     */
    public static int compareMarket(Tradable t1, Tradable t2)
    {
        int result = NO_MARKET_ORDERS;

        if((t1.getPrice().type == PriceTypes.MARKET) &&
           (t2.getPrice().type == PriceTypes.MARKET))
        {
            result = 0;
        }
        else if((t1.getPrice().type == PriceTypes.MARKET) &&
                (t2.getPrice().type != PriceTypes.MARKET))
        {
            result = -1;
        }
        else if((t1.getPrice().type != PriceTypes.MARKET) &&
                (t2.getPrice().type == PriceTypes.MARKET))
        {
            result = 1;
        }

        return result;
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
        int result = compareMarket(t1, t2);

        //if either or both is a market order, perform separate logic
        if(result != NO_MARKET_ORDERS)
        {
            return result;
        }
        else
        {
            arg1 = getValuedPrice(t1.getPrice());
            arg2 = getValuedPrice(t2.getPrice());
            if(arg1 instanceof ValuedPrice && arg2 instanceof ValuedPrice)
            {
                ValuedPrice firstPrice = arg1;
                ValuedPrice secondPrice = arg2;

                result =  valuedPriceComparator.compare(firstPrice, secondPrice);
                if (result == 0)
                {
                    String key1 = t1.getKey();
                    String key2 = t2.getKey();
                    result = key1.compareTo(key2);
                }

                return result;
            }
            else
            {
                throw new ClassCastException("AskPriceComparator error: cannot compare objects that are not ValuedPrice");
            }
        }
    }

    private ValuedPrice getValuedPrice(PriceStruct priceIn)
    {
        ValuedPrice price = new ValuedPrice(priceIn);
        return  price;
    }
}
