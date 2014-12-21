//
// -----------------------------------------------------------------------------------
// Source file: CancelReportImpl.java
//
// PACKAGE: com.cboe.presentation.order;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.order;

import com.cboe.idl.cmiOrder.CancelReportStruct;
import com.cboe.interfaces.domain.dateTime.DateTime;
import com.cboe.interfaces.presentation.order.CancelReport;
import com.cboe.interfaces.presentation.order.OrderId;

import com.cboe.presentation.common.dateTime.DateTimeImpl;

class CancelReportImpl implements CancelReport
{
    protected OrderId   orderId;
    protected Short     cancelReportType;
    protected Short     cancelReason;
    protected Integer   productKey;
    protected String    sessionName;
    protected Integer   cancelledQuantity;
    protected Integer   tlcQuantity;
    protected Integer   mismatchedQuantity;
    protected DateTime  timeSent;
    protected String    orsId;
    protected Integer   totalCancelledQuantity;
    protected Integer   transactionSequenceNumber;
    protected String    userAssignedCancelId;

    protected CancelReportStruct cancelReportStruct;
    public CancelReportImpl(CancelReportStruct cancelReportStruct)
    {
        this.cancelReportStruct = cancelReportStruct;
        initialize();
    }

    private void initialize()
    {
        orderId = OrderIdFactory.createOrderId(cancelReportStruct.orderId);
        cancelReportType = new Short(cancelReportStruct.cancelReportType);
        cancelReason = new Short(cancelReportStruct.cancelReason);
        productKey = new Integer(cancelReportStruct.productKey);
        sessionName = getString(cancelReportStruct.sessionName);
        cancelledQuantity = new Integer(cancelReportStruct.cancelledQuantity);
        tlcQuantity = new Integer(cancelReportStruct.tlcQuantity);
        mismatchedQuantity = new Integer(cancelReportStruct.mismatchedQuantity);
        timeSent = new DateTimeImpl(cancelReportStruct.timeSent);
        orsId = getString(cancelReportStruct.orsId);
        totalCancelledQuantity = new Integer(cancelReportStruct.totalCancelledQuantity);
        transactionSequenceNumber = new Integer(cancelReportStruct.transactionSequenceNumber);
        userAssignedCancelId = getString(cancelReportStruct.userAssignedCancelId);
    }
    private String getString(String value)
    {
        String val = "";
        if(value != null)
        {
            val = new String(value);
        }
        return val;
    }
    public OrderId getOrderId()
    {
        return orderId;
    }

    public Short getCancelReportType()
    {
        return cancelReportType;
    }

    public Short getCancelReason()
    {
        return cancelReason;
    }

    public Integer getProductKey()
    {
        return productKey;
    }

    public String getSessionName()
    {
        return sessionName;
    }

    public Integer getCancelledQuantity()
    {
        return cancelledQuantity;
    }

    public Integer getTlcQuantity()
    {
        return tlcQuantity;
    }

    public Integer getMismatchedQuantity()
    {
        return mismatchedQuantity;
    }

    public DateTime getTimeSent()
    {
        return timeSent;
    }

    public String getOrsId()
    {
        return orsId;
    }

    public Integer getTotalCancelledQuantity()
    {
        return totalCancelledQuantity;
    }

    public Integer getTransactionSequenceNumber()
    {
        return transactionSequenceNumber;
    }

    public String getUserAssignedCancelId()
    {
        return userAssignedCancelId;
    }

    public CancelReportStruct getCancelReportStruct()
    {
        return cancelReportStruct;
    }

    /**
     * Gets the underlying struct
     * @return CancelReportStruct
     * @deprecated Should not use underlying, but exposed interface.
     */
    public CancelReportStruct getStruct()
    {
        return cancelReportStruct;
    }
}
