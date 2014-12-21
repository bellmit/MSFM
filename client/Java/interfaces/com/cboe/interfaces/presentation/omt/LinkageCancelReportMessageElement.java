//
// -----------------------------------------------------------------------------------
// Source file: LinkageCancelReportMessageElement.java
//
// PACKAGE: com.cboe.interfaces.presentation.omt
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.omt;

import com.cboe.idl.order.LinkageExtensionsStruct;

import com.cboe.interfaces.presentation.order.CancelReport;

public interface LinkageCancelReportMessageElement extends InfoMessageElement
{
    CancelReport getCancelReport();
    LinkageExtensionsStruct getLinkageExtensions();
}