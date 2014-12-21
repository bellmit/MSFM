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

import com.cboe.interfaces.presentation.common.businessModels.MutableBusinessModel;

public interface OrbNameAliasModel extends OrbNameAlias,MutableBusinessModel
{
    public void setDisplayName(String displayName);
    public void setCluster(String cluster);
    public void setSubCluster(String subCluster);
}

