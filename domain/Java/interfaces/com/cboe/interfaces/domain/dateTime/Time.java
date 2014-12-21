//
// -----------------------------------------------------------------------------------
// Source file: Time.java
//
// PACKAGE: com.cboe.interfaces.domain.dateTime;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.domain.dateTime;

import java.text.DateFormat;

import com.cboe.idl.cmiUtil.TimeStruct;

/**
 * Represents a basic date as a wrapper for TimeStruct
 */
public interface Time extends Comparable, Cloneable
{
    DateFormat STD_DATE_FORMAT = DateFormat.getTimeInstance(DateFormat.MEDIUM);

    /**
     * Returns the TimeStruct that this object represents
     * @deprecated This method exists primarily for backwards compatability reasons.
     * Please use the wrapper objects whenever possible.
     * @see com.cboe.idl.cmiUtil.TimeStruct
     */
    TimeStruct getTimeStruct();

    /**
     * Returns the hour of the day
     */
    int getHour();

    /**
     * Returns the minute of the hour
     */
    int getMinute();

    /**
     * Returns the second of the minute
     */
    int getSecond();

    /**
     * Returns the milliseconds of the second
     */
    int getMillisecond();

    /**
     * Gets this Date as a java.util.Date
     */
    java.util.Date getDate();
}