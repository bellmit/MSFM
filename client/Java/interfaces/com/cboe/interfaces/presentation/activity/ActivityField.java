//
// -----------------------------------------------------------------------------------
// Source file: ActivityField.java
//
// PACKAGE: com.cboe.presentation.traderActivity
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.activity;

import com.cboe.idl.cmiTraderActivity.ActivityFieldStruct;

import com.cboe.interfaces.presentation.common.formatters.Formattable;

public interface ActivityField extends Formattable
{
    // Format constants
    String NAME_VALUE_FORMAT = "NAME_VALUE_FORMAT";
    String NAME_VALUE_DETAILED_FORMAT = "NAME_VALUE_DETAILED_FORMAT";
    // Format : "Name = Formatted Value"
    String DETAILED_EQUALS_FORMAT = "NAME_VALUE_EQUALS_FORMAT";
    String ACTIVITY_FIELD_NOT_FOUND = "???";

    ActivityFieldStruct getActivityFieldStruct();

    short getFieldTypeCode();

    String getFieldName();

    String getFieldValue();

    String getFormattedFieldValue();

    /**
     * Get the field formatted by the formatSpecified.
     * @param formatSpecified format applied to the field.
     * Format available are {@link com.cboe.presentation.common.formatters.ActivityFieldTypes#TRADERS_FORMAT}
     *  and {@link com.cboe.presentation.common.formatters.ActivityFieldTypes#BOOTH_FORMAT}.
     * @return the field formatted.
     */
    String getFormattedFieldValue(String formatSpecified);

    boolean isPriorityEnforceable();

    String toString();

    String toString(String formatSpecifier);

    boolean isValid();
}