//
// -----------------------------------------------------------------------------------
// Source file: OrderManagementTerminalAPIImpl.java
//
// PACKAGE: com.cboe.presentation.api
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.api;

import java.util.*;

import org.omg.CORBA.UserException;

import com.cboe.idl.cmiOrder.CancelRequestStruct;
import com.cboe.idl.cmiOrder.OrderIdStruct;
import com.cboe.idl.cmiOrder.OrderStruct;
import com.cboe.idl.cmiProduct.ProductKeysStruct;
import com.cboe.idl.cmiTraderActivity.ActivityHistoryStruct;
import com.cboe.idl.cmiUtil.CboeIdStruct;
import com.cboe.idl.cmiUtil.DateTimeStruct;
import com.cboe.idl.marketData.InternalCurrentMarketStruct;
import com.cboe.idl.ohsConsumers.OrderRoutingConsumer;
import com.cboe.idl.omt.OrderManagementService;
import com.cboe.idl.order.ManualFillStruct;
import com.cboe.idl.order.OrderQueryResultStruct;
import com.cboe.idl.util.ServerResponseStructV2;

import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.ExceptionDetails;
import com.cboe.exceptions.NotAcceptedException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.TransactionFailedException;

import com.cboe.interfaces.presentation.api.OrderManagementTerminalAPI;
import com.cboe.interfaces.presentation.api.OrderQueryThrottleException;
import com.cboe.interfaces.presentation.omt.OrderCancelMessageElement;
import com.cboe.interfaces.presentation.omt.OrderCancelReplaceMessageElement;
import com.cboe.interfaces.presentation.permissionMatrix.Permission;
import com.cboe.interfaces.presentation.permissionMatrix.UserPermissionMatrix;

import com.cboe.util.ChannelKey;
import com.cboe.util.event.EventChannelAdapter;
import com.cboe.util.event.EventChannelAdapterFactory;
import com.cboe.util.event.EventChannelListener;

import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.properties.AppPropertiesFileFactory;
import com.cboe.presentation.userSession.UserSessionFactory;

import com.cboe.consumers.callback.OrderRoutingConsumerFactory;

public class OrderManagementTerminalAPIImpl implements OrderManagementTerminalAPI
{
    @SuppressWarnings({"FieldAccessedSynchronizedAndUnsynchronized"})
    protected OrderManagementService omtService;

    @SuppressWarnings({"FieldAccessedSynchronizedAndUnsynchronized"})
    private EventChannelAdapter eventChannel;

    private OrderRoutingConsumer orderManagementConsumer;

    private boolean alreadySubscribed = false;
    private final Object subscribeLockObject = new Object();

    private static final String PROPERTIES_SECTION_NAME = "Timers";
    private static final String ORDER_QUERY_LEVEL_KEY_NAME = "OMTOrderQueryDelayMillis";

    private static final int MIN_QUERY_DELAY = 0;
    private static final int MAX_QUERY_DELAY = 10000;
    private static final int DEFAULT_QUERY_DELAY = 3000;
    private Timer timer = null;
    private int queryDelay = -1;
    private int seconds = 0;
    private boolean timerRunning = false;

    protected OrderManagementTerminalAPIImpl()
    {
        eventChannel = EventChannelAdapterFactory.find();
    }

    protected OrderManagementTerminalAPIImpl(OrderManagementService omtService)
    {
        this();
        initializeService(omtService);
    }

    public void initializeService(OrderManagementService omtService)
    {
        this.omtService = omtService;
    }

    public void subscribeOrdersForManualHandling()
            throws SystemException, CommunicationException, AuthorizationException,
            DataValidationException
    {
        GUILoggerHome.find().debug(getClass().getName() + ": subscribeOrdersForManualHandling",
                GUILoggerBusinessProperty.OMT);

        if (isUserAllowed())
        {
            verifyService();

            synchronized (subscribeLockObject)
            {
                if (!alreadySubscribed)
                {
                    orderManagementConsumer = OrderRoutingConsumerFactory.create(eventChannel);
                    omtService.subscribeOrdersForManualHandling(orderManagementConsumer, true);

                    omtService.publishAllMessagesForDestination();

                    alreadySubscribed = true;
                }
            }
        }
    }

    public void subscribeOMT(int[] channelTypes, EventChannelListener clientListener)
    {
        GUILoggerHome.find().debug(getClass().getName() + ": subscribeOMT",
                GUILoggerBusinessProperty.OMT, clientListener);

        for (int channelType : channelTypes)
        {
            addListener(clientListener, channelType);
        }
    }

    public void unsubscribeOMT(int[] channelTypes, EventChannelListener clientListener)
    {
        GUILoggerHome.find().debug(getClass().getName() + ": unsubscribeOMT",
                GUILoggerBusinessProperty.OMT, clientListener);

        for (int channelType : channelTypes)
        {
            removeListener(clientListener, channelType);
        }
    }

    public OrderStruct getOrderByIdV2(OrderIdStruct orderId)
            throws SystemException, CommunicationException, DataValidationException,
            NotFoundException, AuthorizationException
    {
        logTransaction("getOrderByIdV2", orderId);

        verifyService();
        return omtService.getOrderByIdV2(orderId);
    }


    public OrderStruct getOrderByORSID(String orsid)
            throws SystemException, CommunicationException, DataValidationException,
            NotFoundException, AuthorizationException
    {
        GUILoggerHome.find().debug(getClass().getName() + ": getOrderByORSID",
                GUILoggerBusinessProperty.OMT, orsid);

        verifyService();
        return omtService.getOrderByORSID(orsid);
    }

    public OrderQueryResultStruct getOrdersByClassAndTime(int key, DateTimeStruct dateTimeStruct,
                                                          short dir)
            throws SystemException, CommunicationException, DataValidationException,
            AuthorizationException, OrderQueryThrottleException
    {
        if (GUILoggerHome.find().isDebugOn() &&
                GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.OMT))
        {
            Object[] parms = new Object[3];
            parms[0] = key;
            parms[1] = dateTimeStruct;
            parms[2] = dir;

            GUILoggerHome.find().debug(getClass().getName() + ": getOrdersByClassAndTime:entry",
                    GUILoggerBusinessProperty.OMT, parms);
        }

        verifyService();
        verifyTiming();
        OrderQueryResultStruct result = omtService.getOrdersByClassAndTime(key, dateTimeStruct, dir);

        if (GUILoggerHome.find().isDebugOn() &&
                GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.OMT))
        {
            GUILoggerHome.find().debug(getClass().getName() + ": getOrdersByClassAndTime:exit",
                    GUILoggerBusinessProperty.OMT, result);
        }

        return result;
    }

    public OrderQueryResultStruct getOrdersByProductAndTime(int key,
                                                            DateTimeStruct dateTimeStruct,
                                                            short dir)
            throws SystemException, CommunicationException, DataValidationException,
            AuthorizationException, OrderQueryThrottleException
    {
        if (GUILoggerHome.find().isDebugOn() &&
                GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.OMT))
        {
            Object[] parms = new Object[3];
            parms[0] = key;
            parms[1] = dateTimeStruct;
            parms[2] = dir;

            GUILoggerHome.find().debug(getClass().getName() + ": getOrdersByProductAndTime:entry",
                    GUILoggerBusinessProperty.OMT, parms);
        }

        verifyService();
        verifyTiming();
        OrderQueryResultStruct result =
                omtService.getOrdersByProductAndTime(key, dateTimeStruct, dir);

        if (GUILoggerHome.find().isDebugOn() &&
                GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.OMT))
        {
            GUILoggerHome.find().debug(getClass().getName() + ": getOrdersByProductAndTime:exit",
                    GUILoggerBusinessProperty.OMT, result);
        }

        return result;
    }

    public ServerResponseStructV2[] getOrdersByLocation(String location, String transactionId,
                                                        short[] filters)
            throws SystemException, CommunicationException, DataValidationException, NotAcceptedException,
            AuthorizationException, OrderQueryThrottleException
    {
        if (GUILoggerHome.find().isDebugOn() &&
                GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.OMT))
        {
            Object[] parms = new Object[3];
            parms[0] = location;
            parms[1] = transactionId;
            parms[2] = filters;
            GUILoggerHome.find().debug(getClass().getName() + ": getOrdersByLocation:entry",
                    GUILoggerBusinessProperty.OMT, parms);
        }

        verifyService();
        verifyTiming();

        ServerResponseStructV2[] result =
                omtService.getOrdersByLocation(location, transactionId, filters);

        if (GUILoggerHome.find().isDebugOn() &&
                GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.OMT))
        {
            GUILoggerHome.find().debug(getClass().getName() + ": getOrdersByLocation:exit",
                    GUILoggerBusinessProperty.OMT, result);
        }
        return result;
    }

    public ServerResponseStructV2[] getOrdersByLocationType(short[] locationTypes,
                                                            String transactionId)
            throws SystemException, CommunicationException, DataValidationException,
            AuthorizationException, OrderQueryThrottleException
    {
        if (GUILoggerHome.find().isDebugOn() &&
                GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.OMT))
        {
            Object[] parms = new Object[2];
            parms[0] = locationTypes;
            parms[1] = transactionId;
            GUILoggerHome.find().debug(getClass().getName() + ": getOrdersByLocationType:entry",
                    GUILoggerBusinessProperty.OMT, parms);
        }

        verifyService();
        verifyTiming();

        ServerResponseStructV2[] result =
                omtService.getOrdersByLocationType(locationTypes, transactionId);

        if (GUILoggerHome.find().isDebugOn() &&
                GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.OMT))
        {
            GUILoggerHome.find().debug(getClass().getName() + ": getOrdersByLocationType:exit",
                    GUILoggerBusinessProperty.OMT, result);
        }

        return result;
    }

    public void acceptDirectRoute(String orderRoutingDestination, OrderStruct anOrder, short rerouteFlag)
            throws UserException
    {
        String methodName = "acceptDirectRoute";
        verifyService(methodName);
        logTransaction(methodName, orderRoutingDestination, anOrder, rerouteFlag);
        try
        {
            omtService.acceptDirectRoute(orderRoutingDestination, anOrder, rerouteFlag);
        }
        catch (Exception e)
        {
            handleTransactionException(methodName, e);
        }
    }

    public void acceptManualCancel(CancelRequestStruct cancelRequestStruct,
                                   ProductKeysStruct productKeysStruct, long cancelReqId)
            throws UserException
    // throws SystemException, CommunicationException, DataValidationException,
    // NotFoundException, AuthorizationException, TransactionFailedException, NotAcceptedException
    {
        String methodName = "acceptManualCancel";
        verifyService(methodName);
        logTransaction(methodName, cancelRequestStruct, productKeysStruct, cancelReqId);
        try
        {
            omtService.acceptManualCancel(cancelRequestStruct, productKeysStruct, cancelReqId);
        }
        catch (Exception e)
        {
            handleTransactionException(methodName, e);
        }
    }

    public void acceptManualCancel(OrderCancelMessageElement anElement)
            throws UserException
    // throws SystemException, CommunicationException, DataValidationException,
    // NotFoundException, AuthorizationException, TransactionFailedException, NotAcceptedException
    {
        //noinspection deprecation
        CancelRequestStruct cancelRequestStruct = anElement.getCancelRequest().getStruct();
        ProductKeysStruct productKeysStruct = anElement.getOrder().getSessionProduct().getProductKeysStruct();
        long cancelReqId = anElement.getIdentifier();
        acceptManualCancel(cancelRequestStruct, productKeysStruct, cancelReqId);
    }

    public void acceptManualCancelReplace(CancelRequestStruct cancelRequestStruct,
                                          ProductKeysStruct productKeysStruct, OrderStruct anOrder, long cancelReplaceReqId)
            throws UserException
    // throws SystemException, TransactionFailedException, NotFoundException, DataValidationException,
    // CommunicationException, NotAcceptedException, AuthorizationException
    {
        String methodName = "acceptManualCancelReplace";
        verifyService(methodName);
        logTransaction(methodName, cancelRequestStruct, productKeysStruct,
                anOrder, cancelReplaceReqId);
        try
        {
            omtService.acceptManualCancelReplace(cancelRequestStruct, productKeysStruct, anOrder, cancelReplaceReqId);
        }
        catch (Exception e)
        {
            handleTransactionException(methodName, e);
        }
    }

    @SuppressWarnings({"deprecation"})
    public void acceptManualCancelReplace(OrderCancelReplaceMessageElement anElement)
            throws UserException
    // throws SystemException, TransactionFailedException, NotFoundException, DataValidationException,
    // CommunicationException, NotAcceptedException, AuthorizationException
    {
        CancelRequestStruct cancelRequestStruct = anElement.getCancelRequest().getStruct();
        ProductKeysStruct productKeysStruct = anElement.getOrder().getSessionProduct().getProductKeysStruct();
        OrderStruct anOrder = anElement.getReplacementOrder().getStruct();
        long cancelReplaceReqId = anElement.getIdentifier();
        acceptManualCancelReplace(cancelRequestStruct, productKeysStruct, anOrder, cancelReplaceReqId);
    }

    public void acceptManualUpdate(int remainingQuantity, OrderStruct anOrder)
            throws SystemException,
            CommunicationException, DataValidationException, TransactionFailedException,
            NotAcceptedException, AuthorizationException
    // I did not change this method api to throw UserException because it is used in multiple places.
    {
        String methodName = "acceptManualUpdate";
        verifyService(methodName);
        logTransaction(methodName, remainingQuantity, anOrder);
        try
        {
            omtService.acceptManualUpdate(remainingQuantity, anOrder);
        }
        catch (Exception e)
        {
            try
            {
                handleTransactionException(methodName, e);
            }
            catch (CommunicationException ce)
            {
                throw ce;
            }
            catch (DataValidationException dve)
            {
                throw dve;
            }
            catch (TransactionFailedException tfe)
            {
                throw tfe;
            }
            catch (NotAcceptedException nae)
            {
                throw nae;
            }
            catch (AuthorizationException ae)
            {
                throw ae;
            }
            catch (UserException ue)
            {
                throw new SystemException(methodName + " threw unexpected exception: "
                        + ue.getMessage(), new ExceptionDetails());
            }
        }
    }

    public void acceptManualFillReport(short activityType, ManualFillStruct[] fillReports,
                                       int productKey, int transactionID) throws UserException
    // throws SystemException, CommunicationException, DataValidationException,
    // TransactionFailedException, NotFoundException, AuthorizationException
    {
        String methodName = "acceptManualFillReport";
        verifyService(methodName);
        logTransaction(methodName, activityType, fillReports, productKey,
                transactionID);
        try
        {
            verifyService();
            omtService.acceptManualFillReport(activityType, fillReports, productKey, transactionID);
        }
        catch (Exception e)
        {
            handleTransactionException(methodName, e);
        }
    }

    public void markMessageAsRead(String sessionName, int productKey, long messageId)
            throws UserException
    // throws SystemException, CommunicationException, NotFoundException,
    // DataValidationException, AuthorizationException
    {
        String methodName = "markMesageAsRead";
        verifyService(methodName);
        logTransaction(methodName, sessionName, productKey, messageId);
        try
        {
            omtService.markMessageAsRead(sessionName, productKey, messageId);
        }
        catch (Exception e)
        {
            handleTransactionException(methodName, e);
        }
    }

    public void acceptMessageRoute(String sessionName, int productKey, String location, long messageId)
            throws UserException
    // throws SystemException,TransactionFailedException,DataValidationException,CommunicationException,NotAcceptedException,AuthorizationException
    {
        String methodName = "acceptMessageRoute";
        verifyService(methodName);
        logTransaction(methodName, sessionName, productKey, location, messageId);
        try
        {
            omtService.acceptMessageRoute(sessionName, productKey, location, messageId);
        }
        catch (Exception e)
        {
            handleTransactionException("acceptMessageRoute", e);
        }
    }

    /**
     * Gets the order history for the given order id.
     *
     * @param sessionName the trading session name.
     * @param productKey  the key for the order's product.
     * @param orderId     the order id to get historical information for.
     * @return the orders history.
     */
    public ActivityHistoryStruct queryOrderHistoryV2(String sessionName,
                                                     int productKey, OrderIdStruct orderId)
            throws SystemException, CommunicationException, AuthorizationException,
            DataValidationException
    {
        logTransaction("queryOrderHistoryV2", sessionName, productKey, orderId);

        return omtService.queryOrderHistoryV2(sessionName, productKey, orderId);
    }

    public InternalCurrentMarketStruct getCurrentMarketQuoteForProducts(String session, int productKey)
            throws SystemException, AuthorizationException, CommunicationException, DataValidationException, NotFoundException
    {
        Object[] parms = new Object[2];
        parms[0] = session;
        parms[1] = productKey;
        GUILoggerHome.find().debug(getClass().getName() + ": getCurrentMarketQuoteForProducts",
                GUILoggerBusinessProperty.OMT, parms);
        return omtService.getCurrentMarketQuoteForProduct(session, productKey);
    }

    public com.cboe.idl.trade.TradeReportStructV2 getTradeReportV2ByTradeId(CboeIdStruct tradeId, boolean activeOnly)
            throws SystemException, CommunicationException, AuthorizationException,
            DataValidationException, NotFoundException
    {
        logTransaction("getTradeReportV2ByTradeId:entry", tradeId, activeOnly);
        com.cboe.idl.trade.TradeReportStructV2 result = omtService.getTradeReportV2ByTradeId(tradeId, activeOnly);
        logTransaction("getTradeReportV2ByTradeId:result", result);
        return result;
    }


    protected boolean isUserAllowed()
    {
        UserPermissionMatrix permissionMatrix =
                UserSessionFactory.findUserSession().getUserPermissionMatrix();

        return permissionMatrix.isAllowed(Permission.ORDER_MANAGEMENT_TERMINAL_ACCESS) ||
               permissionMatrix.isAllowed(Permission.OMT_ORDER_QUERY_LOCATION);
    }

    private void addListener(EventChannelListener clientListener, int channeType)
    {
        ChannelKey key = new ChannelKey(channeType, 0);
        eventChannel.addChannelListener(eventChannel, clientListener, key);
    }

    private void removeListener(EventChannelListener clientListener, int channeType)
    {
        ChannelKey key = new ChannelKey(channeType, 0);
        eventChannel.removeChannelListener(eventChannel, clientListener, key);
    }

    private void verifyService()
    {
        verifyService("OrderManagementTerminalAPIImpl");
    }

    private void verifyService(String apiMethodName)
    {
        if (omtService == null)
        {
            String errMsg = "OMT service has not been initialized yet.";
            GUILoggerHome.find().audit(apiMethodName + " threw Illegal State Exception: " + errMsg);
            throw new IllegalStateException(errMsg);
        }
    }

    private int getOrderQueryDelay()
    {
        if (queryDelay < 0)
        {
            if (AppPropertiesFileFactory.isAppPropertiesAvailable())
            {
                String value = AppPropertiesFileFactory.find()
                        .getValue(PROPERTIES_SECTION_NAME, ORDER_QUERY_LEVEL_KEY_NAME);

                if (GUILoggerHome.find().isDebugOn() &&
                        GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.OMT))
                {
                    GUILoggerHome.find().debug(getClass().getName() + ".getOrderQueryDelay",
                            GUILoggerBusinessProperty.OMT,
                            ORDER_QUERY_LEVEL_KEY_NAME + '=' + value);
                }

                try
                {
                    queryDelay = Integer.parseInt(value);
                    queryDelay = Math.max(queryDelay, MIN_QUERY_DELAY);
                    queryDelay = Math.min(queryDelay, MAX_QUERY_DELAY);
                }
                catch (NumberFormatException e)
                {
                    GUILoggerHome.find().exception(getClass().getName() + ".getOrderQueryDelay",
                            "Error parsing " + ORDER_QUERY_LEVEL_KEY_NAME +
                                    ", value =" + value, e);
                    queryDelay = DEFAULT_QUERY_DELAY;
                }
            }
            else
            {
                queryDelay = DEFAULT_QUERY_DELAY;
            }

            if (GUILoggerHome.find().isDebugOn() &&
                    GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.OMT))
            {
                GUILoggerHome.find().debug(getClass().getName() + ".getOrderQueryDelay",
                        GUILoggerBusinessProperty.OMT,
                        "queryDelay = " + queryDelay);
            }
            seconds = queryDelay / 1000;
        }
        return queryDelay;
    }

    private void verifyTiming() throws OrderQueryThrottleException
    {
        if (isTimerRunning())
        {
            throw new OrderQueryThrottleException(
                    "Please wait and try again in " + seconds + " seconds.");
        }
        else
        {
            startTimer();
        }
    }

    private Timer getTimer()
    {
        return timer;
    }

    private void startTimer()
    {
        if (!isTimerRunning())
        {
            timerRunning = true;
            timer = new Timer("OrderQueryThrottleTimer");
            getTimer().schedule(getTimerTask(), getOrderQueryDelay());
        }
    }

    private void stopTimer()
    {
        if (isTimerRunning())
        {
            getTimer().cancel();
            timerRunning = false;
        }
    }

    private boolean isTimerRunning()
    {
        return timerRunning;
    }

    private TimerTask getTimerTask()
    {
        return new TimerTask()
        {
            public void run()
            {
                stopTimer();
            }
        };
    }

    /*
     * Logs any transaction to the audit and debug logs.
     */
    private void logTransaction(String transactionName, Object... parms)
    {
        String text = getClass().getName() + ": " + transactionName;

        GUILoggerHome.find().audit(text, parms);

        if (GUILoggerHome.find().isDebugOn() &&
                GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.OMT))
        {
            GUILoggerHome.find().debug(text, GUILoggerBusinessProperty.OMT, parms);
        }
    }

    private void handleTransactionException(String apiMethodName, Exception e)
            throws UserException
    {
        if (e instanceof UserException)
        {
            StringBuilder buffer = new StringBuilder(apiMethodName).append(" threw CORBA Exception: ")
                    .append(e.getMessage()).append(" Details: ");
            if (e instanceof SystemException)
            {
                SystemException se = (SystemException)e;
                buffer.append(se.details.message);
                GUILoggerHome.find().audit(buffer.toString());
                throw se;
            }
            else if (e instanceof CommunicationException)
            {
                CommunicationException ce = (CommunicationException)e;
                buffer.append(ce.details.message);
                GUILoggerHome.find().audit(buffer.toString());
                throw ce;
            }
            else if (e instanceof DataValidationException)
            {
                DataValidationException dve = (DataValidationException)e;
                buffer.append(dve.details.message);
                GUILoggerHome.find().audit(buffer.toString());
                throw dve;
            }
            else if (e instanceof TransactionFailedException)
            {
                TransactionFailedException tfe = (TransactionFailedException)e;
                buffer.append(tfe.details.message);
                GUILoggerHome.find().audit(buffer.toString());
                throw tfe;
            }
            else if (e instanceof NotAcceptedException)
            {
                NotAcceptedException nae = (NotAcceptedException)e;
                buffer.append(nae.details.message);
                GUILoggerHome.find().audit(buffer.toString());
                throw nae;
            }
            else if (e instanceof AuthorizationException)
            {
                AuthorizationException ae = (AuthorizationException)e;
                buffer.append(ae.details.message);
                GUILoggerHome.find().audit(buffer.toString());
                throw ae;
            }
            else if (e instanceof NotFoundException)
            {
                NotFoundException nfe = (NotFoundException)e;
                buffer.append(nfe.details.message);
                GUILoggerHome.find().audit(buffer.toString());
                throw nfe;
            }
            else
            {
                GUILoggerHome.find().audit(buffer.toString());
                throw (UserException)e;
            }
        }
        else
        {
            String text = apiMethodName + " threw unexpected non-CORBA exception: " + e.getMessage();
            GUILoggerHome.find().audit(text);
            throw new SystemException(text, new ExceptionDetails());
        }
    }
}
