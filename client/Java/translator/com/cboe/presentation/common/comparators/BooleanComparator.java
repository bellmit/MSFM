//
// -----------------------------------------------------------------------------------
// Source file: BooleanComparator.java
//
// PACKAGE: com.cboe.presentation.common.comparators
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.comparators;

import java.util.*;

public class BooleanComparator implements Comparator
{
    public BooleanComparator()
    {
        super();
    }

    public int compare(Object o1, Object o2)
    {
        int retVal = 0;
        boolean o1Prim = (( Boolean ) o1).booleanValue();
        boolean o2Prim = (( Boolean ) o2).booleanValue();

        if(o1Prim == true && o2Prim != true)
        {
            retVal = -1;
        }
        else if(o1Prim != true && o2Prim == true)
        {
            retVal = 1;
        }

        return retVal;
    }
}
