//
// -----------------------------------------------------------------------------------
// Source file: ProductMaturityDateComparator.java
//
// PACKAGE: com.cboe.presentation.common.comparators;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2002 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.comparators;

import com.cboe.idl.cmiUtil.DateStruct;

import com.cboe.interfaces.presentation.product.Product;

/**
 * Compares expiration dates for products.  It compares the year, month, AND DAY
 * of the expirations, since there are Weekly expiring products that would have
 * the same month and year as other products, but with a different day of the
 * month.
 */
public class ProductMaturityDateComparator extends DateStructComparator
{
    /**
     * Passes the two Products' exiration dates to super.compare() and returns the result.
     */
    public int compare(Object product1, Object product2)
    {
        DateStruct date1 = ((Product) product1).getProductNameStruct().expirationDate;
        DateStruct date2 = ((Product) product2).getProductNameStruct().expirationDate;

        int result = super.compare(date1, date2);
        return result;
    }
}