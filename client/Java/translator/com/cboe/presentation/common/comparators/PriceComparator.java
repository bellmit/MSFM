package com.cboe.presentation.common.comparators;

import java.util.*;

import com.cboe.interfaces.domain.Price;

/**
 * Compares Price instances
 */
public class PriceComparator implements Comparator<Price>
{
    private boolean noPriceAsDouble;

    public PriceComparator()
    {
        this(false);
    }

    /**
     * If compareNoPriceAsDouble is true, then treat prices of type NO_PRICE as double "0.0" for comparison.
     */
    public PriceComparator(boolean compareNoPriceAsDouble)
    {
        this.noPriceAsDouble = compareNoPriceAsDouble;
    }

    /**
     * Implements Comparator.
     * If one or both prices is not a ValuedPrice the rules are:
     * valued price is greater than MarketPrice,
     * MarketPrice is greater than NoPrice,
     */
    public int compare(Price price1, Price price2)
    {
        int result = -1;
        if ( price1.isValuedPrice() )
        {
            if (price2.isValuedPrice())
            {
                result = comparePrices(price1, price2);
            }
            else
            {
                result = 1; // ValuedPrice > MarketPrice and NoPrice
                if (noPriceAsDouble)
                {
                    if (price2.isNoPrice() && price1.toDouble() == 0.0)
                    {
                        result = 0;
                    }
                }
            }
        }
        else if (price1.isMarketPrice())
        {
            if (price2.isValuedPrice())
            {
                result = -1; // MarketPrice < ValuedPrice
            }
            else if (price2.isMarketPrice())
            {
                result = 0; // MarketPrice == MarketPrice
            }
            else
            {
                result = 1; // MarketPrice > NoPrice
            }
        }
        else if (price1.isNoPrice())
        {
            if (price2.isValuedPrice())
            {
                result = -1; // NoPrice < ValuedPrice
                if (noPriceAsDouble)
                {
                    if (price2.toDouble() == 0.0)
                    {
                        result = 0;
                    }
                }
            }
            else if (price2.isMarketPrice())
            {
                result = -1; // NoPrice < MarketPrice
            }
            else
            {
                result = 0; // NoPrice == NoPrice
            }
        }
        return result;
    }

    private int comparePrices(Price price1, Price price2)
    {
        int result = -1;
        if (price1.greaterThan(price2))
        {
            result = 1;
        }
        else if (price1.equals(price2))
        {
            result = 0;
        }
        return result;
    }
}
