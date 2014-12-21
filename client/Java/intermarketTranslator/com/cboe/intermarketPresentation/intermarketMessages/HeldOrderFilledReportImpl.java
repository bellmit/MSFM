/**
 * Copyright 2000-2002 (C) Chicago Board Options Exchange
 * Package: com.cboe.intermarketPresentation.intermarketMessages
 * User: torresl
 * Date: Jan 2, 2003 10:01:30 AM
 */
package com.cboe.intermarketPresentation.intermarketMessages;

import com.cboe.interfaces.intermarketPresentation.intermarketMessages.HeldOrderFilledReport;
import com.cboe.interfaces.intermarketPresentation.intermarketMessages.HeldOrderDetail;
import com.cboe.interfaces.presentation.order.FilledReport;

import com.cboe.idl.cmiIntermarketMessages.HeldOrderFilledReportStruct;
import com.cboe.presentation.order.FilledReportFactory;

class HeldOrderFilledReportImpl implements HeldOrderFilledReport
{
    HeldOrderDetail heldOrderDetail;
    FilledReport[] filledReports;
    HeldOrderFilledReportStruct heldOrderFilledReportStruct;

    public HeldOrderFilledReportImpl(HeldOrderFilledReportStruct heldOrderFilledReportStruct)
    {
        this.heldOrderFilledReportStruct = heldOrderFilledReportStruct;
        initialize();
    }

    public HeldOrderDetail getHeldOrderDetail()
    {
        return heldOrderDetail;
    }

    public FilledReport[] getFilledReports()
    {
        return filledReports;
    }

    public HeldOrderFilledReportStruct toStruct()
    {
        return heldOrderFilledReportStruct;
    }

    private void initialize()
    {
        heldOrderDetail = HeldOrderDetailFactory.createHeldOrderDetail(heldOrderFilledReportStruct.heldOrderDetail);
        filledReports = new FilledReport[heldOrderFilledReportStruct.filledReport.length];
        for(int i = 0; i < filledReports.length; i++)
        {
            filledReports[i] = FilledReportFactory.createFilledReport(heldOrderFilledReportStruct.filledReport[i]);
        }
    }
}