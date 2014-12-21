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

import com.cboe.idl.groupElement.ElementStruct;
import com.cboe.idl.groupElement.ElementEntryStruct;
import com.cboe.idl.icsGroupElementEvents.ICSGroupElementEventServicePOA;
import com.cboe.interfaces.events.ICSGroupElementServiceConsumer;

public class ICSGroupElementServiceConsumerProxyImpl 
       extends ICSGroupElementEventServicePOA
       implements ICSGroupElementServiceConsumer
{

    private ICSGroupElementServiceConsumer delegate;
    public ICSGroupElementServiceConsumerProxyImpl(ICSGroupElementServiceConsumer delegate)
    {
        super();
        this.delegate = delegate;
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

    public void createElementsForGroup(long id, long parentKey, ElementEntryStruct[] elementEntryStructs)
    {
        delegate.createElementsForGroup(id, parentKey, elementEntryStructs);
    }

    public void addElementsToGroup(long id, long parentKey, long[] elementKeys)
    {
        delegate.addElementsToGroup(id, parentKey, elementKeys);
    }

    public void updateElement(long id, ElementStruct elementStruct)
    {
        delegate.updateElement(id, elementStruct);
    }

    public void removeElementsFromGroup(long id, long parentKey, long[] elementKeys)
    {
        delegate.removeElementsFromGroup(id, parentKey, elementKeys);
    }

    public void publishRootElementForGroupType(long id, short groupType)
    {
        delegate.publishRootElementForGroupType(id, groupType);
    }

    public void publishElementsForGroup(long id, long elementKey)
    {
        delegate.publishElementsForGroup(id, elementKey);
    }

    public void copyElementsToGroup(long id, long newGroupKey, ElementEntryStruct cloneEntryStruct, long[] elementKeys)
    {
        delegate.copyElementsToGroup(id, newGroupKey, cloneEntryStruct, elementKeys);
    }

    public void moveElementsToGroup(long id, long currentGroupKey, long newGroupKey, long[] elementKeys)
    {
        delegate.moveElementsToGroup(id, currentGroupKey, newGroupKey, elementKeys);
    }
}