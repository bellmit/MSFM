//
// -----------------------------------------------------------------------------------
// Source file: DateImpl.java
//
// PACKAGE: com.cboe.presentation.common.dateTime;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.dateTime;

import com.cboe.idl.cmiUtil.DateStruct;

import com.cboe.interfaces.presentation.common.formatters.DateFormatStrategy;

import com.cboe.presentation.common.formatters.CommonFormatFactory;

/**
 * Represents a basic date as a wrapper for DateStruct
 */
public class DateImpl extends com.cboe.domain.dateTime.DateImpl
{
    private DateFormatStrategy formatter = CommonFormatFactory.getDateFormatStrategy();

    /**
     * Default constructor that initializes to the current date/time.
     */
    public DateImpl()
    {
        //delegates to super()
    }

    /**
     * Memberwise Constructor
     * @param dateStruct to represent
     * @see com.cboe.idl.cmiUtil.DateStruct
     */
    public DateImpl(DateStruct dateStruct)
    {
        super(dateStruct);
    }

    /**
     * Returns a string representation of the object in the standard format
     * @return a string representation of the object
     */
    public String toString()
    {
        if(stdFormat == null)
        {
            stdFormat = formatter.format(getDate(), DateFormatStrategy.DATE_FORMAT_SHORT_STYLE);
        }
        return stdFormat;
    }
}