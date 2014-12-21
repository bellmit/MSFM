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

import com.cboe.exceptions.ExceptionDetails;
import com.cboe.idl.clusterInfo.LogicalOrbNameStruct;
import com.cboe.idl.clusterInfo.OrbNameAliasStruct;
import com.cboe.idl.clusterInfoEvents.OrbNameAliasEventConsumerPOA;
import com.cboe.interfaces.events.OrbNameAliasConsumer;

public class OrbNameAliasConsumerProxyImpl extends OrbNameAliasEventConsumerPOA implements OrbNameAliasConsumer
{

    private OrbNameAliasConsumer  delegate;
    public OrbNameAliasConsumerProxyImpl(OrbNameAliasConsumer  delegate)
    {
        super();
        this.delegate = delegate;
    }

    public void acceptOrbNameAliases(long id, OrbNameAliasStruct[] aliases)
    {
        delegate.acceptOrbNameAliases(id,aliases);
    }

    public void acceptNewOrbNameAlias(long id, OrbNameAliasStruct alias)
    {
        delegate.acceptNewOrbNameAlias(id, alias);
    }

    public void acceptChangedOrbNameAlias(long id, OrbNameAliasStruct alias)
    {
        delegate.acceptChangedOrbNameAlias(id, alias);
    }

    public void acceptDeleteOrbNameAlias(long id, OrbNameAliasStruct alias)
    {
        delegate.acceptDeleteOrbNameAlias(id, alias);
    }

    public void acceptLogicalOrbNames(long id, LogicalOrbNameStruct[] struct)
    {
    	delegate.acceptLogicalOrbNames(id, struct);
    }
    
    public void acceptNewLogicalOrbName(long id, LogicalOrbNameStruct struct)
    {
    	delegate.acceptNewLogicalOrbName(id, struct);
    }
    
    public void acceptChangedLogicalOrbName(long id, LogicalOrbNameStruct struct)
    {
    	delegate.acceptChangedLogicalOrbName(id, struct);
    }
    
    public void acceptDeleteLogicalOrbName(long id, LogicalOrbNameStruct struct)
    {
    	delegate.acceptDeleteLogicalOrbName(id, struct);
    }
    
    public void acceptAlreadyExistsException(long id, ExceptionDetails exception)
    {
        delegate.acceptAlreadyExistsException(id, exception);
    }

    public void acceptDataValidationException(long id, ExceptionDetails exception)
    {
        delegate.acceptDataValidationException(id, exception);

    }

    public void acceptNotFoundException(long id, ExceptionDetails exception)
    {
        delegate.acceptNotFoundException(id, exception);
    }

    public void acceptNotAcceptedException(long id, ExceptionDetails exception)
    {
        delegate.acceptNotAcceptedException(id, exception);
    }

    public void acceptSystemException(long id, ExceptionDetails exception)
    {
        delegate.acceptSystemException(id, exception);
    }

    public void acceptTransactionFailedException(long id, ExceptionDetails exception)
    {
        delegate.acceptTransactionFailedException(id, exception);
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
