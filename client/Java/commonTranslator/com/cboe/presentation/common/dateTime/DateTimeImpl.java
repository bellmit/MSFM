//
// -----------------------------------------------------------------------------------
// Source file: DateTimeImpl.java
//
// PACKAGE: com.cboe.presentation.common.dateTime;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.dateTime;

import java.util.*;

import com.cboe.idl.cmiUtil.DateTimeStruct;
import com.cboe.idl.cmiUtil.DateStruct;

import com.cboe.interfaces.presentation.common.formatters.DateFormatStrategy;

import com.cboe.presentation.common.formatters.CommonFormatFactory;
import com.cboe.domain.util.DateWrapper;

/**
 * Represents a basic date as a wrapper for DateTimeStruct
 */
public class DateTimeImpl extends com.cboe.domain.dateTime.DateTimeImpl
{
    private static final DateFormatStrategy formatter = CommonFormatFactory.getDateFormatStrategy();
    private int hashCode = -1;

    /**
     * Default constructor that initializes to the current date/time.
     */
    public DateTimeImpl()
    {
        //delegates to super()
    }

    /**
     * Memberwise Constructor
     * @param dateTimeStruct to represent
     */
    public DateTimeImpl(DateTimeStruct dateTimeStruct)
    {
        super(dateTimeStruct);
    }

    /**
     * Memberwise Constructor that creates a represented DateTimeStruct from the parameters
     */
    public DateTimeImpl(int year,
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
    public DateTimeImpl(long timeInMillis)
    {
        super(timeInMillis);
    }

    protected DateStruct cloneDateStruct(DateStruct dateStruct)
    {
        DateStruct cachedDateStruct = DateTimeFactory.getDate(dateStruct).getDateStruct();
        return cachedDateStruct;
    }

    /**
     * Returns a string representation of the object in standard format
     * @return a string representation of the object
     */
    public String toString()
    {
        if(stdFormat == null)
        {
            stdFormat = formatter.format(new Date(getTimeInMillis()), DateFormatStrategy.DATE_FORMAT_24_HOURS_STYLE);
        }
        return stdFormat;
    }

    public int hashCode()
    {
        // this is immutable, so don't need to calculate hashCode from Calendar every time
        if(hashCode == -1)
        {
            hashCode = getCalendar().hashCode();
        }
        return hashCode;
    }

    public Calendar getCalendar()
    {
        // not holding onto the Calendar to release memory
        DateWrapper dateWrapper = new DateWrapper(getDateTimeStruct());
        Calendar calendar = dateWrapper.getNewCalendar();
        // calculate the hashCode the first time, to avoid creating a new Calendar everytime hashCode() is called
        if (hashCode == -1)
        {
            hashCode = calendar.hashCode();
        }

        return calendar;
    }
}