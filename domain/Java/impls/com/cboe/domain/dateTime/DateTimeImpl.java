//
// -----------------------------------------------------------------------------------
// Source file: DateTimeImpl.java
//
// PACKAGE: com.cboe.domain.dateTime;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.domain.dateTime;

import java.util.Calendar;

import com.cboe.idl.cmiUtil.DateStruct;
import com.cboe.idl.cmiUtil.DateTimeStruct;
import com.cboe.idl.cmiUtil.TimeStruct;

import com.cboe.interfaces.domain.dateTime.DateTime;
import com.cboe.interfaces.domain.dateTime.Date;
import com.cboe.interfaces.domain.dateTime.Time;

import com.cboe.domain.util.DateWrapper;

/**
 * Represents a basic date as a wrapper for DateTimeStruct
 */
public class DateTimeImpl implements DateTime
{
    protected String stdFormat;

    private DateTimeStruct dateTimeStruct;
    private long millis;
    private Calendar calendar;

    /**
     * Default constructor that initializes to the current date/time.
     */
    public DateTimeImpl()
    {
        this(System.currentTimeMillis());
    }

    /**
     * Memberwise Constructor
     * @param dateTimeStruct to represent
     */
    public DateTimeImpl(DateTimeStruct dateTimeStruct)
    {
        if(dateTimeStruct == null)
        {
            throw new IllegalArgumentException("DateTimeStruct may not be null.");
        }

        this.dateTimeStruct = cloneDateTimeStruct(dateTimeStruct);
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
        DateStruct dateStruct = new DateStruct((byte) month, (byte) date, (short) year);
        TimeStruct timeStruct = new TimeStruct((byte) hour, (byte) minute, (byte) second, (byte) (millisecond / 10));
        dateTimeStruct = new DateTimeStruct(dateStruct, timeStruct);
    }

    /**
     * Memberwise Constructor that creates a DateTimeStruct from the long passed in
     */
    public DateTimeImpl(long timeInMillis)
    {
        this(DateWrapper.convertToDateTime(timeInMillis));
        millis = timeInMillis;
    }

    protected Object clone()
            throws CloneNotSupportedException
    {
        DateTimeImpl newImpl = (DateTimeImpl) super.clone();

        DateStruct dateStruct = dateTimeStruct.date;
        DateStruct newDateStruct = new DateStruct(dateStruct.month, dateStruct.day, dateStruct.year);

        TimeStruct timeStruct = dateTimeStruct.time;
        TimeStruct newTimeStruct =
                new TimeStruct(timeStruct.hour, timeStruct.minute, timeStruct.second, timeStruct.fraction);

        newImpl.dateTimeStruct = new DateTimeStruct(newDateStruct, newTimeStruct);

        return newImpl;
    }

    protected DateTimeStruct cloneDateTimeStruct(DateTimeStruct dateTimeStruct)
    {
        DateStruct newDateStruct = cloneDateStruct(dateTimeStruct.date);
        TimeStruct newTimeStruct = cloneTimeStruct(dateTimeStruct.time);
        return new DateTimeStruct(newDateStruct, newTimeStruct);
    }

    protected DateStruct cloneDateStruct(DateStruct dateStruct)
    {
        DateStruct newDateStruct = new DateStruct(dateStruct.month, dateStruct.day, dateStruct.year);
        return newDateStruct;
    }

    protected TimeStruct cloneTimeStruct(TimeStruct timeStruct)
    {
        TimeStruct newTimeStruct =
                new TimeStruct(timeStruct.hour, timeStruct.minute, timeStruct.second, timeStruct.fraction);
        return newTimeStruct;
    }

    public boolean equals(Object o)
    {
        if(this == o)
        {
            return true;
        }
        if(!(o instanceof DateTime))
        {
            return false;
        }

        final DateTime dateTime = (DateTime) o;

        if(!(getTimeInMillis() == dateTime.getTimeInMillis()))
        {
            return false;
        }

        return true;
    }

    public int hashCode()
    {
        return getCalendar().hashCode();
    }

    /**
     * Returns a string representation of the object in standard format
     * @return a string representation of the object
     */
    public String toString()
    {
        if(stdFormat == null)
        {
            stdFormat = STD_DATE_FORMAT.format(new java.util.Date(getTimeInMillis()));
        }
        return stdFormat;
    }

    public int compareTo(Object object)
    {
        int result;
        long myValue = getTimeInMillis();
        long theirValue = ((DateTime) object).getTimeInMillis();
        result = (myValue < theirValue ? -1 : (myValue == theirValue ? 0 : 1));
        return result;
    }

    /**
     * Returns the DateTimeStruct that this object represents
     * @see com.cboe.idl.cmiUtil.DateTimeStruct
     * @deprecated This method exists primarily for backwards compatability reasons. Please use the wrapper objects
     *             whenever possible.
     */
    public DateTimeStruct getDateTimeStruct()
    {
        DateStruct dateStruct = dateTimeStruct.date;
        DateStruct newDateStruct = new DateStruct(dateStruct.month, dateStruct.day, dateStruct.year);

        TimeStruct timeStruct = dateTimeStruct.time;
        TimeStruct newTimeStruct =
                new TimeStruct(timeStruct.hour, timeStruct.minute, timeStruct.second, timeStruct.fraction);

        DateTimeStruct newStruct = new DateTimeStruct(newDateStruct, newTimeStruct);

        return newStruct;
    }

    /**
     * Returns the date
     */
    public Date getDate()
    {
        return new DateImpl(getDateTimeStruct().date);
    }

    /**
     * Returns the time
     */
    public Time getTime()
    {
        return new TimeImpl(getDateTimeStruct().time);
    }

    /**
     * Returns the date in Calendar format
     */
    public Calendar getCalendar()
    {
        if(calendar == null)
        {
            DateWrapper dateWrapper = new DateWrapper(getDateTimeStruct());
            calendar = dateWrapper.getNewCalendar();
        }
        return calendar;
    }

    /**
     * Returns the time as UTC milliseconds from the epoch
     */
    public long getTimeInMillis()
    {
        if(millis == 0)
        {
            millis = DateWrapper.convertToMillis(getDateTimeStruct());
        }
        return millis;
    }
}