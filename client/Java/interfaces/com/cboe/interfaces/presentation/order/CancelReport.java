//
// -----------------------------------------------------------------------------------
// Source file: CancelReport.java
//
// PACKAGE: com.cboe.interfaces.presentation.order;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2002 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.order;

import com.cboe.idl.cmiOrder.CancelReportStruct;
import com.cboe.interfaces.domain.dateTime.DateTime;

public interface CancelReport
{
    /**
     * Gets the underlying struct
     * @return CancelReportStruct
     * @deprecated Should not use underlying, but exposed interface.
     */
    public CancelReportStruct getStruct();

    public OrderId getOrderId();

    public Short getCancelReportType();

    public Short getCancelReason();

    public Integer getProductKey();

    public String getSessionName();

    public Integer getCancelledQuantity();

    public Integer getTlcQuantity();

    public Integer getMismatchedQuantity();

    public DateTime getTimeSent();

    public String getOrsId();

    public Integer getTotalCancelledQuantity();

    public Integer getTransactionSequenceNumber();

    public String getUserAssignedCancelId();
}