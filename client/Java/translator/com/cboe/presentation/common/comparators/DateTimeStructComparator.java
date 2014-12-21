package com.cboe.presentation.common.comparators;

import java.util.*;

import com.cboe.idl.cmiUtil.DateTimeStruct;

import com.cboe.domain.util.DateWrapper;

/**
  Compares DateTimeStruct instances

*/
public class DateTimeStructComparator implements Comparator
{
    DateWrapper wrapper1 = new DateWrapper();
    DateWrapper wrapper2 = new DateWrapper();
    private final String Category = this.getClass().getName();

    /**
      Implements Comparator.
    */
    public int compare(Object dateTimeStruct1, Object dateTimeStruct2)
    {
        int result = -1;
        this.wrapper1.setDateTime((DateTimeStruct)dateTimeStruct1);
        this.wrapper2.setDateTime((DateTimeStruct)dateTimeStruct2);
        result = this.wrapper1.compareTo(this.wrapper2);
        return result;
    }
}

