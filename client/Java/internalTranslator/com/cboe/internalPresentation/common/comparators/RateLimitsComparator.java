//
// -----------------------------------------------------------------------------------
// Source file: RateLimitsComparator.java
//
// PACKAGE: com.cboe.internalPresentation.common.comparators;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.common.comparators;

import java.util.Comparator;

import com.cboe.interfaces.domain.rateMonitor.RateLimits;

/**
 *  This comparator compares two RateLimits to be able to sort them by session name and rate type
 */
public class RateLimitsComparator implements Comparator<RateLimits>
{
    public RateLimitsComparator()
    {
    }

    /**
     * compare method comment.
     */
    public int compare(RateLimits rateLimits1, RateLimits rateLimits2)
    {
        if(rateLimits1 == rateLimits2)
        {
            return 0;
        }
        else
        {
            int spcResult = rateLimits1.getSessionName().compareToIgnoreCase(rateLimits2.getSessionName());
            if (spcResult == 0)
            {
                if (rateLimits1.getRateMonitorType() == rateLimits2.getRateMonitorType())
                {
                    return 0;
                }
                else if (rateLimits1.getRateMonitorType() < rateLimits2.getRateMonitorType())
                {
                    return -1;
                }
                else
                {
                    return 1;
                }
            }
            else
            {
                return spcResult;
            }
        }
    }
}
