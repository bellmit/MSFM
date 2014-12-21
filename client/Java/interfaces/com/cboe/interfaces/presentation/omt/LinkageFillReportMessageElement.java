//
// -----------------------------------------------------------------------------------
// Source file: LinkageFillReportMessageElement.java
//
// PACKAGE: com.cboe.interfaces.presentation.omt
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.omt;

import com.cboe.idl.order.LinkageExtensionsStruct;

import com.cboe.interfaces.presentation.order.FilledReport;

public interface LinkageFillReportMessageElement extends InfoMessageElement
{
    FilledReport getFillReport();
    LinkageExtensionsStruct getLinkageExtensions();
}