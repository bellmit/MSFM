package com.cboe.consumers.internalPresentation;

import com.cboe.idl.cmiCallback.*;
import com.cboe.util.*;
import com.cboe.util.event.*;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.interfaces.internalCallback.*;
import com.cboe.presentation.common.logging.GUILoggerHome;

/**
 * Factory used to create CMIProductStatusConsumers.
 *
 * @author Connie Feng
 * @version 7/9/99
 */

public class ProductStatusConsumerFactory
{
    /**
     * ProductStatusConsumerFactory constructor.
     *
     * @author Connie Feng
     */
    public ProductStatusConsumerFactory()
    {
        super();
    }

    /**
     * This method creates a new CMIProductStatusConsumer callback Corba object.
     *
     * @author Connie Feng
     *
     * @param channelType the event channel type to publish on.
     * @param eventProcessor the event channel to publish on.
     */
    public static CMIProductStatusConsumer create(EventChannelAdapter eventProcessor)
    {
        try
        {
            ProductStatusConsumer productStatusConsumer = new ProductStatusConsumerImpl(eventProcessor);
            POA_CMIProductStatusConsumer_tie poaObj = new POA_CMIProductStatusConsumer_tie(productStatusConsumer);
            return CMIProductStatusConsumerHelper.narrow(POAHelper.connect(poaObj, null));
        }
        catch (Exception e)
        {
            GUILoggerHome.find().exception(e, "ProductStatusConsumerFactory.create");
            return null;
        }
    }
}
