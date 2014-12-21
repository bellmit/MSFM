//
//
// -----------------------------------------------------------------------------------
// Source file: OrbNameAlias
//
// PACKAGE: com.cboe.interfaces.instrumentation.OrbNameAlias;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------

package com.cboe.interfaces.presentation.processes;

import com.cboe.interfaces.presentation.common.businessModels.BusinessModel;

public interface OrbNameAlias extends BusinessModel
{
    public String getOrbName();
    public String getDisplayName();
    public String getCluster();
    public String getSubCluster();
    public String getClusterSubClusterName();
}

