//
// -----------------------------------------------------------------------------------
// Source file: ProductClassKeyInOrderComparator.java
//
// PACKAGE: com.cboe.presentation.product;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.comparators;

import java.util.Comparator;

import com.cboe.interfaces.presentation.product.ProductClass;

/**
 * Compares ProductClass'es by their keys and returns them in order
 * by the integer key.
 */
public class ProductClassKeyInOrderComparator implements Comparator
{
    /**
     * Default constructor
     */
    public ProductClassKeyInOrderComparator()
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
            ProductClass class1 = (ProductClass)arg1;
            ProductClass class2 = (ProductClass)arg2;

            if(class1.getClassKey() > class2.getClassKey())
            {
                return 1;
            }
            else if(class1.getClassKey() < class2.getClassKey())
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
