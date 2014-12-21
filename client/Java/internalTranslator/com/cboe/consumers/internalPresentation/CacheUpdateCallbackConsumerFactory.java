package com.cboe.consumers.internalPresentation;

import com.cboe.application.shared.*;
import com.cboe.util.*;
import com.cboe.util.event.*;

import com.cboe.delegates.internalCallback.*;
import com.cboe.interfaces.callback.*;
import com.cboe.idl.consumers.*;
import com.cboe.presentation.common.logging.GUILoggerHome;

/**
 * Factory used to create CacheUpdateConsumer.
 *
 * @author William Wei
 * @version 09/21/2001
 */

public class CacheUpdateCallbackConsumerFactory
{
    /**
     * CacheUpdateConsumerFactory constructor.
     */
    public CacheUpdateCallbackConsumerFactory()
    {
        super();
    }

    /**
     * This method creates a new CacheUpdateConsumer callback Corba object.
     *
     * @param channelType the event channel type to publish on.
     * @param eventProcessor the event channel to publish on.
     */
    public static CacheUpdateConsumer create(EventChannelAdapter eventProcessor)
    {
        try
        {
        CacheUpdateCallbackConsumer eventStateConsumer = new CacheUpdateCallbackConsumerImpl(eventProcessor);
        POA_CacheUpdateConsumer_tie evenStatePOAObj = new CacheUpdateConsumerDelegate(eventStateConsumer);
            CacheUpdateConsumer callbackConsumer =
            com.cboe.idl.consumers.CacheUpdateConsumerHelper.narrow(ServicesHelper.connectToOrb(evenStatePOAObj));
        return callbackConsumer;
        }
    catch (Exception e)
        {
            GUILoggerHome.find().exception(e, "CacheUpdateCallbackConsumerFactory.create");
        return null;
        }
    }
}
