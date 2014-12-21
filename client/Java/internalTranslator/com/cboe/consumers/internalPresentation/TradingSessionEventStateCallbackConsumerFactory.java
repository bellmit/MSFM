package com.cboe.consumers.internalPresentation;

import com.cboe.application.shared.*;
import com.cboe.util.*;
import com.cboe.util.event.*;

import com.cboe.delegates.internalCallback.*;
import com.cboe.interfaces.callback.*;
//import com.cboe.interfaces.events.*;
import com.cboe.idl.consumers.*;
import com.cboe.presentation.common.logging.GUILoggerHome;

/**
 * Factory used to create TradingSessionEventStateConsumers.
 *
 * @author Jimmy Wang
 * @version 08/22/2000
 */

public class TradingSessionEventStateCallbackConsumerFactory
{
//  private com.cboe.idl.consumers.POA_TradingSessionEventStateConsumer_tie evenStatePOAObj;

    /**
     * TradingSessionEventStateConsumerFactory constructor.
     *
     * @author Jimmy Wang
     */
    public TradingSessionEventStateCallbackConsumerFactory()
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
    public static TradingSessionEventStateConsumer create(EventChannelAdapter eventProcessor)
    {
        try
        {
            TradingSessionEventStateCallbackConsumer eventStateConsumer = new TradingSessionEventStateCallbackConsumerImpl(eventProcessor);
            POA_TradingSessionEventStateConsumer_tie evenStatePOAObj = new TradingSessionEventStateConsumerDelegate(eventStateConsumer);
            TradingSessionEventStateConsumer callbackConsumer =
                com.cboe.idl.consumers.TradingSessionEventStateConsumerHelper.narrow(ServicesHelper.connectToOrb(evenStatePOAObj));
            return callbackConsumer;
        }
        catch (Exception e)
        {
            GUILoggerHome.find().exception(e, "TradingSessionEventStateCallbackConsumerFactory.create");
            return null;
        }
    }
}
