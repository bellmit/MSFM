//
// -----------------------------------------------------------------------------------
// Source file: FillReportRejectMessageElement.java
//
// PACKAGE: com.cboe.interfaces.presentation.omt
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.omt;

import com.cboe.idl.order.ManualFillStruct;

public interface FillReportRejectMessageElement extends InfoMessageElement
{
    ManualFillStruct getFillReport();
    String getRejectReason();
}