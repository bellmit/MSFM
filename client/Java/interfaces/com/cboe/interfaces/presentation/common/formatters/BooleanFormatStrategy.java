//
// -----------------------------------------------------------------------------------
// Source file: BooleanFormatStrategy.java
//
// PACKAGE: com.cboe.interfaces.presentation.common.formatters
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.common.formatters;

/**
 * Defines a contract for a class that formats a boolean represented by a number.
 *
 * @author Thomas Morrow
 */
public interface BooleanFormatStrategy extends FormatStrategy
{
    String UPPER_CASE_FORMAT = "UPPER_CASE";
    String UPPER_CASE_FORMAT_DESC = "Upper Case";
    String CAPITALIZED_FORMAT = "CAPITALIZED";
    String CAPITALIZED_FORMAT_DESC = "Capitalized";
    String TRUE_STRING = "True";
    String FALSE_STRING = "False";

    String format(Short aShort);

    String format(Short aShort, String styleName);
}