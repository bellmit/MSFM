package com.cboe.consumers.internalPresentation;

import com.cboe.idl.cmiCallback.*;
import com.cboe.util.*;
import com.cboe.util.event.*;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.interfaces.internalCallback.*;
import com.cboe.presentation.common.logging.GUILoggerHome;

/**
 * Factory used to create CMITradingSessionStatusConsumers.
 *
 * @author Connie Feng
 * @version 7/9/99
 */

public class TradingSessionStatusConsumerFactory
{
    /**
     * TradingSessionStatusConsumerFactory constructor.
     *
     * @author Connie Feng
     */
    public TradingSessionStatusConsumerFactory()
    {
        super();
    }

    /**
     * This method creates a new CMITradingSessionStatusConsumer callback Corba object.
     *
     * @author Connie Feng
     *
     * @param channelType the event channel type to publish on.
     * @param eventProcessor the event channel to publish on.
     */
    public static CMITradingSessionStatusConsumer create(EventChannelAdapter eventProcessor)
    {
        try
        {
            TradingSessionStatusConsumer tradingSessionStatusConsumer = new TradingSessionStatusConsumerImpl(eventProcessor);
            POA_CMITradingSessionStatusConsumer_tie poaObj = new POA_CMITradingSessionStatusConsumer_tie(tradingSessionStatusConsumer);
            return CMITradingSessionStatusConsumerHelper.narrow(POAHelper.connect(poaObj, null));
        }
        catch (Exception e)
        {
            GUILoggerHome.find().exception(e, "TradingSessionStatusConsumerFactory.create");
            return null;
        }
    }
}
