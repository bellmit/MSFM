//
// -----------------------------------------------------------------------------------
// Source file: GUILoggerPropertyFormatStrategy.java
//
// PACKAGE: com.cboe.interfaces.presentation.common.formatters;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2001 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.common.formatters;

import com.cboe.interfaces.presentation.common.logging.IGUILoggerProperty;

/**
 * Defines a contract for a class that formats IGUILoggerProperty.
 * @author Alex Brazhnichenko
 */
public interface GUILoggerPropertyFormatStrategy extends FormatStrategy
{
    public static final String BRIEF_NAME = "Brief Name";
    public static final String BRIEF_NAME_AND_VALUE = "Breif Name and Value";
    public static final String FULL_INFORMATION = "Full Information";

    public static final String BRIEF_NAME_DESCRIPTION = "Brief name information.";
    public static final String BRIEF_NAME_AND_VALUE_DESCRIPTION = "Brief name and value information.";
    public static final String FULL_INFORMATION_DESCRIPTION ="Full property information.";

    /**
     * Defines a method for formatting IGUILoggerProperty.
     * @param property to format.
     * @param styleName to use for formatting.
     * @return formatted string
     */
    public String format(IGUILoggerProperty property, String styleName);
}
