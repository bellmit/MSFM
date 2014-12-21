//
// -----------------------------------------------------------------------------------
// Source file: ExchangeAcronymComparator.java
//
// PACKAGE: com.cboe.presentation.common.comparators;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.comparators;

import java.util.Comparator;

import com.cboe.interfaces.presentation.user.ExchangeAcronym;

/**
 * Implements comparator for ExchangeAcronym.
 */
public class ExchangeAcronymComparator implements Comparator
{
    public int compare(Object arg1, Object arg2)
    {
        int result;

        if(arg1 == arg2)
        {
            result = 0;
        }
        else if(arg1 instanceof ExchangeAcronym && arg2 instanceof ExchangeAcronym)
        {
            ExchangeAcronym exAcro1 = (ExchangeAcronym)arg1;
            ExchangeAcronym exAcro2 = (ExchangeAcronym)arg2;

            result = exAcro1.getExchange().compareTo(exAcro2.getExchange());

            if(result == 0)
            {
                result = exAcro1.getAcronym().compareTo(exAcro2.getAcronym());
            }
        }
        else
        {
            result = -1;
        }

        return result;
    }
}