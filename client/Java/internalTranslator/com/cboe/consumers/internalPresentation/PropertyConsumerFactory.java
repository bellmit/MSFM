package com.cboe.consumers.internalPresentation;

import com.cboe.idl.consumers.PropertyConsumer;
import com.cboe.idl.consumers.PropertyConsumerHelper;
import com.cboe.idl.consumers.POA_PropertyConsumer_tie;
import com.cboe.util.*;
import com.cboe.util.event.*;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.presentation.common.logging.GUILoggerHome;

/**
 * Factory used to create PropertyConsumer.
 *
 */
public class PropertyConsumerFactory
{
    /**
     * PropertyConsumerFactory constructor.
     *
     */
    public PropertyConsumerFactory()
    {
        super();
    }

    /**
     * This method creates a new PropertyConsumer callback Corba object.
     *
     * @param channelType the event channel type to publish on.
     * @param eventProcessor the event channel to publish on.
     */
    public static PropertyConsumer create(EventChannelAdapter eventProcessor)
    {
        try
        {
            com.cboe.interfaces.internalCallback.PropertyConsumer propertyConsumer = new PropertyConsumerImpl(eventProcessor);
            POA_PropertyConsumer_tie poaObj = new POA_PropertyConsumer_tie(propertyConsumer);
            return PropertyConsumerHelper.narrow(POAHelper.connect(poaObj, null));
        }
        catch (Exception e)
        {
            GUILoggerHome.find().exception(e, "PropertyConsumerFactory.create");
            return null;
        }
    }
}
