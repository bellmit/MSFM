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

import com.cboe.interfaces.presentation.common.businessModels.MutableBusinessModel;

public interface ProcessInfoModel extends ProcessInfo, MutableBusinessModel
{
    public void setOnlineStatus(short onlineStatus, String eventOriginator, short reasonCode);
    public void setPoaStatus(short poaStatus, String eventOriginator, short reasonCode);
    public void setClusterName(String clusterName);
}