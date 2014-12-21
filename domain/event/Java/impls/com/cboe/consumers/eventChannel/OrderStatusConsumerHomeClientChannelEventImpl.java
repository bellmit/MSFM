package com.cboe.consumers.eventChannel;

import com.cboe.exceptions.SystemException;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.util.ChannelKey;

/**
 * Handle filters for CORBA calls delivering Order Status to a Client. This
 * version uses channel ExternalOrderStatus, for Clients that bypass FE and
 * get status directly from the channel from the Server.
 */
public class OrderStatusConsumerHomeClientChannelEventImpl extends OrderStatusConsumerHomeEventImpl
{
    // Simply, we want every behavior of our base class, except that we want to
    // use a different channel. So redefine CHANNEL_NAME and copy every method
    // that refers to CHANNEL_NAME (a method copied to this class will use the
    // CHANNEL_NAME defined in this class).
    private static final String CHANNEL_NAME = "ExternalOrderStatus";

    public void clientStart()
            throws Exception
    {
        createConsumer();

        String interfaceRepId = com.cboe.idl.events.OrderStatusEventConsumerHelper.id();
        // connect to the event channel without filter; later call addConstraint
        eventChannelFilterHelper.connectConsumer(CHANNEL_NAME, interfaceRepId, orderStatusEvent);
    }

    protected void addConstraint(ChannelKey channelKey)
            throws SystemException
    {
        if (find() != null)
        {
            String constraintString = getConstraintString(channelKey);
            if (Log.isDebugOn())
            {
                Log.debug(this, "constraintString::" + constraintString );
            }

            eventChannelFilterHelper.addEventFilter(orderStatusEvent, channelKey,
                    eventChannelFilterHelper.getChannelName(CHANNEL_NAME), constraintString);
        }
    }

}