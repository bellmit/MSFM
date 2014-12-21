//
// -----------------------------------------------------------------------------------
// Source file: ProductKeyInOrderComparator.java
//
// PACKAGE: com.cboe.presentation.product;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2001 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.comparators;

import java.util.Comparator;

import com.cboe.interfaces.presentation.product.Product;

/**
 * Compares Products by their keys and returns them in order
 * by the integer key.
 */
public class ProductKeyInOrderComparator implements Comparator
{
    /**
     * Default constructor
     */
    public ProductKeyInOrderComparator()
    {
        super();
    }

    /**
     * Performs comparison on ProductClass symbol.
     */
    public int compare(Object arg1, Object arg2)
    {
        if(arg1 == arg2)
        {
            return 0;
        }
        else
        {
            Product product1 = (Product)arg1;
            Product product2 = (Product)arg2;

            if(product1.getProductKey() > product2.getProductKey())
            {
                return 1;
            }
            else if(product1.getProductKey() < product2.getProductKey())
            {
                return -1;
            }
            else
            {
                return 0;
            }
        }
    }
}
