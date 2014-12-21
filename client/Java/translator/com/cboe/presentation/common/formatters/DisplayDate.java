/*
 *  Copyright 2000
 *
 *  CBOE
 *  All rights reserved
 */
package com.cboe.presentation.common.formatters;
import com.cboe.idl.cmiUtil.*;
import com.cboe.domain.util.DateWrapper;

import com.cboe.interfaces.presentation.common.formatters.DateFormatStrategy;

import java.text.*;

import java.util.*;

/**
 *  Description of the Class
 *
 *@author     Nick DePasquale
 *@created    May 2, 2001
 */
public class DisplayDate implements Comparator
{

    /**
     *  Description of the Field
     */
    protected DateWrapper dateWrapper = null;
    private String formattedDate = null;
    private static final DateFormatStrategy defaultDateFormatter = FormatFactory.getDateFormatStrategy();
    private DateFormatStrategy dateFormatter = defaultDateFormatter;


    /**
     *  Constructor for the DisplayDate object
     *
     *@param  aDisplayDate  Description of Parameter
     */
    public DisplayDate(DisplayDate aDisplayDate)
    {
        this();
        if (aDisplayDate != null)
        {
            setDateWrapper(aDisplayDate.getDateWrapper());
        }
        else
        {
            setDateWrapper(new DateWrapper());
        }

    }

    /**
     *  Constructor for the DisplayDate object
     *
     *@param  aDisplayWrapper  Description of Parameter
     */
    public DisplayDate(DateWrapper aDisplayWrapper)
    {
        this();
        setDateWrapper(aDisplayWrapper);
    }


    /**
     *  Creates an instance with the given time.
     *
     *@param  timeInMillis  time for instance
     */
    public DisplayDate(long timeInMillis)
    {
        this();
        setDateWrapper(new DateWrapper(timeInMillis));
    }

    /**
     *  Creates an instance using the date values. The time will default to
     *  midnight.
     *
     *@param  date  DateStruct
     */
    public DisplayDate(DateStruct date)
    {
        this(new DateWrapper(date));
    }

    /**
     *  Creates an instance using the date/time values.
     *
     *@param  dateTime  CORBA struct containing date and time
     */
    public DisplayDate(DateTimeStruct dateTime)
    {
        this(new DateWrapper(dateTime));
    }

    /**
     *  Creates an instance using the time values. The date will be set tp the
     *  current day.
     *
     *@param  time  CORBA struct containing time values
     */
    public DisplayDate(TimeStruct time)
    {
        this(new DateWrapper(time));
    }

    /**
     *  Creates a <code>DateWrapper</code> from a <code>Date</code> .
     *
     *@param  aDate  date to be wrapped
     */
    public DisplayDate(Date aDate)
    {
        this(new DateWrapper(aDate));
    }

    /**
     *  Creates a instance set to the current time.
     */
    private DisplayDate()
    {
        initialize();
    }

    /**
     *  Sets the value of this wrapper from a <code>DateStruct</code> . The time
     *  portion of this wrapper will be set to midnight.
     *
     *@param  newDate  new date value
     */
    public void setDate(DateStruct newDate)
    {
        getDateWrapper().setDate(newDate);
        reInitalizeData();
    }

    /**
     *  Sets the value of this wrapper from a <code>Date</code> .
     *
     *@param  newDate  new date value
     */
    public void setDate(java.util.Date newDate)
    {
        getDateWrapper().setDate(newDate);
        reInitalizeData();
    }

    /**
     *  Sets the value of this wrapper from a <code>DateTimeStruct</code> .
     *
     *@param  dateTime  new date/time value
     */
    public void setDateTime(DateTimeStruct dateTime)
    {
        getDateWrapper().setDateTime(dateTime);
        reInitalizeData();
    }

    /**
     *  Sets the time value of this wrapper from a <code>TimeStruct</code> . The
     *  date portion of this wrapper will be unchanged.
     *
     *@param  time  new time value
     */
    public void setTime(TimeStruct time)
    {
        getDateWrapper().setTime(time);
        reInitalizeData();
    }

    /**
     *  Sets the value of this wrapper from a time in milliseconds.
     *
     *@param  time  time in milliseconds
     */
    public void setTimeInMillis(long time)
    {
        getDateWrapper().setTimeInMillis(time);
        reInitalizeData();
    }

    /**
     *  Sets the DateWrapper attribute of the DisplayDate object
     *
     *@param  aDateWrapper  The new DateWrapper value
     */
    public void setDateWrapper(DateWrapper aDateWrapper)
    {
        this.dateWrapper = aDateWrapper;
        reInitalizeData();
    }

    /**
     *  Returns the <code>Date</code> of this wrapper.
     *
     *@return    date of this wrapper
     */
    public Date getDate()
    {
        return getDateWrapper().getDate();
    }

    /**
     *  Description of the Method
     *
     *@param  displayDate         Description of Parameter
     *@param  anotherDisplayDate  Description of Parameter
     *@return                     Description of the Returned Value
     */
    public int compare(Object displayDate, Object anotherDisplayDate)
    {
        int result = -1;

        if (!equals(anotherDisplayDate))
        {
            result = ( ((DisplayDate)displayDate).getDateWrapper().compareTo( ((DisplayDate)anotherDisplayDate).getDateWrapper()));

            if (result == 0)
            {
                result = -1;
            }
        }
        return result;
    }

    /**
     *  Returns a <code>DateStruct</code> that represents the date portion of
     *  this wrapper.
     *
     *@return    struct containing date values
     */
    public DateStruct toDateStruct()
    {
        return this.dateWrapper.toDateStruct();
    }

    /**
     *  Returns a <code>DateTimeStruct</code> that represents this wrapper.
     *
     *@return    struct containing wrapper values
     */
    public DateTimeStruct toDateTimeStruct()
    {
        return this.dateWrapper.toDateTimeStruct();
    }

    /**
     *  Returns a <code>TimeStruct</code> that represents the time portion of
     *  this wrapper.
     *
     *@return    struct containing time values
     */
    public TimeStruct toTimeStruct()
    {
        return this.dateWrapper.toTimeStruct();
    }

    /**
     *  Description of the Method
     *
     *@param  obj  Description of Parameter
     *@return      Description of the Returned Value
     */
    public boolean equals(Object obj)
    {
        boolean isEqual = false;

        if (this == obj)
        {
            isEqual = true;
        }

        return isEqual;
    }

    /**
     *  Description of the Method
     *
     *@return    Description of the Returned Value
     */
    public String toString()
    {
        if (this.formattedDate == null)
        {
            this.formattedDate = dateFormatter.format(getDate());
        }
        return this.formattedDate;
    }

    /**
     *  Gets the DateWrapper attribute of the DisplayDate object
     *
     *@return    The DateWrapper value
     */
    protected DateWrapper getDateWrapper()
    {
        return this.dateWrapper;
    }

    /**
     *  Description of the Method
     */
    protected void initialize()
    {
//        dateFormatter.setCurrentStyle(dateFormatter.DATE_FORMAT_24_HOURS_STYLE);
    }

    /**
     *  Description of the Method
     */
    protected void reInitalizeData()
    {
        formattedDate = null;
    }
}
