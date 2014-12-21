// $Workfile$ com.cboe.consumers.eventChannel.OrderStatusConsumerHomeInProcessImpl.java
// $Revision$
/* $Log$
*   Initial Version                             Jeff Illian
*   Revision        Increment 6     12/1/00     desaik
*/
//----------------------------------------------------------------------
// Copyright (c) 1999 CBOE. All Rights Reserved.

package com.cboe.consumers.eventChannel;

import com.cboe.idl.order.OrderAcknowledgeStruct;
import com.cboe.interfaces.events.*;
import com.cboe.util.*;
import com.cboe.util.event.*;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.*;
import com.cboe.domain.startup.ClientBOHome;
import com.cboe.exceptions.*;

    /**
     * <b> Description </b>
     * <p>
     *      The Order Status Listener class.
     * </p>
     *
     * @author Jeff Illian
     * @author Keval Desai
     */
public class OrderStatusConsumerHomeInProcessImpl extends ClientBOHome implements IECOrderStatusConsumerV2Home
{
    private OrderStatusConsumerIECImpl orderStatusConsumer;

    /**
     * OrderStatusConsumerHomeEventImpl constructor comment.
     */
    public OrderStatusConsumerHomeInProcessImpl()
    {
        super();
    }

    /**
      * @author Jeff Illian
      * @return OrderStatusConsumer
      */
    public OrderStatusConsumerV2 create()
    {
        return find();
    }

    /**
      * @author Jeff Illian
      * @return OrderStatusConsumer
      */
    public OrderStatusConsumerV2 find()
    {
        return orderStatusConsumer;
    }// end of find

    /**
     * @author Jeff Illian
     * @return void
     */
    public void clientStart()
        throws Exception
    {
        orderStatusConsumer.create(
          String.valueOf(orderStatusConsumer.hashCode()));
        //Every bo object must be added to the container.
        addToContainer(orderStatusConsumer);
    }

    /**
      * @author Jeff Illian
      * @return void
      */
    public void clientInitialize()
    {
        orderStatusConsumer = new OrderStatusConsumerIECImpl();
    }

    /**
      * Adds a  Filter to the internal event channel. Constraints based on the
      * ChannelKey will be added as well. Do not make call to addConstraints when this method has
      * already being called.
      *
      * @param channelKey the event channel key
      *
      * @author Connie Feng
      * @author Keval Desai
      * @version 12/1/00
      */
    public void addFilter ( ChannelKey channelKey )
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
    }

    /**
     * Removes the event channel Filter from the CBOE event channel.
     *
     * @param channelKey the event channel key
     *
     * @author Connie Feng
     * @author Keval Desai
     * @version 12/1/00
     */
    public void removeFilter ( ChannelKey channelKey )
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
    }

    // Unused methods declared in home interface for server usage.
    public void resubscribeOrderStatus(String userId)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {}
    public void publishUnackedOrderStatusByClass(String userId, int classKey)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {}
    public void ackOrderStatus(OrderAcknowledgeStruct orderAcknowledge)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {}
    public void addConsumer(OrderStatusConsumerV2 consumer, ChannelKey key) {}
    public void removeConsumer(OrderStatusConsumerV2 consumer, ChannelKey key) {}
    public void removeConsumer(OrderStatusConsumerV2 consumer) {}

    // Unused method
	public OrderStatusConsumerV2 find(String userId)
	{
		return find();
	}

    // Unused method
	public OrderStatusConsumerV2 create(String userId)
	{
		return find();
	}


}// EOF
