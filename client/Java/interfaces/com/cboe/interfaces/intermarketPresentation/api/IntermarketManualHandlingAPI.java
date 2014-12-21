//
// ------------------------------------------------------------------------
// FILE: IntermarketManualHandlingAPI.java
//
// PACKAGE: com.cboe.interfaces.intermarketPresentation.api
//
// ------------------------------------------------------------------------
// Copyright (c) 1999-2003 The Chicago Board Options Exchange.  All Rights Reserved.
//
// ------------------------------------------------------------------------
//
package com.cboe.interfaces.intermarketPresentation.api;

import com.cboe.idl.cmiOrder.OrderIdStruct;
import com.cboe.idl.cmiOrder.OrderEntryStruct;
import com.cboe.idl.cmiOrder.OrderStruct;
import com.cboe.idl.cmiUtil.CboeIdStruct;
import com.cboe.idl.cmiUtil.PriceStruct;
import com.cboe.idl.cmiIntermarketMessages.*;
import com.cboe.exceptions.*;

public interface IntermarketManualHandlingAPI
{
    void acceptHeldOrderReroute(
            OrderIdStruct heldOrderId,
            String session,
            int productKey,
            boolean nbboProtectionFlag)
            throws SystemException, CommunicationException, AuthorizationException,
            DataValidationException, TransactionFailedException, NotAcceptedException;

    void acceptHeldOrderByClassReroute(
            int classKey,
            String session,
            boolean nbboProtectionFlag)
            throws SystemException, CommunicationException, AuthorizationException,
            DataValidationException, TransactionFailedException, NotAcceptedException;

    void acceptCancelResponse(
            OrderIdStruct orderId,
            CboeIdStruct cancelRequestId,
            String session,
            int productKey)
            throws SystemException, CommunicationException, AuthorizationException,
            NotAcceptedException, TransactionFailedException, DataValidationException;

    void acceptHeldOrderFill(
            OrderIdStruct heldOrderId,
            String session,
            OrderEntryStruct nbboAgentOrder)
            throws SystemException, CommunicationException, AuthorizationException,
            NotAcceptedException, TransactionFailedException, DataValidationException;

    void acceptSatisfactionOrderFill(
            String sessionName,
            OrderIdStruct satisfactionOrderId,
            OrderEntryStruct nbboAgentOrder,
            int crowdQuantity,
            boolean cancelRemaining,
            short satisfactionOrderdisposition)
            throws SystemException, CommunicationException, AuthorizationException,
            DataValidationException, TransactionFailedException, NotAcceptedException;

    void acceptSatisfactionOrderInCrowdFill(
            String sessionName,
            int productKey,
            OrderIdStruct satisfactionOrderId,
            int crowdQuantity,
            boolean cancelRemaining,
            short satisfactionOrderdisposition)
            throws SystemException, CommunicationException, AuthorizationException,
            DataValidationException, TransactionFailedException, NotAcceptedException;

    void acceptSatisfactionOrderReject(
            String sessionName,
            int productKey,
            OrderIdStruct satisfactionOrderId,
            short activityReason)
            throws SystemException, CommunicationException, AuthorizationException,
            DataValidationException, TransactionFailedException, NotAcceptedException;

    void acceptCustomerOrderSatisfy(
            String sessionName,
            OrderIdStruct satisfactionOrderId,
            OrderEntryStruct nbboAgentOrder)
            throws SystemException, CommunicationException, AuthorizationException,
            DataValidationException, TransactionFailedException, NotAcceptedException;

    void acceptFillReject(
            String sessionName,
            int productKey,
            FillRejectRequestStruct fillRejectRequestStruct)
            throws SystemException, CommunicationException, AuthorizationException,
            DataValidationException, TransactionFailedException, NotAcceptedException;

    HeldOrderDetailStruct getHeldOrderById(
            String sessionName,
            int productKey,
            OrderIdStruct orderId)
            throws SystemException, CommunicationException, AuthorizationException,
            DataValidationException, NotFoundException;

    OrderStruct[] getAssociatedOrders(
            String sessionName,
            int productKey,
            OrderIdStruct orderId)
            throws SystemException, CommunicationException, AuthorizationException,
            DataValidationException;

    OrderStruct[] getOrdersByOrderTypeAndClass(
            String sessionName,
            int classKey,
            String[] exchanges,
            char[] originTypes,
            short orderFlowDirection)
            throws SystemException, CommunicationException, AuthorizationException,
            DataValidationException;

    OrderStruct[] getOrdersByOrderTypeAndProduct(
            String sessionName,
            int productKey,
            String[] exchanges,
            char[] originTypes,
            short orderFlowDirection)
            throws SystemException, CommunicationException, AuthorizationException,
            DataValidationException;

    void acceptPreOpeningIndication(
            String sessionName,
            String exchange,
            int productKey,
            PreOpeningIndicationPriceStruct preOpeningIndicationPriceStruct)
            throws SystemException, CommunicationException, AuthorizationException,
            DataValidationException, NotAcceptedException;

    void acceptPreOpeningResponse(
            String sessionName,
            String originatingExchange,
            String destinationExchange,
            int productKey,
            PreOpeningResponsePriceStruct[] preOpeningResponsePriceStructs)
            throws SystemException, CommunicationException, AuthorizationException,
            DataValidationException, NotAcceptedException;

    void acceptAdminMessage(
            String sessionName,
            String exchange,
            int productKey,
            AdminStruct adminMessage)
            throws SystemException, CommunicationException, AuthorizationException,
            DataValidationException, NotAcceptedException;

    boolean lockProduct(
            String sessionName,
            int productKey)
            throws SystemException, CommunicationException, AuthorizationException,
            DataValidationException, NotAcceptedException, NotFoundException;

    boolean unlockProduct(
            String sessionName,
            int productKey)
            throws SystemException, CommunicationException, AuthorizationException,
            DataValidationException, NotAcceptedException, NotFoundException;

    boolean rerouteBookedOrderToHeldOrder(
            OrderIdStruct bookedOrderId,
            String sessionName,
            int productKey,
            boolean nbboProtectionFlag)
            throws SystemException, CommunicationException, AuthorizationException,
            DataValidationException, TransactionFailedException, NotAcceptedException;

    void acceptOpeningPriceForProduct(
            PriceStruct openingPrice,
            String sessionName,
            int productKey)
            throws SystemException, CommunicationException, AuthorizationException,
            DataValidationException, TransactionFailedException, NotAcceptedException;

    // Translator extensions
    HeldOrderDetailStruct[] getHeldOrdersByClassForSession(
            String session,
            int classKey);

}
