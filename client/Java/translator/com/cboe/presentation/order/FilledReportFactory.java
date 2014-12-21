/**
 * Copyright 2000-2002 (C) Chicago Board Options Exchange
 * Package: com.cboe.presentation.order
 * User: torresl
 * Date: Dec 31, 2002 2:49:44 PM
 */
package com.cboe.presentation.order;

import com.cboe.idl.cmiOrder.FilledReportStruct;

import com.cboe.interfaces.presentation.order.FilledReport;

public class FilledReportFactory
{
    public static FilledReport createFilledReport(FilledReportStruct filledReportStruct)
    {
        return new FilledReportImpl(filledReportStruct);
    }
}