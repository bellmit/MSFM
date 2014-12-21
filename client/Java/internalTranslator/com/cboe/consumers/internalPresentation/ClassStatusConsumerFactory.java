package com.cboe.consumers.internalPresentation;

import com.cboe.idl.cmiCallback.*;
import com.cboe.util.*;
import com.cboe.util.event.*;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.interfaces.internalCallback.*;
import com.cboe.presentation.common.logging.GUILoggerHome;

/**
 * Factory used to create CMIClassStatusConsumers.
 *
 * @author Derek T. Chambers-Boucher
 * @version 06/28/1999
 */

public class ClassStatusConsumerFactory
{
    /**
     * ClassStatusConsumerFactory constructor.
     *
     * @author Derek T. Chambers-Boucher
     */
    public ClassStatusConsumerFactory()
    {
        super();
    }

    /**
     * This method creates a new CMIClassStatusConsumer callback Corba object.
     *
     * @author Derek T. Chambers-Boucher
     *
     * @param channelType the event channel type to publish on.
     * @param eventProcessor the event channel to publish on.
     */
    public static CMIClassStatusConsumer create(EventChannelAdapter eventProcessor)
    {
        try
        {
            ClassStatusConsumer classStatusConsumer = new ClassStatusConsumerImpl(eventProcessor);
            POA_CMIClassStatusConsumer_tie poaObj = new POA_CMIClassStatusConsumer_tie(classStatusConsumer);
            return CMIClassStatusConsumerHelper.narrow(POAHelper.connect(poaObj, null));
        }
        catch (Exception e)
        {
            GUILoggerHome.find().exception(e, "ClassStatusConsumerFactory.create");
            return null;
        }
    }
}
