//
// -----------------------------------------------------------------------------------
// Source file: RateMonitorTypesStringComparator.java
//
// PACKAGE: com.cboe.presentation.common.comparators
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.comparators;

import java.util.*;

import com.cboe.presentation.common.formatters.RateMonitorTypes;

public class RateMonitorTypesStringComparator implements Comparator
{
    public int compare(Object o1, Object o2)
    {
        short rateType1 = ((Short) o1).shortValue();
        short rateType2 = ((Short) o2).shortValue();

        return RateMonitorTypes.toString(rateType1).compareTo(RateMonitorTypes.toString(rateType2));
    }
}
