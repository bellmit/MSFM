package com.cboe.presentation.common.comparators;

import java.util.*;

import com.cboe.interfaces.presentation.product.Product;
import com.cboe.interfaces.presentation.product.Strategy;

/**
  @author Will McNabb
 */
public class ProductSymbolComparator implements Comparator
{
    /**
      Implements Comparator.
    */
    public int compare(Object obj1, Object obj2)
    {
        int result = -1;

        if (obj1 instanceof Strategy && obj2 instanceof Strategy)
        {
            result = compare((Strategy)obj1, (Strategy)obj2);
        }
        else if (obj1 instanceof Product && obj2 instanceof Product)
        {
            result = compare((Product)obj1, (Product)obj2);
        }
        return result;
    }

    private int compare(Strategy strategy1, Strategy strategy2)
    {
        int result = -1;
        String symbol1 = strategy1.getProductNameStruct().productSymbol;
        String symbol2 = strategy2.getProductNameStruct().productSymbol;
        if (symbol1 != null && symbol2 != null)
        {
            result = symbol1.compareTo(symbol2);
        }
        return result;
    }

    private int compare(Product product1, Product product2)
    {
        int result = -1;
        String symbol1 = product1.getProductNameStruct().reportingClass;
        String symbol2 = product2.getProductNameStruct().reportingClass;
        if (symbol1 != null && symbol2 != null)
        {
            result = symbol1.compareTo(symbol2);
        }
        return result;
    }

}

