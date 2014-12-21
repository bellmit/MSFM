//
// -----------------------------------------------------------------------------------
// Source file: ProductReportingClassDefaultSortComparator.java
//
// PACKAGE: com.cboe.presentation.common.comparators;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.comparators;

import java.util.*;

import com.cboe.interfaces.presentation.common.formatters.ProductClassFormatStrategy;
import com.cboe.interfaces.presentation.common.formatters.ReportingClassFormatStrategy;
import com.cboe.interfaces.presentation.product.ProductClass;
import com.cboe.interfaces.presentation.product.ReportingClass;

import com.cboe.presentation.common.formatters.FormatFactory;

/**
 * Compares ProductClass'es and Reporting Class'es by their default format string
 */
public class ProductReportingClassDefaultSortComparator implements Comparator
{
    private ProductClassDefaultSortComparator pdClsComparator;
    private ReportingClassDefaultSortComparator rpClsComparator;
    private ProductClassFormatStrategy pdClsStrategy;
    private String pdClsFormatStyle = null;
    private ReportingClassFormatStrategy rpClsStrategy;
    private String rpClsFormatStyle = null;

    /**
     * Default constructor
     */
    public ProductReportingClassDefaultSortComparator()
    {
        super();

        pdClsComparator = new ProductClassDefaultSortComparator();
        rpClsComparator = new ReportingClassDefaultSortComparator();

        pdClsStrategy = FormatFactory.getProductClassFormatStrategy();
        rpClsStrategy = FormatFactory.getReportingClassFormatStrategy();
    }

    /**
     * Default constructor
     * @param productClassStyle to use for formatting ProductClass'es
     * @param reportingClassStyle to use for formatting ReportingClass'es
     */
    public ProductReportingClassDefaultSortComparator(String productClassStyle, String reportingClassStyle)
    {
        super();

        pdClsComparator = new ProductClassDefaultSortComparator(productClassStyle);
        rpClsComparator = new ReportingClassDefaultSortComparator(reportingClassStyle);

        pdClsStrategy = FormatFactory.getProductClassFormatStrategy();
        rpClsStrategy = FormatFactory.getReportingClassFormatStrategy();
        pdClsFormatStyle = (productClassStyle == null) ? pdClsStrategy.getDefaultStyle():productClassStyle;
        rpClsFormatStyle = (reportingClassStyle == null) ? rpClsStrategy.getDefaultStyle():reportingClassStyle;
    }

    /**
     * Performs comparison on ProductClass vs. ReportingClass.
     */
    public int compare(Object arg1, Object arg2)
    {
        if(arg1 == arg2)
        {
            return 0;
        }
        else
        {
            String string1 = format(arg1);
            String string2 = format(arg2);
            return string1.compareTo(string2);
        }
    }

    private String format(Object obj)
    {
        String retVal = null;
        if (obj instanceof ReportingClass)
        {
            retVal = rpClsStrategy.format((ReportingClass)obj, rpClsFormatStyle);
        }
        else
        {
            retVal = pdClsStrategy.format((ProductClass)obj, pdClsFormatStyle);
        }
        return retVal;
    }
}