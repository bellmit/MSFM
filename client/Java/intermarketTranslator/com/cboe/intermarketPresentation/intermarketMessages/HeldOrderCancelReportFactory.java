//
// -----------------------------------------------------------------------------------
// Source file: HeldOrderCancelReportFactory.java
//
// PACKAGE: com.cboe.intermarketPresentation.intermarketMessages;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.intermarketPresentation.intermarketMessages;

import com.cboe.idl.cmiIntermarketMessages.HeldOrderCancelReportStruct;

import com.cboe.interfaces.intermarketPresentation.intermarketMessages.HeldOrderCancelReport;

public class HeldOrderCancelReportFactory
{
    public static HeldOrderCancelReport createHeldOrderCancelReport(HeldOrderCancelReportStruct heldOrderCancelReportStruct)
    {
        return new HeldOrderCancelReportImpl(heldOrderCancelReportStruct);
    }
}