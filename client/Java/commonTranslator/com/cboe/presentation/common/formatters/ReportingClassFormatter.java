//
// -----------------------------------------------------------------------------------
// Source file: ReportingClassFormatter.java
//
// PACKAGE: com.cboe.presentation.common.formatters;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2002 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.formatters;

import com.cboe.interfaces.presentation.common.formatters.ReportingClassFormatStrategy;
import com.cboe.interfaces.presentation.common.formatters.FormatterCache;
import com.cboe.interfaces.presentation.product.ReportingClass;
import com.cboe.presentation.common.formatters.Formatter;

/**
 * Implements the ReportingClassFormatStrategy
 */
class ReportingClassFormatter extends Formatter implements ReportingClassFormatStrategy
{
    FormatterCache cache;
    /**
     * Constructor, defines styles and sets initial default style
     */
    public ReportingClassFormatter()
    {
        super();

        addStyle(PLAIN_CLASS_NAME, PLAIN_CLASS_NAME_DESCRIPTION);
        addStyle(CLASS_TYPE_NAME, CLASS_TYPE_NAME_DESCRIPTION);

        setDefaultStyle(PLAIN_CLASS_NAME);

        cache = FormatterCacheFactory.create(5000, 2);
    }

    public ReportingClassFormatter(boolean cacheEnabled)
    {
        this();
        cache.setCacheEnabled(cacheEnabled);
    }
    /**
     * Formats a ReportingClass
     * @param reportingClass to format
     */
    public String format(ReportingClass reportingClass)
    {
        return format(reportingClass, getDefaultStyle());
    }

    /**
     * Formats a ReportingClass
     * @param reportingClass to format
     * @param styleName to use for formatting
     * @return formatted string
     */
    public String format(ReportingClass reportingClass, String styleName) throws IllegalArgumentException
    {
        if(styleName == null || !containsStyle(styleName))
        {
            throw new IllegalArgumentException("Invalid styleName : " + styleName);
        }

        String retVal = cache.get(reportingClass, styleName);

        if (retVal == null)
        {
            StringBuffer classText = new StringBuffer(15);

            if(styleName.equals(PLAIN_CLASS_NAME))
            {
                classText.append(getClassSymbol(reportingClass));
            }
            else if(styleName.equals(CLASS_TYPE_NAME))
            {
                classText.append(getClassSymbol(reportingClass));
                classText.append(" (");
                classText.append(ProductTypes.toString(reportingClass.getProductType().shortValue()));
                classText.append(')');
            }

            retVal = classText.toString();
            cache.put(reportingClass, styleName, retVal);
        }
        return retVal;
    }

    /**
     * Gets the class symbol
     * @param reportingClass to get symbol from
     * @return Class symbol
     */
    private String getClassSymbol(ReportingClass reportingClass)
    {
        return reportingClass.getReportingClassSymbol();
    }
}
