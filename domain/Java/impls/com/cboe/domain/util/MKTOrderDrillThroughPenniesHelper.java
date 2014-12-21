package com.cboe.domain.util;

import com.cboe.interfaces.domain.tradingProperty.MKTOrderDrillThroughPennies;

/**
 *   Helper class for MKTOrderDrillThroughPennies validation and use.
 *   This class exists to make sure that the MKTOrderDrillThroughPennies trading property behavior is consistent across services.
 *
 *   @author Luis Montiel
 */
public class MKTOrderDrillThroughPenniesHelper
{
    public static double getMaximumNoOfPennies(double value, MKTOrderDrillThroughPennies[] mkt)
    {
        value = Math.abs(value);
        double noOfPennies = (double)-1; //-1 is used If value not set for a given range or to ignore property from price calculation.
        for (int i=0; i < mkt.length; i++)
        {
            MKTOrderDrillThroughPennies currentMkt = mkt[i];
            if (currentMkt.getMinimumNBBORange() <= value && value <= currentMkt.getMaximumNBBORange())
            {
                return currentMkt.getNoOfPennies();
            }
        }
        return noOfPennies;
    }
    
}

