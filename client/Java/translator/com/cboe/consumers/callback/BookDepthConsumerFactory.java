package com.cboe.consumers.callback;

import com.cboe.application.shared.*;
import com.cboe.util.*;
import com.cboe.util.event.*;

import com.cboe.delegates.callback.*;
import com.cboe.interfaces.callback.*;
import com.cboe.idl.cmiCallback.*;
import com.cboe.presentation.common.logging.GUILoggerHome;
import org.omg.CORBA.*;

/**
 * Factory used to create CMIOrderBookConsumers.
 *
 * @author William Wei
 * @version 12/03/2001
 */

public class BookDepthConsumerFactory
{
    /**
     * BookDepthConsumerFactory constructor.
     */
    private EventChannelAdapter eventChannel = null;

    public BookDepthConsumerFactory()
    {
        super();
    }

    /**
     * This method creates a new CMIOrderBookConsumer callback Corba object.
     *
     * @param channelType the event channel type to publish on.
     * @param eventProcessor the event channel to publish on.
     */
    public static CMIOrderBookConsumer create(EventChannelAdapter eventProcessor)
    {
        try
        {
            OrderBookConsumer bookDepthConsumer = new BookDepthConsumerImpl(eventProcessor);
            BookDepthConsumerDelegate delegate = new BookDepthConsumerDelegate(bookDepthConsumer);
            org.omg.CORBA.Object corbaObject = (org.omg.CORBA.Object)RemoteConnectionFactory.find().register_object (delegate);
            return CMIOrderBookConsumerHelper.narrow (corbaObject);
        }

        catch (Exception e)
        {
            GUILoggerHome.find().exception(e, "BookDepthConsumerFactory.create");
            return null;
        }
    }
}
