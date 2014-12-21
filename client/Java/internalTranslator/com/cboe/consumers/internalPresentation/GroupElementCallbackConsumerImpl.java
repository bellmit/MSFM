package com.cboe.consumers.internalPresentation;

import com.cboe.domain.util.GroupElementEventStructContainer;
import com.cboe.idl.groupElement.ElementStruct;
import com.cboe.interfaces.callback.GroupElementCallbackConsumer;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.event.EventChannelAdapter;

/**
 * This is the implementation of the consumer for user events.
 */
public class GroupElementCallbackConsumerImpl implements GroupElementCallbackConsumer
{
    private EventChannelAdapter eventChannel = null;

    public GroupElementCallbackConsumerImpl(EventChannelAdapter eventChannel)
    {
        this.eventChannel = eventChannel;
    }

    public void acceptUpdateElement(ElementStruct updatedElementStruct)
    {

        ChannelKey key = new ChannelKey(ChannelType.GROUP_UPDATE_ELEMENT, new Integer(0));

        ChannelEvent event = eventChannel.getChannelEvent(this, key, updatedElementStruct);
        eventChannel.dispatch(event);
    }

    public void acceptAddElement(long parentGroupElementKey, ElementStruct newElementStruct)
    {
        ChannelKey key = new ChannelKey(ChannelType.GROUP_ADD_ELEMENT, new Integer(0));
        GroupElementEventStructContainer groupElementContainer = new GroupElementEventStructContainer(parentGroupElementKey,
                                                                                                      newElementStruct);
        ChannelEvent event = eventChannel.getChannelEvent(this, key, groupElementContainer);
        eventChannel.dispatch(event);

    }

    public void acceptRemoveElement(long parentGroupElementKey, ElementStruct elementStruct, boolean isRemoveElement)
    {
        ChannelKey key = new ChannelKey(ChannelType.GROUP_REMOVE_ELEMENT, new Integer(0));
        GroupElementEventStructContainer groupElementContainer = new GroupElementEventStructContainer(parentGroupElementKey,
                                                                                                      elementStruct,
                                                                                                      isRemoveElement);
        ChannelEvent event = eventChannel.getChannelEvent(this, key, groupElementContainer);
        eventChannel.dispatch(event);

    }
}
