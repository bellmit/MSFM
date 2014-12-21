package com.cboe.consumers.callback;

//import com.cboe.application.shared.*;
import com.cboe.util.*;
import com.cboe.util.event.*;

import com.cboe.delegates.callback.*;
import com.cboe.interfaces.callback.*;
import com.cboe.idl.cmiCallback.*;
import com.cboe.presentation.common.logging.GUILoggerHome;

import com.cboe.application.shared.RemoteConnectionFactory;

/**
 * Factory used to create CMITradingSessionStatusConsumers.
 *
 * @author Keith A. Korecky
 * @version 06/29/1999
 */

public class TradingSessionStatusConsumerFactory
{
    /**
     * TradingSessionStatusConsumerFactory constructor.
     *
     * @author Keith A. Korecky
     */
    public TradingSessionStatusConsumerFactory()
    {
        super();
    }

    /**
     * This method creates a new CMITradingSessionStatusConsumer callback Corba object.
     *
     * @author Keith A. Korecky
     *
     * @param channelType the event channel type to publish on.
     * @param eventProcessor the event channel to publish on.
     */
    public static CMITradingSessionStatusConsumer create(EventChannelAdapter eventProcessor)
    {
        try
        {
            TradingSessionStatusConsumer TickerConsumer = new TradingSessionStatusConsumerImpl(eventProcessor);
            TradingSessionStatusConsumerDelegate delegate = new TradingSessionStatusConsumerDelegate(TickerConsumer);
            org.omg.CORBA.Object corbaObject = (org.omg.CORBA.Object)RemoteConnectionFactory.find().register_object (delegate);
            return CMITradingSessionStatusConsumerHelper.narrow (corbaObject);
        }
        catch (Exception e)
        {
            GUILoggerHome.find().exception(e, "TradingSessionStatusConsumerFactory.create");
            return null;
        }
    }
}
