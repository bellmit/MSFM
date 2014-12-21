package com.cboe.consumers.internalPresentation;

import com.cboe.idl.cmiCallback.*;
import com.cboe.util.*;
import com.cboe.util.event.*;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.interfaces.internalCallback.*;
import com.cboe.presentation.common.logging.GUILoggerHome;

/**
 * Factory used to create CMIOrderBookConsumers.
 *
 * @author William Wei
 * @version 12/03/2001
 */

public class BookDepthConsumerFactory
{
    /**
     * BookDepthConsumerFactory constructor.
     */
    public BookDepthConsumerFactory()
    {
        super();
    }

    /**
     * This method creates a new CMIOrderBookConsumer callback Corba object.
     *
     * @param eventProcessor the event channel to publish on.
     */
    public static CMIOrderBookConsumer create(EventChannelAdapter eventProcessor)
    {
    try
        {
            BookDepthConsumer bookDepthConsumer = new BookDepthConsumerImpl(eventProcessor);
            POA_CMIOrderBookConsumer_tie poaObj = new POA_CMIOrderBookConsumer_tie(bookDepthConsumer);
            return CMIOrderBookConsumerHelper.narrow(POAHelper.connect(poaObj, null));
        }
        catch (Exception e)
        {
            GUILoggerHome.find().exception(e, "BookDepthConsumerFactory.create");
            return null;
        }
    }
}
