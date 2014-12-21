package com.cboe.consumers.internalPresentation;

import com.cboe.idl.cmiCallback.*;
import com.cboe.util.*;
import com.cboe.util.event.*;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.interfaces.internalCallback.*;
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
            TickerConsumer tickerConsumer = new TickerConsumerImpl(eventProcessor);
            POA_CMITickerConsumer_tie poaObj = new POA_CMITickerConsumer_tie(tickerConsumer);
            return CMITickerConsumerHelper.narrow(POAHelper.connect(poaObj, null));
        }
        catch (Exception e)
        {
            GUILoggerHome.find().exception(e, "TickerConsumerFactory.create");
            return null;
        }
    }
}
