//
// -----------------------------------------------------------------------------------
// Source file: LoggingPrioritiesFormatter.java
//
// PACKAGE: com.cboe.presentation.common.formatters;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.formatters;

import com.cboe.idl.infrastructureServices.loggingService.corba.Priority;

public class LoggingPrioritiesFormatter extends Formatter
{
    private LoggingPrioritiesFormatter()
    {}

    public static String format(int loggingPriority)
    {
        String retVal;
        switch (loggingPriority)
        {
            case (Priority._Critical):
                retVal ="Critical";
                break;
            case (Priority._High):
                retVal ="High";
                break;
            case (Priority._Low):
                retVal ="Low";
                break;
            case (Priority._Medium):
                retVal ="Medium";
                break;
            case (Priority._NoPriority):
                retVal ="No Priority";
                break;
            default:
                retVal = "Unknown:" + loggingPriority;
        }
        
        return retVal;
    }
}
