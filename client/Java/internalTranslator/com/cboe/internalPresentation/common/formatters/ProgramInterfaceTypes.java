// -----------------------------------------------------------------------------------
// Source file: ProgramInterfaceTypes
//
// PACKAGE: com.cboe.internalPresentation.common.formatters
//
// Created: Mar 26, 2004 1:59:50 PM
// -----------------------------------------------------------------------------------
// Copyright (c) 2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.common.formatters;

public class ProgramInterfaceTypes
{
    public static final short ALL = com.cboe.idl.constants.ProgramInterfaces.ALL;
    public static final short CMI = com.cboe.idl.constants.ProgramInterfaces.CMI;
    public static final short ADAPTERS = com.cboe.idl.constants.ProgramInterfaces.ADAPTERS;

    public static final short[] ALL_TYPES = { ALL, ADAPTERS, CMI };

    public static final String FULL_FORMAT    = "FULL_FORMAT";
    public static final String SHORT_FORMAT   = "SHORT_FORMAT";
    public static final String INVALID_FORMAT = "INVALID_FORMAT";
    public static final String INVALID_TYPE   = "INVALID_TYPE:";

    public static final String ALL_SHORT_STRING = "All";
    public static final String CMI_SHORT_STRING = "CMI";
    public static final String ADAPTERS_SHORT_STRING = "ADAPTERS";

    /**
     * The following full names are named this way because of Hybrid Failover and the Republish Product State Design
     */
    public static final String ALL_FULL_STRING = "All";
    public static final String CMI_FULL_STRING = "Trading Session";
    public static final String ADAPTERS_FULL_STRING = "Current Market";

    private ProgramInterfaceTypes(){}

    public static boolean validateProgramInterfaceType(short programInterfaceType)
    {
        switch( programInterfaceType )
        {
            case ALL:
            case CMI:
            case ADAPTERS:
                return true;
            default:
                return false;
        }
    }

    public static String toString(short programInterfaceType)
    {
        return toString(programInterfaceType, FULL_FORMAT);
    }

    public static String toString(short programInterfaceType, String formatSpecifier)
    {
        if(formatSpecifier.equals(FULL_FORMAT))
        {
            switch ( programInterfaceType )
            {
                case ALL:
                    return ALL_FULL_STRING;
                case CMI:
                    return CMI_FULL_STRING;
                case ADAPTERS:
                    return ADAPTERS_FULL_STRING;
                default:
                    return new StringBuffer(30).append(INVALID_TYPE).append(' ').append(programInterfaceType).toString();
            }
        }
        else if(formatSpecifier.equals(SHORT_FORMAT))
        {
            switch( programInterfaceType )
            {
                case ALL:
                    return ALL_SHORT_STRING;
                case CMI:
                    return CMI_SHORT_STRING;
                case ADAPTERS:
                    return ADAPTERS_SHORT_STRING;
                default:
                    return new StringBuffer(30).append(INVALID_TYPE).append(' ').append(programInterfaceType).toString();
            }
        }

        return new StringBuffer(30).append(INVALID_FORMAT).append(' ').append(formatSpecifier).toString();
    }

} // -- end of class ProgramInterfaceTypes
