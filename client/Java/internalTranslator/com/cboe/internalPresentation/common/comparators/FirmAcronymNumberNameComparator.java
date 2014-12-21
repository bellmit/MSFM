//
// -----------------------------------------------------------------------------------
// Source file: FirmAcronymNumberNameComparator.java
//
// PACKAGE: com.cboe.internalPresentation.common.comparators;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.common.comparators;

import java.util.*;

import com.cboe.interfaces.internalPresentation.firm.FirmModel;

public class FirmAcronymNumberNameComparator implements Comparator
{
    public int compare(Object arg1, Object arg2)
    {
        int result;
        if(arg1 == arg2)
        {
            result = 0;
        }
        else
        {
            FirmModel firm1 = (FirmModel)arg1;
            FirmModel firm2 = (FirmModel)arg2;

            result = firm1.getAcronym().compareToIgnoreCase(firm2.getAcronym());

            if(result == 0)
            {
                result = firm1.getFirmExchange().compareToIgnoreCase(firm2.getFirmExchange());
            }
            if(result == 0)
            {
                result = firm1.getFirmNumber().compareToIgnoreCase(firm2.getFirmNumber());
            }
            if(result == 0)
            {
                result = firm1.getFullName().compareToIgnoreCase(firm2.getFullName());
            }
        }
        return result;
    }
}