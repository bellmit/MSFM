package com.cboe.consumers.eventChannel;


import com.cboe.domain.instrumentedChannel.event.InstrumentedEventChannelAdapter;
import com.cboe.domain.instrumentedChannel.event.InstrumentedEventChannelAdapterFactory;
import com.cboe.domain.util.*;
import com.cboe.idl.cmiOrder.*;
import com.cboe.idl.cmiConstants.*;
import com.cboe.idl.cmiUser.ExchangeFirmStruct;
import com.cboe.idl.util.RoutingParameterStruct;
import com.cboe.idl.constants.OrderStatusTypes;
import com.cboe.idl.order.*;
import com.cboe.idl.cmiConstants.StatusUpdateReasons;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.events.OrderStatusConsumerV2;
import com.cboe.interfaces.events.OrderStatusConsumer;
import com.cboe.util.ChannelKey;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.domain.iec.ClientIECFactory;
import com.cboe.domain.iec.ConcurrentEventChannelAdapter;


/**
 * OrderStatusConsumerIECImpl
 *
 * @author Jeff Illian
 */

public class OrderStatusConsumerIECImpl extends BObject implements OrderStatusConsumerV2
{
    // Flags for dispatching to user, firm user, trading firm user, or all three
    public static byte DISPATCH_USER = 1;
    public static byte DISPATCH_FIRM = 2;
    public static byte DISPATH_TRADING_FIRM = 4; // 100
    public static byte DISPATCH_ALL = (byte)(DISPATCH_USER | DISPATCH_FIRM |DISPATH_TRADING_FIRM); //111 =7

    // Delay value for perf testing
    private int blockingDelay = 0;

//    private InstrumentedEventChannelAdapter internalEventChannel;
    private ConcurrentEventChannelAdapter internalEventChannel;
    /**
      * OrderStatusConsumerIECImpl constructor comment.
      */
    public OrderStatusConsumerIECImpl()
    {
        super();
        try
        {
//        internalEventChannel = InstrumentedEventChannelAdapterFactory.find();
        	internalEventChannel = ClientIECFactory.findConcurrentEventChannelAdapter(ClientIECFactory.CAS_INSTRUMENTED_IEC);
        }
        catch (Exception e)
        {
        	Log.exception(this, "Exception getting CAS_INSTRUMENTED_IEC!", e);
        }

        try
        {
            String delayProperty = System.getProperty("OrderBlockingDelay", "0");
            blockingDelay = Integer.parseInt(delayProperty);
            if (Log.isDebugOn())
            {
                Log.debug(this, "OrderBlockingDelay value set to " + blockingDelay);
            }
        }
        catch(NumberFormatException e)
        {
            if (Log.isDebugOn())
            {
                Log.debug(this, "OrderBlockingDelay value is not an integer");
            }
        }
    }

    private String getOrderIdString(OrderIdStruct orderId)
    {
        StringBuilder oid = new StringBuilder(80);
        oid.append(": firm=").append(orderId.executingOrGiveUpFirm.firmNumber)
           .append(": exchange=").append(orderId.executingOrGiveUpFirm.exchange)
           .append(": branch=").append(orderId.branch).append(':').append(orderId.branchSequenceNumber)
           .append(": h=").append(orderId.highCboeId)
           .append(": l=").append(orderId.lowCboeId);
        return oid.toString();
    }

    /**
     * @author Brian Mahoney
     * Accepts blocks of order status messages and processes the individual order status messages
     * according to their OrderStatusType.
     * @param messages an array of order status messages                                                  
     */
   public void acceptOrderStatus(BlockedOrderStatus[] messages)
    { 
	   //FE is stop send CAS a block message
	}

    /**
      * @author Jeff Illian
      */
    public void acceptOrderFillReport(int[] groups, short statusChange, OrderStruct orderStruct, FilledReportStruct[] filledOrder, String eventInitiator)
    {
        StringBuilder received = new StringBuilder(150);
        received.append("event received -> OrderFillReport : Seq # ").append(orderStruct.transactionSequenceNumber)
                .append(getOrderIdString(orderStruct.orderId))
                .append(": statusChange=").append(statusChange);
        Log.information(this, received.toString());
        

        byte dispatchFlag = 0;
        /* 
         * For Fill report from par user (Wnnn) which have orderOrigins  "K",
         * We will send fill report to only user not firm or trading firm.
         * Note. We have tow "K" orderOrigin one for W_MAIN call OrderOrigins.MANUAL_QUOTE_ORDER the other one for W_STOCK call
         * OrderOrigins.BROKER_DEALER_FBW_ICM. This check is only valid for W_MAIN PAR order. 
         */
        if ((orderStruct.orderOriginType == OrderOrigins.MANUAL_QUOTE_ORDER)&& isParUser(orderStruct.userId))
        {
        	dispatchFlag = DISPATCH_USER;
        }
        else
        {

	        if (statusChange != StatusUpdateReasons.POSSIBLE_RESEND)
	        {
	            dispatchFlag = DISPATCH_ALL;
	        }
	        else
	        {
	            
	            if(eventInitiator.indexOf(":") != -1)
	            {
	                dispatchFlag = DISPATCH_FIRM;
	            }
	            else if(eventInitiator.indexOf("&") != -1)
	            {
	                dispatchFlag = DISPATH_TRADING_FIRM;
	            }	
	            else
	            {
	                dispatchFlag = DISPATCH_USER;
	            }
	        }
        }    
        acceptOrderFillReport(groups, statusChange, orderStruct, filledOrder, dispatchFlag);
    }
    private boolean isParUser(String userId)
    {
    	char par = 'W';
    	if ((userId.charAt(0) == par) 
    			&& (Character.isDigit(userId.charAt(1))) 
    			&& (Character.isDigit(userId.charAt(2))) 
    			&& (Character.isDigit(userId.charAt(3))))
    	{
    			return true;
    			
    	}else{
    		return false;
    	}
    }


    private void acceptOrderFillReport(int[] groups, short statusChange, OrderStruct orderStruct, FilledReportStruct[] filledOrder, byte dispatchFlag)
    {
        ChannelKey channelKey;
        ChannelEvent event;

        GroupOrderIdFillReportContainer orderFilledContainer = new GroupOrderIdFillReportContainer(groups, statusChange, orderStruct, filledOrder);

        if((dispatchFlag & DISPATCH_USER) == DISPATCH_USER)
        {
            channelKey = new ChannelKey(ChannelKey.ORDER_FILL_REPORT, orderStruct.userId);
            event = internalEventChannel.getChannelEvent(this, channelKey, orderFilledContainer);
            internalEventChannel.dispatch(event);

        }
        if((dispatchFlag & DISPATH_TRADING_FIRM) == DISPATH_TRADING_FIRM)
        {
           
            channelKey = new ChannelKey(ChannelKey.ORDER_FILL_REPORT_BY_TRADING_FIRM, orderStruct.userId);
            event = internalEventChannel.getChannelEvent(this, channelKey, orderFilledContainer);
            internalEventChannel.dispatch(event);
        }
        if((dispatchFlag & DISPATCH_FIRM) == DISPATCH_FIRM)
        {
            ExchangeFirmStructContainer firmKeyContainer = new ExchangeFirmStructContainer(orderStruct.orderId.executingOrGiveUpFirm);
            channelKey = new ChannelKey(ChannelKey.ORDER_FILL_REPORT_BY_FIRM, firmKeyContainer);
            event = internalEventChannel.getChannelEvent(this, channelKey, orderFilledContainer);
            internalEventChannel.dispatch(event);
        }
    }

    /**
      * @author Jeff Illian
      */
    public void acceptCancelReport(int[] groups, short statusChange, OrderStruct orderStruct, CancelReportStruct[] cancelReport, String eventInitiator)
    {
        StringBuilder received = new StringBuilder(130);
        received.append("event received -> CancelReport : Seq # ").append(orderStruct.transactionSequenceNumber)
                .append(getOrderIdString(orderStruct.orderId));
        Log.information(this, received.toString());
        acceptCancelReport(groups, statusChange, orderStruct, cancelReport, DISPATCH_ALL);
    }

    private void acceptCancelReport(int[] groups, short statusChange, OrderStruct orderStruct, CancelReportStruct[] cancelReport, byte dispatchFlag)
    {
    	
        ChannelKey channelKey;
        ChannelEvent event;

        OrderIdCancelReportContainer cancelReportContainer = new OrderIdCancelReportContainer(orderStruct, cancelReport);
        GroupCancelReportContainer cancelContainer = new GroupCancelReportContainer(orderStruct.userId,  groups, statusChange, cancelReportContainer );

        if((dispatchFlag & DISPATCH_USER) == DISPATCH_USER)
        {
        	
            channelKey = new ChannelKey(ChannelKey.CANCEL_REPORT, orderStruct.userId);
            event = internalEventChannel.getChannelEvent(this, channelKey, cancelContainer);
            internalEventChannel.dispatch(event);
        }
        if((dispatchFlag & DISPATCH_FIRM) == DISPATCH_FIRM)
        {
        	
            ExchangeFirmStructContainer firmKeyContainer = new ExchangeFirmStructContainer(orderStruct.orderId.executingOrGiveUpFirm);
            channelKey = new ChannelKey(ChannelKey.CANCEL_REPORT_BY_FIRM, firmKeyContainer);
            event = internalEventChannel.getChannelEvent(this, channelKey, cancelContainer);
            internalEventChannel.dispatch(event);
        }
    }

    /**
      * @author Jeff Illian
      */
    public void acceptOrderAcceptedByBook(int[] groups, OrderStruct order)
    {
        StringBuilder received = new StringBuilder(130);
        received.append("event received -> OrderAcceptedByBook : Seq # ").append(order.transactionSequenceNumber)
                .append(getOrderIdString(order.orderId));
        Log.information(this, received.toString());
        acceptOrderAcceptedByBook(groups, order, DISPATCH_ALL);
    }

    private void acceptOrderAcceptedByBook(int[] groups, OrderStruct order, byte dispatchFlag)
    {
        ChannelKey channelKey;
        ChannelEvent event;

        if((dispatchFlag & DISPATCH_USER) == DISPATCH_USER)
        {
            channelKey = new ChannelKey(ChannelKey.ORDER_ACCEPTED_BY_BOOK, order.userId);
            event = internalEventChannel.getChannelEvent(this, channelKey, order);
        }
        if((dispatchFlag & DISPATCH_FIRM) == DISPATCH_FIRM)
        {
            ExchangeFirmStructContainer firmKeyContainer = new ExchangeFirmStructContainer(order.orderId.executingOrGiveUpFirm);
            channelKey = new ChannelKey(ChannelKey.ORDER_ACCEPTED_BY_BOOK_BY_FIRM, firmKeyContainer);
            event = internalEventChannel.getChannelEvent(this, channelKey, order);
            internalEventChannel.dispatch(event);
        }
   }

    /**
      * @author Jeff Illian
      */
    public void acceptOrderUpdate(int[] groups, OrderStruct updatedOrder)
    {
        StringBuilder received = new StringBuilder(130);
        received.append("event received -> OrderUpdate : Seq # ").append(updatedOrder.transactionSequenceNumber)
                .append(getOrderIdString(updatedOrder.orderId));
        Log.information(this, received.toString());
        acceptOrderUpdate(groups, updatedOrder, DISPATCH_ALL);
    }

    private void acceptOrderUpdate(int[] groups, OrderStruct updatedOrder, byte dispatchFlag)
    {
        ChannelKey channelKey;
        ChannelEvent event;

        GroupOrderStructContainer orderStructContainer = new GroupOrderStructContainer( groups, updatedOrder );

        if((dispatchFlag & DISPATCH_USER) == DISPATCH_USER)
        {
            channelKey = new ChannelKey(ChannelKey.ORDER_UPDATE, updatedOrder.userId);
            event = internalEventChannel.getChannelEvent(this, channelKey, orderStructContainer);
            internalEventChannel.dispatch(event);
        }
        if((dispatchFlag & DISPATCH_FIRM) == DISPATCH_FIRM)
        {
            ExchangeFirmStructContainer firmKeyContainer = new ExchangeFirmStructContainer(updatedOrder.orderId.executingOrGiveUpFirm);
            channelKey = new ChannelKey(ChannelKey.ORDER_UPDATE_BY_FIRM, firmKeyContainer);
            event = internalEventChannel.getChannelEvent(this, channelKey, orderStructContainer);
            internalEventChannel.dispatch(event);
        }
    }

    /**
      * @author Jeff Illian
      */
    public void acceptNewOrder(int[] groups, short statusChange, OrderStruct newOrder, String eventInitiator)
    {
        StringBuilder received = new StringBuilder(140);
        received.append("event received -> NewOrder : Seq # ").append(newOrder.transactionSequenceNumber)
                .append(getOrderIdString(newOrder.orderId))
                .append(": statusChange=").append(statusChange);
        Log.information(this, received.toString());
        acceptNewOrder(groups, statusChange, newOrder, DISPATCH_ALL);
    }

    private void acceptNewOrder(int[] groups, short statusChange, OrderStruct newOrder, byte dispatchFlag)
    {
        ChannelKey channelKey;
        ChannelEvent event;

        // NOTE:: group member not used on the supplier end - container reused for convenience.
        //
        GroupOrderStructContainer newOrderContainer = new GroupOrderStructContainer( groups, statusChange, newOrder );
        if((dispatchFlag & DISPATCH_USER) == DISPATCH_USER)
        {
            channelKey = new ChannelKey(ChannelKey.NEW_ORDER, newOrder.userId);
            event = internalEventChannel.getChannelEvent(this, channelKey, newOrderContainer);
            internalEventChannel.dispatch(event);
        }
        if((dispatchFlag & DISPATCH_FIRM) == DISPATCH_FIRM)
        {
            ExchangeFirmStructContainer firmKeyContainer = new ExchangeFirmStructContainer(newOrder.orderId.executingOrGiveUpFirm);
            channelKey = new ChannelKey(ChannelKey.NEW_ORDER_BY_FIRM, firmKeyContainer);
            event = internalEventChannel.getChannelEvent(this, channelKey, newOrderContainer);
            internalEventChannel.dispatch(event);
        }
    }

    /**
      * @author Jeff Illian
      */
    public void acceptOrders(int[] groups, String userId, ExchangeFirmStruct firmKey, OrderStruct[] orders)
    {
        acceptOrders(groups, userId, firmKey, orders, DISPATCH_ALL);
        StringBuilder received = new StringBuilder(60);
        received.append("event received -> Orders : ").append(userId).append(" CK:").append(orders[0].classKey);
        Log.information(this, received.toString());
    }

    private void acceptOrders(int[] groups, String userId, ExchangeFirmStruct firmKey, OrderStruct[] orders, byte dispatchFlag)
    {
        ChannelKey channelKey;
        ChannelEvent event;

        GroupOrderStructSequenceContainer orderStructSequence = new GroupOrderStructSequenceContainer( groups, orders );

        if((dispatchFlag & DISPATCH_USER) == DISPATCH_USER)
        {
            channelKey = new ChannelKey(ChannelKey.ACCEPT_ORDERS, userId);
            event = internalEventChannel.getChannelEvent(this, channelKey, orderStructSequence);
            internalEventChannel.dispatch(event);
        }
        if((dispatchFlag & DISPATCH_FIRM) == DISPATCH_FIRM)
        {
            ExchangeFirmStructContainer firmKeyContainer = new ExchangeFirmStructContainer(firmKey);
            channelKey = new ChannelKey(ChannelKey.ACCEPT_ORDERS_BY_FIRM, firmKeyContainer);
            event = internalEventChannel.getChannelEvent(this, channelKey, orderStructSequence);
            internalEventChannel.dispatch(event);
        }
    }

    /**
      * @author Jeff Illian
      */
    public void acceptException(int[] groups, String userId, int exceptionMapNumber, String  description)
    {
        ChannelKey channelKey;
        ChannelEvent event;
        if (Log.isDebugOn())
        {
        	Log.debug(this, "event received -> OrderQueryException : " + description);
        }

        OrderQueryExceptionStructContainer orderQueryException = new OrderQueryExceptionStructContainer(groups, userId, exceptionMapNumber, description);

        channelKey = new ChannelKey(ChannelKey.ORDER_QUERY_EXCEPTION,"");
        event = internalEventChannel.getChannelEvent(this, channelKey, orderQueryException);
        internalEventChannel.dispatch(event);
    }

    public void acceptOrderBustReport(int[] groups, short statusChange, OrderStruct orderStruct, BustReportStruct[] bustedOrder, String eventInitiator)
    {
        StringBuilder received = new StringBuilder(140);
        received.append("event received -> OrderBustReportStruct : Seq # ").append(orderStruct.transactionSequenceNumber)
                .append(getOrderIdString(orderStruct.orderId))
                .append(": statusChange=").append(statusChange);
        Log.information(this, received.toString());

        byte dispatchFlag = 0;
        if (statusChange != StatusUpdateReasons.POSSIBLE_RESEND)
        {
            dispatchFlag = DISPATCH_ALL;
        }
        else
        {
            if(eventInitiator.indexOf(":") != -1)
            {
                dispatchFlag = DISPATCH_FIRM;
            }
            else if(eventInitiator.indexOf("&") != -1)
            {
                dispatchFlag = DISPATH_TRADING_FIRM;
            }
            else
            {
                dispatchFlag = DISPATCH_USER;
            }
        }
        acceptOrderBustReport(groups, statusChange, orderStruct, bustedOrder, dispatchFlag);
    }

    private void acceptOrderBustReport(int[] groups, short statusChange, OrderStruct orderStruct, BustReportStruct[] bustedOrder, byte dispatchFlag)
    {
        ChannelKey channelKey;
        ChannelEvent event;
        OrderIdBustStructContainer orderBustReportContainer = new OrderIdBustStructContainer( groups, statusChange, orderStruct, bustedOrder );

        if((dispatchFlag & DISPATCH_USER) == DISPATCH_USER)
        {
            channelKey = new ChannelKey(ChannelKey.ORDER_BUST_REPORT, orderStruct.userId);
            event = internalEventChannel.getChannelEvent(this, channelKey, orderBustReportContainer);
            internalEventChannel.dispatch(event);
        }
        if((dispatchFlag & DISPATH_TRADING_FIRM) == DISPATH_TRADING_FIRM)
        {
            channelKey = new ChannelKey(ChannelKey.ORDER_BUST_REPORT_BY_TRADING_FIRM, orderStruct.userId);
            event = internalEventChannel.getChannelEvent(this, channelKey, orderBustReportContainer);
            internalEventChannel.dispatch(event);
        }
        if((dispatchFlag & DISPATCH_FIRM) == DISPATCH_FIRM)
        {
            ExchangeFirmStructContainer firmKeyContainer = new ExchangeFirmStructContainer(orderStruct.orderId.executingOrGiveUpFirm);
            channelKey = new ChannelKey(ChannelKey.ORDER_BUST_REPORT_BY_FIRM, firmKeyContainer);
            event =  internalEventChannel.getChannelEvent(this, channelKey, orderBustReportContainer);
            internalEventChannel.dispatch(event);
        }
    }

    public void acceptOrderBustReinstateReport(int[] groups, short statusChange, OrderStruct orderStruct, BustReinstateReportStruct bustReinstatedOrder, String eventInitiator)
    {
        StringBuilder received = new StringBuilder(160);
        received.append("event received -> OrderBustReinstateReportStruct : Seq # ").append(bustReinstatedOrder.transactionSequenceNumber)
                .append(getOrderIdString(orderStruct.orderId))
                .append(": statusChange=").append(statusChange);
        Log.information(this, received.toString());

        byte dispatchFlag = 0;
        if (statusChange != StatusUpdateReasons.POSSIBLE_RESEND)
        {
            dispatchFlag = DISPATCH_ALL;
        }
        else
        {
            if(eventInitiator.indexOf(":") != -1)
            {
                dispatchFlag = DISPATCH_FIRM;
            }
            else if(eventInitiator.indexOf("&") != -1)
            {
                dispatchFlag = DISPATH_TRADING_FIRM;
            }
            else
            {
                dispatchFlag = DISPATCH_USER;
            }
        }
        acceptOrderBustReinstateReport(groups, statusChange, orderStruct, bustReinstatedOrder, dispatchFlag);
    }

    private void acceptOrderBustReinstateReport(int[] groups, short statusChange, OrderStruct orderStruct, BustReinstateReportStruct bustReinstatedOrder, byte dispatchFlag)
    {
        ChannelKey channelKey;
        ChannelEvent event;
        OrderIdReinstateStructContainer reinstatedOrderContainer = new OrderIdReinstateStructContainer(groups, orderStruct.userId, statusChange, orderStruct, bustReinstatedOrder);

        if((dispatchFlag & DISPATCH_USER) == DISPATCH_USER)
        {
            channelKey = new ChannelKey(ChannelKey.ORDER_BUST_REINSTATE_REPORT, orderStruct.userId);
            event = internalEventChannel.getChannelEvent(this, channelKey, reinstatedOrderContainer);
            internalEventChannel.dispatch(event);
        }
        if((dispatchFlag & DISPATH_TRADING_FIRM) == DISPATH_TRADING_FIRM)
        {
            channelKey = new ChannelKey(ChannelKey.ORDER_BUST_REINSTATE_REPORT_BY_TRADING_FIRM, orderStruct.userId);
            event = internalEventChannel.getChannelEvent(this, channelKey, reinstatedOrderContainer);
            internalEventChannel.dispatch(event);
        }
        if((dispatchFlag & DISPATCH_FIRM) == DISPATCH_FIRM)
        {
            ExchangeFirmStructContainer firmKeyContainer = new ExchangeFirmStructContainer(orderStruct.orderId.executingOrGiveUpFirm);
            channelKey = new ChannelKey(ChannelKey.ORDER_BUST_REINSTATE_REPORT_BY_FIRM, firmKeyContainer);
            event = internalEventChannel.getChannelEvent(this, channelKey, reinstatedOrderContainer);
            internalEventChannel.dispatch(event);
        }
    }

    public void acceptOrderStatusUpdate(RoutingParameterStruct routingParameters,  OrderStruct order, short statusChange)
    {
        StringBuilder received = new StringBuilder(210);
        received.append("event received -> acceptOrderStatusUpdate : Seq # ").append(order.transactionSequenceNumber)
                .append(getOrderIdString(order.orderId))
                .append(" statuseChange: ").append(statusChange);
        Log.information(this, received.toString());
        RoutingGroupOrderStructContainer orderStructContainer = new RoutingGroupOrderStructContainer(null, order, statusChange);
        ChannelKey channelKey = null;
        ChannelEvent event = null;

        channelKey = new ChannelKey(ChannelKey.ORDER_STATUS_UPDATE, order.userId);
        event = internalEventChannel.getChannelEvent(this, channelKey, orderStructContainer);
        internalEventChannel.dispatch(event);
    }
}
