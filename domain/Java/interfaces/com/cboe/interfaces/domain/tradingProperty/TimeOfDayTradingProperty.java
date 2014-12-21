//
// -----------------------------------------------------------------------------------
// Source file: TimeOfDayTradingProperty.java
//
// PACKAGE: com.cboe.interfaces.domain.tradingProperty;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.domain.tradingProperty;

import java.util.*;
import java.text.DateFormat;
import java.text.ParseException;

import com.cboe.idl.cmiUtil.TimeStruct;

import com.cboe.interfaces.domain.dateTime.Time;

/**
 * Defines the contract for a TradingProperty that allows setting the time of day for an entity.
 */
public interface TimeOfDayTradingProperty extends TradingProperty
{
    DateFormat STD_DATE_FORMAT = Time.STD_DATE_FORMAT;

    /**
     * Gets the set time as a Time Object
     * @return the currently set time as a Time interface
     */
    Time getTime();

    /**
     * Allows setting the time value using the values from a Time interface
     * @param time to obtain time values from
     */
    void setTime(Time time);

    /**
     * Gets the set time as a TimeStruct
     * @return the currently set time as a TimeStruct
     */
    TimeStruct getTimeStruct();

    /**
     * Allows setting the time value using the values from a TimeStruct
     * @param timeStruct to obtain time values from
     */
    void setTimeStruct(TimeStruct timeStruct);

    /**
     * Gets the set time as a Date.
     * @return the currently set time and todays day as a Date
     */
    Date getTodaysDate();

    /**
     * Allows setting the time value using the values from a Date
     * @param time to obtain time values from, the actual day will be ignored and only the hour/minute/second/millis
     * extracted from the Date
     */
    void setTodaysDate(Date time);

    /**
     * Gets the set time as a Calendar.
     * @return the currently set time and todays day as a Calendar
     */
    Calendar getCalendar();

    /**
     * Allows setting the time value using the values from a Calendar
     * @param calendar to obtain time values from, the actual day will be ignored and only the hour/minute/second/millis
     * extracted from the Calendar
     */
    void setCalendar(Calendar calendar);

    /**
     * Gets the set time of day as the number of millis elapsed since the previous midnight
     * @return the currently set time as the number of millis that should have elapsed since the previous midnight,
     * regardless of day.
     */
    int getAsMillisSinceMidnight();

    /**
     * Sets the set time of day as the number of millis elapsed since the previous midnight
     * @param millisSincePreviousMidnight the number of millis for the desired time of day that should have elapsed
     * since the previous midnight, regardless of day. This can be formulized as:
     * millisSincePreviousMidnight = desired time of today in millis - midnight of the same date.
     */
    void setAsMillisSinceMidnight(int millisSincePreviousMidnight);

    /**
     * Gets the set time of day using the standard date format to format the appropriate String.
     * @return set time formatted using standard date format
     */
    String getDateAsString();

    /**
     * Allows setting of the time of day as a String. The standard date format will be used to parse the String
     * to determine the actual values.
     * @param time String to parse to find actual time values. Standard date format will be used for parsing.
     * @exception ParseException will be thrown if passed time cannot be parsed by the standard date format.
     */
    void setDateAsString(String time)
            throws ParseException;

    /**
     * Displays the current time using the standard date format. This method can be useful to determine how the
     * standard date format expects the String to look for parsing.
     * @return current time formatted using standard date format
     */
    String displayNowUsingStandardDateFormat();

    /**
     * Gets the set time of day using the supplied date format to format the appropriate String.
     * @return set time formatted using supplied date format
     */
    String getDateAsString(DateFormat formatterToUse);

    /**
     * Allows setting of the time of day as a String. The supplied date format will be used to parse the String to
     * determine the actual values.
     * @param time String to parse to find actual time values. Supplied date format will be used for parsing.
     * @param parserToUse the date format object to use for parsing the time String.
     * @exception ParseException will be thrown if passed time cannot be parsed by the supplied date format.
     */
    void setDateAsString(String time, DateFormat parserToUse)
            throws ParseException;
}
