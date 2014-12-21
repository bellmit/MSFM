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

import com.cboe.idl.order.ManualFillStruct;

public interface ManualFillTimeoutMessageElement extends InfoMessageElement
{
    /*
    public OrderStruct getOrderStruct();
    public FilledReport getFillReport();
    public FilledReportStruct getFilledReportStruct();
    public OrderFilledReportStruct getOrderFilledReportStruct() throws CommunicationException,
            AuthorizationException, DataValidationException, NotFoundException, SystemException;
    public OrderDetailStruct getOrderDetailStruct() throws CommunicationException,
            AuthorizationException, DataValidationException, NotFoundException, SystemException;
    */
    public int getTimeoutRequestType();

    public ManualFillStruct getManualFillInfo();
}