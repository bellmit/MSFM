package com.cboe.consumers.intermarketCallback;

import com.cboe.application.shared.*;
import com.cboe.util.*;
import com.cboe.util.event.*;

import com.cboe.delegates.intermarketCallback.IntermarketOrderStatusConsumerDelegate;
import com.cboe.interfaces.intermarketCallback.*;
import com.cboe.idl.cmiIntermarketCallback.*;
import com.cboe.presentation.common.logging.GUILoggerHome;

/**
 * Factory used to create CMIOrderStatusConsumers.
 *
 * @author Derek T. Chambers-Boucher
 * @version 03/16/1999
 */

public class IntermarketOrderStatusConsumerFactory
{
    /**
     * IntermarketOrderStatusConsumerFactory constructor.
     *
     * @author Derek T. Chambers-Boucher
     */
    public IntermarketOrderStatusConsumerFactory()
    {
	    super();
    }

    /**
     * This method creates a new CMIOrderStatusConsumer callback Corba object.
     *
     * @author Derek T. Chambers-Boucher
     *
     * @param channelType the event channel type to publish on.
     * @param eventProcessor the event channel to publish on.
     */
    public static CMIIntermarketOrderStatusConsumer create(EventChannelAdapter eventProcessor)
    {
	    try
        {
		    IntermarketOrderStatusConsumer intermarketOrderStatusConsumer = new IntermarketOrderStatusConsumerImpl(eventProcessor);
    		IntermarketOrderStatusConsumerDelegate delegate= new IntermarketOrderStatusConsumerDelegate(intermarketOrderStatusConsumer);
            org.omg.CORBA.Object corbaObject = (org.omg.CORBA.Object)RemoteConnectionFactory.find().register_object (delegate);
            return CMIIntermarketOrderStatusConsumerHelper.narrow (corbaObject);
    	}
        catch (Exception e)
        {
            GUILoggerHome.find().exception(e, "IntermarketOrderStatusConsumerFactory.create");
		    return null;
    	}
    }
}
