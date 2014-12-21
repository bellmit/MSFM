package com.cboe.presentation.common.comparators;

import com.cboe.interfaces.presentation.product.Product;

/**
 @author Nick DePasquale
 @date 5/6/02
 */
public class StrategyProductComparator extends ProductContainerComparator
{
    /**
     Implements Comparator.
     */
    @Override
    public int compare(Object product1, Object product2)
    {
        int result = -1;
        String symbol1 = null;
        String symbol2 = null;

        symbol1 = ((Product)product1).toString();
        symbol2 = ((Product)product2).toString();
        result = symbol1.compareTo(symbol2);
        return result;
    }
}

