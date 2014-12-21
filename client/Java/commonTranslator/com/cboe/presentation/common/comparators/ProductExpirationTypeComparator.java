//
// -----------------------------------------------------------------------------------
// Source file: ProductExpirationTypeComparator.java
//
// PACKAGE: com.cboe.presentation.common.comparators
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.comparators;

import java.util.*;

import com.cboe.interfaces.presentation.product.Product;
import com.cboe.interfaces.presentation.product.ExpirationType;

public class ProductExpirationTypeComparator implements Comparator
{
    public int compare(Object o1, Object o2)
    {
        int result = -1;

        if(o1 instanceof Product && o2 instanceof Product)
        {

            Product prod1 = (Product) o1;
            Product prod2 = (Product) o2;

            if(prod1.getLeapIndicator() && !prod2.getLeapIndicator()){
                if(prod2.getExpirationType().compareTo(ExpirationType.QUATERLY) <= 0){
                    return 1;
                }
                else if(prod2.getExpirationType().compareTo(ExpirationType.MONTHLY) >= 0){
                    return -1;
                }
            }
            else if (!prod1.getLeapIndicator() && prod2.getLeapIndicator()){
                if (prod1.getExpirationType().compareTo(ExpirationType.QUATERLY) <= 0) {
                    return -1;
                } else if (prod1.getExpirationType().compareTo(ExpirationType.MONTHLY) >= 0) {
                    return 1;
                }
            }
            else if (prod1.getLeapIndicator() && prod2.getLeapIndicator()){
                return 0;
            }
            else{
                result = prod1.getExpirationType().compareTo(prod2.getExpirationType());
            }
        }
        return result;
    }

}
