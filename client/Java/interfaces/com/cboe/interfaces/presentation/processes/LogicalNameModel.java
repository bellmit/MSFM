//
//
// -----------------------------------------------------------------------------------
// Source file: LogicalNameModel
//
// PACKAGE: com.cboe.interfaces.presentation.processes;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2009 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------

package com.cboe.interfaces.presentation.processes;

import com.cboe.interfaces.presentation.common.businessModels.MutableBusinessModel;

public interface LogicalNameModel extends LogicalName, MutableBusinessModel
{
    public void setOrbName(String orbName);
}