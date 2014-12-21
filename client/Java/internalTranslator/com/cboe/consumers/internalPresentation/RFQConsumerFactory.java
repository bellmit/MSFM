package com.cboe.consumers.internalPresentation;

import com.cboe.idl.cmiCallback.*;
import com.cboe.util.*;
import com.cboe.util.event.*;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.interfaces.internalCallback.*;
import com.cboe.presentation.common.logging.GUILoggerHome;

/**
 * Factory used to create CMIRFQConsumers.
 *
 * @author Derek T. Chambers-Boucher
 * @version 03/16/1999
 */

public class RFQConsumerFactory
{
    /**
     * UnderlyingRecapConsumerFactory constructor.
     *
     * @author Derek T. Chambers-Boucher
     */
    public RFQConsumerFactory()
    {
        super();
    }

    /**
     * This method creates a new CMIRFQConsumer callback Corba object.
     *
     * @author Derek T. Chambers-Boucher
     *
     * @param channelType the event channel type to publish on.
     * @param eventProcessor the event channel to publish on.
     */
    public static CMIRFQConsumer create(EventChannelAdapter eventProcessor)
    {
        try
        {
            RFQConsumer rfqConsumer = new RFQConsumerImpl(eventProcessor);
            POA_CMIRFQConsumer_tie poaObj = new POA_CMIRFQConsumer_tie(rfqConsumer);
            return CMIRFQConsumerHelper.narrow(POAHelper.connect(poaObj, null));
        }
        catch (Exception e)
        {
            GUILoggerHome.find().exception(e, "RFQConsumerFactory.create");
            return null;
        }
    }
}
