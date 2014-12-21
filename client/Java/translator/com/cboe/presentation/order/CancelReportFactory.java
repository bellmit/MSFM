//
// -----------------------------------------------------------------------------------
// Source file: CancelReportFactory.java
//
// PACKAGE: com.cboe.presentation.order;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2002 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.order;

import com.cboe.idl.cmiOrder.CancelReportStruct;

import com.cboe.interfaces.presentation.order.CancelReport;

public class CancelReportFactory
{
    public static CancelReport createCancelReport(CancelReportStruct cancelReportStruct)
    {
        return new CancelReportImpl(cancelReportStruct);
    }
}