package com.cboe.presentation.common.comparators;

import java.util.Comparator;

import com.cboe.interfaces.presentation.product.*;
import com.cboe.presentation.product.ProductHelper;

/**
 * Created by IntelliJ IDEA.
 * User: HallB
 * Date: Jun 17, 2003
 * Time: 1:31:43 PM *
 */
public class ProductClassComparator implements Comparator
{
    /**
      Implements Comparator.
    */
    public int compare(Object obj1, Object obj2)
    {
        int result = -1;

        if (obj1 instanceof Product && obj2 instanceof Product)
        {
            String symbol1 = ProductHelper.getProductClass(((Product)obj1).getProductKeysStruct().classKey).getClassSymbol();
            String symbol2 = ProductHelper.getProductClass(((Product)obj2).getProductKeysStruct().classKey).getClassSymbol();

            if (symbol1 != null && symbol2 != null)
            {
                result = symbol1.compareTo(symbol2);
            }
        }

        return result;
    }
}
