//
// -----------------------------------------------------------------------------------
// Source file: LoggingCategoriesFormatter.java
//
// PACKAGE: com.cboe.presentation.common.formatters;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.formatters;

import com.cboe.idl.infrastructureServices.loggingService.corba.Category;

public class LoggingCategoriesFormatter extends Formatter
{
    private LoggingCategoriesFormatter()
    {}

    public static String format(int loggingCategory)
    {
        String retVal;
        switch (loggingCategory)
        {
            case (Category._Debug):
                retVal ="Debug";
                break;
            case (Category._Audit):
                retVal ="Audit";
                break;
            case (Category._Information):
                retVal ="Info";
                break;
            case (Category._NoCategory):
                retVal ="No Category";
                break;
            case (Category._NonRepudiation):
                retVal ="Non Repudiation";
                break;
            case (Category._SystemAlarm):
                retVal ="System Alarm";
                break;
            case (Category._SystemNotification):
                retVal ="System Notification";
                break;
            default:
                retVal = "Unknown:" + loggingCategory;
        }
        
        return retVal;
    }
}
