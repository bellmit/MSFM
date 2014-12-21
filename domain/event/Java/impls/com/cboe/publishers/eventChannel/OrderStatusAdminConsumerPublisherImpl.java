package com.cboe.publishers.eventChannel;

import com.cboe.interfaces.events.OrderStatusAdminConsumer;
import com.cboe.idl.internalEvents.OrderStatusAdminEventConsumer;
import com.cboe.idl.order.OrderAcknowledgeStruct;
import com.cboe.idl.order.OrderAcknowledgeStructV3;
import com.cboe.idl.util.RoutingParameterStruct;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

/**
 * @author Emily Huang
 */ 
public class OrderStatusAdminConsumerPublisherImpl extends BObject implements OrderStatusAdminConsumer
{
    OrderStatusAdminEventConsumer eventChannel;
  
    protected OrderStatusAdminConsumerPublisherImpl(OrderStatusAdminEventConsumer stub)
    {
        super();
        eventChannel = stub;
    }
  
    public void subscribeOrderStatus(String userName)
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "Subscribing OrderStatus for userName = " + userName);
        }
        if (eventChannel != null)
        {
            eventChannel.subscribeOrderStatus(userName);
        }
    }
  
    public void ackOrderStatus(int[] groups, OrderAcknowledgeStruct orderAcknowledge)
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "Publishing OrderStatus Ack, transactionSequenceNumber= " + orderAcknowledge.transactionSequenceNumber );
        }
        if (eventChannel != null)
        {
            eventChannel.ackOrderStatus(groups, orderAcknowledge);
        }
    }

    public void ackOrderStatusV3(RoutingParameterStruct routingParameterStruct, OrderAcknowledgeStructV3 orderAcknowledge)
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "Publishing ackOrderStatusV3 Ack, transactionSequenceNumber= " + orderAcknowledge.transactionSequenceNumber);
        }
        if (eventChannel != null)
        {
            eventChannel.ackOrderStatusV3(routingParameterStruct, orderAcknowledge);
        }
    }

    public void publishUnackedOrderStatus(RoutingParameterStruct routingParameterStruct, String userId)
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "Requesting publishing of unacked order status, userId = " + userId);
        }
        if (eventChannel != null)
        {
            eventChannel.publishUnackedOrderStatus(routingParameterStruct, userId);
        }
    }
}

