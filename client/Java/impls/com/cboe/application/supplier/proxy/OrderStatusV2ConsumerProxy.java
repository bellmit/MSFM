package com.cboe.application.supplier.proxy;

import static com.cboe.application.order.common.UserOrderServiceUtil.*;
import com.cboe.application.order.common.UserOrderServiceUtil;
import com.cboe.application.shared.IOrderAckConstraints;
import com.cboe.application.supplier.OrderStatusV2SupplierFactory;
import com.cboe.domain.instrumentedChannel.supplier.proxy.InstrumentedGMDSupplierProxy;
import com.cboe.domain.util.DateWrapper;
import com.cboe.domain.util.OrderIdStructContainerFactory;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.idl.cmiCallbackV2.CMIOrderStatusConsumer;
import com.cboe.idl.cmiConstants.StatusUpdateReasons;
import com.cboe.idl.cmiOrder.*;
import com.cboe.idl.constants.TransactionClockPoints;
import com.cboe.idl.order.OrderAcknowledgeStructV3;
import com.cboe.idl.util.TransactionClockPointStruct;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.SessionManager;
import com.cboe.interfaces.domain.BaseOrderIdStructContainer;
import com.cboe.interfaces.domain.GMDProxyHome;
import com.cboe.interfaces.domain.SupplierProxyMessageTypes;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.channel.ChannelEvent;

import java.util.Hashtable;
import java.util.Map;

/**
 * OrderStatusV2ConsumerProxy serves as a proxy to the V2 CMIOrderStatusConsumer
 * object on the presentation side in com.cboe.presentation.consumer.  The
 * OrderStatusV2Supplier on the CAS uses this proxy object to communicate to
 * the GUI callback object.
 *
 * @see com.cboe.idl.cmiCallbackV2.CMIOrderStatusConsumer
 *
 * @author Tom Trop
 */

public class OrderStatusV2ConsumerProxy extends InstrumentedGMDSupplierProxy
{
    //--------------------------------------------------------------------------
    // static data members
    //--------------------------------------------------------------------------
    private static final int DEFAULT_SIZE = 10;

    private Map<BaseOrderIdStructContainer, Integer> possibleResendOrderMap = new Hashtable<BaseOrderIdStructContainer, Integer>(DEFAULT_SIZE);
    private IOrderAckConstraints orderAckConstraints = null;

    //--------------------------------------------------------------------------
    // constructors
    //--------------------------------------------------------------------------
    /**
     * OrderStatusConsumerProxy constructor.
     *
     * NOTE: package-level access.
     */
    OrderStatusV2ConsumerProxy(CMIOrderStatusConsumer orderStatus,
                               BaseSessionManager sessionManager,
                               boolean gmdProxy,
                               GMDProxyHome home)
    {
        super(sessionManager, OrderStatusV2SupplierFactory.find(sessionManager), gmdProxy, home, orderStatus);
        interceptor = new OrderStatusV2ConsumerInterceptor(orderStatus);
    }

    OrderStatusV2ConsumerProxy(CMIOrderStatusConsumer orderStatus,
                               BaseSessionManager sessionManager,
                               boolean gmdProxy,
                               GMDProxyHome home,
                               IOrderAckConstraints orderAckConstraints
                               )
    {
        this(orderStatus, sessionManager, gmdProxy, home);
        this.orderAckConstraints = orderAckConstraints;
    }

    
    //--------------------------------------------------------------------------
    // BaseSupplierProxy methods
    //--------------------------------------------------------------------------
    /**
     * This method is called by ChannelThreadCommand object.  It takes the passed
     * EventChannelEvent, parses out the relevant data for the proxied object,
     * and calls the proxied objects callback method passing in the appropriate
     * data.
     *
     * @param event the ChannelEvent containing the data to send the listener.
     */
    public void channelUpdate(ChannelEvent event)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this,"calling channelUpdate for " + getSessionManager());
        }

        if (event != null)
        {
            try
            {
                ChannelKey key = (ChannelKey) event.getChannel();
                BaseSessionManager baseSessionManager = getSessionManager();

                switch (key.channelType)
                {
                case ChannelType.CB_ALL_ORDERS_V2:
                case ChannelType.CB_ORDERS_FOR_CLASS_V2:
                case ChannelType.CB_ORDERS_FOR_FIRM_V2:
                case ChannelType.CB_ORDERS_FOR_FIRM_FOR_CLASS_V2:
                case ChannelType.CB_ORDERS_FOR_PRODUCT_V2:
                case ChannelType.CB_ORDERS_FOR_TYPE_V2:
                case ChannelType.CB_ORDERS_FOR_SESSION_V2:
                case ChannelType.CB_ORDER_STATUS_UPDATE_V2:
                case ChannelType.CB_ORDER_STATUS_UPDATE_FOR_CLASS_V2:
                    handleAcceptOrderStatus(event);
                    break;

                case ChannelType.CB_ORDER_FILLED_REPORT_V2:
                case ChannelType.CB_ORDER_FILLED_REPORT_FOR_CLASS_V2:
                case ChannelType.CB_ORDER_FILLED_REPORT_FOR_FIRM_V2:
                case ChannelType.CB_ORDER_FILLED_REPORT_FOR_FIRM_FOR_CLASS_V2:
                case ChannelType.CB_ORDER_FILLED_REPORT_FOR_PRODUCT_V2:
                case ChannelType.CB_ORDER_FILLED_REPORT_FOR_TYPE_V2:
                case ChannelType.CB_ORDER_FILLED_REPORT_FOR_SESSION_V2:
                case ChannelType.CB_ORDER_FILLED_REPORT_FOR_TRADING_FIRM:
                    handleAcceptOrderFilledReport(event, baseSessionManager);
                    break;

                case ChannelType.CB_ORDER_CANCELED_REPORT_V2:
                case ChannelType.CB_ORDER_CANCELED_REPORT_FOR_CLASS_V2:
                case ChannelType.CB_ORDER_CANCELED_REPORT_FOR_FIRM_V2:
                case ChannelType.CB_ORDER_CANCELED_REPORT_FOR_FIRM_FOR_CLASS_V2:
                case ChannelType.CB_ORDER_CANCELED_REPORT_FOR_PRODUCT_V2:
                case ChannelType.CB_ORDER_CANCELED_REPORT_FOR_TYPE_V2:
                case ChannelType.CB_ORDER_CANCELED_REPORT_FOR_SESSION_V2:
                    handleAcceptOrderCanceledReport(event, baseSessionManager);
                    break;

                case ChannelType.CB_ORDER_BUST_REPORT_V2:
                case ChannelType.CB_ORDER_BUST_REPORT_FOR_CLASS_V2:
                case ChannelType.CB_ORDER_BUST_REPORT_FOR_FIRM_V2:
                case ChannelType.CB_ORDER_BUST_REPORT_FOR_FIRM_FOR_CLASS_V2:
                case ChannelType.CB_ORDER_BUST_REPORT_FOR_PRODUCT_V2:
                case ChannelType.CB_ORDER_BUST_REPORT_FOR_TYPE_V2:
                case ChannelType.CB_ORDER_BUST_REPORT_FOR_SESSION_V2:
                case ChannelType.CB_ORDER_BUST_REPORT_FOR_TRADING_FIRM:
                    handleAcceptOrderBustReport(event, baseSessionManager);
                    break;

                case ChannelType.CB_ORDER_BUST_REINSTATE_REPORT_V2:
                case ChannelType.CB_ORDER_BUST_REINSTATE_REPORT_FOR_CLASS_V2:
                case ChannelType.CB_ORDER_BUST_REINSTATE_REPORT_FOR_FIRM_V2:
                case ChannelType.CB_ORDER_BUST_REINSTATE_REPORT_FOR_FIRM_FOR_CLASS_V2:
                case ChannelType.CB_ORDER_BUST_REINSTATE_REPORT_FOR_PRODUCT_V2:
                case ChannelType.CB_ORDER_BUST_REINSTATE_REPORT_FOR_TYPE_V2:
                case ChannelType.CB_ORDER_BUST_REINSTATE_REPORT_FOR_SESSION_V2:
                case ChannelType.CB_ORDER_BUST_REINSTATE_REPORT_FOR_TRADING_FIRM:
                    handleAcceptOrderBustReinstateReport(event, baseSessionManager);
                    break;

                case ChannelType.CB_NEW_ORDER_REPORT_V2:
                case ChannelType.CB_NEW_ORDER_REPORT_FOR_CLASS_V2:
                case ChannelType.CB_NEW_ORDER_REPORT_FOR_FIRM_V2:
                case ChannelType.CB_NEW_ORDER_REPORT_FOR_FIRM_FOR_CLASS_V2:
                case ChannelType.CB_NEW_ORDER_REPORT_FOR_PRODUCT_V2:
                case ChannelType.CB_NEW_ORDER_REPORT_FOR_TYPE_V2:
                case ChannelType.CB_NEW_ORDER_REPORT_FOR_SESSION_V2:
                    handleAcceptNewOrder(event, baseSessionManager);
                    break;
                }
            }
            catch(Exception e)
            {
                Log.exception(this, "session:" + getSessionManager(), e);
                lostConnection(event);
            }
        }
        else
        {
            Log.information(this, "Null event");
        }
    }

    /**
     *
     */
    public String getMethodName(ChannelEvent event)
    {
        String method = "";

        ChannelKey key = (ChannelKey) event.getChannel();

        switch (key.channelType)
        {
        case ChannelType.CB_ALL_ORDERS_V2:
        case ChannelType.CB_ORDERS_FOR_CLASS_V2:
        case ChannelType.CB_ORDERS_FOR_FIRM_V2:
        case ChannelType.CB_ORDERS_FOR_FIRM_FOR_CLASS_V2:
        case ChannelType.CB_ORDERS_FOR_PRODUCT_V2:
        case ChannelType.CB_ORDERS_FOR_TYPE_V2:
        case ChannelType.CB_ORDERS_FOR_SESSION_V2:
            // Call the proxied method passing the extracted OrderDetailStruct[] from the EventChannelEvent.
            method = "acceptOrderStatus";
            break;

        case ChannelType.CB_ORDER_FILLED_REPORT_V2:
        case ChannelType.CB_ORDER_FILLED_REPORT_FOR_CLASS_V2:
        case ChannelType.CB_ORDER_FILLED_REPORT_FOR_FIRM_V2:
        case ChannelType.CB_ORDER_FILLED_REPORT_FOR_FIRM_FOR_CLASS_V2:
        case ChannelType.CB_ORDER_FILLED_REPORT_FOR_PRODUCT_V2:
        case ChannelType.CB_ORDER_FILLED_REPORT_FOR_TYPE_V2:
        case ChannelType.CB_ORDER_FILLED_REPORT_FOR_SESSION_V2:
        case ChannelType.CB_ORDER_FILLED_REPORT_FOR_TRADING_FIRM:
            // Call the proxied method passing the extracted QuoteFilledReportStruct from the EventChannelEvent.
            method = "acceptOrderFilledReport";
            break;

        case ChannelType.CB_ORDER_CANCELED_REPORT_V2:
        case ChannelType.CB_ORDER_CANCELED_REPORT_FOR_CLASS_V2:
        case ChannelType.CB_ORDER_CANCELED_REPORT_FOR_FIRM_V2:
        case ChannelType.CB_ORDER_CANCELED_REPORT_FOR_FIRM_FOR_CLASS_V2:
        case ChannelType.CB_ORDER_CANCELED_REPORT_FOR_PRODUCT_V2:
        case ChannelType.CB_ORDER_CANCELED_REPORT_FOR_TYPE_V2:
        case ChannelType.CB_ORDER_CANCELED_REPORT_FOR_SESSION_V2:
            method = "acceptOrderCanceledReport";
            break;

        case ChannelType.CB_ORDER_BUST_REPORT_V2:
        case ChannelType.CB_ORDER_BUST_REPORT_FOR_CLASS_V2:
        case ChannelType.CB_ORDER_BUST_REPORT_FOR_FIRM_V2:
        case ChannelType.CB_ORDER_BUST_REPORT_FOR_FIRM_FOR_CLASS_V2:
        case ChannelType.CB_ORDER_BUST_REPORT_FOR_PRODUCT_V2:
        case ChannelType.CB_ORDER_BUST_REPORT_FOR_TYPE_V2:
        case ChannelType.CB_ORDER_BUST_REPORT_FOR_SESSION_V2:
        case ChannelType.CB_ORDER_BUST_REPORT_FOR_TRADING_FIRM:
            method = "acceptOrderBustReport";
            break;

        case ChannelType.CB_ORDER_BUST_REINSTATE_REPORT_V2:
        case ChannelType.CB_ORDER_BUST_REINSTATE_REPORT_FOR_CLASS_V2:
        case ChannelType.CB_ORDER_BUST_REINSTATE_REPORT_FOR_FIRM_V2:
        case ChannelType.CB_ORDER_BUST_REINSTATE_REPORT_FOR_FIRM_FOR_CLASS_V2:
        case ChannelType.CB_ORDER_BUST_REINSTATE_REPORT_FOR_PRODUCT_V2:
        case ChannelType.CB_ORDER_BUST_REINSTATE_REPORT_FOR_TYPE_V2:
        case ChannelType.CB_ORDER_BUST_REINSTATE_REPORT_FOR_SESSION_V2:
        case ChannelType.CB_ORDER_BUST_REINSTATE_REPORT_FOR_TRADING_FIRM:
            method = "acceptOrderBustReinstateReport";
            break;
        }

        return method;
    }


    //--------------------------------------------------------------------------
    // private methods
    //--------------------------------------------------------------------------
    /**
     *
     */
    private void handleAcceptOrderStatus(ChannelEvent event) {
        String smgr = getSessionManager().toString();
        StringBuilder calling = new StringBuilder(smgr.length()+30);
        calling.append("calling acceptOrderStatus for ").append(smgr);
        Log.information(this, calling.toString());

        // Call the proxied method passing the extracted OrderDetailStruct[] from the EventChannelEvent.
        OrderDetailStruct[] orders = (OrderDetailStruct[])event.getEventData();

        ((OrderStatusV2ConsumerInterceptor)interceptor).acceptOrderStatus(orders, getProxyWrapper().getQueueSize());
    }

    /**
     *
     */
    private void handleAcceptOrderFilledReport(ChannelEvent event,
                                               BaseSessionManager baseSessionManager)
        throws  SystemException,
                CommunicationException,
                AuthorizationException,
                DataValidationException
    {
        OrderFilledReportStruct filledReport = (OrderFilledReportStruct)event.getEventData();

        TransactionClockPointStruct[] orderTime = null;
        if (getGMDStatus())
        {
            orderTime = getTransactionClockPointStructs();
            orderTime[0].clockPoint = TransactionClockPoints.CAS_RECEIVE;
            orderTime[0].timestamp = new DateWrapper ().toDateTimeStruct();
            orderTime[1].clockPoint = TransactionClockPoints.CAS_SEND;
            orderTime[1].timestamp = new DateWrapper ().toDateTimeStruct();
        }

        if (filledReport.filledOrder.statusChange == StatusUpdateReasons.POSSIBLE_RESEND)
        {
            if (!processPossibleResend(filledReport.filledOrder.orderStruct))
            {
                return;
            }
        }

        String smgr = getSessionManager().toString();
        String oid = getOrderIdString(filledReport.filledOrder.orderStruct.orderId);
        StringBuilder calling = new StringBuilder(smgr.length()+oid.length()+95);
        calling.append("calling acceptOrderFilledReport for ").append(smgr).append(oid)
               .append(" : queue = ").append(getProxyWrapper().getQueueSize())
               .append(" : max = ").append(getProxyWrapper().getMaxQueueSize())
               .append(" : statusChange=").append(filledReport.filledOrder.statusChange);
        Log.information(this, calling.toString());

        ((OrderStatusV2ConsumerInterceptor)interceptor).acceptOrderFilledReport(filledReport, getProxyWrapper().getQueueSize());

        calling.setLength(0);
        calling.append("finished calling acceptOrderFilledReport for ").append(smgr).append(oid);
        Log.information(this, calling.toString());

        if (getGMDStatus())
        {
            orderTime[2].clockPoint = TransactionClockPoints.USER_ACK;
            orderTime[2].timestamp = new DateWrapper ().toDateTimeStruct();
            OrderAcknowledgeStructV3 orderAck  = new OrderAcknowledgeStructV3(
                baseSessionManager.getUserId(),
                filledReport.filledOrder.orderStruct.orderId,
                filledReport.filledOrder.orderStruct.productKey,
                filledReport.filledOrder.orderStruct.classKey,
                filledReport.filledOrder.orderStruct.transactionSequenceNumber,
                orderTime
                );

            ((SessionManager)baseSessionManager).ackOrderStatusV3(orderAck);
        }
    }

    /**
     *
     */
    private void handleAcceptOrderCanceledReport(ChannelEvent event,
                                                 BaseSessionManager baseSessionManager)
        throws  SystemException,
                CommunicationException,
                AuthorizationException,
                DataValidationException
    {
        OrderCancelReportStruct cancelReport =
            (OrderCancelReportStruct)event.getEventData();

        TransactionClockPointStruct[] orderTime = null;
        if (getGMDStatus())
        {
            orderTime = getTransactionClockPointStructs();
            orderTime[0].clockPoint = TransactionClockPoints.CAS_RECEIVE;
            orderTime[0].timestamp = new DateWrapper().toDateTimeStruct();
            orderTime[1].clockPoint = TransactionClockPoints.CAS_SEND;
            orderTime[1].timestamp = new DateWrapper().toDateTimeStruct();
        }
        if (cancelReport.cancelledOrder.statusChange == StatusUpdateReasons.POSSIBLE_RESEND)
        {
            if (!processPossibleResend(cancelReport.cancelledOrder.orderStruct))
            {
                return;
            }
        }

        String smgr = getSessionManager().toString();
        String oid = getOrderIdString(cancelReport.cancelledOrder.orderStruct.orderId);
        StringBuilder calling = new StringBuilder(smgr.length()+oid.length()+100);
        calling.append("calling acceptOrderCanceledReport for ").append(smgr).append(oid)
               .append(" : queue = ").append(getProxyWrapper().getQueueSize())
               .append(" : max = ").append(getProxyWrapper().getMaxQueueSize())
               .append(" : statusChange=").append(cancelReport.cancelledOrder.statusChange);
        Log.information(this, calling.toString());

        ((OrderStatusV2ConsumerInterceptor)interceptor).acceptOrderCanceledReport(cancelReport, getProxyWrapper().getQueueSize());

        calling.setLength(0);
        calling.append("finished calling acceptOrderCanceledReport for ").append(smgr).append(oid);
        Log.information(this, calling.toString());

        boolean ack = UserOrderServiceUtil.ackOrder(cancelReport.cancelledOrder.orderStruct,orderAckConstraints);

        if (ack && getGMDStatus())
        {
            orderTime[2].clockPoint = TransactionClockPoints.USER_ACK;
            orderTime[2].timestamp = new DateWrapper().toDateTimeStruct();
            OrderAcknowledgeStructV3 orderAck  = new OrderAcknowledgeStructV3(
                baseSessionManager.getUserId(),
                cancelReport.cancelledOrder.orderStruct.orderId,
                cancelReport.cancelledOrder.orderStruct.productKey,
                cancelReport.cancelledOrder.orderStruct.classKey,
                cancelReport.cancelledOrder.orderStruct.transactionSequenceNumber,
                orderTime
                );

            ((SessionManager)baseSessionManager).ackOrderStatusV3(orderAck);
        }
    }

    /**
     *
     */
    private void handleAcceptOrderBustReport(ChannelEvent event,
                                             BaseSessionManager baseSessionManager)
        throws  SystemException,
                CommunicationException,
                AuthorizationException,
                DataValidationException
    {
        String smgr = getSessionManager().toString();
        StringBuilder calling = new StringBuilder(smgr.length()+34);
        calling.append("calling acceptOrderBustReport for ").append(smgr);
        Log.information(this, calling.toString());

        TransactionClockPointStruct[] orderTime = null;
        if (getGMDStatus())
        {
            orderTime = getTransactionClockPointStructs();
            orderTime[0].clockPoint = TransactionClockPoints.CAS_RECEIVE;
            orderTime[0].timestamp = new DateWrapper().toDateTimeStruct();
            orderTime[1].clockPoint = TransactionClockPoints.CAS_SEND;
            orderTime[1].timestamp = new DateWrapper().toDateTimeStruct();
        }

        OrderBustReportStruct bustReport =
            (OrderBustReportStruct)event.getEventData();

        if (bustReport.bustedOrder.statusChange == StatusUpdateReasons.POSSIBLE_RESEND)
        {
            if (!processPossibleResend(bustReport.bustedOrder.orderStruct))
            {
                return;
            }
        }

        ((OrderStatusV2ConsumerInterceptor)interceptor).acceptOrderBustReport(bustReport, getProxyWrapper().getQueueSize());

        if (getGMDStatus())
        {
            orderTime[2].clockPoint = TransactionClockPoints.USER_ACK;
            orderTime[2].timestamp = new DateWrapper ().toDateTimeStruct();
            OrderAcknowledgeStructV3 orderAck = new OrderAcknowledgeStructV3(
                baseSessionManager.getUserId(),
                bustReport.bustedOrder.orderStruct.orderId,
                bustReport.bustedOrder.orderStruct.productKey,
                bustReport.bustedOrder.orderStruct.classKey,
                bustReport.bustedOrder.orderStruct.transactionSequenceNumber,
                orderTime
                );
            ((SessionManager)baseSessionManager).ackOrderStatusV3(orderAck);
        }
    }

    /**
     *
     */
    private void handleAcceptOrderBustReinstateReport(ChannelEvent event,
                                                      BaseSessionManager baseSessionManager)
        throws  SystemException,
                CommunicationException,
                AuthorizationException,
                DataValidationException
    {
        String smgr = getSessionManager().toString();
        StringBuilder calling = new StringBuilder(smgr.length()+43);
        calling.append("calling acceptOrderBustReinstateReport for ").append(smgr);
        Log.information(this, calling.toString());

        TransactionClockPointStruct[] orderTime = null;
        if (getGMDStatus())
        {
            orderTime = getTransactionClockPointStructs();
            orderTime[0].clockPoint = TransactionClockPoints.CAS_RECEIVE;
            orderTime[0].timestamp = new DateWrapper ().toDateTimeStruct();
            orderTime[1].clockPoint = TransactionClockPoints.CAS_SEND;
            orderTime[1].timestamp = new DateWrapper ().toDateTimeStruct();
        }
        OrderBustReinstateReportStruct reinstateReport =
            (OrderBustReinstateReportStruct)event.getEventData();

        if (reinstateReport.reinstatedOrder.statusChange == StatusUpdateReasons.POSSIBLE_RESEND)
        {
            if (!processPossibleResend(reinstateReport.reinstatedOrder.orderStruct))
            {
                return;
            }
        }

        ((OrderStatusV2ConsumerInterceptor)interceptor).acceptOrderBustReinstateReport(reinstateReport, getProxyWrapper().getQueueSize());

        if (getGMDStatus())
        {
            orderTime[2].clockPoint = TransactionClockPoints.USER_ACK;
            orderTime[2].timestamp = new DateWrapper ().toDateTimeStruct();
            OrderAcknowledgeStructV3 orderAck = new OrderAcknowledgeStructV3(
                baseSessionManager.getUserId(),
                reinstateReport.reinstatedOrder.orderStruct.orderId,
                reinstateReport.reinstatedOrder.orderStruct.productKey,
                reinstateReport.reinstatedOrder.orderStruct.classKey,
                reinstateReport.reinstatedOrder.orderStruct.transactionSequenceNumber,
                orderTime
                    );

            ((SessionManager)baseSessionManager).ackOrderStatusV3(orderAck);
        }
    }

    /**
     * 7/19/7 - NEW messages are no GMD from CSI-2 onwards. So, the ACKing is controlled by the 
     * disableAckNew property which is supposed to be set to true. Default also is true (disabled).
     */
    private void handleAcceptNewOrder(ChannelEvent event,
                                      BaseSessionManager baseSessionManager)
        throws  SystemException,
                CommunicationException,
                AuthorizationException,
                DataValidationException
    {
        OrderDetailStruct order = (OrderDetailStruct)event.getEventData();


        TransactionClockPointStruct[] orderTime = null;
        if (!disableNewAck())
        {
	        if (getGMDStatus())
	        {
	            orderTime = getTransactionClockPointStructs();
	            orderTime[0].clockPoint = TransactionClockPoints.CAS_RECEIVE;
	            orderTime[0].timestamp = new DateWrapper().toDateTimeStruct();
	            orderTime[1].clockPoint = TransactionClockPoints.CAS_SEND;
	            orderTime[1].timestamp = new DateWrapper().toDateTimeStruct();
	        }
	    }

        if (order.statusChange == StatusUpdateReasons.POSSIBLE_RESEND)
        {
            if (!processPossibleResend(order.orderStruct))
            {
                return;
            }
        }

        String smgr = getSessionManager().toString();
        String oid = getOrderIdString(order.orderStruct.orderId);
        StringBuilder calling = new StringBuilder(smgr.length()+oid.length()+90);
        calling.append("calling acceptNewOrder for ").append(smgr).append(oid)
               .append(" : queue = ").append(getProxyWrapper().getQueueSize())
               .append(" : max = ").append(getProxyWrapper().getMaxQueueSize())
               .append(" : statusChange=").append(order.statusChange);
        Log.information(this, calling.toString());

        ((OrderStatusV2ConsumerInterceptor)interceptor).acceptNewOrder(order, getProxyWrapper().getQueueSize());

        calling.setLength(0);
        calling.append("finished calling acceptNewOrder for ").append(smgr).append(oid);
        Log.information(this, calling.toString());

        if (!disableNewAck())
        {
	        boolean ack = UserOrderServiceUtil.ackOrder(order.orderStruct,orderAckConstraints);

	        if (ack && getGMDStatus())
	        {
	            orderTime[2].clockPoint = TransactionClockPoints.USER_ACK;
	            orderTime[2].timestamp = new DateWrapper().toDateTimeStruct();
	            OrderAcknowledgeStructV3 orderAck = new OrderAcknowledgeStructV3(
	                baseSessionManager.getUserId(),
	                order.orderStruct.orderId,
	                order.orderStruct.productKey,
	                order.orderStruct.classKey,
	                order.orderStruct.transactionSequenceNumber,
	                orderTime
	            );
	
	            ((SessionManager)baseSessionManager).ackOrderStatusV3(orderAck);
	        }
        }
    }

    /**
     *
     */
    private String getOrderIdString(OrderIdStruct orderId)
    {
        StringBuilder oid = new StringBuilder(orderId.executingOrGiveUpFirm.exchange.length()+orderId.executingOrGiveUpFirm.firmNumber.length()+45);
        oid.append(":oid=").append(orderId.executingOrGiveUpFirm.exchange)
           .append(":").append(orderId.executingOrGiveUpFirm.firmNumber)
           .append(":").append(orderId.branch).append(":").append(orderId.branchSequenceNumber)
           .append(": h=").append(orderId.highCboeId)
           .append(": l=").append(orderId.lowCboeId);
        return oid.toString();
    }

    /**
     *
     */
    private boolean processPossibleResend(OrderStruct orderStruct)
    {
        boolean resend = false;
        BaseOrderIdStructContainer  orderIndex  = OrderIdStructContainerFactory.createValidOrderIdStructContainer(orderStruct.orderId);
        Integer oldTransactionSequenceNumber = possibleResendOrderMap.get(orderIndex);
        if (oldTransactionSequenceNumber == null || oldTransactionSequenceNumber < orderStruct.transactionSequenceNumber)
        {
            possibleResendOrderMap.put(orderIndex, orderStruct.transactionSequenceNumber);
            resend = true;
        }
        return resend;
    }

    public String getMessageType()
    {
        return SupplierProxyMessageTypes.ORDER;
    }

    private TransactionClockPointStruct[] getTransactionClockPointStructs()
    {
        int CAS_CLOCK_POINT_SIZE = 3;
        TransactionClockPointStruct[] clockPoints = new TransactionClockPointStruct[CAS_CLOCK_POINT_SIZE];
        for (int i = 0; i < clockPoints.length; i++)
        {
            clockPoints[i] = new TransactionClockPointStruct();
        }
	return clockPoints;
    }


}
