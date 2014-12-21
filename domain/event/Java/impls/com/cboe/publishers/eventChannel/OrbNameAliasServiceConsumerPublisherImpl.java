//
// -----------------------------------------------------------------------------------
// Source file: AlarmWatchdogServiceConsumerPublisherImpl.java
//
// PACKAGE: com.cboe.publishers.eventChannel
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.publishers.eventChannel;

import com.cboe.idl.clusterInfo.LogicalOrbNameStruct;
import com.cboe.idl.clusterInfo.OrbNameAliasStruct;
import com.cboe.idl.clusterInfoEvents.OrbNameAliasEventService;
import com.cboe.interfaces.events.OrbNameAliasEventDelegateServiceConsumer;

public class OrbNameAliasServiceConsumerPublisherImpl implements OrbNameAliasEventDelegateServiceConsumer
{
    protected OrbNameAliasEventService eventChannelDelegate;

    public OrbNameAliasServiceConsumerPublisherImpl()
    {}

    public OrbNameAliasServiceConsumerPublisherImpl(OrbNameAliasEventService eventChannelDelegate)
    {
        this();
        setOrbNameAliasEventServiceDelegate(eventChannelDelegate);
    }

    public void setOrbNameAliasEventServiceDelegate(OrbNameAliasEventService eventChannelDelegate)
    {
        this.eventChannelDelegate = eventChannelDelegate;
    }

    public void publishOrbNameAliasByName(long id, String orbName)
    {
        eventChannelDelegate.publishOrbNameAliasByName(id, orbName);
    }

    public void publishAllOrbNameAliases(long id)
    {
        eventChannelDelegate.publishAllOrbNameAliases(id);
    }

    public void createOrbNameAlias(long id, OrbNameAliasStruct orbNameAlias)
    {
        eventChannelDelegate.createOrbNameAlias(id, orbNameAlias);
    }

    public void updateOrbNameAlias(long id, OrbNameAliasStruct orbNameAlias)
    {
        eventChannelDelegate.updateOrbNameAlias(id, orbNameAlias);
    }

    public void deleteOrbNameAlias(long id, OrbNameAliasStruct orbNameAlias)
    {
        eventChannelDelegate.deleteOrbNameAlias(id, orbNameAlias);
    }

	public void createLogicalOrbName(long id, LogicalOrbNameStruct struct)
    {
	    eventChannelDelegate.createLogicalOrbName(id, struct);
    }

	public void deleteLogicalOrbName(long id, LogicalOrbNameStruct struct)
    {
		eventChannelDelegate.deleteLogicalOrbName(id, struct);
    }

	public void publishAllLogicalOrbNames(long id)
    {
		eventChannelDelegate.publishAllLogicalOrbNames(id);
    }

	public void publishLogicalOrbNameByName(long id, String name)
    {
		eventChannelDelegate.publishLogicalOrbNameByName(id, name);
    }

	public void updateLogicalOrbName(long id, LogicalOrbNameStruct struct)
    {
		eventChannelDelegate.updateLogicalOrbName(id, struct);
    }

}
