package com.cboe.consumers.callback;

import com.cboe.idl.cmiCallbackV2.*;
import com.cboe.util.event.EventChannelAdapter;
import com.cboe.interfaces.callback.*;
import com.cboe.delegates.callback.*;
import com.cboe.application.shared.RemoteConnectionFactory;
import com.cboe.presentation.common.logging.GUILoggerHome;

/**
 * Created by IntelliJ IDEA.
 * User: HallB
 * Date: Mar 25, 2003
 * Time: 2:17:28 PM
 * To change this template use Options | File Templates.
 */
public class OrderStatusV2ConsumerFactory
{
    /**
     * OrderStatusConsumerFactory constructor.
     *
     * @author Derek T. Chambers-Boucher
     */
    public OrderStatusV2ConsumerFactory()
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
    public static CMIOrderStatusConsumer create(EventChannelAdapter eventProcessor)
    {
        try
        {
            OrderStatusV2Consumer orderStatusV2Consumer = new OrderStatusV2ConsumerImpl(eventProcessor);
            OrderStatusV2ConsumerDelegate delegate = new OrderStatusV2ConsumerDelegate(orderStatusV2Consumer);
            org.omg.CORBA.Object corbaObject = (org.omg.CORBA.Object)RemoteConnectionFactory.find().register_object (delegate);
            return CMIOrderStatusConsumerHelper.narrow (corbaObject);
        }
        catch (Exception e)
        {
            GUILoggerHome.find().exception(e, "OrderStatusV2ConsumerFactory.create");
            return null;
        }
    }
}
