package com.cboe.consumers.intermarketCallback;

import com.cboe.application.shared.*;
import com.cboe.util.*;
import com.cboe.util.event.*;

import com.cboe.delegates.intermarketCallback.*;
import com.cboe.interfaces.intermarketCallback.*;
import com.cboe.idl.cmiIntermarketCallback.*;
import com.cboe.presentation.common.logging.GUILoggerHome;

/**
 * Factory used to create CMINBBOAgentSessionAdmin.
 *
 * @author Connie Feng
 */

public class NBBOAgentSessionAdminConsumerFactory
{
    /**
     * NBBOAgentSessionAdminConsumerFactory constructor.
     *
     * @author Connie Feng
     */
    public NBBOAgentSessionAdminConsumerFactory()
    {
    	super();
    }

    /**
     * This method creates a new CMINBBOAgentSessionAdmin callback Corba object.
     *
     * @author Connie Feng
     *
     * @param channelType the event channel type to publish on.
     * @param eventProcessor the event channel to publish on.
     */
    public static CMINBBOAgentSessionAdmin create(EventChannelAdapter eventProcessor)
    {
	    try
    	{
	    	NBBOAgentSessionAdminConsumer theConsumer = new NBBOAgentSessionAdminConsumerImpl(eventProcessor);
		    NBBOAgentSessionAdminConsumerDelegate delegate = new NBBOAgentSessionAdminConsumerDelegate(theConsumer);
            org.omg.CORBA.Object corbaObject = (org.omg.CORBA.Object)RemoteConnectionFactory.find().register_object (delegate);
            return CMINBBOAgentSessionAdminHelper.narrow (corbaObject);
    	}
	    catch (Exception e)
    	{
            GUILoggerHome.find().exception(e, "NBBOAgentSessionAdminConsumerFactory.create");
		    return null;
    	}
    }
}
