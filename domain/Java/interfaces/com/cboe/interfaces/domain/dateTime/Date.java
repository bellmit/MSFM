//
// -----------------------------------------------------------------------------------
// Source file: Date.java
//
// PACKAGE: com.cboe.interfaces.domain.dateTime;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.domain.dateTime;

import java.text.DateFormat;

import com.cboe.idl.cmiUtil.DateStruct;

/**
 * Represents a basic date as a wrapper for DateStruct
 */
public interface Date extends Comparable, Cloneable
{
    DateFormat STD_DATE_FORMAT = DateFormat.getDateInstance(DateFormat.MEDIUM);

    /**
     * Returns the DateStruct that this object represents
     * @deprecated This method exists primarily for backwards compatability reasons.
     * Please use the wrapper objects whenever possible.
     * @see com.cboe.idl.cmiUtil.DateStruct
     */
    DateStruct getDateStruct();

    /**
     * Returns the day of the month
     */
    int getDay();

    /**
     * Returns the month of the year
     */
    int getMonth();

    /**
     * Returns the year
     */
    int getYear();

    /**
     * Gets this Date as a java.util.Date
     */
    java.util.Date getDate();
}