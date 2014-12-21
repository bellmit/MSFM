//
// -----------------------------------------------------------------------------------
// Source file: MarketabilityIndicator.java
//
// PACKAGE: com.cboe.interfaces.presentation.omt
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.omt;

/**
 * @author Thomas Morrow
 * @since May 14, 2008
 */
public enum MarketabilityIndicator
{
    MARKETABLE, SUPER_MARKETABLE, NOT_MARKETABLE, X_NOT_MARKETABLE, UNKOWN;

    public String toString()
    {
        String returnValue = null;
        switch (this)
        {
            case MARKETABLE:
                returnValue = "Marketable";
                break;
            case SUPER_MARKETABLE:
                returnValue = "Super Marketable";
                break;
            case NOT_MARKETABLE:
                returnValue = "Not Marketable";
                break;
                
            case X_NOT_MARKETABLE:
            	returnValue = "X Not Marketable";
            	break;
                
            case UNKOWN:
                returnValue = "Unknown";
                break;
        }
        return returnValue;
    }

    public char getAbbreviation()
    {
        return toString().charAt(0);
    }
}
