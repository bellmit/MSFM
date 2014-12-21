//
// -----------------------------------------------------------------------------------
// Source file: FormattableDateTimeImpl.java
//
// PACKAGE: com.cboe.presentation.common.dateTime;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2008 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.dateTime;

import java.util.*;

import com.cboe.idl.cmiUtil.DateTimeStruct;
import com.cboe.interfaces.presentation.common.formatters.DateFormatStrategy;
import com.cboe.interfaces.presentation.omt.FormattableDateTime;
import com.cboe.presentation.common.formatters.CommonFormatFactory;

/**
 * Allows flexible formatting of a DateTime object
 */
public class FormattableDateTimeImpl extends com.cboe.presentation.common.dateTime.DateTimeImpl 
   implements FormattableDateTime
{
    private static final DateFormatStrategy formatter = CommonFormatFactory.getDateFormatStrategy();

    /**
     *
     * Default constructor that initializes to the current date/time.
     */
    public FormattableDateTimeImpl()
    {
        //delegates to super()
    }

    /**
     * Memberwise Constructor
     * @param dateTimeStruct to represent
     */
    public FormattableDateTimeImpl(DateTimeStruct dateTimeStruct)
    {
        super(dateTimeStruct);
    }

    /**
     * Memberwise Constructor that creates a represented DateTimeStruct from the parameters
     */
    public FormattableDateTimeImpl(int year,
                                   int month,
                                   int date,
                                   int hour,
                                   int minute,
                                   int second,
                                   int millisecond)
    {
        super(year, month, date, hour, minute, second, millisecond);
    }

    /**
     * Memberwise Constructor that creates a DateTimeStruct from the long passed in
     */
    public FormattableDateTimeImpl(long timeInMillis)
    {
        super(timeInMillis);
    }

    /**
     * Returns a string representation of the object in specified format
     * @return a string representation of the object
     */
    public String toString(String displayFormat)
    {
        return formatter.format(new Date(getTimeInMillis()), displayFormat);
    }
}