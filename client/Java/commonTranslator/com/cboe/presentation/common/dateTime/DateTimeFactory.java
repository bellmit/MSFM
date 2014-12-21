//
// -----------------------------------------------------------------------------------
// Source file: DateTimeFactory.java
//
// PACKAGE: com.cboe.presentation.common.dateTime
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2009 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.dateTime;

import com.cboe.interfaces.domain.dateTime.Date;
import com.cboe.interfaces.domain.dateTime.DateTime;
import com.cboe.idl.cmiUtil.DateStruct;
import com.cboe.idl.cmiUtil.DateTimeStruct;

import java.util.Map;
import java.util.Collections;
import java.util.WeakHashMap;

public class DateTimeFactory
{
    private static final DateTimeFactory instance = new DateTimeFactory();

    private Map<Date, Date> dateMap;
    private Map<DateTime, DateTime> dateTimeMap;

    private DateTimeFactory()
    {
        dateMap = Collections.synchronizedMap(new WeakHashMap<Date, Date>());
        dateTimeMap = Collections.synchronizedMap(new WeakHashMap<DateTime, DateTime>());
    }

    public static Date getDate(DateStruct struct)
    {
        Date tmp = new DateImpl(struct);
        Date retVal = instance.dateMap.get(tmp);
        if (retVal == null)
        {
            retVal = tmp;
            instance.dateMap.put(retVal, retVal);
        }
        return retVal;
    }

    public static DateTime getDateTime(DateTimeStruct struct)
    {
        DateTime tmp = new DateTimeImpl(struct);
        DateTime retVal = instance.dateTimeMap.get(tmp);
        if (retVal == null)
        {
            retVal = tmp;
            instance.dateTimeMap.put(retVal, retVal);
        }
        return retVal;
    }
}
