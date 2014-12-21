//
// -----------------------------------------------------------------------------------
// Source file: ProcessInfo.java
//
// PACKAGE: com.cboe.interfaces.instrumentation
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.processes;

import com.cboe.interfaces.presentation.common.businessModels.BusinessModel;

public interface GroupInfo extends BusinessModel
{
    public String getGroupName();
    public String[] getHostNames();
}