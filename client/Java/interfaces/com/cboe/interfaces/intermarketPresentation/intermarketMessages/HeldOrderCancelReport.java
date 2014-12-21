//
// -----------------------------------------------------------------------------------
// Source file: HeldOrderCancelReport.java
//
// PACKAGE: com.cboe.interfaces.intermarketPresentation.intermarketMessages;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2002 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.intermarketPresentation.intermarketMessages;

import com.cboe.idl.cmiIntermarketMessages.HeldOrderCancelReportStruct;

import com.cboe.interfaces.presentation.util.CBOEId;
import com.cboe.interfaces.presentation.order.CancelReport;

public interface HeldOrderCancelReport
{
    /**
     * Gets the underlying struct
     * @return HeldOrderCancelReportStruct
     * @deprecated Should not use underlying, but exposed interface.
     */
    public HeldOrderCancelReportStruct getStruct();

    public HeldOrderDetail getHeldOrderDetail();
    public CBOEId getCancelRequestId();
    public CancelReport getCancelReport();
}