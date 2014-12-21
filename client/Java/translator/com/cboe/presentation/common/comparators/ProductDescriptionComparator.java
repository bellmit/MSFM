package com.cboe.presentation.common.comparators;

import java.util.*;

import com.cboe.interfaces.presentation.product.Product;

/**
  @author Will McNabb
 */
public class ProductDescriptionComparator implements Comparator
{
    /**
      Implements Comparator.
    */
    public int compare(Object product1, Object product2)
    {
        int result = -1;
        String description1 = ((Product)product1).getDescription();
        String description2 = ((Product)product2).getDescription();
        result = description1.compareTo(description2);
        return result;
    }
}

