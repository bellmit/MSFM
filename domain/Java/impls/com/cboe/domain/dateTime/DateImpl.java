//
// -----------------------------------------------------------------------------------
// Source file: DateImpl.java
//
// PACKAGE: com.cboe.domain.dateTime;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.domain.dateTime;

import com.cboe.idl.cmiUtil.DateStruct;

import com.cboe.interfaces.domain.dateTime.Date;

import com.cboe.domain.util.DateWrapper;

/**
 * Represents a basic date as a wrapper for DateStruct
 */
public class DateImpl implements Date
{
    protected String stdFormat;

    private java.util.Date date;
    private DateStruct dateStruct;

    /**
     * Default constructor that initializes to the current date/time.
     */
    public DateImpl()
    {
        DateStruct struct = DateWrapper.convertToDate(System.currentTimeMillis());
        dateStruct = struct;
    }

    /**
     * Memberwise Constructor
     * @param dateStruct to represent
     * @see com.cboe.idl.cmiUtil.DateStruct
     */
    public DateImpl(DateStruct dateStruct)
    {
        if(dateStruct == null)
        {
            throw new IllegalArgumentException("DateStruct may not be null.");
        }
        this.dateStruct = new DateStruct(dateStruct.month, dateStruct.day, dateStruct.year);
    }

    protected Object clone()
            throws CloneNotSupportedException
    {
        DateImpl newImpl = (DateImpl) super.clone();
        DateStruct newStruct = new DateStruct(dateStruct.month, dateStruct.day, dateStruct.year);
        newImpl.dateStruct = newStruct;

        return newImpl;
    }

    public boolean equals(Object o)
    {
        if(this == o)
        {
            return true;
        }
        if(!(o instanceof Date))
        {
            return false;
        }

        final Date date = (Date) o;

        if(!getDate().equals(date.getDate()))
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
        long theirValue = ((Date) object).getDate().getTime();
        result = (myValue < theirValue ? -1 : (myValue == theirValue ? 0 : 1));
        return result;
    }

    /**
     * Returns the DateStruct that this object represents
     * @deprecated This method exists primarily for backwards compatability reasons.
     * Please use the wrapper objects whenever possible.
     * @see com.cboe.idl.cmiUtil.DateStruct
     */
    public DateStruct getDateStruct()
    {
        DateStruct newStruct = new DateStruct(dateStruct.month, dateStruct.day, dateStruct.year);
        return newStruct;
    }

    /**
     * Returns the day of the month
     */
    public int getDay()
    {
        return (int) getDateStruct().day;
    }

    /**
     * Returns the month of the year
     */
    public int getMonth()
    {
        return (int) getDateStruct().month;
    }

    /**
     * Returns the year
     */
    public int getYear()
    {
        return (int) getDateStruct().year;
    }

    /**
     * Gets this Date as a java.util.Date
     */
    public java.util.Date getDate()
    {
        if(date == null)
        {
            date = new java.util.Date(DateWrapper.convertToMillis(getDateStruct()));
        }
        return date;
    }
}