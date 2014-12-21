//
// -----------------------------------------------------------------------------------
// Source file: ExpressMDTimeHelper.java
//
// PACKAGE: com.cboe.presentation.marketData.express
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.marketData.express;

import com.cboe.idl.cmiUtil.DateStruct;
import com.cboe.idl.cmiUtil.TimeStruct;

import com.cboe.domain.util.DateWrapper;

public class ExpressMDTimeHelper
{
    /**
     * Returns a DateWrapper representing the current date, plus the milliseconds since midnight.
     * @param millis
     * @return
     */
    public static DateWrapper convertMillisSinceMidnight(int millis)
    {
        DateWrapper currentDate = new DateWrapper();
        DateStruct dateStruct = currentDate.toDateStruct();
        // DateWrapper for today's date, with time = 0
        DateWrapper wrapper = new DateWrapper(dateStruct);
        // add the milliseconds since midnight
        long convertedMillis = wrapper.getDate().getTime()+millis;
        return new DateWrapper(convertedMillis);
    }

    /**
     * Returns a TimeStruct representing the milliseconds since midnight.
     * @param millis
     * @return
     */
    public static TimeStruct convertMillisSinceMidnightToTime(int millis)
    {
        return convertMillisSinceMidnight(millis).toTimeStruct();
    }
}
