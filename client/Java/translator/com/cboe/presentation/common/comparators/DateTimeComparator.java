//
// -----------------------------------------------------------------------------------
// Source file: DateTimeComparator.java
//
// PACKAGE: com.cboe.presentation.common.comparators
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.comparators;

import java.util.*;

import com.cboe.interfaces.domain.dateTime.DateTime;

public class DateTimeComparator implements Comparator
{
    public DateTimeComparator()
    {
        super();
    }

    public int compare(Object dateTime1, Object dateTime2)
    {
        int result;

        if(dateTime1 != null && dateTime2 != null)
        {
            result = ((DateTime)dateTime1).compareTo((DateTime)dateTime2);
        }
        else if (dateTime1 == null && dateTime2 == null)
        {
            result = 0;
        }
        else if(dateTime1 == null)
        {
            result = -1;
        }
        else
        {
            result = 1;
        }

        return result;
    }
}