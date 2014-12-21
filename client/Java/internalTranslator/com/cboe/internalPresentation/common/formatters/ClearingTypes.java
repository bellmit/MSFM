//
// -----------------------------------------------------------------------------------
// Source file: ClearingTypes.java
//
// PACKAGE: com.cboe.internalPresentation.common.formatters
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2009 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.common.formatters;

public class ClearingTypes
{
    public static final String CLEAR = "Clear";
    public static final String IGNORE = "Ignore";
    public static final String DEFERRED = "Deferred";
    public static final String NAMES_LATER = "Names Later";
    public static final String FORCE_CLEAR = "Force Clear";

    public static String toString(char clearingType)
    {
        String retVal;
        switch(clearingType)
        {
            case com.cboe.idl.constants.ClearingTypes.CLEAR:
                retVal = CLEAR;
                break;
            case com.cboe.idl.constants.ClearingTypes.IGNORE:
                retVal = IGNORE;
                break;
            case com.cboe.idl.constants.ClearingTypes.DEFERRED:
                retVal = DEFERRED;
                break;
            case com.cboe.idl.constants.ClearingTypes.NAMES_LATER:
                retVal = NAMES_LATER;
                break;
            case com.cboe.idl.constants.ClearingTypes.FORCE_CLEAR:
                retVal = FORCE_CLEAR;
                break;
            default:
                retVal = Character.toString(clearingType);
                break;
        }
        return retVal;
    }
}
