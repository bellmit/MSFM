//
// -----------------------------------------------------------------------------------
// Source file: TimeOfDayTradingPropertyImpl.java
//
// PACKAGE: com.cboe.domain.tradingProperty;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.domain.tradingProperty;

import java.util.*;
import java.text.DateFormat;
import java.text.ParseException;

import com.cboe.idl.cmiUtil.TimeStruct;

import com.cboe.exceptions.DataValidationException;

import com.cboe.interfaces.domain.property.Property;
import com.cboe.interfaces.domain.tradingProperty.TimeOfDayTradingProperty;
import com.cboe.interfaces.domain.tradingProperty.TradingPropertyType;
import com.cboe.interfaces.domain.dateTime.Time;

import com.cboe.domain.dateTime.TimeImpl;

public class TimeOfDayTradingPropertyImpl extends AbstractTradingProperty
        implements TimeOfDayTradingProperty
{
    private TradingPropertyType tradingPropertyType;

    private TimeStruct cachedTimeStruct;
    private Calendar cachedCalendar;
    private Date cachedDate;
    private String cachedString;
    private Time cachedTime;

    /**
     * @param tradingPropertyType type of TradingProperty that this instance represents. Since this TradingProperty
     * implementation class type can represent the data for multiple TradingProperty types, it must be told which type
     * this instance represents.
     * @param sessionName that this TradingProperty is for
     * @param classKey that this TradingProperty is for
     */
    public TimeOfDayTradingPropertyImpl(TradingPropertyType tradingPropertyType, String sessionName, int classKey)
    {
        super(tradingPropertyType.getName(), sessionName, classKey);
        this.tradingPropertyType = tradingPropertyType;
        clearCachedObjects();
    }

    /**
     * @param tradingPropertyType type of TradingProperty that this instance represents. Since this TradingProperty
     * implementation class type can represent the data for multiple TradingProperty types, it must be told which type
     * this instance represents.
     * @param sessionName that this TradingProperty is for
     * @param classKey that this TradingProperty is for
     * @param property to initialize with
     */
    public TimeOfDayTradingPropertyImpl(TradingPropertyType tradingPropertyType, String sessionName, int classKey,
                                       Property property)
            throws DataValidationException
    {
        super(sessionName, classKey, property);
        this.tradingPropertyType = tradingPropertyType;
        clearCachedObjects();
    }

    /**
     * @param tradingPropertyType type of TradingProperty that this instance represents. Since this TradingProperty
     * implementation class type can represent the data for multiple TradingProperty types, it must be told which type
     * this instance represents.
     * @param sessionName that this TradingProperty is for
     * @param classKey that this TradingProperty is for
     * @param millisSincePreviousMidnight the number of millis for the desired time of day that should have elapsed
     * since the previous midnight, regardless of day. This can be formulized as:
     * millisSincePreviousMidnight = desired time of today in millis - midnight of the same date.
     * See setAsMillisSinceMidnight(int)
     */
    public TimeOfDayTradingPropertyImpl(TradingPropertyType tradingPropertyType, String sessionName, int classKey,
                                        int millisSincePreviousMidnight)
    {
        this(tradingPropertyType, sessionName, classKey);
        setAsMillisSinceMidnight(millisSincePreviousMidnight);
    }

    /**
     * @param tradingPropertyType type of TradingProperty that this instance represents. Since this TradingProperty
     * implementation class type can represent the data for multiple TradingProperty types, it must be told which type
     * this instance represents.
     * @param sessionName that this TradingProperty is for
     * @param classKey that this TradingProperty is for
     * @param value String to parse field values from
     */
    public TimeOfDayTradingPropertyImpl(TradingPropertyType tradingPropertyType,
                                       String sessionName, int classKey, String value)
    {
        super(tradingPropertyType.getName(), sessionName, classKey, value);
        this.tradingPropertyType = tradingPropertyType;
        clearCachedObjects();
    }

    /**
     * Compares based on getAsMillisSinceMidnight()
     */
    public int compareTo(Object object)
    {
        int result;
        int myValue = getAsMillisSinceMidnight();
        int theirValue = ((TimeOfDayTradingProperty) object).getAsMillisSinceMidnight();
        result = (myValue < theirValue ? -1 : (myValue == theirValue ? 0 : 1));
        return result;
    }

    public int hashCode()
    {
        return getAsMillisSinceMidnight();
    }

    public TradingPropertyType getTradingPropertyType()
    {
        return tradingPropertyType;
    }

    public String getPropertyName()
    {
        return getDateAsString();
    }

    /**
     * Gets the set time of day as the number of millis elapsed since the previous midnight
     * @return the currently set time as the number of millis that should have elapsed since the previous midnight,
     *         regardless of day.
     */
    public int getAsMillisSinceMidnight()
    {
        return getInteger1();
    }

    /**
     * Sets the set time of day as the number of millis elapsed since the previous midnight
     * @param millisSincePreviousMidnight the number of millis for the desired time of day that should have elapsed
     * since the previous midnight, regardless of day. This can be formulized as: millisSincePreviousMidnight = desired
     * time of today in millis - midnight of the same date.
     */
    public void setAsMillisSinceMidnight(int millisSincePreviousMidnight)
    {
        setInteger1(millisSincePreviousMidnight);
        clearCachedObjects();
    }

    /**
     * Gets the set time as a Calendar.
     * @return the currently set time and todays day as a Calendar
     */
    public Calendar getCalendar()
    {
        if(cachedCalendar == null)
        {
            cachedCalendar = getTodaysMidnightCalendar();
            long midnightMillis = cachedCalendar.getTimeInMillis();
            long setTimeMillis = midnightMillis + getAsMillisSinceMidnight();
            cachedCalendar.setTimeInMillis(setTimeMillis);
        }
        return cachedCalendar;
    }

    /**
     * Allows setting the time value using the values from a Calendar
     * @param calendar to obtain time values from, the actual day will be ignored and only the hour/minute/second/millis
     * extracted from the Calendar
     */
    public void setCalendar(Calendar calendar)
    {
        Calendar midnightCalendar = getTodaysMidnightCalendar();
        long midnightMillis = midnightCalendar.getTimeInMillis();
        long setTimeMillis = calendar.getTimeInMillis();
        int millisSinceMidnight = (int) (setTimeMillis - midnightMillis);
        setAsMillisSinceMidnight(millisSinceMidnight);
    }

    /**
     * Gets the set time as a TimeStruct
     * @return the currently set time as a TimeStruct
     */
    public TimeStruct getTimeStruct()
    {
        if(cachedTimeStruct == null)
        {
            cachedTimeStruct = new TimeStruct();
            Calendar calendar = getCalendar();
            cachedTimeStruct.hour = (byte) calendar.get(Calendar.HOUR_OF_DAY);
            cachedTimeStruct.minute = (byte) calendar.get(Calendar.MINUTE);
            cachedTimeStruct.second = (byte) calendar.get(Calendar.SECOND);

            int millis = calendar.get(Calendar.MILLISECOND);
            int roundedMillis = millis / 10;
            cachedTimeStruct.fraction = (byte) roundedMillis;
        }
        return cachedTimeStruct;
    }

    /**
     * Allows setting the time value using the values from a TimeStruct
     * @param timeStruct to obtain time values from
     */
    public void setTimeStruct(TimeStruct timeStruct)
    {
        Calendar calendar = getTodaysMidnightCalendar();
        calendar.set(Calendar.HOUR_OF_DAY, timeStruct.hour);
        calendar.set(Calendar.MINUTE, timeStruct.minute);
        calendar.set(Calendar.SECOND, timeStruct.second);

        int millisFraction = timeStruct.fraction;
        int expandedMillis = millisFraction * 10;
        calendar.set(Calendar.MILLISECOND, expandedMillis);

        setCalendar(calendar);
    }

    /**
     * Gets the set time as a Time Object
     * @return the currently set time as a Time interface
     */
    public Time getTime()
    {
        if(cachedTime == null)
        {
            cachedTime = new TimeImpl(getTimeStruct());
        }
        return cachedTime;
    }

    /**
     * Allows setting the time value using the values from a Time interface
     * @param time to obtain time values from
     */
    public void setTime(Time time)
    {
        Date date = time.getDate();
        setTodaysDate(date);
    }

    /**
     * Gets the set time as a Date.
     * @return the currently set time and todays day as a Date
     */
    public Date getTodaysDate()
    {
        if(cachedDate == null)
        {
            Calendar calendar = getCalendar();
            cachedDate = calendar.getTime();
        }
        return cachedDate;
    }

    /**
     * Allows setting the time value using the values from a Date
     * @param date to obtain time values from, the actual day will be ignored and only the hour/minute/second/millis
     * extracted from the Date
     */
    public void setTodaysDate(Date date)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date.getTime());
        setCalendar(calendar);
    }

    /**
     * Gets the set time of day using the standard date format to format the appropriate String.
     * @return set time formatted using standard date format
     */
    public String getDateAsString()
    {
        if(cachedString == null)
        {
            cachedString = getStandardDateFormat().format(getTodaysDate());
        }
        return cachedString;
    }

    /**
     * Allows setting of the time of day as a String. The standard date format will be used to parse the String to
     * determine the actual values.
     * @param time String to parse to find actual time values. Standard date format will be used for parsing.
     * @exception ParseException will be thrown if passed time cannot be parsed by the standard date format.
     */
    public void setDateAsString(String time)
            throws ParseException
    {
        Date parsedDate = getStandardDateFormat().parse(time);
        setTodaysDate(parsedDate);
    }

    /**
     * Gets the set time of day using the supplied date format to format the appropriate String.
     * @return set time formatted using supplied date format
     */
    public String getDateAsString(DateFormat formatterToUse)
    {
        return formatterToUse.format(getTodaysDate());
    }

    /**
     * Allows setting of the time of day as a String. The supplied date format will be used to parse the String to
     * determine the actual values.
     * @param time String to parse to find actual time values. Supplied date format will be used for parsing.
     * @param parserToUse the date format object to use for parsing the time String.
     * @exception ParseException will be thrown if passed time cannot be parsed by the supplied date format.
     */
    public void setDateAsString(String time, DateFormat parserToUse)
            throws ParseException
    {
        Date parsedDate = parserToUse.parse(time);
        setTodaysDate(parsedDate);
    }

    /**
     * Displays the current time using the standard date format. This method can be useful to determine how the standard
     * date format expects the String to look for parsing.
     * @return current time formatted using standard date format
     */
    public String displayNowUsingStandardDateFormat()
    {
        return getStandardDateFormat().format(new Date());
    }

    protected DateFormat getStandardDateFormat()
    {
        return STD_DATE_FORMAT;
    }

    protected Calendar getTodaysMidnightCalendar()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.getTime();
        return calendar;
    }

    protected void clearCachedObjects()
    {
        cachedCalendar = null;
        cachedDate = null;
        cachedString = null;
        cachedTimeStruct = null;
        cachedTime = null;
    }
}
