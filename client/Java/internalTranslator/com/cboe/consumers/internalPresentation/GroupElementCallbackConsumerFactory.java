package com.cboe.consumers.internalPresentation;

import com.cboe.application.shared.*;
import com.cboe.util.*;
import com.cboe.util.event.*;

import com.cboe.delegates.internalCallback.*;
import com.cboe.interfaces.callback.*;
import com.cboe.idl.consumers.*;
import com.cboe.presentation.common.logging.GUILoggerHome;

/**
 * Factory used to create GroupElementConsumer.
 * 
 */

public class GroupElementCallbackConsumerFactory
{
    /**
     * GroupElementConsumerFactory constructor.
     */
    public GroupElementCallbackConsumerFactory()
    {
        super();
    }

    /**
     * This method creates a new GroupElementConsumer callback Corba object.
     *
     * @param channelType the event channel type to publish on.
     * @param eventProcessor the event channel to publish on.
     */
    public static GroupElementConsumer create(EventChannelAdapter eventProcessor)
    {
        try
        {
        GroupElementCallbackConsumer eventStateConsumer = new GroupElementCallbackConsumerImpl(eventProcessor);
        POA_GroupElementConsumer_tie evenStatePOAObj = new GroupElementConsumerDelegate(eventStateConsumer);
            GroupElementConsumer callbackConsumer =
            com.cboe.idl.consumers.GroupElementConsumerHelper.narrow(ServicesHelper.connectToOrb(evenStatePOAObj));
        return callbackConsumer;
        }
    catch (Exception e)
        {
            GUILoggerHome.find().exception(e, "GroupElementCallbackConsumerFactory.create");
        return null;
        }
    }
}
