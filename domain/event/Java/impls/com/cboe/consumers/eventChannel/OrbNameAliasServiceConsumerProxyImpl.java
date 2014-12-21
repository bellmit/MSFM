/*
 * Created on Dec 7, 2005
 * -----------------------------------------------------------------------------------
 * Copyright (c) 2005 The Chicago Board Options Exchange. All Rights Reserved.
 *-----------------------------------------------------------------------------------
 */
package com.cboe.consumers.eventChannel;

import org.omg.CORBA.Any;
import org.omg.CORBA.Object;
import org.omg.CosEventComm.Disconnected;

import com.cboe.idl.clusterInfo.LogicalOrbNameStruct;
import com.cboe.idl.clusterInfo.OrbNameAliasStruct;
import com.cboe.idl.clusterInfoEvents.OrbNameAliasEventServicePOA;
import com.cboe.interfaces.events.OrbNameAliasServiceConsumer;

public class OrbNameAliasServiceConsumerProxyImpl 
       extends OrbNameAliasEventServicePOA 
       implements OrbNameAliasServiceConsumer
{
     
    private OrbNameAliasServiceConsumer delegate;
    public OrbNameAliasServiceConsumerProxyImpl(OrbNameAliasServiceConsumer delegate)
    {
        super();
        this.delegate = delegate;
    }

    public void publishOrbNameAliasByName(long id, String orbName)
    {
        delegate.publishOrbNameAliasByName(id, orbName);
    }

    public void publishAllOrbNameAliases(long id)
    {
        delegate.publishAllOrbNameAliases(id);
    }

    public void createOrbNameAlias(long id, OrbNameAliasStruct alias)
    {
        delegate.createOrbNameAlias(id, alias);
    }

    public void updateOrbNameAlias(long id, OrbNameAliasStruct alias)
    {
        delegate.updateOrbNameAlias(id, alias);
    }

    public void deleteOrbNameAlias(long id, OrbNameAliasStruct alias)
    {
        delegate.deleteOrbNameAlias(id, alias);
    }

    public void publishLogicalOrbNameByName(long id, String name)
    {
    	delegate.publishLogicalOrbNameByName(id, name);
    }
    
    public void publishAllLogicalOrbNames(long id)
    {
    	delegate.publishAllLogicalOrbNames(id);
    }
    
	public void createLogicalOrbName(long id, LogicalOrbNameStruct struct)
    {
		delegate.createLogicalOrbName(id, struct);
    }

	public void updateLogicalOrbName(long id, LogicalOrbNameStruct struct)
	{
		delegate.updateLogicalOrbName(id, struct);
	}
	
	public void deleteLogicalOrbName(long id, LogicalOrbNameStruct struct)
    {
	    delegate.deleteLogicalOrbName(id, struct);
    }

    public Object get_typed_consumer()
    {
        return null;
    }

    public void push(Any arg0) throws Disconnected
    {

    }

    public void disconnect_push_consumer()
    {

    }

}
