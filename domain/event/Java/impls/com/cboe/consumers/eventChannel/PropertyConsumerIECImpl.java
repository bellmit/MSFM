package com.cboe.consumers.eventChannel;


import com.cboe.interfaces.events.*;
import com.cboe.util.event.*;
import com.cboe.util.*;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.*;
import com.cboe.idl.property.PropertyGroupStruct;
import com.cboe.interfaces.domain.property.PropertyCategoryTypes;
import com.cboe.domain.util.CategoryPropertyKeyContainer;

public class PropertyConsumerIECImpl extends BObject implements PropertyConsumer {

    private EventChannelAdapter internalEventChannel = null;
    private static final Integer INT_0 = 0;

    public PropertyConsumerIECImpl() {
        super();
        internalEventChannel = EventChannelAdapterFactory.find();
    }

    public void acceptPropertyUpdate(PropertyGroupStruct property)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "event received -> acceptPropertyUpdate,  " + property.category + "/" + property.propertyKey);
        }
        ChannelKey channelKey = null;
        ChannelEvent event = null;

        channelKey = new ChannelKey(ChannelKey.UPDATE_PROPERTY, INT_0);
        event = EventChannelAdapterFactory.find().getChannelEvent(this, channelKey, property);
        internalEventChannel.dispatch(event);

        if(property.category.equals(PropertyCategoryTypes.USER_ENABLEMENT))
        {
            channelKey = new ChannelKey(ChannelKey.UPDATE_PROPERTY_ENABLEMENT, property.propertyKey);
            event = EventChannelAdapterFactory.find().getChannelEvent(this, channelKey, property);
            internalEventChannel.dispatch(event);
        }
        else if(property.category.equals(PropertyCategoryTypes.RATE_LIMITS))
        {
            channelKey = new ChannelKey(ChannelKey.UPDATE_PROPERTY_RATELIMIT, property.propertyKey);
            event = EventChannelAdapterFactory.find().getChannelEvent(this, channelKey, property);
            internalEventChannel.dispatch(event);
        }
    }

    public void acceptPropertyRemove(String category, String propertyKey)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "event received -> acceptPropertyRemove,  " + category + "/" + propertyKey);
        }
        ChannelKey channelKey = null;
        ChannelEvent event = null;

        channelKey = new ChannelKey(ChannelKey.REMOVE_PROPERTY, INT_0);
        event = EventChannelAdapterFactory.find().getChannelEvent(this, channelKey, new CategoryPropertyKeyContainer(category, propertyKey));
        internalEventChannel.dispatch(event);

        if(category.equals(PropertyCategoryTypes.USER_ENABLEMENT))
        {
            channelKey = new ChannelKey(ChannelKey.REMOVE_PROPERTY_ENABLEMENT, propertyKey);
            event = EventChannelAdapterFactory.find().getChannelEvent(this, channelKey, propertyKey);
            internalEventChannel.dispatch(event);
        }
        else if(category.equals(PropertyCategoryTypes.RATE_LIMITS))
        {
            channelKey = new ChannelKey(ChannelKey.REMOVE_PROPERTY_RATELIMIT, propertyKey);
            event = EventChannelAdapterFactory.find().getChannelEvent(this, channelKey, propertyKey);
            internalEventChannel.dispatch(event);
        }
    }

}
