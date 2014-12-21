//
// -----------------------------------------------------------------------------------
// Source file: ReportingClassDefaultSortComparator.java
//
// PACKAGE: com.cboe.presentation.common.comparators;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2002 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.comparators;

import java.util.*;

import com.cboe.interfaces.presentation.common.formatters.FormatStrategy;
import com.cboe.interfaces.presentation.common.formatters.ReportingClassFormatStrategy;
import com.cboe.interfaces.presentation.product.ReportingClass;

import com.cboe.presentation.common.formatters.FormatFactory;

/**
 * ReportingClass comparator using formatter for contents to compare
 */
public class ReportingClassDefaultSortComparator implements Comparator, FormatStrategy
{
    private ReportingClassFormatStrategy strategy;
    private String formatStyle = null;

    /**
     * Default constructor
     */
    public ReportingClassDefaultSortComparator()
    {
        super();
        strategy = FormatFactory.getReportingClassFormatStrategy();
    }

    /**
     * Constructor for the ReportingClassDefaultSortComparator object
     * @param style to use for formatting
     */
    public ReportingClassDefaultSortComparator(String style)
    {
        this();
        setFormatStyle(style);
    }

    /**
     * Compares the formatted strings
     */
    public int compare(Object o1, Object o2)
    {
        String style = (formatStyle == null) ? getDefaultStyle():formatStyle;
        return strategy.format((ReportingClass)o1, style).compareTo(strategy.format((ReportingClass)o2, style));
    }

    /**
     * Sets the style name to use for formatting with the ProductClassFormatStrategy
     * @param style to use for formatting
     * @exception IllegalArgumentException styleName was not valid for a ProductClassFormatStrategy
     */
    public void setFormatStyle(String styleName) throws IllegalArgumentException
    {
        if(styleName == null || !containsStyle(styleName))
        {
            throw new IllegalArgumentException("Invalid styleName : " + styleName);
        }

        this.formatStyle = styleName;
    }

    /**
     * Determines if the passed style is present in the ProductClassFormatStrategy.
     * @param styleName to test.
     */
    public boolean containsStyle(String styleName)
    {
        return strategy.containsStyle(styleName);
    }

    /**
     * Gets the DefaultStyle attribute of the ProductClassFormatStrategy object
     * @return DefaultStyle value
     */
    public String getDefaultStyle()
    {
        return strategy.getDefaultStyle();
    }

    /**
     * Gets the FormatStyles attribute of the ProductClassFormatStrategy object
     * @return FormatStyles value
     */
    public Map getFormatStyles()
    {
        return strategy.getFormatStyles();
    }
}