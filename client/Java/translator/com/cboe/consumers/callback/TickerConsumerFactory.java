package com.cboe.consumers.callback;

import com.cboe.application.shared.*;
import com.cboe.util.*;
import com.cboe.util.event.*;

import com.cboe.delegates.callback.*;
import com.cboe.interfaces.callback.*;
import com.cboe.idl.cmiCallback.*;
import com.cboe.presentation.common.logging.GUILoggerHome;

/**
 * Factory used to create CMITickerConsumers.
 *
 * @author Keith A. Korecky
 * @version 06/29/1999
 */

public class TickerConsumerFactory
{
    /**
     * TickerConsumerFactory constructor.
     *
     * @author Keith A. Korecky
     */
    public TickerConsumerFactory()
    {
        super();
    }

    /**
     * This method creates a new CMITickerConsumer callback Corba object.
     *
     * @author Keith A. Korecky
     *
     * @param channelType the event channel type to publish on.
     * @param eventProcessor the event channel to publish on.
     */
    public static CMITickerConsumer create(EventChannelAdapter eventProcessor)
    {
        try
        {
            TickerConsumer TickerConsumer = new TickerConsumerImpl(eventProcessor);
            TickerConsumerDelegate delegate = new TickerConsumerDelegate(TickerConsumer);
            org.omg.CORBA.Object corbaObject = (org.omg.CORBA.Object)RemoteConnectionFactory.find().register_object (delegate);
            return CMITickerConsumerHelper.narrow (corbaObject);
        }
        catch (Exception e)
        {
            GUILoggerHome.find().exception(e, "TickerConsumerFactory.create");
            return null;
        }
    }
}
