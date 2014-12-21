package com.cboe.presentation.common.comparators;

import java.util.*;

import com.cboe.idl.cmiUtil.DateStruct;

import com.cboe.interfaces.presentation.product.Product;

/**
  @author Will McNabb
 */
public class ProductActivationDateComparator implements Comparator
{
    protected static DateStructComparator comp = new DateStructComparator();
    /**
      Implements Comparator.
    */
    public int compare(Object product1, Object product2)
    {
        int result = -1;
        DateStruct ds1 = ((Product)product1).getActivationDate();
        DateStruct ds2 = ((Product)product2).getActivationDate();
        result = comp.compare(ds1, ds2);
        return result;
    }
}

