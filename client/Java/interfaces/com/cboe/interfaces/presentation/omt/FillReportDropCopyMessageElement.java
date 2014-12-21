//
// -----------------------------------------------------------------------------------
// Source file: FillReportDropCopyMessageElement.java
//
// PACKAGE: com.cboe.interfaces.presentation.omt
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.omt;

import com.cboe.idl.cmiOrder.FilledReportStruct;
import com.cboe.idl.cmiOrder.OrderDetailStruct;
import com.cboe.idl.cmiOrder.OrderFilledReportStruct;
import com.cboe.idl.cmiOrder.OrderStruct;

import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;

import com.cboe.interfaces.presentation.order.FilledReport;

public interface FillReportDropCopyMessageElement extends DropCopyMessageElement
{
    public OrderStruct getOrderStruct();
    public FilledReport getFillReport();
    public FilledReportStruct getFilledReportStruct();
    public OrderFilledReportStruct getOrderFilledReportStruct() throws CommunicationException,
            AuthorizationException, DataValidationException, NotFoundException, SystemException;
}