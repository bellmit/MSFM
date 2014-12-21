//
// -----------------------------------------------------------------------------------
// Source file: TimeImpl.java
//
// PACKAGE: com.cboe.presentation.common.dateTime;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.dateTime;

import com.cboe.idl.cmiUtil.TimeStruct;

import com.cboe.interfaces.presentation.common.formatters.DateFormatStrategy;

import com.cboe.presentation.common.formatters.CommonFormatFactory;

/**
 * Represents a basic date as a wrapper for TimeStruct
 */
public class TimeImpl extends com.cboe.domain.dateTime.TimeImpl
{
    private DateFormatStrategy formatter = CommonFormatFactory.getDateFormatStrategy();

    /**
     * Default constructor that initializes to the current date/time.
     */
    public TimeImpl()
    {
        //delegates to super()
    }

    /**
     * Memberwise Constructor
     * @param timeStruct to represent
     * @see com.cboe.idl.cmiUtil.TimeStruct
     */
    public TimeImpl(TimeStruct timeStruct)
    {
        super(timeStruct);
    }

    /**
     * Returns a string representation of the object in the standard format
     * @return a string representation of the object
     */
    public String toString()
    {
        if(stdFormat == null)
        {
            stdFormat = formatter.format(getDate(), DateFormatStrategy.TIME_FORMAT_24_HOURS_SECONDS_STYLE);
        }
        return stdFormat;
    }
}