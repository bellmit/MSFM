package com.cboe.consumers.internalPresentation;

import com.cboe.idl.cmiCallback.*;
import com.cboe.util.*;
import com.cboe.util.event.*;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.interfaces.internalCallback.*;
import com.cboe.presentation.common.logging.GUILoggerHome;

/**
 * Factory used to create CMICurrentMarketConsumers.
 *
 * @author Derek T. Chambers-Boucher
 * @version 03/16/1999
 */

public class CurrentMarketConsumerFactory
{
    /**
     * CurrentMarketConsumerFactory constructor.
     *
     * @author Derek T. Chambers-Boucher
     */
    public CurrentMarketConsumerFactory()
    {
        super();
    }

    /**
     * This method creates a new CMICurrentMarketConsumer callback Corba object.
     *
     * @author Derek T. Chambers-Boucher
     *
     * @param channelType the event channel type to publish on.
     * @param eventProcessor the event channel to publish on.
     */
    public static CMICurrentMarketConsumer create(EventChannelAdapter eventProcessor)
    {
        try
        {
            CurrentMarketConsumer currentMarketConsumer = new CurrentMarketConsumerImpl(eventProcessor);
            POA_CMICurrentMarketConsumer_tie poaObj = new POA_CMICurrentMarketConsumer_tie(currentMarketConsumer);
            return CMICurrentMarketConsumerHelper.narrow(POAHelper.connect(poaObj, null));
        }
        catch (Exception e)
        {
            GUILoggerHome.find().exception(e, "CurrentMarketConsumerFactory.create");
            return null;
        }
    }
}
