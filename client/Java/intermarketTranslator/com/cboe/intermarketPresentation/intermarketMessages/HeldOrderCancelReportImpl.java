//
// -----------------------------------------------------------------------------------
// Source file: HeldOrderCancelReportImpl.java
//
// PACKAGE: com.cboe.intermarketPresentation.intermarketMessages;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.intermarketPresentation.intermarketMessages;

import com.cboe.idl.cmiIntermarketMessages.HeldOrderCancelReportStruct;

import com.cboe.interfaces.intermarketPresentation.intermarketMessages.HeldOrderCancelReport;
import com.cboe.interfaces.intermarketPresentation.intermarketMessages.HeldOrderDetail;
import com.cboe.interfaces.presentation.util.CBOEId;
import com.cboe.interfaces.presentation.order.CancelReport;

import com.cboe.presentation.util.CBOEIdImpl;
import com.cboe.presentation.order.CancelReportFactory;

class HeldOrderCancelReportImpl implements HeldOrderCancelReport
{
    protected HeldOrderDetail heldOrderDetail;
    protected CBOEId cancelRequestId;
    protected CancelReport cancelReport;
    protected HeldOrderCancelReportStruct heldOrderCancelReportStruct;
    public HeldOrderCancelReportImpl(HeldOrderCancelReportStruct heldOrderCancelReportStruct)
    {
        this.heldOrderCancelReportStruct = heldOrderCancelReportStruct;
        initialize();
    }

    private void initialize()
    {
        heldOrderDetail = HeldOrderDetailFactory.createHeldOrderDetail(heldOrderCancelReportStruct.heldOrderDetail);
        cancelRequestId = new CBOEIdImpl(heldOrderCancelReportStruct.cancelReqId);
        cancelReport = CancelReportFactory.createCancelReport(heldOrderCancelReportStruct.cancelReport);
    }

    public HeldOrderDetail getHeldOrderDetail()
    {
        return heldOrderDetail;
    }

    public CBOEId getCancelRequestId()
    {
        return cancelRequestId;
    }

    public CancelReport getCancelReport()
    {
        return cancelReport;
    }

    /**
     * Gets the underlying struct
     * @return HeldOrderCancelReportStruct
     * @deprecated Should not use underlying, but exposed interface.
     */
    public HeldOrderCancelReportStruct getStruct()
    {
        return heldOrderCancelReportStruct;
    }
}
