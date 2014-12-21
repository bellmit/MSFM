//
// -----------------------------------------------------------------------------------
// Source file: OrderManagementTerminalAPI.java
//
// PACKAGE: com.cboe.interfaces.presentation.api
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.api;

import org.omg.CORBA.UserException;

import com.cboe.idl.cmiOrder.CancelRequestStruct;
import com.cboe.idl.cmiOrder.OrderIdStruct;
import com.cboe.idl.cmiOrder.OrderStruct;
import com.cboe.idl.cmiProduct.ProductKeysStruct;
import com.cboe.idl.cmiTraderActivity.ActivityHistoryStruct;
import com.cboe.idl.cmiUtil.CboeIdStruct;
import com.cboe.idl.cmiUtil.DateTimeStruct;
import com.cboe.idl.marketData.InternalCurrentMarketStruct;
import com.cboe.idl.omt.OrderManagementService;
import com.cboe.idl.order.ManualFillStruct;
import com.cboe.idl.order.OrderQueryResultStruct;
import com.cboe.idl.util.ServerResponseStructV2;

import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotAcceptedException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.TransactionFailedException;

import com.cboe.interfaces.presentation.omt.OrderCancelMessageElement;
import com.cboe.interfaces.presentation.omt.OrderCancelReplaceMessageElement;

import com.cboe.util.event.EventChannelListener;

/**
 * Internal interface CBOE usage only.
 *
 * OrderManagementTerminalAPI sends a synchronous call to the CAS.
 * The CAS will then send a query to the FE (Front-End) which will 
 * locate the correct BC (Business Class) and will send back the reply via the CAS to the client for
 * a specific information.
 *
 */
public interface OrderManagementTerminalAPI
{
    String ALLOW_OMT_ACCESS_PROPERTY_NAME = "AllowOMTAPIAccess";    // TODO do we need this ??

    void subscribeOMT(int[] channelTypes, EventChannelListener clientListener);

    void unsubscribeOMT(int[] channelTypes, EventChannelListener clientListener);

    OrderStruct getOrderByIdV2(OrderIdStruct orderId)
            throws SystemException, CommunicationException, DataValidationException,
            NotFoundException, AuthorizationException;

    OrderStruct getOrderByORSID(String orsid)
            throws SystemException, CommunicationException, DataValidationException,
            NotFoundException, AuthorizationException;

    OrderQueryResultStruct getOrdersByClassAndTime(int i, DateTimeStruct dateTimeStruct,
                                                   short i1)
            throws SystemException, CommunicationException, DataValidationException,
            AuthorizationException, OrderQueryThrottleException;

    OrderQueryResultStruct getOrdersByProductAndTime(int i, DateTimeStruct dateTimeStruct, short i1)
            throws SystemException, CommunicationException, DataValidationException,
            AuthorizationException, OrderQueryThrottleException;

    ServerResponseStructV2[] getOrdersByLocation(String location, String transactionId, short[] filters)
            throws SystemException, CommunicationException, DataValidationException, NotAcceptedException,
            AuthorizationException, OrderQueryThrottleException;

    ServerResponseStructV2[] getOrdersByLocationType(short[] locationTypes, String transactionId)
            throws SystemException, CommunicationException, DataValidationException,
            AuthorizationException, OrderQueryThrottleException;

    void acceptDirectRoute(String routingDestination, OrderStruct anOrder, short rerouteFlag)
            throws UserException;

    void acceptManualCancel(CancelRequestStruct cancelRequestStruct,
                            ProductKeysStruct productKeysStruct, long cancelReqId) throws UserException;

    void acceptManualCancel(OrderCancelMessageElement anElement) throws UserException;

    void acceptManualCancelReplace(CancelRequestStruct cancelRequestStruct,
                                   ProductKeysStruct productKeysStruct, OrderStruct anOrder, long cancelReqId)
            throws UserException;

    void acceptManualCancelReplace(OrderCancelReplaceMessageElement anElement) throws
            IllegalStateException, UserException;

    void subscribeOrdersForManualHandling()
            throws SystemException, CommunicationException, AuthorizationException,
            DataValidationException;

    void markMessageAsRead(String sessionName, int productKey, long messageId) throws UserException;

    void acceptMessageRoute(String sessionName, int productKey, String location, long messageId)
            throws UserException;

    void acceptManualUpdate(int remainingQuantity, OrderStruct anOrder)
            throws SystemException, CommunicationException, DataValidationException,
            TransactionFailedException, NotAcceptedException, AuthorizationException;

    void acceptManualFillReport(short activityType, ManualFillStruct[] fillReports, int productKey,
                                int transactionID) throws UserException;

    ActivityHistoryStruct queryOrderHistoryV2(String sessionName, int productKey,
                                              OrderIdStruct orderId) throws SystemException, CommunicationException,
            AuthorizationException, DataValidationException;

    /**
     * Get the InternalCurrentMarketStruct for a specific product.
     * @param session product to query.
     * @param productKey to query.
     * @return the InternalCurrentMarketStruct for the specified session and productKey.
     * @throws SystemException 
     * @throws AuthorizationException
     * @throws CommunicationException sent by CAS if communication issue occured.
     * @throws DataValidationException sent by server when the value passed aren't valid.
     * @throws NotFoundException sent by server when data wasn't found.
     */
    InternalCurrentMarketStruct getCurrentMarketQuoteForProducts(String session, int productKey)
            throws SystemException, AuthorizationException, CommunicationException, DataValidationException, NotFoundException;

    com.cboe.idl.trade.TradeReportStructV2 getTradeReportV2ByTradeId(CboeIdStruct tradeId, boolean activeOnly) throws
            SystemException, CommunicationException, AuthorizationException,
            DataValidationException, NotFoundException;

    void initializeService(OrderManagementService omtService);
}
