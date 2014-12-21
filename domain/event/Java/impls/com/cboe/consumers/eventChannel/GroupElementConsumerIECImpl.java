package com.cboe.consumers.eventChannel;


import com.cboe.interfaces.events.*;
import com.cboe.util.event.*;
import com.cboe.idl.cmiUser.*;
import com.cboe.util.*;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.*;
import com.cboe.idl.groupElement.ElementStruct;
import com.cboe.domain.util.GroupElementEventStructContainer;

public class GroupElementConsumerIECImpl extends BObject implements GroupElementConsumer {
    private EventChannelAdapter internalEventChannel = null;
    private static final Integer INT_0 = 0;

    public GroupElementConsumerIECImpl() {
        super();
        internalEventChannel = EventChannelAdapterFactory.find();
    }

    public void acceptUpdateElement(ElementStruct updatedElementStruct)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "event received -> acceptUpdateElement,elementKey =  " + updatedElementStruct.elementKey);
        }
        ChannelKey channelKey = null;
        ChannelEvent event = null;

        channelKey = new ChannelKey(ChannelKey.GROUP_UPDATE_ELEMENT, INT_0);

        event = EventChannelAdapterFactory.find().getChannelEvent(this, channelKey, updatedElementStruct);
        internalEventChannel.dispatch(event);
    }
    public void acceptAddElement(long parentGroupElementKey,ElementStruct newElementStruct)
    {
        GroupElementEventStructContainer groupElementContainer = new GroupElementEventStructContainer(parentGroupElementKey,
                                                                                                      newElementStruct);
        ChannelKey channelKey = new ChannelKey(ChannelKey.GROUP_ADD_ELEMENT, INT_0);
        ChannelEvent event =  EventChannelAdapterFactory.find().getChannelEvent(this, channelKey, groupElementContainer);
        internalEventChannel.dispatch(event);
    }

    public void acceptRemoveElement(long parentGroupElementKey,ElementStruct elementStruct,boolean isRemoveElement)
    {
        GroupElementEventStructContainer groupElementContainer = new GroupElementEventStructContainer(parentGroupElementKey,
                                                                                                          elementStruct,
                                                                                                          isRemoveElement);

        ChannelKey channelKey = new ChannelKey(ChannelKey.GROUP_REMOVE_ELEMENT, INT_0);
        ChannelEvent event =  EventChannelAdapterFactory.find().getChannelEvent(this, channelKey, groupElementContainer);
        internalEventChannel.dispatch(event);
    }

}
