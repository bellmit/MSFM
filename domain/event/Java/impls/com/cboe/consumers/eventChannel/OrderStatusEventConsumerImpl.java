package com.cboe.consumers.eventChannel;

import com.cboe.idl.cmiOrder.*;
import com.cboe.interfaces.events.*;
import com.cboe.idl.cmiUser.ExchangeFirmStruct;
import com.cboe.idl.util.RoutingParameterStruct;

/**
 * OrderStatusEventConsumerImpl
 *
 * @author Jeff Illian
 */

public class OrderStatusEventConsumerImpl extends com.cboe.idl.events.POA_OrderStatusEventConsumer implements OrderStatusConsumer
{
    private OrderStatusConsumer delegate;

    /**
     * OrderStatusEventConsumerImpl constructor comment.
     */
    public OrderStatusEventConsumerImpl(OrderStatusConsumer orderStatusConsumer)
    {
        super();
        this.delegate = orderStatusConsumer;
    }

    /**
      * acceptOrderFillReport
      *
      * Receives event(s) from the CBOE event channel and delegates to the
      * FoundationFramework based consumer
      *
      * @author Keith Korecky
      *
      * @param    filledOrder - OrderFillReportStruct structure to delegate
      *
      * @return void
      */
    public void acceptOrderFillReport(int[] groups, short statusChange, OrderStruct orderStruct, FilledReportStruct[] filledOrder, String eventInitiator)
    {
        delegate.acceptOrderFillReport(groups, statusChange, orderStruct, filledOrder, eventInitiator);
    }

    /**
      * acceptCancelReport
      *
      * Receives event(s) from the CBOE event channel and delegates to the
      * FoundationFramework based consumer
      *
      * @author Keith Korecky
      *
      * @param    userId      - user/channel identifier
      * @param    canceReport - CancelReportStruct structure to delegate
      *
      * @return void
      */
    public void acceptCancelReport(int[] groups, short statusChange, OrderStruct orderStruct, CancelReportStruct[] cancelReport, String eventInitiator)
    {
        delegate.acceptCancelReport(groups, statusChange, orderStruct, cancelReport,eventInitiator);
    }

    /**
      * acceptOrderAcceptedByBook
      *
      * Receives event(s) from the CBOE event channel and delegates to the
      * FoundationFramework based consumer
      *
      * @author Keith Korecky
      *
      * @param    order - OrderStruct structure to delegate
      *
      * @return void
      */
    public void acceptOrderAcceptedByBook( int[] groups, OrderStruct order)
    {
        delegate.acceptOrderAcceptedByBook(groups, order);
    }

    /**
      * acceptOrderUpdate
      *
      * Receives event(s) from the CBOE event channel and delegates to the
      * FoundationFramework based consumer
      *
      * @author Keith Korecky
      *
      * @param    updatedOrder - OrderStruct structure to delegate
      *
      * @return void
      */
    public void acceptOrderUpdate(int[] groups, OrderStruct updatedOrder)
    {
        delegate.acceptOrderUpdate(groups, updatedOrder);
    }

    /**
      * acceptNewOrder
      *
      * Receives event(s) from the CBOE event channel and delegates to the
      * FoundationFramework based consumer
      *
      * @author Keith Korecky
      *
      * @param    newOrder - OrderStruct structure to delegate
      *
      * @return void
      */
    public void acceptNewOrder(int[] groups, short statusChange, OrderStruct newOrder,String eventInitiator)
    {
        delegate.acceptNewOrder(groups, statusChange, newOrder, eventInitiator);
    }

    /**
      * acceptException
      *
      * Receives event(s) from the CBOE event channel and delegates to the
      * FoundationFramework based consumer
      *
      * @author Keith Korecky
      *
      * @param    userId      - user/channel identifier
      * @param    int         - exception map ID
      * @param    description - exception text
      *
      * @return void
      */
    public void acceptException(int[] groups, String userId, int exceptionMapNumber, String description)
    {
        delegate.acceptException(groups, userId, exceptionMapNumber, description);
    }

    /**
      * acceptOrders
      *
      * Receives event(s) from the CBOE event channel and delegates to the
      * FoundationFramework based consumer
      *
      * @author Keith Korecky
      *
      * @param    userId      - user/channel identifier
      * @param    orders      - array of OrderStruct structures to delegate
      *
      * @return void
      */
    public void acceptOrders(int[] groups, String userId, ExchangeFirmStruct firmKey, OrderStruct[] orders)
    {
        delegate.acceptOrders(groups, userId, firmKey, orders);
    }

    public void acceptOrderBustReport(int[] groups, short statusChange, OrderStruct orderStruct, BustReportStruct[] bustedOrder, String eventInitiator)
    {
        delegate.acceptOrderBustReport(groups, statusChange, orderStruct, bustedOrder, eventInitiator);
    }

    public void acceptOrderBustReinstateReport(int[] groups,  short statusChange, OrderStruct orderStruct, BustReinstateReportStruct bustReinstatedOrder, String eventInitiator)
    {
        delegate.acceptOrderBustReinstateReport(groups, statusChange, orderStruct, bustReinstatedOrder, eventInitiator);
    }
    public void acceptOrderStatusUpdate(RoutingParameterStruct routingParameters,  OrderStruct order, short statusChange)
    {
        delegate.acceptOrderStatusUpdate(routingParameters, order, statusChange);
    }
    /**
      * @author Jeff Illian
      */
    public org.omg.CORBA.Object get_typed_consumer()
    {
        return null;
    }

    /**
      * @author Jeff Illian
      */
    public void push(org.omg.CORBA.Any data) throws org.omg.CosEventComm.Disconnected
    {
    }

    /**
      * @author Jeff Illian
      */
    public void disconnect_push_consumer()
    {
    }
}
