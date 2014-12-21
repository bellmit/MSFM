package com.cboe.application.supplier.proxy;

import static com.cboe.application.order.common.UserOrderServiceUtil.*;
import com.cboe.application.order.common.UserOrderServiceUtil;
import com.cboe.application.shared.IOrderAckConstraints;
import com.cboe.application.supplier.OrderStatusSupplierFactory;
import com.cboe.domain.instrumentedChannel.supplier.proxy.InstrumentedGMDSupplierProxy;
import com.cboe.domain.util.DateWrapper;
import com.cboe.domain.util.OrderIdStructContainerFactory;
import com.cboe.idl.cmiCallback.CMIOrderStatusConsumer;
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
 * OrderStatusConsumerProxy serves as a proxy to the OrderStatusConsumer
 * object on the presentation side in com.cboe.presentation.consumer.  The
 * OrderStatusSupplier on the CAS uses this proxy object to communicate to
 * the GUI callback object.
 *
 * @see com.cboe.consumers.internalPresentation.OrderStatusConsumerImpl
 * @see com.cboe.idl.cmiCallback.CMIOrderStatusConsumer
 *
 * @author Derek T. Chambers-Boucher
 * @version 06/25/1999 
 */

public class OrderStatusConsumerProxy extends InstrumentedGMDSupplierProxy
{
    private static final int        DEFAULT_SIZE        = 10;
    private IOrderAckConstraints orderAckConstraints = null;
    private Map<BaseOrderIdStructContainer, Integer> possibleResendOrderMap = new Hashtable<BaseOrderIdStructContainer, Integer>(DEFAULT_SIZE);

    /**
     * OrderStatusConsumerProxy constructor.
     *
     * @param orderStatus a reference to the proxied implementation object.
     * @param sessionManager a reference to the SessionManager managing subscriptions for this proxy.
     */
    protected OrderStatusConsumerProxy(CMIOrderStatusConsumer orderStatus,
                                       BaseSessionManager sessionManager,
                                       boolean gmdProxy,
                                       GMDProxyHome home
                                       )
    {
        super(sessionManager, OrderStatusSupplierFactory.find(sessionManager), gmdProxy, home, orderStatus);
        interceptor = new OrderStatusConsumerInterceptor(orderStatus);
    }

    protected OrderStatusConsumerProxy(CMIOrderStatusConsumer orderStatus,
                                       BaseSessionManager sessionManager,
                                       boolean gmdProxy,
                                       GMDProxyHome home,
                                       IOrderAckConstraints orderAckConstraints)
    {
        this(orderStatus, sessionManager, gmdProxy, home);
        this.orderAckConstraints = orderAckConstraints;
    }

    private String getOrderIdString(OrderIdStruct orderId)
    {
        StringBuilder oid = new StringBuilder(orderId.executingOrGiveUpFirm.exchange.length()+orderId.executingOrGiveUpFirm.firmNumber.length()+50);
        oid.append(":oid=").append(orderId.executingOrGiveUpFirm.exchange)
           .append(":").append(orderId.executingOrGiveUpFirm.firmNumber)
           .append(":").append(orderId.branch).append(":").append(orderId.branchSequenceNumber)
           .append(": h=").append(orderId.highCboeId)
           .append(": l=").append(orderId.lowCboeId);
        return oid.toString();
    }

//    private Hashtable getPossibleResendOrders()
//    {
//        if (possibleResendOrders == null)
//        {
//            possibleResendOrders = new Hashtable(DEFAULT_SIZE);
//        }
//        return possibleResendOrders;
//    }


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

        BaseSessionManager      baseSessionManager;
        OrderAcknowledgeStructV3  orderAck;

        if (event != null)
        {
            try
            {
                ChannelKey key = (ChannelKey) event.getChannel();
                baseSessionManager = getSessionManager();
                String smgr = baseSessionManager.toString();
                String oid;
                StringBuilder calling;

                switch(key.channelType)
                {
                    case ChannelType.CB_ORDERS_FOR_PRODUCT:
                    case ChannelType.CB_ORDERS_BY_FIRM:
                    case ChannelType.CB_ALL_ORDERS:
                    case ChannelType.CB_ALL_ORDERS_FOR_TYPE:
                    case ChannelType.CB_ORDERS_BY_CLASS:
                    case ChannelType.CB_ORDERS_FOR_SESSION:
                    case ChannelType.CB_ORDER_STATUS_UPDATE:
                        calling = new StringBuilder(smgr.length()+30);
                        calling.append("calling acceptOrderStatus for ").append(smgr);
                        Log.information(this, calling.toString());

                        // Call the proxied method passing the extracted OrderDetailStruct[] from the EventChannelEvent.
                        ((OrderStatusConsumerInterceptor)interceptor).acceptOrderStatus((OrderDetailStruct[])event.getEventData());
                    break;

                    case ChannelType.CB_FILLED_REPORT:
                    case ChannelType.CB_FILLED_REPORT_BY_FIRM:
                    case ChannelType.CB_FILLED_REPORT_FOR_PRODUCT:
                    case ChannelType.CB_FILLED_REPORT_FOR_TYPE:
                    case ChannelType.CB_FILLED_REPORT_BY_CLASS:
                    case ChannelType.CB_FILLED_REPORT_FOR_SESSION:
                    case ChannelType.CB_ORDER_FILLED_REPORT_FOR_TRADING_FIRM:
                        OrderFilledReportStruct filledReport = (OrderFilledReportStruct)event.getEventData();
                        TransactionClockPointStruct[] clockPoints = null;
                        if (getGMDStatus())
                        {
                            clockPoints = getTransactionClockPointStructs();
                            clockPoints[0].clockPoint = TransactionClockPoints.CAS_RECEIVE;
                            clockPoints[0].timestamp = new DateWrapper ().toDateTimeStruct();
                            clockPoints[1].clockPoint = TransactionClockPoints.CAS_SEND;
                            clockPoints[1].timestamp = new DateWrapper ().toDateTimeStruct();
			            }
                        if (filledReport.filledOrder.statusChange == StatusUpdateReasons.POSSIBLE_RESEND) {
                            if (!processPossibleResend(filledReport.filledOrder.orderStruct)){
                                return;
                            }
                        }
                        oid = getOrderIdString(filledReport.filledOrder.orderStruct.orderId);
                        calling = new StringBuilder(smgr.length()+oid.length()+95);
                        calling.append("calling acceptOrderFilledReport for ").append(smgr).append(oid)
                               .append(" : queue = ").append(getProxyWrapper().getQueueSize())
                               .append(" : max = ").append(getProxyWrapper().getMaxQueueSize())
                               .append(": statusChange=").append(filledReport.filledOrder.statusChange);
                        Log.information(this, calling.toString());

                        // Call the proxied method passing the extracted QuoteFilledReportStruct from the EventChannelEvent.
                        ((OrderStatusConsumerInterceptor)interceptor).acceptOrderFilledReport(filledReport);
                        calling.setLength(0);
                        calling.append("finished calling acceptOrderFilledReport for ").append(smgr).append(oid);
                        Log.information(this, calling.toString());

                        if (getGMDStatus())
                        {
                            clockPoints[2].clockPoint = TransactionClockPoints.USER_ACK;
                            clockPoints[2].timestamp = new DateWrapper ().toDateTimeStruct();

                            orderAck = new OrderAcknowledgeStructV3(baseSessionManager.getUserId()
                                                                     , filledReport.filledOrder.orderStruct.orderId
                                                                     , filledReport.filledOrder.orderStruct.productKey
                                                                     , filledReport.filledOrder.orderStruct.classKey
                                                                     , filledReport.filledOrder.orderStruct.transactionSequenceNumber
                                                                     , clockPoints
                                                                     );
                            ((SessionManager)baseSessionManager).ackOrderStatusV3(orderAck);
                        }
                    break;

                    case ChannelType.CB_CANCELED_REPORT:
                    case ChannelType.CB_CANCELED_REPORT_BY_FIRM:
                    case ChannelType.CB_CANCELED_REPORT_FOR_PRODUCT:
                    case ChannelType.CB_CANCELED_REPORT_FOR_TYPE:
                    case ChannelType.CB_CANCELED_REPORT_BY_CLASS:
                    case ChannelType.CB_CANCELED_REPORT_FOR_SESSION:
                    {
                        OrderCancelReportStruct cancelReport = (OrderCancelReportStruct)event.getEventData();

                        TransactionClockPointStruct[] cancelTime = null;
                        if (getGMDStatus())
                        {
                            cancelTime = getTransactionClockPointStructs();
                            cancelTime[0].clockPoint = TransactionClockPoints.CAS_RECEIVE;
                            cancelTime[0].timestamp = new DateWrapper ().toDateTimeStruct();
                            cancelTime[1].clockPoint = TransactionClockPoints.CAS_SEND;
                            cancelTime[1].timestamp = new DateWrapper ().toDateTimeStruct();
                        }
                        if (cancelReport.cancelledOrder.statusChange == StatusUpdateReasons.POSSIBLE_RESEND) {
                            if (!processPossibleResend(cancelReport.cancelledOrder.orderStruct)){
                                return;
                            }
                        }
                        oid = getOrderIdString(cancelReport.cancelledOrder.orderStruct.orderId);
                        boolean isCasGeneratedCancelReport=false;
                        for(CancelReportStruct report:cancelReport.cancelReport)
                        {
                            if(UserOrderServiceUtil.isCasGeneratedCancellReport(report.orsId))
                            {
                                //Remove the value as it was assigned only to diff between server generated vs cas generated.
                                //dont break the for loop because there could be more orsid to change.
                                report.orsId="";
                                isCasGeneratedCancelReport=true;
                            }

                        }
                        calling = new StringBuilder(smgr.length()+oid.length()+95);
                        calling.append("calling acceptOrderCanceledReport for ").append(smgr).append(oid)
                               .append(" : queue = ").append(getProxyWrapper().getQueueSize())
                               .append(" : max = ").append(getProxyWrapper().getMaxQueueSize())
                               .append(" : statusChange = ").append(cancelReport.cancelledOrder.statusChange)
                               .append(" : cancelReason = ").append(cancelReport.cancelReport[0].cancelReason);
                        Log.information(this, calling.toString());
                        ((OrderStatusConsumerInterceptor)interceptor).acceptOrderCanceledReport(cancelReport);
                        calling.setLength(0);
                        calling.append("finished calling acceptOrderCanceledReport for ").append(smgr).append(oid);
                        Log.information(this, calling.toString());
                        
                        if (isCasGeneratedCancelReport)
                        {
                            if (Log.isDebugOn())
                            {
                                Log.debug("OrderStatusConsumerProxy: No Acking to Status Server for "+ UserOrderServiceUtil.CAS_GENERATED_CANCEL_REPORT +" CancelReport");
                                
                            }
                        
                        }
                        else
                        {	
	                        boolean ack = UserOrderServiceUtil.ackOrder(cancelReport.cancelledOrder.orderStruct,orderAckConstraints);
                            
                            if (ack && getGMDStatus())
                            {
                                cancelTime[2].clockPoint = TransactionClockPoints.USER_ACK;
                                cancelTime[2].timestamp = new DateWrapper ().toDateTimeStruct();
                                orderAck = new OrderAcknowledgeStructV3(   baseSessionManager.getUserId()
                                                                         , cancelReport.cancelledOrder.orderStruct.orderId
                                                                         , cancelReport.cancelledOrder.orderStruct.productKey
                                                                         , cancelReport.cancelledOrder.orderStruct.classKey
                                                                         , cancelReport.cancelledOrder.orderStruct.transactionSequenceNumber
                                                                         , cancelTime
                                                                         );
                                ((SessionManager)baseSessionManager).ackOrderStatusV3(orderAck);
                            }
                        }
                    }
                    break;

                    case ChannelType.CB_ORDER_BUST_REPORT:
                    case ChannelType.CB_ORDER_BUST_REPORT_BY_FIRM:
                    case ChannelType.CB_ORDER_BUST_REPORT_FOR_PRODUCT:
                    case ChannelType.CB_ORDER_BUST_REPORT_FOR_TYPE:
                    case ChannelType.CB_ORDER_BUST_REPORT_BY_CLASS:
                    case ChannelType.CB_ORDER_BUST_REPORT_FOR_SESSION:
                    case ChannelType.CB_ORDER_BUST_REPORT_FOR_TRADING_FIRM:
                        calling = new StringBuilder(smgr.length()+34);
                        calling.append("calling acceptOrderBustReport for ").append(smgr);
                        Log.information(this, calling.toString());

                        TransactionClockPointStruct[] bustTime = null;
                        if (getGMDStatus())
                        {
                            bustTime = getTransactionClockPointStructs();
                            bustTime[0].clockPoint = TransactionClockPoints.CAS_RECEIVE;
                            bustTime[0].timestamp = new DateWrapper ().toDateTimeStruct();
                            bustTime[1].clockPoint = TransactionClockPoints.CAS_SEND;
                            bustTime[1].timestamp = new DateWrapper ().toDateTimeStruct();
                        }
                        OrderBustReportStruct bustReport = (OrderBustReportStruct)event.getEventData();
                        if (bustReport.bustedOrder.statusChange == StatusUpdateReasons.POSSIBLE_RESEND) {
                            if (!processPossibleResend(bustReport.bustedOrder.orderStruct)){
                                return;
                            }
                        }
                        ((OrderStatusConsumerInterceptor)interceptor).acceptOrderBustReport(bustReport);

                        if (getGMDStatus())
                        {
                            bustTime[2].clockPoint = TransactionClockPoints.USER_ACK;
                            bustTime[2].timestamp = new DateWrapper ().toDateTimeStruct();
                            orderAck = new OrderAcknowledgeStructV3(   baseSessionManager.getUserId()
                                                                     , bustReport.bustedOrder.orderStruct.orderId
                                                                     , bustReport.bustedOrder.orderStruct.productKey
                                                                     , bustReport.bustedOrder.orderStruct.classKey
                                                                     , bustReport.bustedOrder.orderStruct.transactionSequenceNumber
                                                                     , bustTime
                                                                     );
                            ((SessionManager)baseSessionManager).ackOrderStatusV3(orderAck);
                        }
                    break;

                    case ChannelType.CB_ORDER_BUST_REINSTATE_REPORT:
                    case ChannelType.CB_ORDER_BUST_REINSTATE_REPORT_BY_FIRM:
                    case ChannelType.CB_ORDER_BUST_REINSTATE_REPORT_FOR_PRODUCT:
                    case ChannelType.CB_ORDER_BUST_REINSTATE_REPORT_FOR_TYPE:
                    case ChannelType.CB_ORDER_BUST_REINSTATE_REPORT_BY_CLASS:
                    case ChannelType.CB_ORDER_BUST_REINSTATE_REPORT_FOR_SESSION:
                    case ChannelType.CB_ORDER_BUST_REINSTATE_REPORT_FOR_TRADING_FIRM:
                        calling = new StringBuilder(smgr.length()+43);
                        calling.append("calling acceptOrderBustReinstateReport for ").append(smgr);
                        Log.information(this, calling.toString());

                        TransactionClockPointStruct[] reInstate = null;
                        if (getGMDStatus())
                        {
                            reInstate = getTransactionClockPointStructs();
                            reInstate[0].clockPoint = TransactionClockPoints.CAS_RECEIVE;
                            reInstate[0].timestamp = new DateWrapper().toDateTimeStruct();
                            reInstate[1].clockPoint = TransactionClockPoints.CAS_SEND;
                            reInstate[1].timestamp = new DateWrapper().toDateTimeStruct();
                        }
                        OrderBustReinstateReportStruct reinstateReport = (OrderBustReinstateReportStruct)event.getEventData();
                        if (reinstateReport.reinstatedOrder.statusChange == StatusUpdateReasons.POSSIBLE_RESEND) {
                            if (!processPossibleResend(reinstateReport.reinstatedOrder.orderStruct)){
                                return;
                            }
                        }
                        ((OrderStatusConsumerInterceptor)interceptor).acceptOrderBustReinstateReport(reinstateReport);

                        if (getGMDStatus())
                        {
                            reInstate[2].clockPoint = TransactionClockPoints.USER_ACK;
                            reInstate[2].timestamp = new DateWrapper().toDateTimeStruct();
                            orderAck = new OrderAcknowledgeStructV3(   baseSessionManager.getUserId()
                                                                     , reinstateReport.reinstatedOrder.orderStruct.orderId
                                                                     , reinstateReport.reinstatedOrder.orderStruct.productKey
                                                                     , reinstateReport.reinstatedOrder.orderStruct.classKey
                                                                     , reinstateReport.reinstatedOrder.orderStruct.transactionSequenceNumber
                                                                     , reInstate
                                                                     );
                            ((SessionManager)baseSessionManager).ackOrderStatusV3(orderAck);
                        }
                    break;

                    case ChannelType.CB_NEW_ORDER_REPORT:
                    case ChannelType.CB_NEW_ORDER_REPORT_BY_FIRM:
                    case ChannelType.CB_NEW_ORDER_REPORT_FOR_PRODUCT:
                    case ChannelType.CB_NEW_ORDER_REPORT_FOR_TYPE:
                    case ChannelType.CB_NEW_ORDER_REPORT_BY_CLASS:
                    case ChannelType.CB_NEW_ORDER_REPORT_FOR_SESSION:
                    {
                        OrderDetailStruct order = (OrderDetailStruct)event.getEventData();
                        if (order.statusChange == StatusUpdateReasons.POSSIBLE_RESEND) {
                            if (!processPossibleResend(order.orderStruct)){
                                return;
                            }
                        }
                        // NEW messages are no longer GMD and hence not ACKed, as of CSI2.
                        // Gijo - 7/19/7.

                        TransactionClockPointStruct[] newOrderClock = null;
				        if (!disableNewAck())
				        {
	                        if (getGMDStatus())
	                        {
	                            newOrderClock = getTransactionClockPointStructs();
	                            newOrderClock[0].clockPoint = TransactionClockPoints.CAS_RECEIVE;
	                            newOrderClock[0].timestamp = new DateWrapper ().toDateTimeStruct();
	                            newOrderClock[1].clockPoint = TransactionClockPoints.CAS_SEND;
	                            newOrderClock[1].timestamp = new DateWrapper ().toDateTimeStruct();
	                        }
				        }
                        oid = getOrderIdString(order.orderStruct.orderId);
                        calling = new StringBuilder(smgr.length()+oid.length()+90);
                        calling.append("calling acceptNewOrder for ").append(smgr).append(oid)
                               .append(" : queue = ").append(getProxyWrapper().getQueueSize())
                               .append(" : max = ").append(getProxyWrapper().getMaxQueueSize())
                               .append(": statusChange=").append(order.statusChange);
                        Log.information(this, calling.toString());
                        ((OrderStatusConsumerInterceptor)interceptor).acceptNewOrder(order);
                        calling.setLength(0);
                        calling.append("finished calling acceptNewOrder for ").append(smgr).append(oid);
                        Log.information(this, calling.toString());

                        if (!disableNewAck())
                        {
                            boolean ack = UserOrderServiceUtil.ackOrder(order.orderStruct,orderAckConstraints);

	                        if (ack && getGMDStatus())
	                        {
	                            newOrderClock[2].clockPoint = TransactionClockPoints.USER_ACK;
	                            newOrderClock[2].timestamp = new DateWrapper().toDateTimeStruct();
	                            orderAck = new OrderAcknowledgeStructV3(   baseSessionManager.getUserId()
	                                                                     , order.orderStruct.orderId
	                                                                     , order.orderStruct.productKey
	                                                                     , order.orderStruct.classKey
	                                                                     , order.orderStruct.transactionSequenceNumber
	                                                                     , newOrderClock
	                                                                     );
	                            ((SessionManager)baseSessionManager).ackOrderStatusV3(orderAck);
	                        }
                        }
                    }
                    break;

                    default:
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

    public String getMethodName(ChannelEvent event)
    {
        String method = "";

        ChannelKey key = (ChannelKey) event.getChannel();

        switch(key.channelType)
        {
            case ChannelType.CB_ORDERS_FOR_PRODUCT:
            case ChannelType.CB_ORDERS_BY_FIRM:
            case ChannelType.CB_ALL_ORDERS:
            case ChannelType.CB_ALL_ORDERS_FOR_TYPE:
            case ChannelType.CB_ORDERS_BY_CLASS:
                // Call the proxied method passing the extracted OrderDetailStruct[] from the EventChannelEvent.
                method = "acceptOrderStatus";
                break;

            case ChannelType.CB_FILLED_REPORT:
            case ChannelType.CB_FILLED_REPORT_BY_FIRM:
            case ChannelType.CB_FILLED_REPORT_FOR_PRODUCT:
            case ChannelType.CB_FILLED_REPORT_FOR_TYPE:
            case ChannelType.CB_FILLED_REPORT_BY_CLASS:
            case ChannelType.CB_ORDER_FILLED_REPORT_FOR_TRADING_FIRM:
                // Call the proxied method passing the extracted QuoteFilledReportStruct from the EventChannelEvent.
                method = "acceptOrderFilledReport";
                break;

            case ChannelType.CB_CANCELED_REPORT:
            case ChannelType.CB_CANCELED_REPORT_BY_FIRM:
            case ChannelType.CB_CANCELED_REPORT_FOR_PRODUCT:
            case ChannelType.CB_CANCELED_REPORT_FOR_TYPE:
            case ChannelType.CB_CANCELED_REPORT_BY_CLASS:
                method = "acceptOrderCanceledReport";
                break;

            case ChannelType.CB_ORDER_BUST_REPORT:
            case ChannelType.CB_ORDER_BUST_REPORT_BY_FIRM:
            case ChannelType.CB_ORDER_BUST_REPORT_FOR_PRODUCT:
            case ChannelType.CB_ORDER_BUST_REPORT_FOR_TYPE:
            case ChannelType.CB_ORDER_BUST_REPORT_BY_CLASS:
            case ChannelType.CB_ORDER_BUST_REPORT_FOR_TRADING_FIRM:
                    method = "acceptOrderBustReport";
                break;

            case ChannelType.CB_ORDER_BUST_REINSTATE_REPORT:
            case ChannelType.CB_ORDER_BUST_REINSTATE_REPORT_BY_FIRM:
            case ChannelType.CB_ORDER_BUST_REINSTATE_REPORT_FOR_PRODUCT:
            case ChannelType.CB_ORDER_BUST_REINSTATE_REPORT_FOR_TYPE:
            case ChannelType.CB_ORDER_BUST_REINSTATE_REPORT_BY_CLASS:
            case ChannelType.CB_ORDER_BUST_REINSTATE_REPORT_FOR_TRADING_FIRM:
                    method = "acceptOrderBustReinstateReport";

            default:
                break;
        }

        return method;
    }

    private boolean processPossibleResend(OrderStruct orderStruct)
    {
        BaseOrderIdStructContainer  orderIndex  = OrderIdStructContainerFactory.createValidOrderIdStructContainer(orderStruct.orderId);
        boolean resend = false;
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
