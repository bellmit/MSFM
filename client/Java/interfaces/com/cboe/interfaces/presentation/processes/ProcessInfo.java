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

public interface ProcessInfo extends BusinessModel
{
    public String getProcessName();
    public String getOrbName();
    public String getHostName();
    public int getPort();
    public short getOnlineStatus();
    public String getOnlineStatusOriginator();
    public short getOnlineStatusReasonCode();
    public short getMasterSlaveStatus();
    public short getPoaStatus();
    public String getPoaStatusOriginator();
    public short getPoaStatusReasonCode();
    public short getOnlinePoaStatusCombo();
    public String getClusterName();
}