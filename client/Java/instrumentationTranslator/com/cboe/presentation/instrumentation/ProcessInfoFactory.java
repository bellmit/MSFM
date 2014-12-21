//
// -----------------------------------------------------------------------------------
// Source file: ProcessInfoFactory.java
//
// PACKAGE: com.cboe.presentation.instrumentation
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.instrumentation;

import com.cboe.idl.processWatcher.PWEventCodes;

import com.cboe.interfaces.presentation.processes.ProcessInfo;
import com.cboe.interfaces.presentation.processes.ProcessInfoModel;


public abstract class ProcessInfoFactory
{
    /**
     * Creates an instance of ProcessInfo
     * @param orbName - Corba ORB name String for this ProcessInfo object
     * @param onlineStatus for this ProcessInfo object
     * @return ProcessInfo
     */
    public static ProcessInfo createProcessInfo(String orbName, short onlineStatus)
    {
        return createProcessInfo("", orbName, "", 0,
                                 onlineStatus, "", PWEventCodes.Unknown,
                                 (short) 0, "", PWEventCodes.Unknown,
                                 "");
    }

    /**
     * Creates an instance of ProcessInfo
     * @param processName - process name String for this ProcessInfo object
     * @param orbName - Corba ORB name String for this ProcessInfo object
     * @param hostName - the hostname String for this ProcessInfo object
     * @param port - the port number for this ProcessInfo object
     * @param onlineStatus for this ProcessInfo object
     * @param poaStatus - POA status for this ProcessInfo object
     * @return ProcessInfo
     */
    public static ProcessInfo createProcessInfo(String processName, String orbName, String hostName, int port,
                                                short onlineStatus, String onlineStatusOriginator, short onlineStatusReasonCode,
                                                short poaStatus, String poaStatusOriginator, short poaStatusReasonCode,
                                                String clusterName)
    {
        return createProcessInfoModel(processName, orbName, hostName, port, onlineStatus,
                                      onlineStatusOriginator, onlineStatusReasonCode, poaStatus,
                                      poaStatusOriginator, poaStatusReasonCode, clusterName);
    }

    /**
     * Creates an instance of ProcessInfoModel.
     * @param orbName - Corba ORB name String for this ProcessInfo object
     * @param onlineStatus for this ProcessInfo object
     * @return ProcessInfoModel
     */
    public static ProcessInfoModel createProcessInfoModel(String orbName, short onlineStatus)
    {
        return createProcessInfoModel("", orbName, "", 0, onlineStatus, "", PWEventCodes.Unknown, (short)0, "",
                                      PWEventCodes.Unknown, "");
    }

    /**
     * Creates an instance of ProcessInfoModel.
     * @param processName - process name String for this ProcessInfo object
     * @param orbName - Corba ORB name String for this ProcessInfo object
     * @param hostName - the hostname String for this ProcessInfo object
     * @param port - the port number for this ProcessInfo object
     * @param onlineStatus for this ProcessInfo object
     * @param poaStatus - POA status for this ProcessInfo object
     * @return ProcessInfoModel
     */
    public static ProcessInfoModel createProcessInfoModel(String processName, String orbName, String hostName, int port,
                                                          short onlineStatus, String onlineStatusOriginator, short onlineStatusReasonCode,
                                                          short poaStatus, String poaStatusOriginator, short poaStatusReasonCode,
                                                          String clusterName)
    {
        return new ProcessInfoImpl(processName, orbName, hostName, port, onlineStatus, onlineStatusOriginator, onlineStatusReasonCode,
                                   poaStatus, poaStatusOriginator, poaStatusReasonCode, clusterName);
    }

    public static ProcessInfoModel createProcessInfoModel(ProcessInfo process)
    {
        return new ProcessInfoImpl(process);
    }
}
