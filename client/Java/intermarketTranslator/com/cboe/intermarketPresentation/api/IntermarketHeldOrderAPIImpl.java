
package com.cboe.intermarketPresentation.api;

import com.cboe.interfaces.intermarketPresentation.api.IntermarketHeldOrderAPI;
import com.cboe.interfaces.intermarketPresentation.api.IntermarketAPI;
import com.cboe.interfaces.presentation.product.SessionProductClass;

import com.cboe.idl.cmiIntermarket.NBBOAgentSessionManager;
import com.cboe.idl.cmiIntermarket.IntermarketManualHandling;
import com.cboe.idl.cmiIntermarketMessages.*;
import com.cboe.idl.cmiOrder.OrderEntryStruct;
import com.cboe.idl.cmiUtil.CboeIdStruct;
import com.cboe.idl.cmiUtil.PriceStruct;
import com.cboe.idl.cmiOrder.OrderIdStruct;
import com.cboe.idl.cmiOrder.OrderStruct;

import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.TransactionFailedException;
import com.cboe.exceptions.NotAcceptedException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.util.ChannelKey;
import com.cboe.util.event.*;
import com.cboe.util.ChannelType;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.domain.util.SessionKeyContainer;


public class IntermarketHeldOrderAPIImpl implements IntermarketHeldOrderAPI
{
    // TODO: USE CACHES
    protected NBBOAgentSessionManager nbboAgentSessionManager;
    protected IntermarketManualHandling intermarketManualHandling;
    protected IntermarketOrderQueryCache intermarketOrderQueryCache;
    protected SessionProductClass registeredClass;
    public IntermarketHeldOrderAPIImpl(SessionProductClass registeredClass)
    {
        this.registeredClass = registeredClass;
        initializeIntermarketOrderCache();
    }
    public void initialize(NBBOAgentSessionManager sessionManager)
        throws Exception
    {
        nbboAgentSessionManager = sessionManager;
        intermarketManualHandling = nbboAgentSessionManager.getIntermarketManualHandling();
//        initializeIntermarketOrderCache();
    }

    protected void initializeIntermarketOrderCache() //throws Exception
    {
//        try {
            ChannelKey key;

            //Register the Caches directly with the IEC
            intermarketOrderQueryCache = new IntermarketOrderQueryCache();

            key = new ChannelKey(ChannelType.CB_HELD_ORDERS, new SessionKeyContainer(registeredClass.getTradingSessionName(), registeredClass.getClassKey()));
            EventChannelAdapterFactory.find().addChannelListener(EventChannelAdapterFactory.find(), intermarketOrderQueryCache, key);

//         } catch (Exception e) {
//            GUILoggerHome.find().exception(IntermarketAPI.INTERMARKET_TRANSLATOR_NAME+": initializeIntermarketOrderCache()","",e);
//            throw e;
//        }
    }

    public void acceptHeldOrderReroute(OrderIdStruct heldOrderId, String session, int productKey, boolean nbboProtectionFlag)
        throws SystemException,CommunicationException,AuthorizationException,
              DataValidationException,TransactionFailedException,NotAcceptedException
    {
        intermarketManualHandling.acceptHeldOrderReroute(heldOrderId,session,productKey,nbboProtectionFlag);
    }

    public void acceptHeldOrderByClassReroute(int classKey,String session,boolean nbboProtectionFlag)
        throws SystemException,CommunicationException,AuthorizationException,
              DataValidationException,TransactionFailedException,NotAcceptedException
    {
        intermarketManualHandling.acceptHeldOrderByClassReroute(classKey,session,nbboProtectionFlag);
    }

    public void acceptCancelResponse(OrderIdStruct orderId,CboeIdStruct cancelRequestId,String session,int productKey)
        throws SystemException,CommunicationException,AuthorizationException,
              NotAcceptedException,TransactionFailedException,DataValidationException
    {
        intermarketManualHandling.acceptCancelResponse(orderId,cancelRequestId,session,productKey);
    }

    public void acceptHeldOrderFill(OrderIdStruct heldOrderId,String session,OrderEntryStruct nbboAgentOrder)
        throws SystemException,CommunicationException,AuthorizationException,
              NotAcceptedException,TransactionFailedException,DataValidationException
    {
        intermarketManualHandling.acceptHeldOrderFill(heldOrderId,session,nbboAgentOrder);
    }

    public HeldOrderDetailStruct getHeldOrderById(String sessionName, int productKey, OrderIdStruct orderId)
        throws SystemException,CommunicationException,AuthorizationException,DataValidationException,NotFoundException
    {
        HeldOrderDetailStruct heldOrder = intermarketOrderQueryCache.getHeldOrderById(orderId);
        if ( heldOrder == null ) {
           heldOrder = intermarketManualHandling.getHeldOrderById(sessionName, productKey, orderId);
           intermarketOrderQueryCache.addOrder(heldOrder);
        }
        return heldOrder;
    }

    public HeldOrderDetailStruct[] getHeldOrdersByClassForSession(String session, int classKey)
    {
        HeldOrderDetailStruct[] heldOrders = intermarketOrderQueryCache.getOrdersForSessionClass(session, classKey);
        return heldOrders;
    }

    public void acceptSatisfactionOrderFill(
            String sessionName,
            OrderIdStruct satisfactionOrderId,
            OrderEntryStruct nbboAgentOrder,
            int crowdQuantity,
            boolean cancelRemaining,
            short satisfactionOrderdisposition)
            throws SystemException, CommunicationException, AuthorizationException,
            DataValidationException, TransactionFailedException, NotAcceptedException
    {
        intermarketManualHandling.acceptSatisfactionOrderFill(
                sessionName,
                satisfactionOrderId,
                nbboAgentOrder,
                crowdQuantity,
                cancelRemaining,
                satisfactionOrderdisposition
        );
    }

    public void acceptSatisfactionOrderInCrowdFill(
            String sessionName,
            int productKey,
            OrderIdStruct satisfactionOrderId,
            int crowdQuantity,
            boolean cancelRemaining,
            short satisfactionOrderdisposition)
            throws SystemException, CommunicationException, AuthorizationException,
            DataValidationException, TransactionFailedException, NotAcceptedException
    {
        intermarketManualHandling.acceptSatisfactionOrderInCrowdFill(
                sessionName,
                productKey,
                satisfactionOrderId,
                crowdQuantity,
                cancelRemaining,
                satisfactionOrderdisposition
        );
    }

    public void acceptSatisfactionOrderReject(
            String sessionName,
            int productKey,
            OrderIdStruct satisfactionOrderId,
            short activityReason
            )
            throws SystemException, CommunicationException, AuthorizationException,
            DataValidationException, TransactionFailedException, NotAcceptedException
    {
        intermarketManualHandling.acceptSatisfactionOrderReject(
                sessionName,
                productKey,
                satisfactionOrderId,
                activityReason
        );
    }

    public void acceptCustomerOrderSatisfy(
            String sessionName,
            OrderIdStruct satisfactionOrderId,
            OrderEntryStruct nbboAgentOrder
            )
            throws SystemException, CommunicationException, AuthorizationException,
            DataValidationException, TransactionFailedException, NotAcceptedException
    {
        intermarketManualHandling.acceptCustomerOrderSatisfy(
                sessionName,
                satisfactionOrderId,
                nbboAgentOrder
        );
    }

    public void acceptFillReject(
            String sessionName,
            int productKey,
            FillRejectRequestStruct fillRejectRequestStruct
            )
            throws SystemException, CommunicationException, AuthorizationException,
            DataValidationException, TransactionFailedException, NotAcceptedException
    {
        intermarketManualHandling.acceptFillReject(
                sessionName,
                productKey,
                fillRejectRequestStruct
        );
    }

//    public HeldOrderDetailStruct getHeldOrderById(
//            String sessionName,
//            int productKey,
//            OrderIdStruct orderId
//            )
//            throws SystemException, CommunicationException, AuthorizationException,
//            DataValidationException, NotFoundException
//    {
//        return intermarketManualHandling.getHeldOrderById(
//                sessionName,
//                productKey,
//                orderId);
//    }

    public OrderStruct[] getAssociatedOrders(
            String sessionName,
            int productKey,
            OrderIdStruct orderId
            )
            throws SystemException, CommunicationException, AuthorizationException,
            DataValidationException
    {
        return intermarketManualHandling.getAssociatedOrders(
                sessionName,
                productKey,
                orderId);
    }

    public OrderStruct[] getOrdersByOrderTypeAndClass(
            String sessionName,
            int classKey,
            String[] exchanges,
            char[] originTypes,
            short orderFlowDirection
            )
            throws SystemException, CommunicationException, AuthorizationException,
            DataValidationException
    {
        return intermarketManualHandling.getOrdersByOrderTypeAndClass(
                sessionName,
                classKey,
                exchanges,
                originTypes,
                orderFlowDirection);
    }

    public OrderStruct[] getOrdersByOrderTypeAndProduct(
            String sessionName,
            int productKey,
            String[] exchanges,
            char[] originTypes,
            short orderFlowDirection
            )
            throws SystemException, CommunicationException, AuthorizationException,
            DataValidationException
    {
        return intermarketManualHandling.getOrdersByOrderTypeAndProduct(
                sessionName,
                productKey,
                exchanges,
                originTypes,
                orderFlowDirection
                );

    }

    // ADMIN MESSAGES
    public void acceptPreOpeningIndication(
            String sessionName,
            String exchange,
            int productKey,
            PreOpeningIndicationPriceStruct preOpeningIndicationPriceStruct
            )
            throws SystemException, CommunicationException, AuthorizationException,
            DataValidationException, NotAcceptedException
    {
        intermarketManualHandling.acceptPreOpeningIndication(
                sessionName,
                exchange,
                productKey,
                preOpeningIndicationPriceStruct);
    }

    public void acceptPreOpeningResponse(
            String sessionName,
            String originatingExchange,
            String destinationExchange,
            int productKey,
            PreOpeningResponsePriceStruct[] preOpeningResponsePriceStructs
            )
            throws SystemException, CommunicationException, AuthorizationException,
            DataValidationException, NotAcceptedException
    {
        intermarketManualHandling.acceptPreOpeningResponse(
                sessionName,
				originatingExchange,
				destinationExchange,
                productKey,
                preOpeningResponsePriceStructs);
    }

    public void acceptAdminMessage(
            String sessionName,
            String exchange,
            int productKey,
            AdminStruct adminMessage
            )
            throws SystemException, CommunicationException, AuthorizationException,
            DataValidationException, NotAcceptedException
    {
        intermarketManualHandling.acceptAdminMessage(
                sessionName,
                exchange,
                productKey,
                adminMessage);
    }

    public boolean lockProduct(
            String sessionName,
            int productKey
            )
            throws SystemException, CommunicationException, AuthorizationException,
            DataValidationException, NotAcceptedException, NotFoundException
    {
        return intermarketManualHandling.lockProduct(sessionName, productKey);
    }

    public boolean unlockProduct(
            String sessionName,
            int productKey
            )
            throws SystemException, CommunicationException, AuthorizationException,
            DataValidationException, NotAcceptedException, NotFoundException
    {
        return intermarketManualHandling.unlockProduct(sessionName, productKey);
    }

    public boolean rerouteBookedOrderToHeldOrder(
            OrderIdStruct bookedOrderId,
            String sessionName,
            int productKey,
            boolean nbboProtectionFlag
            )
            throws SystemException, CommunicationException, AuthorizationException,
            DataValidationException, TransactionFailedException, NotAcceptedException
    {
        return intermarketManualHandling.rerouteBookedOrderToHeldOrder(
                bookedOrderId,
                sessionName,
                productKey,
                nbboProtectionFlag);
    }

    public void acceptOpeningPriceForProduct(
            PriceStruct openingPrice,
            String sessionName,
            int productKey
            )
            throws SystemException, CommunicationException, AuthorizationException,
            DataValidationException, TransactionFailedException, NotAcceptedException
    {
        intermarketManualHandling.acceptOpeningPriceForProduct(
                openingPrice,
                sessionName,
                productKey);
    }
}
