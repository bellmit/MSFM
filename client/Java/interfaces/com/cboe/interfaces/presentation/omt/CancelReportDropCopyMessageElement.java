//
// -----------------------------------------------------------------------------------
// Source file: CancelReportDropCopyMessageElement.java
//
// PACKAGE: com.cboe.interfaces.presentation.omt
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.omt;

import com.cboe.idl.cmiOrder.CancelReportStruct;
import com.cboe.idl.cmiOrder.OrderCancelReportStruct;

import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;

import com.cboe.interfaces.presentation.order.CancelReport;

public interface CancelReportDropCopyMessageElement extends DropCopyMessageElement
{
    public CancelReportStruct getCancelReportStruct();
    public CancelReport getCancelReport();
    public OrderCancelReportStruct getOrderCancelReportStruct() throws CommunicationException,
            AuthorizationException, DataValidationException, NotFoundException, SystemException;
}
