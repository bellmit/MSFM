package com.cboe.consumers.internalPresentation;

import com.cboe.idl.cmiCallback.*;
import com.cboe.util.*;
import com.cboe.util.event.*;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.interfaces.internalCallback.*;
import com.cboe.presentation.common.logging.GUILoggerHome;

/**
 * Factory used to create CMINBBOConsumers.
 *
 * @author Keith A. Korecky
 * @version 10/10/2000
 */

public class NBBOConsumerFactory
{
    /**
     * NBBOConsumerFactory constructor.
     *
     * @author Keith A. Korecky
     */
    public NBBOConsumerFactory()
    {
        super();
    }

    /**
     * This method creates a new CMINBBOConsumer callback Corba object.
     *
     * @author Keith A. Korecky
     *
     * @param channelType the event channel type to publish on.
     * @param eventProcessor the event channel to publish on.
     */
    public static CMINBBOConsumer create(EventChannelAdapter eventProcessor)
    {
        try
        {
            NBBOConsumer tickerConsumer = new NBBOConsumerImpl(eventProcessor);
            POA_CMINBBOConsumer_tie poaObj = new POA_CMINBBOConsumer_tie(tickerConsumer);
            return CMINBBOConsumerHelper.narrow(POAHelper.connect(poaObj, null));
        }
        catch (Exception e)
        {
            GUILoggerHome.find().exception(e, "NBBOConsumerFactory.create");
            return null;
        }
    }
}
