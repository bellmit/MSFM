package com.cboe.presentation.common.comparators;

import java.util.*;

import com.cboe.interfaces.presentation.product.Product;

/**
  @author Will McNabb
 */
public class ProductOptionTypeComparator extends OptionTypeComparator
{

    /**
      Implements Comparator.
    */
    public int compare(Object product1, Object product2)
    {
        int result = -1;
        result = compareOptionTypes(((Product)product1).getProductNameStruct().optionType, ((Product)product2).getProductNameStruct().optionType );
        return result;
    }
}

