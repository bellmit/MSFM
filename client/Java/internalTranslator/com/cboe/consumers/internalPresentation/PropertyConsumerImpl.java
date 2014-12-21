package com.cboe.consumers.internalPresentation;

import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.event.EventChannelAdapter;
import com.cboe.util.event.EventChannelAdapterFactory;

import com.cboe.idl.property.PropertyGroupStruct;

import com.cboe.interfaces.internalCallback.PropertyConsumer;

/**
 * This is the implementation of the PropertyConsumer callback object which
 * receives property updates on a designated event channel.
 *
 */
public class PropertyConsumerImpl implements PropertyConsumer
{
    private EventChannelAdapter eventChannel = null;
    private int channelType = 0;

    /**
     * PropertyConsumerImpl constructor.
     *
     * @param channelType the channel type to publish on.
     * @param eventChannel the event channel to publish to.
     */
    public PropertyConsumerImpl(EventChannelAdapter eventChannel)
    {
        super();

        this.eventChannel = eventChannel;
    }

    /**
     * The callback method used by the CAS to publish updates to property groups.  The
     * event is posted with a key of category and a key of propertyKey + category.
     *
     * @param struct The updated property group struct
     */
    public void acceptPropertyUpdate(PropertyGroupStruct struct)
    {
        ChannelKey key = new ChannelKey(ChannelType.UPDATE_PROPERTY, struct.category);
        ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, key, struct);
        eventChannel.dispatch(event);

        StringBuffer comboKeyString = new StringBuffer();
        comboKeyString.append(struct.propertyKey);
        comboKeyString.append(struct.category);
        ChannelKey comboKey = new ChannelKey(ChannelType.UPDATE_PROPERTY, comboKeyString.toString());
        if (!(key.equals(comboKey)))
        {
            ChannelEvent comboEvent = EventChannelAdapterFactory.find().getChannelEvent(this, comboKey, struct);
            eventChannel.dispatch(comboEvent);
        }
    }

    /**
     * The callback method used by the CAS to publish removal of a property group
     *
     * @param category The category of the removed group.
     * @param propertyKey The key of the removed group.
     */
    public void acceptPropertyRemove(String category, String propertyKey)
    {
        ChannelKey key = new ChannelKey(ChannelType.REMOVE_PROPERTY, category);
        ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, key, propertyKey);
        eventChannel.dispatch(event);

        StringBuffer comboKeyString = new StringBuffer();
        comboKeyString.append(propertyKey);
        comboKeyString.append(category);
        ChannelKey comboKey = new ChannelKey(ChannelType.REMOVE_PROPERTY, comboKeyString.toString());
        if (!(key.equals(comboKey)))
        {
            ChannelEvent comboEvent = EventChannelAdapterFactory.find().getChannelEvent(this, comboKey, propertyKey);
            eventChannel.dispatch(comboEvent);
        }
    }
}
