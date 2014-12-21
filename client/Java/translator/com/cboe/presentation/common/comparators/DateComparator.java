//
// -----------------------------------------------------------------------------------
// Source file: DateComparator.java
//
// PACKAGE: com.cboe.presentation.common.comparators
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.comparators;

import java.util.Comparator;
import com.cboe.interfaces.domain.dateTime.Date;

import com.cboe.domain.util.DateWrapper;

public class DateComparator implements Comparator
{
    public int compare(Object object1, Object object2)
    {
        int result = 0;

        Date date1 = (Date)object1;
        Date date2 = (Date)object2;
        long millis1 = DateWrapper.convertToMillis(date1.getDateStruct());
        long millis2 = DateWrapper.convertToMillis(date2.getDateStruct());

        if(millis1 < millis2)
        {
            result = -1;
        }
        else if(millis1 > millis2)
        {
            result = 1;
        }

        return result;
    }
}
