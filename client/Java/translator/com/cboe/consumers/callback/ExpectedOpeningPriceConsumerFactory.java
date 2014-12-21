package com.cboe.consumers.callback;

import com.cboe.application.shared.*;
import com.cboe.util.*;
import com.cboe.util.event.*;

import com.cboe.delegates.callback.*;
import com.cboe.interfaces.callback.*;
import com.cboe.idl.cmiCallback.*;
import com.cboe.presentation.common.logging.GUILoggerHome;
/**
 * Factory used to create CMIExpectedOpeningPriceConsumers.
 *
 * @author Derek T. Chambers-Boucher
 * @version 06/07/1999
 */

public class ExpectedOpeningPriceConsumerFactory
{
    /**
     * ExpectedOpeningPriceConsumerFactory constructor.
     *
     * @author Derek T. Chambers-Boucher
     */
    public ExpectedOpeningPriceConsumerFactory()
    {
        super();
    }

    /**
     * This method creates a new CMIExpectedOpeningPriceConsumer callback Corba object.
     *
     * @author Derek T. Chambers-Boucher
     *
     * @param channelType the event channel type to publish on.
     * @param eventProcessor the event channel to publish on.
     */
    public static CMIExpectedOpeningPriceConsumer create(EventChannelAdapter eventProcessor)
    {
        try
        {
            ExpectedOpeningPriceConsumer expectedOpeningPriceConsumer = new ExpectedOpeningPriceConsumerImpl(eventProcessor);
            ExpectedOpeningPriceConsumerDelegate delegate = new ExpectedOpeningPriceConsumerDelegate(expectedOpeningPriceConsumer);
            org.omg.CORBA.Object corbaObject = (org.omg.CORBA.Object)RemoteConnectionFactory.find().register_object (delegate);
            return CMIExpectedOpeningPriceConsumerHelper.narrow (corbaObject);
        }
        catch (Exception e)
        {
            GUILoggerHome.find().exception(e, "ExpectedOpeningPriceConsumerFactory.create");
            return null;
        }
    }
}
