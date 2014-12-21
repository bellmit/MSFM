//
// ------------------------------------------------------------------------
// FILE: AlarmDefinitionServiceConsumerPublisherImpl.java
// 
// PACKAGE: com.cboe.publishers.eventChannel
// 
// ------------------------------------------------------------------------
// Copyright (c) 1999-2004 The Chicago Board Options Exchange.  All Rights Reserved.
// ------------------------------------------------------------------------
//
package com.cboe.publishers.eventChannel;

import com.cboe.idl.icsGroupElementEvents.ICSGroupElementEventService;
import com.cboe.idl.groupElement.ElementEntryStruct;
import com.cboe.idl.groupElement.ElementStruct;
import com.cboe.interfaces.events.ICSGroupElementEventDelegateServiceConsumer;

/**
 * @author torresl@cboe.com
 */
public class ICSGroupElementServiceConsumerPublisherImpl
        implements ICSGroupElementEventDelegateServiceConsumer
{
    protected ICSGroupElementEventService eventChannelDelegate;

    public ICSGroupElementServiceConsumerPublisherImpl()
    {
    }

    public ICSGroupElementServiceConsumerPublisherImpl(ICSGroupElementEventService eventChannelDelegate)
    {
        this();
        setGroupElementEventServiceDelegate(eventChannelDelegate);
    }

    public void setGroupElementEventServiceDelegate(ICSGroupElementEventService service)
    {
        this.eventChannelDelegate = service;
    }

    public void createElementsForGroup(long requestId, long groupElementKey, ElementEntryStruct[] elementEntryStructs)
    {
        eventChannelDelegate.createElementsForGroup(requestId, groupElementKey, elementEntryStructs);
    }

    public void addElementsToGroup(long requestId, long groupElementKey, long[] elementKeys)
    {
        eventChannelDelegate.addElementsToGroup(requestId, groupElementKey, elementKeys);
    }

    public void updateElement(long requestId, ElementStruct elementStruct)
    {
        eventChannelDelegate.updateElement(requestId, elementStruct);
    }

    public void removeElementsFromGroup(long requestId, long groupElementKey, long[] elementKeys)
    {
        eventChannelDelegate.removeElementsFromGroup(requestId, groupElementKey, elementKeys);
    }

    public void publishRootElementForGroupType(long requestId, short groupType)
    {
        eventChannelDelegate.publishRootElementForGroupType(requestId, groupType);
    }

    public void publishElementsForGroup(long requestId, long groupElementKey)
    {
        eventChannelDelegate.publishElementsForGroup(requestId, groupElementKey);
    }

    public void copyElementsToGroup(long id, long groupKey, ElementEntryStruct cloneEntryStruct, long[] elementKeys)
    {
        eventChannelDelegate.copyElementsToGroup(id, groupKey, cloneEntryStruct, elementKeys);
    }

    public void moveElementsToGroup(long id, long currentGroupKey, long newGroupKey, long[] elementKeys)
    {
        eventChannelDelegate.moveElementsToGroup(id, currentGroupKey, newGroupKey, elementKeys);
    }
}