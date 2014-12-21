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

import com.cboe.idl.cmiUtil.DateTimeStruct;
import com.cboe.idl.order.ManualOrderTimeoutRoutingStruct;
import com.cboe.idl.order.OrderHandlingInstructionStruct;

public interface ManualOrderTimeoutMessageElement extends InfoMessageElement
{
    /*
    public OrderDetailStruct getOrderDetailStruct() throws CommunicationException,
            AuthorizationException, DataValidationException, NotFoundException, SystemException;
    */
    public int getTimeoutRequestType();
    public ManualOrderTimeoutRoutingStruct getManualOrderTimeoutInfo();
    public OrderHandlingInstructionStruct getOrderHandlingInstruction();
    public int getQuantity();
    public int[] getLegQuantities();
    public DateTimeStruct getTimeoutTime();
}