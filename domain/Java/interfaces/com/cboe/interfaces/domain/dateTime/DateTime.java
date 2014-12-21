//
// -----------------------------------------------------------------------------------
// Source file: DateTime.java
//
// PACKAGE: com.cboe.interfaces.domain.dateTime;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2002 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.domain.dateTime;

import java.text.DateFormat;
import java.util.*;

import com.cboe.idl.cmiUtil.DateTimeStruct;

/**
 * Represents a basic date as a wrapper for DateTimeStruct
 */
public interface DateTime extends Comparable, Cloneable
{
    DateFormat STD_DATE_FORMAT = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);

    /**
     * Returns the DateTimeStruct that this object represents
     * @deprecated This method exists primarily for backwards compatability reasons. Please use the wrapper objects
     *             whenever possible.
     */
    DateTimeStruct getDateTimeStruct();

    /**
     * Returns the date
     */
    Date getDate();

    /**
     * Returns the time
     */
    Time getTime();

    /**
     * Returns the date in Calendar format
     */
    Calendar getCalendar();

    /**
     * Returns the time as UTC milliseconds from the epoch
     */
    long getTimeInMillis();
}