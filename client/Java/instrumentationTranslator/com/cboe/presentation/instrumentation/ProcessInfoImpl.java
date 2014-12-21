// -----------------------------------------------------------------------------------
// Source file: ProcessInfoImpl.java
//
// PACKAGE: com.cboe.presentation.instrumentation;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.instrumentation;

import com.cboe.presentation.common.businessModels.AbstractMutableBusinessModel;
import com.cboe.interfaces.instrumentation.Status;
import com.cboe.interfaces.presentation.common.businessModels.MutableBusinessModel;
import com.cboe.interfaces.presentation.processes.ProcessInfo;
import com.cboe.interfaces.presentation.processes.ProcessInfoModel;

public class ProcessInfoImpl extends AbstractMutableBusinessModel implements ProcessInfoModel
{
    private short onlineStatus;
    private String onlineStatusEventOriginator;
    private short onlineStatusReasonCode;
    private short poaStatus;
    private String poaStatusEventOriginator;
    private short poaStatusReasonCode;
    private String processName;
    private String orbName;
    private String hostName;
    private int port;
    private String clusterName;

    protected ProcessInfoImpl()
    {
        super();
    }

    protected ProcessInfoImpl(String processName, String orbName, String hostName, int port,
                              short onlineStatus, String onlineStatusOriginator, short onlineStatusReasonCode,
                              short poaStatus, String poaStatusOriginator, short poaStatusReasonCode,
                              String clusterName)
    {
        this();
        this.processName = processName;
        this.orbName = orbName;
        this.hostName = hostName;
        this.port = port;
        this.onlineStatus = onlineStatus;
        this.onlineStatusEventOriginator = onlineStatusOriginator;
        this.onlineStatusReasonCode = onlineStatusReasonCode;
        this.poaStatus = poaStatus;
        this.poaStatusEventOriginator = poaStatusOriginator;
        this.poaStatusReasonCode = poaStatusReasonCode;
        this.clusterName = clusterName;
    }

    protected ProcessInfoImpl(ProcessInfo process)
    {
        this(process.getProcessName(),
             process.getOrbName(),
             process.getHostName(),
             process.getPort(),
             process.getOnlineStatus(),
             process.getOnlineStatusOriginator(),
             process.getOnlineStatusReasonCode(),
             process.getPoaStatus(),
             process.getPoaStatusOriginator(),
             process.getPoaStatusReasonCode(),
             process.getClusterName());
    }

    public int hashCode()
    {
        return getOrbName().hashCode();
    }

    public boolean equals(Object obj)
    {
        return getOrbName().equals(obj);
    }

    public String toString()
    {
        return getOrbName();
    }

    public short getMasterSlaveStatus()
    {
        short masterSlaveStatus;
        short poaStatus = getPoaStatus();
        if(poaStatus == Status.UP)
        {
            masterSlaveStatus = Status.MASTER;
        }
        else if(poaStatus == Status.DOWN)
        {
            masterSlaveStatus = Status.SLAVE;
        }
        else
        {
            masterSlaveStatus = poaStatus;
        }
        return masterSlaveStatus;
    }

    public short getOnlineStatus()
    {
        return onlineStatus;
    }

    public String getOnlineStatusOriginator()
    {
        return onlineStatusEventOriginator;
    }

    public short getOnlineStatusReasonCode()
    {
        return onlineStatusReasonCode;
    }

    /**
     *  Return the status of the online, or the poa status, if the online status is up.
     */
    public short getOnlinePoaStatusCombo()
    {
        short status = getOnlineStatus();
        if (status == Status.UP)
        {
            short masterSlaveStatus = getMasterSlaveStatus();
            if (masterSlaveStatus != Status.UNKNOWN && masterSlaveStatus != Status.NO_RESPONSE)
            {
                status = masterSlaveStatus;
            }
        }
        return status;
    }

    public String getProcessName()
    {
        return processName;
    }

    public String getOrbName()
    {
        return orbName;
    }

    public String getHostName()
    {
        return hostName;
    }

    public int getPort()
    {
        return port;
    }

    public short getPoaStatus()
    {
        return poaStatus;
    }

    public String getPoaStatusOriginator()
    {
        return poaStatusEventOriginator;
    }

    public short getPoaStatusReasonCode()
    {
        return poaStatusReasonCode;
    }

    public void setOnlineStatus(short onlineStatus, String eventOriginator, short reasonCode)
    {
        setOnlineStatus(onlineStatus, eventOriginator, reasonCode, true);
    }

    public void setPoaStatus(short poaStatus, String eventOriginator, short reasonCode)
    {
        if (this.poaStatus != poaStatus)
        {
            short oldValue = this.poaStatus;
            this.poaStatus = poaStatus;
            this.poaStatusEventOriginator = eventOriginator;
            this.poaStatusReasonCode = reasonCode;
            setModified(true);
            firePropertyChange(MutableBusinessModel.DATA_CHANGE_EVENT, oldValue, poaStatus);
        }
    }



    protected void setOnlineStatus(short onlineStatus, String eventOriginator, short reasonCode, boolean fireEvent)
    {
        if( this.onlineStatus != onlineStatus )
        {
            short oldValue = this.onlineStatus;
            this.onlineStatus = onlineStatus;
            this.onlineStatusEventOriginator = eventOriginator;
            this.onlineStatusReasonCode = reasonCode;
            setModified(true);
            if(fireEvent)
            {
                firePropertyChange(MutableBusinessModel.DATA_CHANGE_EVENT, oldValue, onlineStatus);
            }
        }
    }



    /**
     * Implements Cloneable
     */
    public Object clone() throws CloneNotSupportedException
    {
        ProcessInfoModel clonedObject = ProcessInfoFactory.createProcessInfoModel(getProcessName(),
                                                                                  getOrbName(),
                                                                                  getHostName(),
                                                                                  getPort(),
                                                                                  getOnlineStatus(),
                                                                                  getOnlineStatusOriginator(),
                                                                                  getOnlineStatusReasonCode(),
                                                                                  getPoaStatus(),
                                                                                  getPoaStatusOriginator(),
                                                                                  getPoaStatusReasonCode(),
                                                                                  getClusterName());
        return clonedObject;
    }

    public String getClusterName()
    {
        return clusterName;
    }
    public void setClusterName(String clusterName)
    {
        this.clusterName = clusterName;
    }
}
