//
// -----------------------------------------------------------------------------------
// Source file: ProductClassSymbolInOrderComparator.java
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
 * Compares ProductClass'es by their symbols and returns them in alpha order
 * by the symbol
 */
public class ProductClassSymbolInOrderComparator implements Comparator
{
    /**
     * Default constructor
     */
    public ProductClassSymbolInOrderComparator()
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

            return class1.getClassSymbol().compareTo(class2.getClassSymbol());
        }
    }
}
