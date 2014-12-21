//
//
// -----------------------------------------------------------------------------------
// Source file: LogicalName
//
// PACKAGE: com.cboe.interfaces.presentation.processes;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2009 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------

package com.cboe.interfaces.presentation.processes;

import com.cboe.interfaces.presentation.common.businessModels.BusinessModel;

public interface LogicalName extends BusinessModel
{
    public String getLogicalName();
    public String getOrbName();
}