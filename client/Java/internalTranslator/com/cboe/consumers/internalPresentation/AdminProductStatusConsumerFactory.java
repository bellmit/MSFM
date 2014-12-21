package com.cboe.consumers.internalPresentation;

import com.cboe.application.shared.*;
import com.cboe.util.*;
import com.cboe.util.event.*;

import com.cboe.delegates.internalCallback.*;
import com.cboe.idl.consumers.*;
import com.cboe.presentation.common.logging.GUILoggerHome;

/**
 * Factory used to create AdminProductStatusConsumerConsumers.
 *
 * @author Jing Chen
 * @version 10/26/2000
 */

public class AdminProductStatusConsumerFactory
{

    /**
     * AdminProductStatusConsumerConsumerFactory constructor.
     *
     */
    public AdminProductStatusConsumerFactory()
    {
        super();
    }

    /**
     * This method creates a new TradingSessionEventStateConsumer callback Corba object.
     *
     * @author Jimmy Wang
     *
     * @param channelType the event channel type to publish on.
     * @param eventProcessor the event channel to publish on.
     */
    public static com.cboe.idl.consumers.ProductStatusConsumer create(EventChannelAdapter eventProcessor)
    {
        try
        {
            com.cboe.interfaces.events.ProductStatusConsumer eventStateConsumer = new AdminProductStatusConsumerImpl(eventProcessor);
            POA_ProductStatusConsumer_tie evenStatePOAObj = new AdminProductStatusConsumerDelegate(eventStateConsumer);
            com.cboe.idl.consumers.ProductStatusConsumer callbackConsumer =
                 com.cboe.idl.consumers.ProductStatusConsumerHelper.narrow(ServicesHelper.connectToOrb(evenStatePOAObj));
            return callbackConsumer;
        }
        catch (Exception e)
        {
            GUILoggerHome.find().exception(e, "AdminProductStatusConsumerFactory.create");
            return null;
        }
    }
}
