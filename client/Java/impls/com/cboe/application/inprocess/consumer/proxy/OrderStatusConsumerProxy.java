package com.cboe.application.inprocess.consumer.proxy;

import com.cboe.application.inprocess.session.InProcessSessionManagerImpl;
import com.cboe.application.order.common.UserOrderServiceUtil;
import com.cboe.application.shared.IOrderAckConstraints;
import com.cboe.application.shared.ServicesHelper;
import com.cboe.application.supplier.proxy.OrderStatusCollectorProxy;
import com.cboe.domain.util.*;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiConstants.StatusUpdateReasons;
import com.cboe.idl.cmiOrder.*;
import com.cboe.idl.cmiProduct.ProductStruct;
import com.cboe.idl.constants.TransactionClockPoints;
import com.cboe.idl.order.OrderAcknowledgeStructV3;
import com.cboe.idl.util.TransactionClockPointStruct;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.ProductQueryServiceAdapter;
import com.cboe.interfaces.application.SessionManager;
import com.cboe.interfaces.application.inprocess.OrderStatusConsumer;
import com.cboe.interfaces.domain.BaseOrderIdStructContainer;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.channel.ListenerProxyQueueControl;

import java.util.Hashtable;
import java.util.Map;

import static com.cboe.application.order.common.UserOrderServiceUtil.isExpressOrder;

/**
 * @author Jing Chen
 */

public class OrderStatusConsumerProxy extends OrderStatusCollectorProxy {
    private ProductQueryServiceAdapter pqAdapter;
    private OrderStatusConsumer orderStatusConsumer;
    private static int DEFAULT_SIZE = 10;
    private ListenerProxyQueueControl proxyWrapper;
    private Map<BaseOrderIdStructContainer, Integer> possibleResendOrderMap = new Hashtable<BaseOrderIdStructContainer, Integer>(DEFAULT_SIZE);
    private IOrderAckConstraints orderAckConstraints = null;
    private BaseSessionManager baseSessionManager;

    public OrderStatusConsumerProxy(OrderStatusConsumer orderStatusConsumer,
                                       BaseSessionManager sessionManager) {
        super(null, sessionManager, orderStatusConsumer);
        this.orderStatusConsumer = orderStatusConsumer;
        this.baseSessionManager = sessionManager;
    }

    public OrderStatusConsumerProxy(OrderStatusConsumer orderStatusConsumer,
                                       BaseSessionManager sessionManager, IOrderAckConstraints orderAckConstraints) {
        this(orderStatusConsumer, sessionManager);
        this.orderAckConstraints = orderAckConstraints;
    }

//    private Hashtable getPossibleResendOrders()
//    {
//        if (possibleResendOrders == null)
//        {
//            possibleResendOrders = new Hashtable(DEFAULT_SIZE);
//        }
//        return possibleResendOrders;
//    }

 /*   public ListenerProxyQueueControl getProxyWrapper() {
        if (proxyWrapper == null) {
            proxyWrapper = this.getChannelAdapter().getProxyForDelegate(this);
        }
        return proxyWrapper;
    }*/

    private boolean processPossibleResend(OrderStruct orderStruct) {
        BaseOrderIdStructContainer  orderIndex  = OrderIdStructContainerFactory.createValidOrderIdStructContainer(orderStruct.orderId);
        boolean resend = false;
        Integer oldTransactionSequenceNumber = possibleResendOrderMap.get(orderIndex);
        if (oldTransactionSequenceNumber == null || oldTransactionSequenceNumber < orderStruct.transactionSequenceNumber) {
            possibleResendOrderMap.put(orderIndex, orderStruct.transactionSequenceNumber);
            resend = true;
        }
        return resend;
    }

    private String getOrderIdString(OrderIdStruct orderId) {
        StringBuilder oid = new StringBuilder(50);
        oid.append(":oid=").append(orderId.executingOrGiveUpFirm.exchange)
           .append(':').append(orderId.executingOrGiveUpFirm.firmNumber)
           .append(':').append(orderId.branch).append(':').append(orderId.branchSequenceNumber)
           .append(": h=").append(orderId.highCboeId).append(": l=").append(orderId.lowCboeId);
        return oid.toString();
    }

    public void channelUpdate(ChannelEvent event) {
        /*
        if (Log.isDebugOn())
        {
            Log.debug(this,"calling channelUpdate for " + getSessionManager());
        }

        BaseSessionManager baseSessionManager;

        if (event != null)
        {
            try
            {
                ChannelKey key = (ChannelKey) event.getChannel();
                baseSessionManager = getSessionManager();
                switch(key.channelType)
                {
                    case ChannelType.ORDER_ACCEPTED_BY_BOOK:
                    case ChannelType.ORDER_ACCEPTED_BY_BOOK_BY_FIRM:
                        processBookedOrder(event);
                        break;

                    case ChannelType.ORDER_UPDATE:
                    case ChannelType.ORDER_UPDATE_BY_FIRM:
                        processOrderUpdate(event);
                        break;

                    case ChannelType.ACCEPT_ORDERS:
                    case ChannelType.ACCEPT_ORDERS_BY_FIRM:
                        processOrderAccept(event);
                        break;

                    case ChannelType.ORDER_FILL_REPORT:
                    case ChannelType.ORDER_FILL_REPORT_BY_FIRM:
                    case ChannelType.ORDER_FILL_REPORT_BY_TRADING_FIRM:
                        processOrderFill(event,baseSessionManager);
                        break;

                    case ChannelType.CANCEL_REPORT:
                    case ChannelType.CANCEL_REPORT_BY_FIRM:
                        processOrderCancel(event,baseSessionManager);
                        break;

                    case ChannelType.ORDER_BUST_REPORT:
                    case ChannelType.ORDER_BUST_REPORT_BY_FIRM:
                    case ChannelType.ORDER_BUST_REPORT_BY_TRADING_FIRM:
                        processOrderBust(event,baseSessionManager);
                        break;

                    case ChannelType.ORDER_BUST_REINSTATE_REPORT:
                    case ChannelType.ORDER_BUST_REINSTATE_REPORT_BY_FIRM:
                    case ChannelType.ORDER_BUST_REINSTATE_REPORT_BY_TRADING_FIRM:
                        processOrderBustReinstate(event,baseSessionManager);
                        break;

                    case ChannelType.NEW_ORDER:
                    case ChannelType.NEW_ORDER_BY_FIRM:
                        processNewOrder(event,baseSessionManager);
                        break;

                    case ChannelType.ORDER_STATUS_UPDATE:
                        processOrderStatusUpdate(event);
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
        */
    }

    private void processDataValidationException(OrderStruct orderStruct, String text, short statusChange) {
        StringBuilder dve = new StringBuilder(250);
        dve.append("PQS DVE on (PITS 9894) productKey:").append(orderStruct.productKey)
           .append(" for ").append(getOrderIdString(orderStruct.orderId))
           .append(" corresFirm:").append(orderStruct.orderId.correspondentFirm)
           .append(" userId: ").append(orderStruct.userId).append(" statusChange:").append(statusChange)
           .append(" text: ").append(text);
        Log.alarm(this, dve.toString());

        orderStatusConsumer.acceptConsumerException(orderStruct, text, statusChange, 0);
    }

    private void processOrderStatusUpdate(OrderStruct orderStruct, short statusChange) {
        try {
            ProductStruct product = null;


        try {
            product = getProductQueryServiceAdapter().getProductByKey(orderStruct.productKey);

            // Call the proxied method passing the extracted OrderStruct from the EventChannelEvent.
            StringBuilder calling = new StringBuilder(150);
            calling.append("calling acceptOrderStatusUpdate for ").append(getSessionManager())
                   .append(getOrderIdString(orderStruct.orderId)).append(";statusChange=").append(statusChange);
            Log.information(this,calling.toString());
                orderStatusConsumer.acceptOrderStatusUpdate(orderStruct, product, statusChange, 0);
            calling.setLength(0);
            calling.append("finished calling acceptOrderStatusUpdate for ").append(getSessionManager())
                   .append(getOrderIdString(orderStruct.orderId));
            Log.information(this, calling.toString());
        } catch (DataValidationException dve) {
            Log.exception(this, "(PITS 9894) productKey:" + orderStruct.productKey, dve);
            processDataValidationException(orderStruct, "Status Update", statusChange);
        }
        } catch (Throwable th) {
            forceLogout();
        }
    }

    private void processNewOrder(ChannelEvent event, BaseSessionManager baseSessionManager) throws DataValidationException, SystemException, NotFoundException, AuthorizationException, CommunicationException {

        if (Log.isDebugOn()) {
            throw new RuntimeException("This method should never be called");
        }
        Log.alarm(this, "OrderStatusConsumerProxy.processNewOrder() should never be called.");
        /*
        OrderAcknowledgeStructV3 orderAck;
        ProductStruct product = null;
        short statusChange;
        OrderStruct orderStruct;

        GroupOrderStructContainer orderStructContainer;
        orderStructContainer = (GroupOrderStructContainer)event.getEventData();
        orderStruct = orderStructContainer.getOrderStruct();
        statusChange = orderStructContainer.getStatusChange();
        if (statusChange == StatusUpdateReasons.POSSIBLE_RESEND)
        {
            if (!processPossibleResend(orderStructContainer.getOrderStruct())){
                return;
            }
        }
        try {
            product = getProductQueryServiceAdapter().getProductByKey(orderStruct.productKey);

            // Call the proxied method passing the extracted OrderStruct from the EventChannelEvent.
            StringBuilder calling = new StringBuilder(150);
            calling.append("calling acceptNewOrder for ").append(getSessionManager())
                   .append(getOrderIdString(orderStruct.orderId))
                   .append(" : queue = ").append(getProxyWrapper().getQueueSize())
                   .append(" : max = ").append(getProxyWrapper().getMaxQueueSize())
                   .append(" : statusChange=").append(statusChange);
            Log.information(this, calling.toString());
            orderStatusConsumer.acceptNewOrder(orderStruct, product, statusChange, getProxyWrapper().getQueueSize());
            calling.setLength(0);
            calling.append("finished calling acceptNewOrder for ").append(getSessionManager())
                .append(getOrderIdString(orderStruct.orderId));
            Log.information(this, calling.toString());
        } catch (DataValidationException dve) {
            Log.exception(this, "(PITS 9894) productKey:" + orderStruct.productKey, dve);
            processDataValidationException(orderStruct, "New Order", statusChange);
        }

        if (!disableNewAck())
        {
	        // Disabling ACK for I-order NEW/Cancel for Stock session
	        boolean ack = UserOrderServiceUtil.ackOrder(orderStruct,orderAckConstraints);

	        if (ack )
	        {
	            TransactionClockPointStruct[] newOrder = new TransactionClockPointStruct[1];
	            newOrder[0] = new TransactionClockPointStruct();
	            newOrder[0].clockPoint = TransactionClockPoints.CAS_RECEIVE;
	            newOrder[0].timestamp = new DateWrapper ().toDateTimeStruct();
	            orderAck = new OrderAcknowledgeStructV3(baseSessionManager.getUserId()
	                                                         , orderStruct.orderId
	                                                         , orderStruct.productKey
	                                                         , orderStruct.classKey
	                                                         , orderStruct.transactionSequenceNumber
	                                                         , newOrder);
	            ((SessionManager)baseSessionManager).ackOrderStatusV3(orderAck);
	        }
        }
        */
    }

    public void processOrderBustReinstate(BustReinstateReportStruct bustReinstateReportStruct,
                                           OrderStruct orderStruct,
                                           short statusChange) {
        try {
            OrderAcknowledgeStructV3 orderAck;
            ProductStruct product = null;

        TransactionClockPointStruct[] reinstate = getTransactionClockPointStructs();

        reinstate[0].clockPoint = TransactionClockPoints.CAS_RECEIVE;
        reinstate[0].timestamp = new DateWrapper().toDateTimeStruct();
        reinstate[1].clockPoint = TransactionClockPoints.CAS_SEND;
        reinstate[1].timestamp = new DateWrapper().toDateTimeStruct();

            if (statusChange == StatusUpdateReasons.POSSIBLE_RESEND) {
                if (!processPossibleResend(orderStruct)) {
                return;
            }
        }
        try {
            product = getProductQueryServiceAdapter().getProductByKey(orderStruct.productKey);

            // Call the proxied method passing the extracted OrderStruct from the EventChannelEvent.
            StringBuilder calling = new StringBuilder(150);
            calling.append("calling acceptOrderBustReinstateReport for ").append(getSessionManager())
                   .append(";statusChange=").append(statusChange);
            Log.information(this, calling.toString());
                orderStatusConsumer.acceptOrderBustReinstateReport(orderStruct, product, statusChange, bustReinstateReportStruct, 0);
            calling.setLength(0);
            calling.append("finished calling acceptOrderBustReinstateReport for ").append(getSessionManager());
            Log.information(this, calling.toString());
        } catch (DataValidationException dve) {
            Log.exception(this, "(PITS 9894) productKey:" + orderStruct.productKey, dve);
            processDataValidationException(orderStruct, "Order Bust Reinstate", statusChange);
        }

        reinstate[2].clockPoint = TransactionClockPoints.USER_ACK;
        reinstate[2].timestamp = new DateWrapper ().toDateTimeStruct();
        orderAck = new OrderAcknowledgeStructV3( baseSessionManager.getUserId()
                                              ,orderStruct.orderId
                                              ,orderStruct.productKey
                                              ,orderStruct.classKey
                                              ,orderStruct.transactionSequenceNumber
                                              ,reinstate);
        ((SessionManager)baseSessionManager).ackOrderStatusV3(orderAck);
        } catch (Throwable th) {
            forceLogout();
        }

    }

    public void processOrderBust(BustReportStruct[] bustReportStruct,
                                           OrderStruct orderStruct,
                                           short statusChange) {
        try {
            OrderAcknowledgeStructV3 orderAck;
            ProductStruct product = null;


        TransactionClockPointStruct[] bustTime = getTransactionClockPointStructs();
        bustTime[0].clockPoint = TransactionClockPoints.CAS_RECEIVE;
            bustTime[0].timestamp = new DateWrapper().toDateTimeStruct();
        bustTime[1].clockPoint = TransactionClockPoints.CAS_SEND;
        bustTime[1].timestamp = new DateWrapper ().toDateTimeStruct();

        if (statusChange == StatusUpdateReasons.POSSIBLE_RESEND) {
            if (!processPossibleResend(orderStruct)){
                return;
            }
        }
        try {
            product = getProductQueryServiceAdapter().getProductByKey(orderStruct.productKey);

            // Call the proxied method passing the extracted OrderStruct from the EventChannelEvent.
            StringBuilder calling = new StringBuilder(150);
            calling.append("calling acceptOrderBustReport for ").append(getSessionManager())
                   .append(";statusChange=").append(statusChange);
            Log.information(this, calling.toString());
                orderStatusConsumer.acceptOrderBustReport(orderStruct, product, statusChange, bustReportStruct, 0);
            calling.setLength(0);
            calling.append("finished calling acceptOrderBustReport for ").append(getSessionManager());
            Log.information(this, calling.toString());
        } catch (DataValidationException dve) {
            Log.exception(this, "(PITS 9894) productKey:" + orderStruct.productKey, dve);
            processDataValidationException(orderStruct, "Order Bust", statusChange);
        }
        bustTime[2].clockPoint = TransactionClockPoints.USER_ACK;
        bustTime[2].timestamp = new DateWrapper ().toDateTimeStruct();
        orderAck = new OrderAcknowledgeStructV3(   baseSessionManager.getUserId(),
                                                 orderStruct.orderId,
                                                 orderStruct.productKey,
                                                 orderStruct.classKey,
                                                 orderStruct.transactionSequenceNumber,
                                                 bustTime);
        ((SessionManager)baseSessionManager).ackOrderStatusV3(orderAck);
        } catch (Throwable th) {
            forceLogout();
        }
    }

    //private void processOrderCancel(ChannelEvent event, BaseSessionManager baseSessionManager) throws DataValidationException, SystemException, NotFoundException, AuthorizationException, CommunicationException {

    public void processOrderCancel(CancelReportStruct[] cancelReports,
                                   OrderStruct orderStruct,
                                   short statusChange
    ) {
        try {
            OrderAcknowledgeStructV3 orderAck;
            ProductStruct product = null;
            TransactionClockPointStruct[] cancelTime = getTransactionClockPointStructs();
            cancelTime[0].clockPoint = TransactionClockPoints.CAS_RECEIVE;
            cancelTime[0].timestamp = new DateWrapper().toDateTimeStruct();
            cancelTime[1].clockPoint = TransactionClockPoints.CAS_SEND;
            cancelTime[1].timestamp = new DateWrapper ().toDateTimeStruct();


            if (statusChange == StatusUpdateReasons.POSSIBLE_RESEND) {
                if (!processPossibleResend(orderStruct)) {
                    return;
                }
            }
            boolean isCasGeneratedCancelReport=false;
            try {
                product = getProductQueryServiceAdapter().getProductByKey(orderStruct.productKey);

                for(CancelReportStruct report:cancelReports)
                {

                    if(UserOrderServiceUtil.isCasGeneratedCancellReport(report.orsId))
                    {
                        //Remove the value as it was assigned only to difference between server generated v/s CAS generated.
                        //don't break the for loop because there could be more ORSId to change.
                        report.orsId="";
                        isCasGeneratedCancelReport=true;
                    }
                }

                orderStatusConsumer.acceptOrderCanceledReport(orderStruct, product, statusChange, cancelReports, 0);


            } catch (DataValidationException dve) {
                Log.exception(this, "(PITS 9894) productKey:" + orderStruct.productKey, dve);
                processDataValidationException(orderStruct, "Order Cancel", statusChange);
            }
            if (isCasGeneratedCancelReport)
            {
                if (Log.isDebugOn())
                {
                    Log.debug("OrderStatusConsumerProxy: No Acking to Status Server for "+UserOrderServiceUtil.CAS_GENERATED_CANCEL_REPORT +" CancelReport");
                }
            }else
            {	
                boolean ack = UserOrderServiceUtil.ackOrder(orderStruct,orderAckConstraints);
                if (ack) {
                    cancelTime[2].clockPoint = TransactionClockPoints.USER_ACK;
                    cancelTime[2].timestamp = new DateWrapper ().toDateTimeStruct();
                    orderAck = new OrderAcknowledgeStructV3(   baseSessionManager.getUserId()
                                                     , orderStruct.orderId
                                                     , orderStruct.productKey
                                                     , orderStruct.classKey
                                                     , orderStruct.transactionSequenceNumber
                                                     , cancelTime);
                    ((SessionManager)baseSessionManager).ackOrderStatusV3(orderAck);
                }
            }
        } catch (Throwable th) {
            forceLogout();
        }
    }

    private void forceLogout() {
        try {
            InProcessSessionManagerImpl sessionMgr = (InProcessSessionManagerImpl) baseSessionManager;
            sessionMgr.acceptForcedLogout(sessionMgr.getSessionKey(), "Exception in FIXOrderStatusConsumerProxy");
        } catch (Exception e) {
            Log.exception("Exception getting sessionKey - should never happen", e);
        }
    }
    //private void processOrderFill(ChannelEvent event,BaseSessionManager baseSessionManager) throws DataValidationException, SystemException, NotFoundException, AuthorizationException, CommunicationException {

    public void processOrderFill(FilledReportStruct[] filledReports,
                                 OrderStruct orderStruct,
                                 short statusChange) {

        try {
            OrderAcknowledgeStructV3 orderAck;
            ProductStruct product = null;


        TransactionClockPointStruct[] fillTime = getTransactionClockPointStructs();
        fillTime[0].clockPoint = TransactionClockPoints.CAS_RECEIVE;
            fillTime[0].timestamp = new DateWrapper().toDateTimeStruct();
        fillTime[1].clockPoint = TransactionClockPoints.CAS_SEND;
        fillTime[1].timestamp = new DateWrapper ().toDateTimeStruct();


            if (statusChange == StatusUpdateReasons.POSSIBLE_RESEND) {
                if (!processPossibleResend(orderStruct)) {
                    return;
                }
            }
            try {
                product = getProductQueryServiceAdapter().getProductByKey(orderStruct.productKey);
                orderStatusConsumer.acceptOrderFilledReport(orderStruct, product, statusChange, filledReports, 0);
        } catch (DataValidationException dve) {
            Log.exception(this, "(PITS 9894) productKey:" + orderStruct.productKey, dve);
            processDataValidationException(orderStruct, "Order Fill", statusChange);
        }


        fillTime[2].clockPoint = TransactionClockPoints.CAS_SEND;
        fillTime[2].timestamp = new DateWrapper ().toDateTimeStruct();
        orderAck = new OrderAcknowledgeStructV3(   baseSessionManager.getUserId()
                                                     , orderStruct.orderId
                                                     , orderStruct.productKey
                                                     , orderStruct.classKey
                                                     , orderStruct.transactionSequenceNumber
                                                     , fillTime);
        ((SessionManager)baseSessionManager).ackOrderStatusV3(orderAck);
        } catch (Throwable th) {
            forceLogout();
        }
    }

    private void processOrderAccept(ChannelEvent event) throws DataValidationException, SystemException, NotFoundException, AuthorizationException, CommunicationException {
        ProductStruct product = null;
        GroupOrderStructSequenceContainer orderStructSequence = (GroupOrderStructSequenceContainer)event.getEventData();
        OrderStruct[] orderStructs = orderStructSequence.getOrderStructSequence();

        StringBuilder calling = new StringBuilder(180);
        calling.append("calling acceptOrderStatus for ").append(getSessionManager());
        Log.information(this,calling.toString());

        for (int i = 0; i < orderStructs.length; i++) {
            try {
                product = getProductQueryServiceAdapter().getProductByKey(orderStructs[i].productKey);
                
                // Call the proxied method passing the extracted OrderStruct from the EventChannelEvent.
                calling.setLength(0);
                calling.append("calling acceptOrderStatus in method processOrderAccept() for ").append(getSessionManager())
                       .append(getOrderIdString(orderStructs[i].orderId))
                        .append(" : queue = ").append(0)
                        .append(" : max = ").append(0);
                Log.information(this, calling.toString());
                orderStatusConsumer.acceptOrderStatus(orderStructs[i], product, StatusUpdateReasons.QUERY, 0);
                calling.setLength(0);
                calling.append("finished calling acceptOrderStatus in method processOrderAccept() for ").append(getSessionManager())
                       .append(getOrderIdString(orderStructs[i].orderId));
                Log.information(this, calling.toString());
            } catch (DataValidationException dve) {
                Log.exception(this, "(PITS 9894) productKey:" + orderStructs[i].productKey, dve);
                processDataValidationException(orderStructs[i], "Order Accept", StatusUpdateReasons.QUERY);
            }
        }
    }

    private void processOrderUpdate(OrderStruct orderStruct,
            short statusChange)
    {
        try {
            ProductStruct product = null;
        try {
            product = getProductQueryServiceAdapter().getProductByKey(orderStruct.productKey);

            // Call the proxied method passing the extracted OrderStruct from the EventChannelEvent.
            StringBuilder calling = new StringBuilder(190);
            calling.append("calling acceptOrderStatus in method processOrderUpdate() for ").append(getSessionManager())
                   .append(getOrderIdString(orderStruct.orderId))
                        .append(" : queue = ").append(0)
                        .append(" : max = ").append(0)
                   .append(" : statusChange=").append(statusChange);
            Log.information(this, calling.toString());
                orderStatusConsumer.acceptOrderStatus(orderStruct, product, StatusUpdateReasons.UPDATE, 0);
            calling.setLength(0);
            calling.append("finished calling acceptOrderStatus in method processOrderUpdate() for ").append(getSessionManager())
                   .append(getOrderIdString(orderStruct.orderId));
            Log.information(this, calling.toString());
        } catch (DataValidationException dve) {
            Log.exception(this, "(PITS 9894) productKey:" + orderStruct.productKey, dve);
            processDataValidationException(orderStruct, "Order Update", statusChange);
        }
        } catch (Throwable th) {
            forceLogout();
        }
    }

    private void processBookedOrder(OrderStruct orderStruct)  {
        try {
        ProductStruct product = null;
        try {
            product = getProductQueryServiceAdapter().getProductByKey(orderStruct.productKey);

            // Call the proxied method passing the extracted OrderStruct from the EventChannelEvent.
            StringBuilder calling = new StringBuilder(170);
            calling.append("calling acceptOrderStatus in method processBookedOrder() for ").append(getSessionManager())
                   .append(getOrderIdString(orderStruct.orderId))
                    .append(" : queue = ").append(0)
                    .append(" : max = ").append(0);
            Log.information(this, calling.toString());
            orderStatusConsumer.acceptOrderStatus(orderStruct, product, StatusUpdateReasons.BOOKED, 0);
            calling.setLength(0);
            calling.append("finished calling acceptOrderStatus in method processBookedOrder() for ").append(getSessionManager())
                   .append(getOrderIdString(orderStruct.orderId));
            Log.information(this, calling.toString());
        } catch (DataValidationException dve) {
            Log.exception(this, "(PITS 9894) productKey:" + orderStruct.productKey, dve);
            processDataValidationException(orderStruct, "Booked Order", StatusUpdateReasons.BOOKED);
        }
             } catch (Throwable th) {
            forceLogout();
        }
    }

    protected ProductQueryServiceAdapter getProductQueryServiceAdapter() {
        if (pqAdapter == null) {
            pqAdapter = ServicesHelper.getProductQueryServiceAdapter();
        }
        return pqAdapter;
    }

    private TransactionClockPointStruct[] getTransactionClockPointStructs() {
        int CAS_CLOCK_POINT_SIZE = 3;
        TransactionClockPointStruct[] clockPoints = new TransactionClockPointStruct[CAS_CLOCK_POINT_SIZE];
        for (int i = 0; i < clockPoints.length; i++) {
            clockPoints[i] = new TransactionClockPointStruct();
        }
	    return clockPoints;
    }

  
}
