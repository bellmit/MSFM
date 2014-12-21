//
// -----------------------------------------------------------------------------------
// Source file: OrderManagementServiceImpl.java
//
// PACKAGE: com.cboe.application.orderManagement;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.application.orderManagement;

import static com.cboe.application.order.common.UserOrderServiceUtil.createLegOrderDetails;
import static com.cboe.application.order.common.UserOrderServiceUtil.generateNewMsg;
import static com.cboe.application.order.common.UserOrderServiceUtil.getOrderIdString;
import static com.cboe.application.order.common.UserOrderServiceUtil.publishNewMessage;
import static com.cboe.application.shared.LoggingUtil.createLogSnapshot;
import static com.cboe.application.shared.LoggingUtil.createOrderLogSnapshot;
import com.cboe.application.shared.ServicesHelper;
import com.cboe.application.shared.TransactionTimingRegistration;
import com.cboe.application.shared.TransactionTimingUtil;
import com.cboe.application.shared.consumer.OrderRoutingProcessor;
import com.cboe.application.shared.consumer.OrderRoutingProcessorFactory;
import com.cboe.application.shared.consumer.UserSessionLogoutProcessor;
import com.cboe.application.shared.consumer.UserSessionLogoutProcessorFactory;
import com.cboe.application.supplier.OrderRoutingSupplier;
import com.cboe.application.supplier.OrderRoutingSupplierFactory;
import com.cboe.application.util.OrderCallSnapshot;
import com.cboe.domain.iec.ClientIECFactory;
import com.cboe.domain.iec.ConcurrentEventChannelAdapter;
import com.cboe.domain.logout.LogoutServiceFactory;
import com.cboe.domain.util.RoutingGroupCancelReportDropCopyContainer;
import com.cboe.domain.util.RoutingGroupFillReportDropCopyContainer;
import com.cboe.domain.util.RoutingGroupFillReportRejectContainer;
import com.cboe.domain.util.RoutingGroupLinkageCancelReportContainer;
import com.cboe.domain.util.RoutingGroupLinkageFillReportContainer;
import com.cboe.domain.util.RoutingGroupManualFillTimeoutContainer;
import com.cboe.domain.util.RoutingGroupManualOrderTimeoutContainer;
import com.cboe.domain.util.RoutingGroupOrderCancelContainer;
import com.cboe.domain.util.RoutingGroupOrderCancelReplaceContainer;
import com.cboe.domain.util.RoutingGroupOrderIdStructSequenceContainer;
import com.cboe.domain.util.RoutingGroupOrderStructSequenceContainer;
import com.cboe.domain.util.RoutingGroupRemoveMessageContainer;
import com.cboe.domain.util.RoutingGroupTradeNotificationContainer;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotAcceptedException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.TransactionFailedException;
import com.cboe.idl.cmiConstants.ProductTypes;
import com.cboe.idl.cmiErrorCodes.DataValidationCodes;
import com.cboe.idl.cmiOrder.CancelRequestStruct;
import com.cboe.idl.cmiOrder.LegOrderDetailStruct;
import com.cboe.idl.cmiOrder.OrderIdStruct;
import com.cboe.idl.cmiOrder.OrderStruct;
import com.cboe.idl.cmiProduct.ProductKeysStruct;
import com.cboe.idl.cmiTraderActivity.ActivityHistoryStruct;
import com.cboe.idl.cmiUtil.CboeIdStruct;
import com.cboe.idl.cmiUtil.DateTimeStruct;
import com.cboe.idl.marketData.InternalCurrentMarketStruct;
import com.cboe.idl.ohsConsumers.OrderRoutingConsumer;
import com.cboe.idl.order.CancelReplaceRoutingStruct;
import com.cboe.idl.order.CancelReportDropCopyRoutingStruct;
import com.cboe.idl.order.CancelRoutingStruct;
import com.cboe.idl.order.FillReportDropCopyRoutingStruct;
import com.cboe.idl.order.FillReportRejectRoutingStruct;
import com.cboe.idl.order.LinkageCancelReportRoutingStruct;
import com.cboe.idl.order.LinkageFillReportRoutingStruct;
import com.cboe.idl.order.ManualCancelReplaceStruct;
import com.cboe.idl.order.ManualCancelRequestStructV2;
import com.cboe.idl.order.ManualFillStruct;
import com.cboe.idl.order.ManualFillTimeoutRoutingStruct;
import com.cboe.idl.order.ManualOrderTimeoutRoutingStruct;
import com.cboe.idl.order.OrderIdRoutingStruct;
import com.cboe.idl.order.OrderLocationServerResponseStruct;
import com.cboe.idl.order.OrderLocationSummaryServerResponseStruct;
import com.cboe.idl.order.OrderManualHandlingStructV2;
import com.cboe.idl.order.OrderQueryResultStruct;
import com.cboe.idl.order.OrderRoutingStruct;
import com.cboe.idl.order.TradeNotificationRoutingStruct;
import com.cboe.idl.trade.TradeReportStructV2;
import com.cboe.idl.util.RoutingParameterV2Struct;
import com.cboe.idl.util.ServerResponseStructV2;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.HomeFactory;
import com.cboe.infrastructureServices.foundationFramework.exceptionHandling.CBOELoggableException;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.instrumentationService.transactionTimingCommon.TransactionTimer;
import com.cboe.interfaces.application.OrderManagementService;
import com.cboe.interfaces.application.OrderRoutingCollector;
import com.cboe.interfaces.application.SessionManager;
import com.cboe.interfaces.application.UserSessionLogoutCollector;
import com.cboe.interfaces.businessServices.MarketDataService;
import com.cboe.interfaces.businessServices.OrderHandlingService;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.interfaces.internalBusinessServices.TradeMaintenanceService;
import com.cboe.interfaces.internalBusinessServices.TradeMaintenanceServiceHome;
import com.cboe.interfaces.ohsEvents.IECOrderRoutingConsumerHome;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.ExceptionBuilder;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.channel.ChannelListener;
import com.cboe.util.event.EventChannelAdapterFactory;
import org.omg.CORBA.UserException;

@SuppressWarnings({"ObjectToString"})
public class OrderManagementServiceImpl
        extends BObject
        implements OrderManagementService, OrderRoutingCollector, UserSessionLogoutCollector
{
    protected SessionManager currentSession;
    protected TradeMaintenanceService tms;
    protected OrderHandlingService orderHandlingService;
    protected MarketDataService marketDataService;
    protected String destination;
    protected String userId;


    private OrderRoutingSupplier orderRoutingSupplier = null;
    private OrderRoutingProcessor orderRoutingProcessor = null;
    private UserSessionLogoutProcessor logoutProcessor = null;
    private ConcurrentEventChannelAdapter internalInstrumentedEventChannel;// for publishing NEW
    private ConcurrentEventChannelAdapter internalEventChannel;

    private static final LegOrderDetailStruct[] EMPTY_LegOrderDetailStruct_ARRAY = new LegOrderDetailStruct[0];

    public static final String OMT_FILTER_DELAY_TIME = "OMTFilterDelayTime";
    public static final String OMT_FILTER_DELAY_TIME_DEFAULT = "10000";
    private long omtFilterDelayTime = 10000;

    @SuppressWarnings({"ThisEscapedInObjectConstruction"})
    public OrderManagementServiceImpl(SessionManager sessionManager)
    {
        try
        {
            internalInstrumentedEventChannel = ClientIECFactory.findConcurrentEventChannelAdapter(ClientIECFactory.CAS_INSTRUMENTED_IEC);
        }
        catch (Exception e)
        {
            Log.exception("Exception getting CAS_INSTRUMENTED_IEC!", e);
        }
        try
        {
            internalEventChannel = ClientIECFactory.findConcurrentEventChannelAdapter(ClientIECFactory.CAS_IEC);
        }
        catch (Exception e)
        {
            Log.exception("Exception getting CAS_IEC!", e);
        }

        currentSession = sessionManager;
        tms = findTradeMaintenanceService();

        try
        {
            userId = sessionManager.getValidUser().userId;


        }
        catch (UserException e)
        {
            Log.exception(this, "session : " + sessionManager, e);
        }
    }

    /**
     * Retrieves the server side implementation on Trade Maintenance Service
     */
    private TradeMaintenanceService findTradeMaintenanceService()
    {
        if (tms == null)
        {
            try
            {
                TradeMaintenanceServiceHome
                        home = (TradeMaintenanceServiceHome) HomeFactory.getInstance().findHome(TradeMaintenanceServiceHome.ADMIN_HOME_NAME);

                tms = (TradeMaintenanceService) home.find();
            }
            catch (CBOELoggableException e)
            {
                throw new NullPointerException("Could not find TradeMaintenanceServiceHome");
            }
        }
        return tms;
    }

    public void create(String name)
    {
        super.create(name);
        orderHandlingService = ServicesHelper.getOrderHandlingService();

        logoutProcessor = UserSessionLogoutProcessorFactory.create(this);
        EventChannelAdapterFactory.find().addChannelListener(this, logoutProcessor, currentSession);
        LogoutServiceFactory.find().addLogoutListener(currentSession, this);

        orderRoutingSupplier = OrderRoutingSupplierFactory.create(currentSession);
        orderRoutingSupplier.setDynamicChannels(true);

        orderRoutingProcessor = OrderRoutingProcessorFactory.create(this);
        try
        {
            String value = System.getProperty(OMT_FILTER_DELAY_TIME, OMT_FILTER_DELAY_TIME_DEFAULT);
            if (value != null && value.trim().length() > 0)
            {
                omtFilterDelayTime = Long.parseLong(value);
            }
        }
        catch (Exception e)
        {
            Log.exception(e);
        }
        if (Log.isDebugOn())
        {
            Log.debug(this, "OMT_FILTER_DELAY_TIME set to: " + omtFilterDelayTime + " ms for OMT subscription for " + currentSession);
        }

    }


    public BaseSessionManager getSessionManager()
    {
        return currentSession;
    }

    public void acceptUserSessionLogout()
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling acceptUserSessionLogout for " + getSessionManager());
        }
        try
        {
            removeFilterForManualHandling();
        }
        catch (Exception e)
        {
            Log.exception(this, "Error cannot remove filters for OrderRoutingConsumer.", e);
        }


        EventChannelAdapterFactory.find().removeListenerGroup(this);
        LogoutServiceFactory.find().logoutComplete(getSessionManager(), this);

        orderHandlingService = null;

        orderRoutingSupplier.removeListenerGroup(this);
        orderRoutingSupplier = null;

        logoutProcessor.setParent(null);
        logoutProcessor = null;

        currentSession = null;
    }

    public void subscribeOrdersForManualHandling(OrderRoutingConsumer orderManagementConsumer,
                                                 boolean gmdCallback)
            throws SystemException, CommunicationException, AuthorizationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling subscribeOrdersForManualHandling for " + currentSession +
                    " gmd:" + gmdCallback);
        }
        if (orderManagementConsumer != null)
        {
            try
            {
                ChannelListener proxyListener =
                        ServicesHelper.getOrderRoutingConsumerProxy(orderManagementConsumer,
                                                                    currentSession, gmdCallback);
                ChannelKey channelKey;

                IECOrderRoutingConsumerHome consumerHome =
                        ServicesHelper.getOrderRoutingConsumerHome();

                channelKey = new ChannelKey(ChannelType.CB_OMT_ORDER_ACCEPTED, currentSession);
                orderRoutingSupplier.addChannelListener(this, proxyListener, channelKey);

                channelKey = new ChannelKey(ChannelType.OMT_ORDER_ACCEPTED, userId);
                internalEventChannel.addChannelListener(this, orderRoutingProcessor, channelKey);
                consumerHome.addFilter(channelKey);

                channelKey = new ChannelKey(ChannelType.CB_OMT_ORDER_CANCELED, currentSession);
                orderRoutingSupplier.addChannelListener(this, proxyListener, channelKey);

                channelKey = new ChannelKey(ChannelType.OMT_ORDER_CANCELED, userId);
                internalEventChannel
                        .addChannelListener(this, orderRoutingProcessor, channelKey);
                consumerHome.addFilter(channelKey);

                channelKey = new ChannelKey(ChannelType.CB_OMT_ORDER_CANCEL_REPLACED, currentSession);
                orderRoutingSupplier.addChannelListener(this, proxyListener, channelKey);

                channelKey = new ChannelKey(ChannelType.OMT_ORDER_CANCEL_REPLACED, userId);
                internalEventChannel
                        .addChannelListener(this, orderRoutingProcessor, channelKey);
                consumerHome.addFilter(channelKey);

                channelKey = new ChannelKey(ChannelType.CB_OMT_FILL_REPORT_REJECT, currentSession);
                orderRoutingSupplier.addChannelListener(this, proxyListener, channelKey);

                channelKey = new ChannelKey(ChannelType.OMT_FILL_REPORT_REJECT, userId);
                internalEventChannel
                        .addChannelListener(this, orderRoutingProcessor, channelKey);
                consumerHome.addFilter(channelKey);

                channelKey = new ChannelKey(ChannelType.CB_OMT_ORDER_REMOVED, currentSession);
                orderRoutingSupplier.addChannelListener(this, proxyListener, channelKey);

                channelKey = new ChannelKey(ChannelType.OMT_ORDER_REMOVED, userId);
                internalEventChannel
                        .addChannelListener(this, orderRoutingProcessor, channelKey);
                consumerHome.addFilter(channelKey);

                channelKey = new ChannelKey(ChannelType.CB_OMT_LINKAGE_CANCEL_REPORT, currentSession);
                orderRoutingSupplier.addChannelListener(this, proxyListener, channelKey);

                channelKey = new ChannelKey(ChannelType.OMT_LINKAGE_CANCEL_REPORT, userId);
                internalEventChannel
                        .addChannelListener(this, orderRoutingProcessor, channelKey);
                consumerHome.addFilter(channelKey);

                channelKey = new ChannelKey(ChannelType.CB_OMT_LINKAGE_FILL_REPORT, currentSession);
                orderRoutingSupplier.addChannelListener(this, proxyListener, channelKey);

                channelKey = new ChannelKey(ChannelType.OMT_LINKAGE_FILL_REPORT, userId);
                internalEventChannel
                        .addChannelListener(this, orderRoutingProcessor, channelKey);
                consumerHome.addFilter(channelKey);

                channelKey = new ChannelKey(ChannelType.CB_OMT_ORDERS_FOR_LOCATION, currentSession);
                orderRoutingSupplier.addChannelListener(this, proxyListener, channelKey);

                channelKey = new ChannelKey(ChannelType.OMT_ORDERS_FOR_LOCATION, userId);
                internalEventChannel
                        .addChannelListener(this, orderRoutingProcessor, channelKey);
                consumerHome.addFilter(channelKey);

                channelKey = new ChannelKey(ChannelType.CB_OMT_LOCATION_SUMMARY, currentSession);
                orderRoutingSupplier.addChannelListener(this, proxyListener, channelKey);

                channelKey = new ChannelKey(ChannelType.OMT_LOCATION_SUMMARY, userId);
                internalEventChannel
                        .addChannelListener(this, orderRoutingProcessor, channelKey);
                consumerHome.addFilter(channelKey);

                channelKey = new ChannelKey(ChannelType.CB_OMT_REMOVE_MESSAGE, currentSession);
                orderRoutingSupplier.addChannelListener(this, proxyListener, channelKey);

                channelKey = new ChannelKey(ChannelType.OMT_REMOVE_MESSAGE, userId);
                internalEventChannel
                        .addChannelListener(this, orderRoutingProcessor, channelKey);
                consumerHome.addFilter(channelKey);

                channelKey = new ChannelKey(ChannelType.CB_OMT_TRADE_NOTIFICATION, currentSession);
                orderRoutingSupplier.addChannelListener(this, proxyListener, channelKey);

                channelKey = new ChannelKey(ChannelType.OMT_TRADE_NOTIFICATION, userId);
                internalEventChannel
                        .addChannelListener(this, orderRoutingProcessor, channelKey);
                consumerHome.addFilter(channelKey);

                channelKey = new ChannelKey(ChannelType.CB_OMT_FILL_REPORT_DROP_COPY, currentSession);
                orderRoutingSupplier.addChannelListener(this, proxyListener, channelKey);

                channelKey = new ChannelKey(ChannelType.OMT_FILL_REPORT_DROP_COPY, userId);
                internalEventChannel
                        .addChannelListener(this, orderRoutingProcessor, channelKey);
                consumerHome.addFilter(channelKey);

                channelKey = new ChannelKey(ChannelType.CB_OMT_CANCEL_REPORT_DROP_COPY, currentSession);
                orderRoutingSupplier.addChannelListener(this, proxyListener, channelKey);

                channelKey = new ChannelKey(ChannelType.OMT_CANCEL_REPORT_DROP_COPY, userId);
                internalEventChannel
                        .addChannelListener(this, orderRoutingProcessor, channelKey);
                consumerHome.addFilter(channelKey);

                channelKey = new ChannelKey(ChannelType.CB_OMT_MANUAL_ORDER_TIMEOUT, currentSession);
                orderRoutingSupplier.addChannelListener(this, proxyListener, channelKey);

                channelKey = new ChannelKey(ChannelType.OMT_MANUAL_ORDER_TIMEOUT, userId);
                internalEventChannel.addChannelListener(this, orderRoutingProcessor, channelKey);
                consumerHome.addFilter(channelKey);

                channelKey = new ChannelKey(ChannelType.CB_OMT_MANUAL_FILL_TIMEOUT, currentSession);
                orderRoutingSupplier.addChannelListener(this, proxyListener, channelKey);

                channelKey = new ChannelKey(ChannelType.OMT_MANUAL_FILL_TIMEOUT, userId);
                internalEventChannel.addChannelListener(this, orderRoutingProcessor, channelKey);
                consumerHome.addFilter(channelKey);

            }
            catch (DataValidationException e)
            {
                Log.exception(this, "Error adding channel listeners for OrderRoutingConsumer.", e);
            }
        }
        else
        {
            Log.alarm(this, "null OrderRoutingConsumer in subscribeOrdersForManualHandling " +
                    currentSession);
        }


        try
        {

            Thread.sleep(omtFilterDelayTime);
        }
        catch (java.lang.InterruptedException ex)
        {
            if (Log.isDebugOn())
            {
                Log.debug(this, "OMT_FILTER_DELAY_TIME set to: " + omtFilterDelayTime + " ms for OMT subscription was interrupted for user " + currentSession);
            }
        }
    }

    private void removeFilterForManualHandling()
            throws SystemException, CommunicationException, AuthorizationException
    {
        String smgr = currentSession.toString();
        StringBuilder calling = new StringBuilder(smgr.toString() + 45);
        calling.append("calling removeFilterForManualHandling for ").append(smgr);
        Log.information(this, calling.toString());

        try
        {

            ChannelKey channelKey;
            IECOrderRoutingConsumerHome consumerHome = ServicesHelper.getOrderRoutingConsumerHome();


            channelKey = new ChannelKey(ChannelType.OMT_ORDER_ACCEPTED, userId);
            internalEventChannel.removeChannelListener(this, orderRoutingProcessor, channelKey);
            consumerHome.removeFilter(channelKey);

            channelKey = new ChannelKey(ChannelType.OMT_ORDER_CANCELED, userId);
            internalEventChannel.removeChannelListener(this, orderRoutingProcessor, channelKey);
            consumerHome.removeFilter(channelKey);

            channelKey = new ChannelKey(ChannelType.OMT_ORDER_CANCEL_REPLACED, userId);
            internalEventChannel.removeChannelListener(this, orderRoutingProcessor, channelKey);
            consumerHome.removeFilter(channelKey);

            channelKey = new ChannelKey(ChannelType.OMT_FILL_REPORT_REJECT, userId);
            internalEventChannel.removeChannelListener(this, orderRoutingProcessor, channelKey);
            consumerHome.removeFilter(channelKey);

            channelKey = new ChannelKey(ChannelType.OMT_ORDER_REMOVED, userId);
            internalEventChannel.removeChannelListener(this, orderRoutingProcessor, channelKey);
            consumerHome.removeFilter(channelKey);

            channelKey = new ChannelKey(ChannelType.OMT_LINKAGE_CANCEL_REPORT, userId);
            internalEventChannel.removeChannelListener(this, orderRoutingProcessor, channelKey);
            consumerHome.removeFilter(channelKey);

            channelKey = new ChannelKey(ChannelType.OMT_LINKAGE_FILL_REPORT, userId);
            internalEventChannel.removeChannelListener(this, orderRoutingProcessor, channelKey);
            consumerHome.removeFilter(channelKey);

            channelKey = new ChannelKey(ChannelType.OMT_ORDERS_FOR_LOCATION, userId);
            internalEventChannel.removeChannelListener(this, orderRoutingProcessor, channelKey);
            consumerHome.removeFilter(channelKey);

            channelKey = new ChannelKey(ChannelType.OMT_LOCATION_SUMMARY, userId);
            internalEventChannel.removeChannelListener(this, orderRoutingProcessor, channelKey);
            consumerHome.removeFilter(channelKey);

            channelKey = new ChannelKey(ChannelType.OMT_REMOVE_MESSAGE, userId);
            internalEventChannel.removeChannelListener(this, orderRoutingProcessor, channelKey);
            consumerHome.removeFilter(channelKey);

            channelKey = new ChannelKey(ChannelType.OMT_TRADE_NOTIFICATION, userId);
            internalEventChannel.removeChannelListener(this, orderRoutingProcessor, channelKey);
            consumerHome.removeFilter(channelKey);

            channelKey = new ChannelKey(ChannelType.OMT_FILL_REPORT_DROP_COPY, userId);
            internalEventChannel.removeChannelListener(this, orderRoutingProcessor, channelKey);
            consumerHome.removeFilter(channelKey);

            channelKey = new ChannelKey(ChannelType.OMT_CANCEL_REPORT_DROP_COPY, userId);
            internalEventChannel.removeChannelListener(this, orderRoutingProcessor, channelKey);
            consumerHome.removeFilter(channelKey);

            channelKey = new ChannelKey(ChannelType.OMT_MANUAL_ORDER_TIMEOUT, userId);
            internalEventChannel.removeChannelListener(this, orderRoutingProcessor, channelKey);
            consumerHome.removeFilter(channelKey);

            channelKey = new ChannelKey(ChannelType.OMT_MANUAL_FILL_TIMEOUT, userId);
            internalEventChannel.removeChannelListener(this, orderRoutingProcessor, channelKey);
            consumerHome.removeFilter(channelKey);

        }
        catch (Exception e)
        {
            Log.exception(this, "Error removing filters for OrderRoutingConsumer.", e);
        }

    }

    public void publishAllMessagesForDestination()
            throws SystemException, CommunicationException, DataValidationException,
                   AuthorizationException
    {
        String smgr = currentSession.toString();
        StringBuilder calling = new StringBuilder(smgr.length() + 50);
        calling.append("calling publishAllMessagesForDestination for:").append(smgr);
        Log.information(this, calling.toString());
        orderHandlingService.publishAllMessagesForDestination(userId);
    }

    /*
     * *******************************************************************
     * OMT entry/query
     * *******************************************************************
     */

    public TradeReportStructV2 getTradeReportV2ByTradeId(CboeIdStruct tradeId,
                                                         boolean activeOnly)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        String smgr = currentSession.toString();
        StringBuilder calling = new StringBuilder(smgr.length() + 50);
        calling.append("calling getTradeReportV2ByTradeId for session: ").append(smgr);
        Log.information(this, calling.toString());
        return tms.getTradeReportV2ByTradeId(tradeId, activeOnly);
    }


    public InternalCurrentMarketStruct getCurrentMarketQuoteForProduct(String sessionName,
                                                                       int productKey)
            throws SystemException, CommunicationException,
                   DataValidationException, NotFoundException, AuthorizationException

    {
        String smgr = currentSession.toString();
        StringBuilder calling = new StringBuilder(smgr.length() + 50);
        calling.append("calling getCurrentMarketQuoteForProduct for: ").append(smgr);
        Log.information(this, calling.toString());
        InternalCurrentMarketStruct currentMarketQuoteForProduct = getMarketDataService().getCurrentMarketForProductV3(sessionName, productKey);
        return currentMarketQuoteForProduct;
    }


    public OrderStruct getOrderByIdV2(OrderIdStruct orderId)
            throws SystemException, CommunicationException, DataValidationException,
                   NotFoundException, AuthorizationException
    {
        String smgr = currentSession.toString();
        String oid = getOrderIdString(orderId);
        StringBuilder calling = new StringBuilder(smgr.length() + oid.length() + 55);
        calling.append("calling getOrderByIdV2 for sessionManager: ").append(smgr).append(" orderId:").append(oid);
        Log.information(this, calling.toString());
        return orderHandlingService.getOrderByIdV2(userId, orderId);
    }

    public OrderStruct getOrderByORSID(String orsId)
            throws SystemException, CommunicationException, DataValidationException,
                   NotFoundException, AuthorizationException
    {
        String smgr = currentSession.toString();
        StringBuilder calling = new StringBuilder(smgr.length() + orsId.length() + 40);
        calling.append("calling getOrderByORSID for:").append(smgr).append(" orsID:").append(orsId);
        Log.information(this, calling.toString());
        return orderHandlingService.getOrderByORSID(userId, orsId);
    }

    public OrderQueryResultStruct getOrdersByClassAndTime(int classKey, DateTimeStruct dateTimeStruct,
                                                          short direction)
            throws SystemException, CommunicationException, DataValidationException,
                   AuthorizationException
    {
        String smgr = currentSession.toString();
        StringBuilder calling = new StringBuilder(smgr.length() + 40);
        calling.append("calling getOrdersByClassAndTime for:").append(smgr);
        Log.information(this, calling.toString());
        return orderHandlingService.getOrdersByClassAndTime(userId, classKey, dateTimeStruct, direction);
    }

    public OrderQueryResultStruct getOrdersByProductAndTime(int productKey, DateTimeStruct dateTimeStruct,
                                                            short direction)
            throws SystemException, CommunicationException, DataValidationException,
                   AuthorizationException
    {
        String smgr = currentSession.toString();
        StringBuilder calling = new StringBuilder(smgr.length() + 40);
        calling.append("calling getOrdersByProductAndTime for:").append(smgr);
        Log.information(this, calling.toString());
        return orderHandlingService.getOrdersByProductAndTime(userId, productKey, dateTimeStruct, direction);
    }

    public ServerResponseStructV2[] getOrdersByLocation(String location, String transactionId, short[] filterTypes)
            throws SystemException, CommunicationException, DataValidationException, NotAcceptedException,
                   AuthorizationException
    {
        String smgr = currentSession.toString();
        StringBuilder calling = new StringBuilder(smgr.length() + 35);
        calling.append("calling getOrdersBylocation for:").append(smgr);
        Log.information(this, calling.toString());
        return orderHandlingService.getOrdersByLocation(location, transactionId, userId, filterTypes);
    }

    public ServerResponseStructV2[] getOrdersByLocationType(short[] locationTypes, String transactionId)
            throws SystemException, CommunicationException, DataValidationException,
                   AuthorizationException
    {
        StringBuilder sb = new StringBuilder(100);
        if (Log.isDebugOn())
        {
            for (short locationType : locationTypes)
            {
                sb.append(locationType).append(' ');
            }

            Log.debug(this, "calling getOrdersBylocationType: " + sb +
                    " transactionId: " + transactionId +
                    " userId: " + userId);
            sb.setLength(0);
        }

        sb.append("calling getOrdersBylocationType for:").append(currentSession);
        Log.information(this, sb.toString());
        return orderHandlingService.getOrdersByLocationType(locationTypes, transactionId, userId);
    }

    public ActivityHistoryStruct queryOrderHistoryV2(
            String sessionName,
            int productKey,
            OrderIdStruct orderId)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        String smgr = currentSession.toString();
        String oid = getOrderIdString(orderId);
        StringBuilder calling = new StringBuilder(smgr.length() + oid.length() + 45);
        calling.append("calling queryOrderHistoryV2 for:").append(smgr).append(" orderId:").append(oid);
        Log.information(this, calling.toString());
        return orderHandlingService.queryOrderHistory(userId, sessionName, productKey, orderId);
    }


    public void acceptDirectRoute(String destination, OrderStruct anOrder, short rerouteFlag)
            throws SystemException,
                   CommunicationException,
                   DataValidationException,
                   TransactionFailedException,
                   NotAcceptedException,
                   AuthorizationException
    {
        boolean exceptionWasThrown = true; /* Prepare for the worst --- clear flag after success */
        String smgr = currentSession.toString();
        String oid = getOrderIdString(anOrder.orderId);
        StringBuilder calling = new StringBuilder(smgr.length() + oid.length() + destination.length() + 55);
        calling.append("calling acceptDirectRoute for:").append(smgr)
                .append(" orderId:").append(oid)
                .append("destination: ").append(destination);
        Log.information(this, calling.toString());
        long entityId = 0L;

        try
        {
            entityId = TransactionTimingUtil.setEntityID();
        }
        catch (Exception e)
        {
            Log.information(this, "Unable to set EntityID! Exception details: " + e.getMessage());
        }
        //enter TTE emitpoint for direct rout order from OMT.
        TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getOmtDirectRerouteOrderEmitPoint(), entityId, TransactionTimer.Enter);
        try
        {

            orderHandlingService.acceptDirectRoute(destination, anOrder, rerouteFlag, userId);
            exceptionWasThrown = false;

        }
        finally
        {
            // exit TTE emitpoint for direct rout order from OMT.
            TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getOmtDirectRerouteOrderEmitPoint(), entityId,
                                                     exceptionWasThrown ? TransactionTimer.LeaveWithException : TransactionTimer.Leave);
        }
    }


    public void acceptManualCancel(CancelRequestStruct cancelRequest, ProductKeysStruct productKey, long cancelReqId)
            throws SystemException,
                   CommunicationException,
                   DataValidationException,
                   NotFoundException,
                   AuthorizationException,
                   TransactionFailedException,
                   NotAcceptedException
    {

        //cancelReqId is message ID. Server uses to track the cancel request.
        OrderCallSnapshot.enter();
        boolean exceptionWasThrown = true; /* Prepare for the worst --- clear flag after success */
        String smgr = currentSession.toString();
        String oid = getOrderIdString(cancelRequest.orderId);
        StringBuilder calling = new StringBuilder(smgr.length() + oid.length() + 42);
        calling.append("calling acceptManualCancel for:").append(smgr)
                .append(" orderId:").append(oid);
        Log.information(this, calling.toString());

        String sessInfo = currentSession.toString();

        long entityId = 0L;
        try
        {
            entityId = TransactionTimingUtil.setEntityID();
        }
        catch (Exception e)
        {
            Log.information(this, "Unable to set EntityID! Exception details: " + e.getMessage());
        }
        OrderCallSnapshot.startServerCall();
        // enter TTE emitpoint for accept Manual Cancel from OMT.
        TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getOmtManaulCancelOrderEmitPoint(), entityId, TransactionTimer.Enter);
        try
        {
            orderHandlingService.acceptManualCancel(cancelRequest, productKey, cancelReqId, userId);
            exceptionWasThrown = false;


        }
        catch (NotFoundException e)
        {
            Log.exception(this, e);
            throw ExceptionBuilder.dataValidationException("Order has invalid product", DataValidationCodes.INVALID_PRODUCT);
        }

        finally
        {
            // exit TTE emitpoint for accept Manual Cancel from OMT.
            TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getOmtManaulCancelOrderEmitPoint(), entityId,
                                                     exceptionWasThrown ? TransactionTimer.LeaveWithException : TransactionTimer.Leave);
        }
        OrderCallSnapshot.endServerCall();
        // We want to remove IDs from the cache after cancel because we don't expect to
        // refer to this order again. For now we won't remove the IDs because we're
        // afraid that users may try to cancel an order multiple times, and cleaning up
        // our cache would cause the extra cancels to go to the server.
        //----> orderProductCache.removeIds(cancelRequest.orderId);
        OrderCallSnapshot.done();
        Log.information(this, createLogSnapshot("acceptManualCancel", productKey, cancelRequest.sessionName, cancelRequest.orderId, cancelRequest.userAssignedCancelId, cancelRequest.quantity, entityId, sessInfo));


    }

    public void acceptManualCancelReplace(CancelRequestStruct cancelRequest,
                                          ProductKeysStruct productKey,
                                          OrderStruct anOrder,
                                          long cancelReqId)


            throws SystemException,
                   CommunicationException,
                   DataValidationException,
                   NotFoundException,
                   AuthorizationException,
                   TransactionFailedException,
                   NotAcceptedException
    {
        OrderCallSnapshot.enter();
        boolean exceptionWasThrown = true; /* Prepare for the worst --- clear flag after success */
        OrderIdStruct results;
        String smgr = currentSession.toString();
        String oid = getOrderIdString(cancelRequest.orderId);
        StringBuilder calling = new StringBuilder(smgr.length() + oid.length() + 50);
        calling.append("calling acceptManualCancelReplace for:").append(smgr)
                .append(" orderId:").append(oid);
        Log.information(this, calling.toString());

        long entityId = 0L;
        String sessInfo = currentSession.toString();
        try
        {
            entityId = TransactionTimingUtil.setEntityID();
        }
        catch (Exception e)
        {
            Log.information(this, "Unable to set EntityID! Exception details: " + e.getMessage());
        }
        OrderCallSnapshot.startServerCall();
        TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getOmtManaulCancelReplaceOrderEmitPoint(), entityId, TransactionTimer.Enter);
        try
        {
            results = orderHandlingService.acceptManualCancelReplace(cancelRequest, productKey, anOrder, cancelReqId, userId);
            exceptionWasThrown = false;

        }
        finally
        {
            // exit TTE emitpoint for accept Manual Cancel from OMT.
            TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getOmtManaulCancelReplaceOrderEmitPoint(), entityId,
                                                     exceptionWasThrown ? TransactionTimer.LeaveWithException : TransactionTimer.Leave);
        }
        OrderCallSnapshot.endServerCall();
        OrderCallSnapshot.done();
        Log.information(this, createOrderLogSnapshot("acceptManualCancelReplace", anOrder, entityId, sessInfo));

        if (generateNewMsg())
        {
            anOrder.orderId = results;
            if (anOrder.productType == ProductTypes.STRATEGY)
            {
                anOrder.legOrderDetails = createLegOrderDetails(anOrder);
            }
            else
            {
                anOrder.legOrderDetails = EMPTY_LegOrderDetailStruct_ARRAY;
            }
            publishNewMessage(this, internalInstrumentedEventChannel, anOrder);
        }

    }

    public void acceptManualUpdate(int remainingQuantity, OrderStruct anOrder)
            throws SystemException,
                   CommunicationException,
                   DataValidationException,
                   AuthorizationException,
                   TransactionFailedException,
                   NotAcceptedException
    {
        OrderCallSnapshot.enter();
        String smgr = currentSession.toString();
        String oid = getOrderIdString(anOrder.orderId);
        StringBuilder calling = new StringBuilder(smgr.length() + oid.length() + 42);
        calling.append("calling acceptManualUpdate for:").append(smgr).append(" orderId:").append(oid);
        Log.information(this, calling.toString());
        long entityId = 0L;
        String sessInfo = currentSession.toString();
        try
        {
            entityId = TransactionTimingUtil.setEntityID();
        }
        catch (Exception e)
        {
            Log.information(this, "Unable to set EntityID! Exception details: " + e.getMessage());
        }
        OrderCallSnapshot.startServerCall();
        orderHandlingService.acceptManualUpdate(remainingQuantity, anOrder, userId);
        OrderCallSnapshot.endServerCall();
        OrderCallSnapshot.done();
        Log.information(this, createOrderLogSnapshot("acceptManualUpdate", anOrder, entityId, sessInfo));
    }


    public void markMessageAsRead(String sessionName, int productKey, long messageId)
            throws SystemException,
                   CommunicationException,
                   NotFoundException,
                   DataValidationException,
                   AuthorizationException

    {
        String smgr = currentSession.toString();
        StringBuilder calling = new StringBuilder(smgr.length() + 80);
        calling.append("calling markMessageAsRead for:").append(smgr)
                .append(" prodKey:").append(productKey)
                .append(" msgId:").append(messageId);
        Log.information(this, calling.toString());
        orderHandlingService.markMessageAsRead(userId, sessionName, productKey, messageId);
    }

    /*
    * Re-route a routed message to different OMT
    */
    public void acceptMessageRoute(String sessionName, int productKey, String newLocation, long msgId)
            throws SystemException,
                   CommunicationException,
                   DataValidationException,
                   TransactionFailedException,
                   NotAcceptedException,
                   AuthorizationException

    {
        String smgr = currentSession.toString();
        StringBuilder calling = new StringBuilder(smgr.length() + newLocation.length() + 90);
        calling.append("calling acceptMessageRoute for: ").append(smgr)
                .append(" Loc:").append(newLocation)
                .append(" prodKey:").append(productKey)
                .append(" msgId:").append(msgId);
        Log.information(this, calling.toString());
        orderHandlingService.acceptMessageRoute(userId, sessionName, productKey, newLocation, msgId);

    }

    public void acceptManualFillReport(short activityType, ManualFillStruct[] fillReports, int productKey, int transactionSequenceNumber)
            throws SystemException,
                   CommunicationException,
                   DataValidationException,
                   NotFoundException,
                   AuthorizationException,
                   TransactionFailedException,
                   NotAcceptedException
    {
        String smgr = currentSession.toString();
        StringBuilder calling = new StringBuilder(smgr.length() + 40);
        calling.append("calling acceptManualFillReport for: ").append(smgr);
        Log.information(this, calling.toString());
        orderHandlingService.acceptManualFillReport(activityType, userId, fillReports, productKey, transactionSequenceNumber);
    }


    protected MarketDataService getMarketDataService()
    {
        if (marketDataService == null)
        {
            marketDataService = ServicesHelper.getMarketDataService();
        }
        return marketDataService;
    }


    /*
    * ************************************************************************************************************************************
    * Call back
    **************************************************************************************************************************************
    */
    public void acceptOrders(RoutingParameterV2Struct routingParameterV2Struct,
                             OrderRoutingStruct[] orders)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling acceptOrders for " + getSessionManager() + " source:" +
                    routingParameterV2Struct.source);
        }

        RoutingGroupOrderStructSequenceContainer container =
                new RoutingGroupOrderStructSequenceContainer(routingParameterV2Struct,
                                                             orders);
        ChannelKey key = new ChannelKey(ChannelType.CB_OMT_ORDER_ACCEPTED, currentSession);
        ChannelEvent event = internalEventChannel.getChannelEvent(this, key, container);
        orderRoutingSupplier.dispatch(event);
    }


    public void acceptCancels(RoutingParameterV2Struct routingParameterV2Struct,
                              CancelRoutingStruct[] cancels)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling acceptCancels for " + getSessionManager() + " source:" +
                    routingParameterV2Struct.source);
        }

        RoutingGroupOrderCancelContainer container = new RoutingGroupOrderCancelContainer(
                routingParameterV2Struct, cancels);
        ChannelKey key = new ChannelKey(ChannelType.CB_OMT_ORDER_CANCELED, currentSession);
        ChannelEvent event = internalEventChannel.getChannelEvent(this, key, container);
        orderRoutingSupplier.dispatch(event);
    }

    public void acceptCancelReplaces(RoutingParameterV2Struct routingParameterV2Struct,
                                     CancelReplaceRoutingStruct[] cancelReplaces)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling acceptCancelReplaces for " + getSessionManager() + " source:" +
                    routingParameterV2Struct.source);
        }

        RoutingGroupOrderCancelReplaceContainer container =
                new RoutingGroupOrderCancelReplaceContainer(routingParameterV2Struct,
                                                            cancelReplaces);
        ChannelKey key = new ChannelKey(ChannelType.CB_OMT_ORDER_CANCEL_REPLACED, currentSession);
        ChannelEvent event = internalEventChannel.getChannelEvent(this, key, container);
        orderRoutingSupplier.dispatch(event);
    }

    public void acceptFillReportReject(RoutingParameterV2Struct routingParameterV2Struct,
                                       FillReportRejectRoutingStruct[] fillReportRejects)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling acceptFillReportReject for " + getSessionManager() + " source:" +
                    routingParameterV2Struct.source);
        }

        RoutingGroupFillReportRejectContainer container = new RoutingGroupFillReportRejectContainer(
                routingParameterV2Struct, fillReportRejects);
        ChannelKey key = new ChannelKey(ChannelType.CB_OMT_FILL_REPORT_REJECT, currentSession);
        ChannelEvent event = internalEventChannel.getChannelEvent(this, key, container);
        orderRoutingSupplier.dispatch(event);
    }

    public void acceptRemoveOrder(RoutingParameterV2Struct routingParameterV2Struct,
                                  OrderIdRoutingStruct[] orderIds)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling acceptRemoveOrder for " + getSessionManager() + " source:" +
                    routingParameterV2Struct.source);
        }

        RoutingGroupOrderIdStructSequenceContainer container =
                new RoutingGroupOrderIdStructSequenceContainer(routingParameterV2Struct,
                                                               orderIds);
        ChannelKey key = new ChannelKey(ChannelType.CB_OMT_ORDER_REMOVED, currentSession);
        ChannelEvent event = internalEventChannel.getChannelEvent(this, key, container);
        orderRoutingSupplier.dispatch(event);
    }

    public void acceptLinkageCancelReport(RoutingParameterV2Struct routingParameterV2Struct,
                                          LinkageCancelReportRoutingStruct[] cancelReports)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling acceptLinkageCancelReport for " + getSessionManager() +
                    " source:" + routingParameterV2Struct.source);
        }

        RoutingGroupLinkageCancelReportContainer container =
                new RoutingGroupLinkageCancelReportContainer(routingParameterV2Struct,
                                                             cancelReports);
        ChannelKey key = new ChannelKey(ChannelType.CB_OMT_LINKAGE_CANCEL_REPORT, currentSession);
        ChannelEvent event = internalEventChannel.getChannelEvent(this, key, container);
        orderRoutingSupplier.dispatch(event);
    }

    public void acceptLinkageFillReport(RoutingParameterV2Struct routingParameterV2Struct,
                                        LinkageFillReportRoutingStruct[] fillReports)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling acceptLinkageFillReport for " + getSessionManager() +
                    " source:" + routingParameterV2Struct.source);
        }

        RoutingGroupLinkageFillReportContainer container =
                new RoutingGroupLinkageFillReportContainer(routingParameterV2Struct, fillReports);
        ChannelKey key = new ChannelKey(ChannelType.CB_OMT_LINKAGE_FILL_REPORT, currentSession);
        ChannelEvent event = internalEventChannel.getChannelEvent(this, key, container);
        orderRoutingSupplier.dispatch(event);
    }

    /*
    * This callback for getOrdersByLocation
     */
    public void acceptOrderLocationServerResponse(OrderLocationServerResponseStruct response)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling acceptOrderLocationServerResponse for " + getSessionManager());
        }
        ChannelKey key = new ChannelKey(ChannelType.CB_OMT_ORDERS_FOR_LOCATION, currentSession);
        ChannelEvent event = internalEventChannel.getChannelEvent(this, key, response);
        orderRoutingSupplier.dispatch(event);
    }

    /*
    * this callback for getOrdersByLocationType.
    */
    public void acceptOrderLocationSummaryServerResponse(OrderLocationSummaryServerResponseStruct response)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling acceptOrderLocationSummaryServerResponse for " + getSessionManager());
        }
        ChannelKey key = new ChannelKey(ChannelType.CB_OMT_LOCATION_SUMMARY, currentSession);
        ChannelEvent event = internalEventChannel.getChannelEvent(this, key, response);
        orderRoutingSupplier.dispatch(event);
    }

    /*
     * This call made from OHS to OMT to remove all non-order messages. e.g. FillReportReject
     * Cxl and Cx/Re consider as order message.
     */
    public void acceptRemoveMessage(RoutingParameterV2Struct routingParameterV2Struct, long msgId)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling acceptRemoveMessage for " + getSessionManager());
        }
        RoutingGroupRemoveMessageContainer container =
                new RoutingGroupRemoveMessageContainer(routingParameterV2Struct, msgId);

        ChannelKey key = new ChannelKey(ChannelType.CB_OMT_REMOVE_MESSAGE, currentSession);
        ChannelEvent event = internalEventChannel.getChannelEvent(this, key, container);
        orderRoutingSupplier.dispatch(event);
    }

    public void acceptTradeNotifications(RoutingParameterV2Struct routingParameterV2Struct,
                                         TradeNotificationRoutingStruct[] tradeNotifications)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling acceptTradeNotifications for " + getSessionManager() + " source = " + routingParameterV2Struct.source);
        }
        RoutingGroupTradeNotificationContainer container =
                new RoutingGroupTradeNotificationContainer(routingParameterV2Struct, tradeNotifications);
        ChannelKey key = new ChannelKey(ChannelType.CB_OMT_TRADE_NOTIFICATION, currentSession);
        ChannelEvent event = internalEventChannel.getChannelEvent(this, key, container);
        orderRoutingSupplier.dispatch(event);
    }

    public void acceptFillReportDropCopy(RoutingParameterV2Struct routingParameterV2Struct,
                                         FillReportDropCopyRoutingStruct[] fillReportDropCopies)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling acceptFillReportDropCopy for " + getSessionManager() + " source = " + routingParameterV2Struct.source);
        }
        RoutingGroupFillReportDropCopyContainer container =
                new RoutingGroupFillReportDropCopyContainer(routingParameterV2Struct, fillReportDropCopies);
        ChannelKey key = new ChannelKey(ChannelType.CB_OMT_FILL_REPORT_DROP_COPY, currentSession);
        ChannelEvent event = internalEventChannel.getChannelEvent(this, key, container);
        orderRoutingSupplier.dispatch(event);
    }

    public void acceptCancelReportDropCopy(RoutingParameterV2Struct routingParameterV2Struct,
                                           CancelReportDropCopyRoutingStruct[] cancelRoprtDropCopies)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling acceptCancelReportDropCopy for " + getSessionManager() + " source = " + routingParameterV2Struct.source);
        }
        RoutingGroupCancelReportDropCopyContainer container =
                new RoutingGroupCancelReportDropCopyContainer(routingParameterV2Struct, cancelRoprtDropCopies);
        ChannelKey key = new ChannelKey(ChannelType.CB_OMT_CANCEL_REPORT_DROP_COPY, currentSession);
        ChannelEvent event = internalEventChannel.getChannelEvent(this, key, container);
        orderRoutingSupplier.dispatch(event);
    }

    public void acceptManualOrderTimeout(RoutingParameterV2Struct routingParameters, ManualOrderTimeoutRoutingStruct[] manualOrderTimeouts)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling acceptManualOrderTimeout for " + getSessionManager() + " source:" +
                    routingParameters.source);
        }

        RoutingGroupManualOrderTimeoutContainer container =
                new RoutingGroupManualOrderTimeoutContainer(routingParameters, manualOrderTimeouts);
        ChannelKey key = new ChannelKey(ChannelType.CB_OMT_MANUAL_ORDER_TIMEOUT, currentSession);
        ChannelEvent event = internalEventChannel.getChannelEvent(this, key, container);
        orderRoutingSupplier.dispatch(event);
    }

    public void acceptManualFillTimeout(RoutingParameterV2Struct routingParameters, ManualFillTimeoutRoutingStruct[] fillReports)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling acceptManualFillTimeout for " + getSessionManager() + " source:" +
                    routingParameters.source);
        }

        RoutingGroupManualFillTimeoutContainer container =
                new RoutingGroupManualFillTimeoutContainer(routingParameters, fillReports);
        ChannelKey key = new ChannelKey(ChannelType.CB_OMT_MANUAL_FILL_TIMEOUT, currentSession);
        ChannelEvent event = internalEventChannel.getChannelEvent(this, key, container);
        orderRoutingSupplier.dispatch(event);
    }

    public void acceptManualOrders(RoutingParameterV2Struct routingParameterV2Struct,
                                   OrderManualHandlingStructV2[] orders)
    {
        throw new RuntimeException("acceptManualOrders is not supported");
    }

    public void acceptManualCancels(RoutingParameterV2Struct routingParameterV2Struct,
                                    ManualCancelRequestStructV2[] cancelRequests)
    {
        throw new RuntimeException("acceptManualOrders is not supported");
    }

    public void acceptManualCancelReplaces(RoutingParameterV2Struct routingParameterV2Struct,
                                           ManualCancelReplaceStruct[] cancelReplaces)
    {
        throw new RuntimeException("acceptManualOrders is not supported");
    }


}
