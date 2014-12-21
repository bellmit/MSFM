package com.cboe.consumers.callback;

import com.cboe.application.shared.*;
import com.cboe.util.*;
import com.cboe.util.event.*;

import com.cboe.delegates.callback.*;
import com.cboe.interfaces.callback.*;
import com.cboe.idl.cmiCallback.*;
import com.cboe.presentation.common.logging.GUILoggerHome;

/**
 * Factory used to create CMIQuoteStatusConsumers.
 *
 * @author Derek T. Chambers-Boucher
 * @version 03/16/1999
 */

public class QuoteStatusConsumerFactory
{
    /**
     * QuoteStatusConsumerFactory constructor.
     *
     * @author Derek T. Chambers-Boucher
     */
    public QuoteStatusConsumerFactory()
    {
        super();
    }

    /**
     * This method creates a new CMIQuoteStatusConsumer callback Corba object.
     *
     * @author Derek T. Chambers-Boucher
     *
     * @param channelType the event channel type to publish on.
     * @param eventProcessor the event channel to publish on.
     */
    public static CMIQuoteStatusConsumer create(EventChannelAdapter eventProcessor)
    {
        try
        {
            QuoteStatusConsumer quoteStatusConsumer = new QuoteStatusConsumerImpl(eventProcessor);
            QuoteStatusConsumerDelegate delegate = new QuoteStatusConsumerDelegate(quoteStatusConsumer);
            org.omg.CORBA.Object corbaObject = (org.omg.CORBA.Object)RemoteConnectionFactory.find().register_object (delegate);
            return CMIQuoteStatusConsumerHelper.narrow (corbaObject);
        }
        catch (Exception e)
        {
            GUILoggerHome.find().exception(e, "QuoteStatusConsumerFactory.create");
            return null;
        }
    }
}
