package com.cboe.consumers.internalPresentation;

import com.cboe.idl.cmiCallback.*;
import com.cboe.util.*;
import com.cboe.util.event.*;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.interfaces.internalCallback.*;
import com.cboe.presentation.common.logging.GUILoggerHome;

/**
 * Factory used to create CMIStrategyStatusConsumers.
 *
 * @author Connie Feng
 * @version 7/9/99
 */

public class StrategyStatusConsumerFactory
{
    /**
     * StrategyStatusConsumerFactory constructor.
     *
     * @author Connie Feng
     */
    public StrategyStatusConsumerFactory()
    {
        super();
    }

    /**
     * This method creates a new CMIStrategyStatusConsumer callback Corba object.
     *
     * @author Connie Feng
     *
     * @param channelType the event channel type to publish on.
     * @param eventProcessor the event channel to publish on.
     */
    public static CMIStrategyStatusConsumer create(EventChannelAdapter eventProcessor)
    {
        try
        {
            StrategyStatusConsumer strategyStatusConsumer = new StrategyStatusConsumerImpl(eventProcessor);
            POA_CMIStrategyStatusConsumer_tie poaObj = new POA_CMIStrategyStatusConsumer_tie(strategyStatusConsumer);
            return CMIStrategyStatusConsumerHelper.narrow(POAHelper.connect(poaObj, null));
        }
        catch (Exception e)
        {
            GUILoggerHome.find().exception(e, "StrategyStatusConsumerFactory.create");
            return null;
        }
    }
}
