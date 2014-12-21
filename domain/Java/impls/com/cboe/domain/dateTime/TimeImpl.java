//
// -----------------------------------------------------------------------------------
// Source file: TimeImpl.java
//
// PACKAGE: com.cboe.domain.dateTime;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.domain.dateTime;

import java.util.*;

import com.cboe.idl.cmiUtil.TimeStruct;

import com.cboe.interfaces.domain.dateTime.Time;

import com.cboe.domain.util.DateWrapper;

/**
 * Represents a basic date as a wrapper for TimeStruct
 */
public class TimeImpl implements Time
{
    protected String stdFormat;

    private Date date;
    private TimeStruct timeStruct;

    /**
     * Default constructor that initializes to the current date/time.
     */
    public TimeImpl()
    {
        TimeStruct struct = DateWrapper.convertToTime(System.currentTimeMillis());
        timeStruct = struct;
    }

    /**
     * Memberwise Constructor
     * @param timeStruct to represent
     * @see com.cboe.idl.cmiUtil.TimeStruct
     */
    public TimeImpl(TimeStruct timeStruct)
    {
        if(timeStruct == null)
        {
            throw new IllegalArgumentException("TimeStruct may not be null.");
        }
        this.timeStruct = new TimeStruct(timeStruct.hour, timeStruct.minute, timeStruct.second, timeStruct.fraction);
    }

    protected Object clone()
            throws CloneNotSupportedException
    {
        TimeImpl newImpl = (TimeImpl) super.clone();
        TimeStruct newStruct =
                new TimeStruct(timeStruct.hour, timeStruct.minute, timeStruct.second, timeStruct.fraction);
        newImpl.timeStruct = newStruct;

        return newImpl;
    }

    public boolean equals(Object o)
    {
        if(this == o)
        {
            return true;
        }
        if(!(o instanceof Time))
        {
            return false;
        }

        final Time time = (Time) o;

        if(!getDate().equals(time.getDate()))
        {
            return false;
        }

        return true;
    }

    public int hashCode()
    {
        return getDate().hashCode();
    }

    /**
     * Returns a string representation of the object in the standard format
     * @return a string representation of the object
     */
    public String toString()
    {
        if(stdFormat == null)
        {
            stdFormat = STD_DATE_FORMAT.format(getDate());
        }
        return stdFormat;
    }

    public int compareTo(Object object)
    {
        int result;
        long myValue = getDate().getTime();
        long theirValue = ((Time) object).getDate().getTime();
        result = (myValue < theirValue ? -1 : (myValue == theirValue ? 0 : 1));
        return result;
    }

    /**
     * Returns the TimeStruct that this object represents
     * @deprecated This method exists primarily for backwards compatability reasons.
     * Please use the wrapper objects whenever possible.
     * @see com.cboe.idl.cmiUtil.TimeStruct
     */
    public TimeStruct getTimeStruct()
    {
        TimeStruct newStruct =
                new TimeStruct(timeStruct.hour, timeStruct.minute, timeStruct.second, timeStruct.fraction);
        return newStruct;
    }

    /**
     * Returns the hour of the day
     */
    public int getHour()
    {
        return (int) getTimeStruct().hour;
    }

    /**
     * Returns the minute of the hour
     */
    public int getMinute()
    {
        return (int) getTimeStruct().minute;
    }

    /**
     * Returns the second of the minute
     */
    public int getSecond()
    {
        return (int) getTimeStruct().second;
    }

    /**
     * Returns the milliseconds of the second
     */
    public int getMillisecond()
    {
        return (int) getTimeStruct().fraction;
    }

    /**
     * Gets this Date as a java.util.Date
     */
    public Date getDate()
    {
        if(date == null)
        {
            date = new Date(DateWrapper.convertToMillis(getTimeStruct()));
        }
        return date;
    }
}