//
// -----------------------------------------------------------------------------------
// Source file: DateStructComparator.java
//
// PACKAGE: com.cboe.presentation.common.comparators;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2002 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.comparators;

import java.util.*;

import com.cboe.idl.cmiUtil.DateStruct;

/**
 * Compares DateStructs
 */
public class DateStructComparator implements Comparator
{
    /**
     * Implements Comparator.
     */
    public int compare(Object dateStruct1, Object dateStruct2)
    {
        int result = -1;
        DateStruct date1 = (DateStruct)dateStruct1;
        DateStruct date2 = (DateStruct)dateStruct2;

        result = compareYear(date1, date2);

        if(result == 0)
        {
            result = compareMonth(date1, date2);

            if(result == 0)
            {
                result = compareDay(date1, date2);
            }
        }
        return result;
    }

    /**
     * Compares year
     * @param year1 to compare with
     * @param year2
     */
    protected int compareYear(DateStruct year1, DateStruct year2)
    {
        int result = 0;

        result = compare(year1.year, year2.year);

        return result;
    }

    /**
     * Compares month
     * @param month1 to compare with
     * @param month2
     */
    protected int compareMonth(DateStruct month1, DateStruct month2)
    {
        int result = 0;

        result = compare(month1.month, month2.month);

        return result;
    }

    /**
     * Compares day
     * @param day1 to compare with
     * @param day2
     */
    protected int compareDay(DateStruct day1, DateStruct day2)
    {
        int result = 0;

        result = compare(day1.day, day2.day);

        return result;
    }

    /**
     * Compares date bytes
     * @param value1 to compare with
     * @param value2
     */
    private int compare(short value1, short value2)
    {
        int result = 0;

        int delta = value1 - value2;

        if(delta < 0)
        {
            result = -1;
        }
        else if(delta > 0)
        {
            result = 1;
        }

        return result;
    }
}