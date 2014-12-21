//
//
// -----------------------------------------------------------------------------------
// Source file: LogicalNameImpl
//
// PACKAGE: com.cboe.presentation.logicalName;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2009 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.orbNameAlias;

import com.cboe.interfaces.presentation.common.businessModels.MutableBusinessModel;
import com.cboe.interfaces.presentation.processes.LogicalNameModel;

import com.cboe.presentation.common.businessModels.AbstractMutableBusinessModel;

public class LogicalNameImpl extends AbstractMutableBusinessModel implements LogicalNameModel
{
    protected String orbName;
    protected String logicalName;

    public LogicalNameImpl(String logicalName)
    {
        this.logicalName = logicalName;
    }

    public LogicalNameImpl(String logicalName, String orbName)
    {
        this.orbName = orbName;
        this.logicalName = logicalName;
    }

    public String getLogicalName()
    {
        return logicalName;
    }

    public void setLogicalName(String logicalName)
    {
        this.logicalName = logicalName;
    }

    public String getOrbName()
    {
        return orbName;
    }

    public void setOrbName(String orbName)
    {
        if(this.orbName != orbName)
        {
            String oldValue = this.orbName;
            this.orbName = orbName;
            setModified(true);
            firePropertyChange(MutableBusinessModel.DATA_CHANGE_EVENT, oldValue, orbName);
        }
    }

    public Object clone() throws CloneNotSupportedException
    {
        LogicalNameImpl impl = new LogicalNameImpl(logicalName, orbName);
        return impl;
    }

    public Object getKey()
    {
        return logicalName;
    }
}