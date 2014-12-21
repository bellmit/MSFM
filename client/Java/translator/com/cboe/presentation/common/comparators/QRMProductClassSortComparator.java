package com.cboe.presentation.common.comparators;
// -----------------------------------------------------------------------------------
// Source file: QRMProductClassSortComparator
//
// PACKAGE: com.cboe.presentation.common.comparators
// 
// Created: Mar 24, 2006 9:30:30 AM
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
import java.util.*;

import com.cboe.interfaces.presentation.product.ProductClass;
import com.cboe.presentation.api.APIHome;
import com.cboe.presentation.common.exceptionHandling.DefaultExceptionHandlerHome;

public class QRMProductClassSortComparator implements Comparator
{
    public int compare(Object o1, Object o2)
    {
        int result;

        if (o1 instanceof ProductClass && o2 instanceof ProductClass)
        {
            result = compareTwoProductClasses((ProductClass) o1, (ProductClass) o2);
        }
        else if (o1 instanceof Integer && o2 instanceof Integer)
        {
            Integer pckey1 = (Integer) o1;
            Integer pckey2 = (Integer) o2;
            try
            {
                int pcInt1 = pckey1.intValue();
                int pcInt2 = pckey2.intValue();
                ProductClass prodClass1 = APIHome.findProductQueryAPI().getProductClassByKey(pcInt1);
                ProductClass prodClass2 = APIHome.findProductQueryAPI().getProductClassByKey(pcInt2);
                result = compareTwoProductClasses(prodClass1, prodClass2);
            }
            catch (Exception e)
            {
                DefaultExceptionHandlerHome.find().process(e);
                result = pckey1.compareTo(pckey2);
            }
        }
        else
        {
            String s1 = o1.toString();
            String s2 = o2.toString();

            result =  s1.compareTo(s2);
        }

        return result;
    }

    private int compareTwoProductClasses(ProductClass productClass1, ProductClass productClass2)
    {
        int compareResult;
        if (!productClass1.isValid() || productClass1.isDefaultProductClass())
        {
            compareResult = -1;
        }
        else if (!productClass2.isValid() || productClass2.isDefaultProductClass())
        {
            compareResult = 1;
        }
        else
        {
            compareResult = productClass1.getClassSymbol().compareToIgnoreCase(productClass2.getClassSymbol());
            // If class symbols are the same, sort on product type
            if (compareResult == 0 && productClass1.getClassKey() != productClass2.getClassKey())
            {
                compareResult = productClass1.getProductType() < productClass2.getProductType() ? -1 : 1;
            }
        }
        return compareResult;
    }
}
