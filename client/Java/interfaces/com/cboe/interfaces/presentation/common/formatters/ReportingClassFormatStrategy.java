//
// -----------------------------------------------------------------------------------
// Source file: ReportingClassFormatStrategy.java
//
// PACKAGE: com.cboe.interfaces.presentation.common.formatters;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2001 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.common.formatters;

import com.cboe.interfaces.presentation.product.ReportingClass;

/**
 * Defines a contract for a class that formats ReportingClass'es
 */
public interface ReportingClassFormatStrategy extends FormatStrategy
{
    public static final String PLAIN_CLASS_NAME = "Plain Class Name";
    public static final String CLASS_TYPE_NAME =  "Class Name w/ Type";

    public static final String PLAIN_CLASS_NAME_DESCRIPTION = "Plain Class Symbol with nothing added.";
    public static final String CLASS_TYPE_NAME_DESCRIPTION =  "Class Symbol with the class type added.";

    /**
     * Defines a method for formatting ReportingClass
     * @param reportingClass to format
     * @return formatted string
     */
    public String format(ReportingClass reportingClass);

    /**
     * Defines a method for formatting ReportingClass
     * @param reportingClass to format
     * @param styleName to use for formatting
     * @return formatted string
     */
    public String format(ReportingClass reportingClass, String styleName);
}
