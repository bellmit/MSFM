package com.cboe.presentation.common.comparators;

import java.util.*;

import com.cboe.idl.cmiUtil.PriceStruct;

import com.cboe.interfaces.domain.Price;

import com.cboe.interfaces.presentation.product.Product;

/**
  @author Will McNabb
 */
public class ProductPriceComparator implements Comparator
{

//    protected static ValuedPriceComparator comp = new ValuedPriceComparator();
    /**
      Implements Comparator.
    */
    public int compare(Object product1, Object product2)
    {
        int result = -1;
        Price ps1 = ((Product)product1).getExercisePrice();
        Price  ps2 = ((Product)product2).getExercisePrice();
        result = compareValue(ps1,ps2);

        return result;
    }
    protected int compareValue(Price firstPrice, Price secondPrice)
    {
        if(firstPrice.lessThan(secondPrice) )
        {
            return -1;
        }
        else if (firstPrice.equals(secondPrice) )
        {
            return 0;
        }
        else
        {
            return 1;
        }
    }
}

