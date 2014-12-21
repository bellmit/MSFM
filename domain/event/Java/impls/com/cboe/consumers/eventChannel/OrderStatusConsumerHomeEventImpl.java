// $Workfile$ com.cboe.consumers.eventChannel.OrderStatusConsumerHomeEventImpl.java
// $Revision$
/* $Log$
*   Initial Version                             Jeff Illian
*   Revision        Increment 6     12/1/00     desaik
*/
//----------------------------------------------------------------------
// Copyright (c) 1999 CBOE. All Rights Reserved.

package com.cboe.consumers.eventChannel;

import com.cboe.idl.order.OrderAcknowledgeStruct;
import com.cboe.idl.cmiUser.ExchangeFirmStruct;
import com.cboe.interfaces.events.*;
import com.cboe.util.*;
import com.cboe.util.event.*;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.systemsManagementService.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.*;
import com.cboe.domain.startup.ClientBOHome;
import com.cboe.domain.util.ExchangeFirmStructContainer;

import com.cboe.infrastructureServices.orbService.*;
import com.cboe.infrastructureServices.eventService.*;
import com.cboe.infrastructureServices.traderService.*;
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
public class OrderStatusConsumerHomeEventImpl extends ClientBOHome implements IECOrderStatusConsumerHome
{
    //protected OrderStatusConsumerIECImpl orderStatusConsumer;
    protected OrderStatusConsumer   orderStatusConsumer;
    protected OrderStatusEventConsumerImpl orderStatusEvent;
    protected EventService eventService;
    protected EventChannelFilterHelper eventChannelFilterHelper;
    protected final String CHANNEL_NAME = "OrderStatus";

    /**
     * OrderStatusConsumerHomeEventImpl constructor comment.
     */
    public OrderStatusConsumerHomeEventImpl()
    {
        super();
        if (Log.isDebugOn())
        {
            Log.debug( "constructor::OrderStatusConsumerHomeEventImpl" );
        }
    }

    /**
      * @author Jeff Illian
      * @return OrderStatusConsumer
      */
    public OrderStatusConsumer create()
    {
        return find();
    }

    /**
      * @author Jeff Illian
      * @return OrderStatusConsumer
      */
    public OrderStatusConsumer find()
    {
        return orderStatusConsumer;
    }// end of find

    /**
     * @author Jeff Illian
     * @return void
     */
    public void clientStart ()
        throws Exception
    {
        createConsumer();

        //The addToContainer call MUST occur prior to creation of the interceptor.
        //      manageObject(orderStatusConsumer);

        String interfaceRepId =
          com.cboe.idl.events.OrderStatusEventConsumerHelper.id();
        // connect to the event channel !!without filter!! and add the constraint filter later on
        // using addConstraint - Connie Feng
        eventChannelFilterHelper.connectConsumer( CHANNEL_NAME, interfaceRepId, orderStatusEvent );
    }

    protected void createConsumer()
    {
        ((OrderStatusConsumerIECImpl)orderStatusConsumer).create(
          String.valueOf(orderStatusConsumer.hashCode()));
        //Every bo object must be added to the container.
        addToContainer((OrderStatusConsumerIECImpl)orderStatusConsumer);
    }

    protected void initializeConsumer()
    {
       orderStatusConsumer   = new OrderStatusConsumerIECImpl();
    }

    /**
      * @author Jeff Illian
      * @return void
      */
    public void clientInitialize()
    {
        initializeConsumer();

        orderStatusEvent = new OrderStatusEventConsumerImpl(orderStatusConsumer);
        eventChannelFilterHelper = new EventChannelFilterHelper();
        eventService = eventChannelFilterHelper.connectEventService();
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
        // Add the filter to the CBOE event channel
        addConstraint(channelKey);
    }

    /**
     * Adds constraint based on the channel key
     *
     * @param channelKey the event channel key
     *
     * @author Connie Feng
     */
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
                                                    eventChannelFilterHelper.getChannelName(CHANNEL_NAME),
                                                    constraintString);
        }
    }// end of addConstraint


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
        removeConstraint(channelKey);
    }

    /**
     * Removes constraint based on the channel key
     *
     * @param channelKey the event channel key
     *
     * @author Connie Feng
     */
    protected void removeConstraint(ChannelKey channelKey)
        throws SystemException
    {
        if ( find() != null ) {
            String constraintString = getConstraintString(channelKey);
            eventChannelFilterHelper.removeEventFilter( channelKey, constraintString);
        }
    }// end of addConstraint

    /**
     * Returns the constraint string based on the channel key
     *
     * @param channelKey the event channel key
     *
     * @author Connie Feng
     */
    protected String getConstraintString(ChannelKey channelKey)
    {
        String parm = getParmName(channelKey);

        if(parm.equals(EventChannelFilterHelper.ALL_EVENTS_CONSTRAINT) ||
           parm.equals(EventChannelFilterHelper.NO_EVENTS_CONSTRAINT))
        {
            return parm;
        }
        
        StringBuilder buf = new StringBuilder(parm.length()+2);
        buf.append("$.").append(parm);
        return buf.toString();
    }

    /**
     * Returns the constraint parameter string based on the channel key
     *
     * @param channelKey the event channel key
     *
     * @author Connie Feng
     */
    protected String getParmName(ChannelKey channelKey)
    {
        ExchangeFirmStructContainer firmKeyContainer;
        StringBuilder name = new StringBuilder(250);
        switch (channelKey.channelType)
        {
            case ChannelType.NEW_ORDER :
                name.append("acceptNewOrder.newOrder.userId=='").append(channelKey.key).append("'");
                return name.toString();
            case ChannelType.NEW_ORDER_BY_FIRM :
                firmKeyContainer = (ExchangeFirmStructContainer)channelKey.key;
                name.append("acceptNewOrder.newOrder.orderId.executingOrGiveUpFirm.exchange=='")
                    .append(firmKeyContainer.getExchange())
                    .append("' and $.acceptNewOrder.newOrder.orderId.executingOrGiveUpFirm.firmNumber=='")
                    .append(firmKeyContainer.getFirmNumber()).append("'");
                return name.toString();
            case ChannelType.ORDER_ACCEPTED_BY_BOOK :
                name.append("acceptOrderAcceptedByBook.order.userId=='").append(channelKey.key).append("'");
                return name.toString();
            case ChannelType.ORDER_ACCEPTED_BY_BOOK_BY_FIRM :
                firmKeyContainer = (ExchangeFirmStructContainer)channelKey.key;
                name.append("acceptOrderAcceptedByBook.order.orderId.executingOrGiveUpFirm.exchange=='")
                    .append(firmKeyContainer.getExchange())
                    .append("' and $.acceptOrderAcceptedByBook.order.orderId.executingOrGiveUpFirm.firmNumber=='")
                    .append(firmKeyContainer.getFirmNumber()).append("'");
                return name.toString();
            case ChannelType.ORDER_FILL_REPORT :
                name.append("acceptOrderFillReport.orderStruct.userId=='").append(channelKey.key).append("'");
                return name.toString();
            case ChannelType.ORDER_FILL_REPORT_BY_FIRM :
                firmKeyContainer = (ExchangeFirmStructContainer)channelKey.key;
                name.append("acceptOrderFillReport.orderStruct.orderId.executingOrGiveUpFirm.exchange=='")
                    .append(firmKeyContainer.getExchange())
                    .append("'and $.acceptOrderFillReport.orderStruct.orderId.executingOrGiveUpFirm.firmNumber=='")
                    .append(firmKeyContainer.getFirmNumber()).append("'");
                return name.toString();
            case ChannelType.ORDER_BUST_REPORT :
                name.append("acceptOrderBustReport.orderStruct.userId=='").append(channelKey.key).append("'");
                return name.toString();
            case ChannelType.ORDER_BUST_REPORT_BY_FIRM :
                firmKeyContainer = (ExchangeFirmStructContainer)channelKey.key;
                name.append("acceptOrderBustReport.orderStruct.orderId.executingOrGiveUpFirm.exchange=='")
                    .append(firmKeyContainer.getExchange())
                    .append("' and $.acceptOrderBustReport.orderStruct.orderId.executingOrGiveUpFirm.firmNumber=='")
                    .append(firmKeyContainer.getFirmNumber()).append("'");
                return name.toString();
            case ChannelType.ORDER_BUST_REINSTATE_REPORT :
                name.append("acceptOrderBustReinstateReport.orderStruct.userId=='").append(channelKey.key).append("'");
                return name.toString();
            case ChannelType.ORDER_BUST_REINSTATE_REPORT_BY_FIRM :
                firmKeyContainer = (ExchangeFirmStructContainer)channelKey.key;
                name.append("acceptOrderBustReinstateReport.orderStruct.orderId.executingOrGiveUpFirm.exchange=='")
                    .append(firmKeyContainer.getExchange())
                    .append("' and $.acceptOrderBustReinstateReport.orderStruct.orderId.executingOrGiveUpFirm.firmNumber=='")
                    .append(firmKeyContainer.getFirmNumber()).append("'");
                return name.toString();
            case ChannelType.CANCEL_REPORT :
                name.append("acceptCancelReport.orderStruct.userId=='").append(channelKey.key).append("'");
                return name.toString();
            case ChannelType.CANCEL_REPORT_BY_FIRM :
                firmKeyContainer = (ExchangeFirmStructContainer)channelKey.key;
                name.append("acceptCancelReport.orderStruct.orderId.executingOrGiveUpFirm.exchange=='")
                    .append(firmKeyContainer.getExchange())
                    .append("' and $.acceptCancelReport.orderStruct.orderId.executingOrGiveUpFirm.firmNumber=='")
                    .append(firmKeyContainer.getFirmNumber()).append("'");
                return name.toString();
            case ChannelType.ORDER_UPDATE :
                name.append("acceptOrderUpdate.updatedOrder.userId=='").append(channelKey.key).append("'");
                return name.toString();
            case ChannelType.ORDER_UPDATE_BY_FIRM :
                firmKeyContainer = (ExchangeFirmStructContainer)channelKey.key;
                name.append("acceptOrderUpdate.updatedOrder.orderId.executingOrGiveUpFirm.exchange=='")
                    .append(firmKeyContainer.getExchange())
                    .append("' and $.acceptOrderUpdate.updatedOrder.orderId.executingOrGiveUpFirm.firmNumber=='")
                    .append(firmKeyContainer.getFirmNumber()).append("'");
                return name.toString();
            case ChannelType.ORDER_QUERY_EXCEPTION :
                name.append("acceptException.userId=='").append(channelKey.key).append("'");
                return name.toString();
            case ChannelType.ACCEPT_ORDERS :
                name.append("acceptOrders.userId=='").append(channelKey.key).append("'");
                return name.toString();
            case ChannelType.ACCEPT_ORDERS_BY_FIRM :
                firmKeyContainer = (ExchangeFirmStructContainer)channelKey.key;
                name.append("acceptOrders.firmKey.exchange=='")
                    .append(firmKeyContainer.getExchange())
                    .append("' and $.acceptOrders.firmKey.firmNumber=='")
                    .append(firmKeyContainer.getFirmNumber()).append("'");
                return name.toString();
            case ChannelType.ORDER_STATUS_UPDATE :
                name.append("acceptOrderStatusUpdate.order.userId=='").append(channelKey.key).append("'");
                return name.toString();

            default :
                Log.alarm(this, "Invalid Channel Type for filtering for context: " + channelKey.channelType);
                return EventChannelFilterHelper.NO_EVENTS_CONSTRAINT;
        }
    } // end of getParmName

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
    public void addConsumer(OrderStatusConsumer consumer, ChannelKey key) {}
    public void removeConsumer(OrderStatusConsumer consumer, ChannelKey key) {}
    public void removeConsumer(OrderStatusConsumer consumer) {}
    
    // Unused method
	public OrderStatusConsumer find(String userId) 
	{
		return find();
	}

    // Unused method
	public OrderStatusConsumer create(String userId) 
	{
		return find();
	}

}// EOF
