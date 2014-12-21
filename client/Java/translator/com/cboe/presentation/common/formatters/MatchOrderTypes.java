// -----------------------------------------------------------------------------------
// Source file: MatchOrderTypes
//
// PACKAGE: com.cboe.presentation.common.formatters
// 
// Created: Sep 13, 2004 3:47:37 PM
// -----------------------------------------------------------------------------------
// Copyright (c) 2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.formatters;

import java.util.Enumeration;
import java.util.Vector;

public abstract class MatchOrderTypes
{
    public static final short AUTO_MATCH               = com.cboe.idl.cmiConstants.MatchTypes.AUTO_MATCH;
    public static final short GUARANTEE_STARTING_PRICE = com.cboe.idl.cmiConstants.MatchTypes.GUARANTEE_STARTING_PRICE;
    public static final short LIMIT_PRICE              = com.cboe.idl.cmiConstants.MatchTypes.LIMIT_PRICE;
    public static final short UNSPECIFIED              = com.cboe.idl.cmiConstants.MatchTypes.UNSPECIFIED;

    public static final String TRADERS_FORMAT = "TRADERS_FORMAT";
    public static final String INVALID_FORMAT = "INVALID_FORMAT";

    private static final String AUTO_MATCH_STRING               = "Auto Match";
    private static final String GUARANTEE_STARTING_PRICE_STRING = "Guarantee Starting Price";
    private static final String LIMIT_PRICE_STRING              = "Limit Price";
    private static final String UNSPECIFIED_STRING              = "Unspecified";
    private static final String UNKNOWN_STRING                  = "Unknown";

    public static String toString(short matchOrderType)
    {
        return toString(matchOrderType, TRADERS_FORMAT);
    }

    public static String toString(short matchOrderType, String formatSpecifier)
    {
        if (formatSpecifier.equals(TRADERS_FORMAT))
        {
            switch (matchOrderType)
            {
                case AUTO_MATCH:
                    return AUTO_MATCH_STRING;
                case LIMIT_PRICE:
                    return LIMIT_PRICE_STRING;
                case GUARANTEE_STARTING_PRICE:
                    return GUARANTEE_STARTING_PRICE_STRING;
                case UNSPECIFIED:
                    return UNSPECIFIED_STRING;
                default :
                    return new StringBuffer().append(UNKNOWN_STRING).append("[ ").append(matchOrderType).append(" ]").toString();
            }
        }

        return INVALID_FORMAT;
    }
    
    public static boolean validateMatchOrderType(short type)
    {
        switch(type)
        {
            case AUTO_MATCH:
            case LIMIT_PRICE:
            case GUARANTEE_STARTING_PRICE:
            case UNSPECIFIED:
                return true;
            default:
                return false;
        }
    }
    
    public static Vector getShortTypeList ()
    {
        Vector aList = new Vector();
        aList.addElement(new Short(AUTO_MATCH));
        aList.addElement(new Short(GUARANTEE_STARTING_PRICE));
        aList.addElement(new Short(LIMIT_PRICE));
        aList.addElement(new Short(UNSPECIFIED));
        
        return aList;
    }
}
