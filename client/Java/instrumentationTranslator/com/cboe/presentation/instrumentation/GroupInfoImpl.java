//
// -----------------------------------------------------------------------------------
// Source file: GroupInfoImpl.java
//
// PACKAGE: com.cboe.presentation.instrumentation
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.instrumentation;

import com.cboe.interfaces.presentation.processes.GroupInfo;

import com.cboe.presentation.common.businessModels.AbstractMutableBusinessModel;

public class GroupInfoImpl extends AbstractMutableBusinessModel implements GroupInfo
{
    String groupName;
    String[] hostNames;


    public GroupInfoImpl(String groupName, String[] hostNames)
    {
        super();
        setGroupName(groupName);
        setHostNames(hostNames);
    }

    public String getGroupName()
    {
        return groupName;
    }

    public String[] getHostNames()
    {
        return hostNames;
    }

    public void setGroupName(String name)
    {
        groupName = name;
    }

    public void setHostNames(String[] names)
    {
        hostNames = names;
    }
}
