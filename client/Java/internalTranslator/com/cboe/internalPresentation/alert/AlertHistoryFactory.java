//
// ------------------------------------------------------------------------
// Source file: AlertHistoryFactory.java
//
// PACKAGE: com.cboe.internalPresentation.alert
//
// ------------------------------------------------------------------------
// Copyright (c) 1999-2003 The Chicago Board Options Exchange.  All Rights Reserved.
//
// ------------------------------------------------------------------------

package com.cboe.internalPresentation.alert;

import com.cboe.interfaces.internalPresentation.alert.AlertHistory;
import com.cboe.idl.alert.AlertHistoryStruct;

public class AlertHistoryFactory
{
    public static AlertHistory createAlertHistory(AlertHistoryStruct alertHistoryStruct)
    {
        return new AlertHistoryImpl(alertHistoryStruct);
    }
}