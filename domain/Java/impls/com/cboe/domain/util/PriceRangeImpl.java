package com.cboe.domain.util;

import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.domain.PriceRange;

/**
 * This utility class is designed to facilitate the need for specifying a price range.
 */
public class PriceRangeImpl implements PriceRange {

    Price startingPrice;
    Price endingPrice;

    public PriceRangeImpl(Price aStartingPrice, Price aEndingPrice)
    {
        startingPrice = aStartingPrice;
        endingPrice = aEndingPrice;
    }

    public Price getStartingPrice()
    {
        return startingPrice;
    }

    public Price getEndingPrice()
    {
        return endingPrice;
    }

    //introduced by SAL project. This helps to find incoming price is within NBBO price range
    public boolean isPriceWithinRange(Price midPrice)
    {
        //Normally the startingPrice would be less than endingPrice but in the case of crossed NBBO it can't be.
        //In that case, return false
        if (pricesAreCrossed()){
            return false;
        }

        if(midPrice.greaterThanOrEqual(startingPrice) && midPrice.lessThanOrEqual(endingPrice)){
            return true;
        }

        return false;
    }

    public boolean pricesAreCrossed()
    {
        return endingPrice.lessThan(startingPrice);
    }
}
