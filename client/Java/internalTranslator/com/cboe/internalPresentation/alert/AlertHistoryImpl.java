//
// ------------------------------------------------------------------------
// Source file: AlertHistoryImpl.java
//
// PACKAGE: com.cboe.internalPresentation.alert
//
// ------------------------------------------------------------------------
// Copyright (c) 1999-2003 The Chicago Board Options Exchange.  All Rights Reserved.
//
// ------------------------------------------------------------------------

package com.cboe.internalPresentation.alert;

import com.cboe.intermarketPresentation.intermarketMessages.AlertFactory;
import com.cboe.interfaces.internalPresentation.alert.AlertHistory;
import com.cboe.interfaces.domain.dateTime.DateTime;
import com.cboe.interfaces.intermarketPresentation.intermarketMessages.Alert;
import com.cboe.presentation.common.dateTime.DateTimeImpl;
import com.cboe.idl.alert.AlertHistoryStruct;

/**
 * @author torresl@cboe.com
 */
class AlertHistoryImpl implements AlertHistory
{
    DateTime startTime;
    DateTime endTime;
    boolean moreAlertsIndicator;
    Alert[] entries;

    AlertHistoryStruct alertHistoryStruct;

    public AlertHistoryImpl(AlertHistoryStruct alertHistoryStruct)
    {
        super();
        this.alertHistoryStruct = alertHistoryStruct;
        initialize();
    }

    private void initialize()
    {
        startTime = new DateTimeImpl(alertHistoryStruct.alertHistoryHdr.startTime);
        endTime = new DateTimeImpl(alertHistoryStruct.alertHistoryHdr.endTime);
        moreAlertsIndicator = alertHistoryStruct.alertHistoryHdr.moreAlertsIndicator;
        entries = new Alert[alertHistoryStruct.entries.length];
        for (int i = 0; i < alertHistoryStruct.entries.length; i++)
        {
            entries[i] = AlertFactory.createAlert(alertHistoryStruct.entries[i]);
        }
    }

    public DateTime getStartTime()
    {
        return startTime;
    }

    public DateTime getEndTime()
    {
        return endTime;
    }

    public boolean getMoreAlertsIndicator()
    {
        return moreAlertsIndicator;
    }

    public Alert[] getEntries()
    {
        return entries;
    }

    public AlertHistoryStruct toStruct()
    {
        return alertHistoryStruct;
    }
}
