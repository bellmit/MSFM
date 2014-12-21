package com.cboe.presentation.common.comparators;

import com.cboe.interfaces.presentation.product.ProductContainer;

/**
 @author Nick DePasquale
 @date 5/6/02
 */
public class StrategyTableProductComparator extends ProductContainerComparator
{
    /**
     Implements Comparator.
     */
    public int compare(Object product1, Object product2)
    {
        int result = -1;
        String symbol1 = null;
        String symbol2 = null;

        symbol1 = ((ProductContainer)product1).getContainedProduct().toString();
        symbol2 = ((ProductContainer)product2).getContainedProduct().toString();
        result = symbol1.compareTo(symbol2);
        return result;
    }
}

