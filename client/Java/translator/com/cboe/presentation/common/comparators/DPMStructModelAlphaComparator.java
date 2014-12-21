//
// -----------------------------------------------------------------------------------
// Source file: DPMStructModelAlphaComparator.java
//
// PACKAGE: com.cboe.presentation.common.comparators;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.comparators;

import java.util.Comparator;

import com.cboe.presentation.dpm.DPMStructModel;

/**
 * Compares DPMStructModel's and return a -1, 0, or 1 to allow ordering.
 * The compare is done on getUserId
 * Note: this comparator imposes orderings that are inconsistent with equals
 */
public class DPMStructModelAlphaComparator implements Comparator
{
    /**
     * DPMStructModelAlphaComparator constructor comment.
     */
    public DPMStructModelAlphaComparator()
    {
        super();
    }

    /**
     * compare method comment.
     */
    public int compare(Object arg1, Object arg2)
    {
        if(arg1 == arg2)
        {
            return 0;
        }
        else
        {
            DPMStructModel dpm1 = (DPMStructModel)arg1;
            DPMStructModel dpm2 = (DPMStructModel)arg2;

            return dpm1.getUserId().compareTo(dpm2.getUserId());
        }
    }
}
