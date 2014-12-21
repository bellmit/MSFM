package com.cboe.application.marketData;

import com.cboe.application.shared.RemoteConnectionFactory;
import com.cboe.application.shared.ServicesHelper;
import com.cboe.application.shared.consumer.RemoteCASCallbackRemovalProcessor;
import com.cboe.application.shared.consumer.RemoteCASCallbackRemovalProcessorFactory;
import com.cboe.application.shared.consumer.UserSessionLogoutProcessorFactory;
import com.cboe.domain.logout.LogoutServiceFactory;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiErrorCodes.AuthenticationCodes;
import com.cboe.idl.cmiErrorCodes.DataValidationCodes;
import com.cboe.idl.cmiMarketData.BookDepthStruct;
import com.cboe.idl.cmiMarketData.BookDepthStructV2;
import com.cboe.idl.cmiMarketData.MarketDataHistoryDetailStruct;
import com.cboe.idl.cmiMarketData.MarketDataHistoryStruct;
import com.cboe.idl.cmiProduct.ProductStruct;
import com.cboe.idl.cmiUtil.DateTimeStruct;
import com.cboe.idl.consumers.TickerConsumer;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.*;
import com.cboe.interfaces.internalBusinessServices.ProductConfigurationService;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.ExceptionBuilder;
import com.cboe.util.channel.ChannelListener;
import com.cboe.util.event.EventChannelAdapterFactory;
import com.cboe.interfaces.floorApplication.LastSaleService;

import java.util.List;

/**
 * This service is responsible for managing the interface to the market
 * data caches and the subscription to further market data information
 *
 * @author Jeff Illian
 * @version 07/9/1999
 * BookDepth added 12/27/2001
 */
public class SystemMarketQueryImpl extends MarketQueryBaseImpl 
	implements SystemMarketQuery
{
    private SessionManager currentSession;
    //--------------------------------------------------------------------------
    // static data members
    //--------------------------------------------------------------------------
    // These lists contain the names of trading sessions whose market data
    // subscriptions must be handled either _ONLY_ locally or _ONLY_ remotely.
    // Any trading session whose name is not in either of these lists will have
    // its market data subscriptions handled _BOTH_ locally and remotely.
    private static List localFilterOnlySessionsList;
    private static List remoteFilterOnlySessionsList;

    protected RemoteCASCallbackRemovalProcessor callbackRemovalProcessor;
    protected ProductConfigurationService productConfigurationService;
    protected UserMarketDataService userMarketQuery;

    private String userIor;
    //--------------------------------------------------------------------------
    // static methods
    //--------------------------------------------------------------------------
    /**
     * This method inits the static data members of this class.  It should
     * only be called on time during the lifetime of the process (probably by
     * this class's home class).
     */
    static void initStaticData(List localOnlySessionsList, List remoteOnlySessionsList)
    {
        if ((localFilterOnlySessionsList == null) && (localOnlySessionsList != null))
        {
            localFilterOnlySessionsList = localOnlySessionsList;
        }

        if ((remoteFilterOnlySessionsList == null) && (remoteOnlySessionsList != null))
        {
            remoteFilterOnlySessionsList = remoteOnlySessionsList;
        }
    }


    /**
     * SystemMarketQueryImpl constructor
     */
    public SystemMarketQueryImpl(int callabackTimeout)
    {
        super();
        this.marketDataCallbackTimeout =  callabackTimeout;
        localMarketDataService = true;
    }

    /**
     * sets the session manager for the market data class and creates a new
     * helper
     *
     * @param session SessionManager
     */
    public void setSessionManager(SessionManager session)
    {
        baseSession = session;
        currentSession = session;
        logoutProcessor = UserSessionLogoutProcessorFactory.create(this);
        EventChannelAdapterFactory.find().addChannelListener(this, logoutProcessor, session);
        LogoutServiceFactory.find().addLogoutListener(session, this);
        RemoteCASCallbackRemovalCollector marketDataCallbackRemovalCollector = new RemoteCASMarketDataCallbackRemovalCollectorImpl(session);
        callbackRemovalProcessor = RemoteCASCallbackRemovalProcessorFactory.create(marketDataCallbackRemovalCollector);
        try
        {
            this.userId = currentSession.getValidSessionProfileUser().userId;
            SessionManagerHome sessionManagerHome = ServicesHelper.getSessionManagerHome();
            if (sessionManagerHome!=null)
            {
//This is to subscribe cas for market data callback removal from mdcas.
                this.userIor = sessionManagerHome.getUserSessionIor(currentSession);
                ChannelKey channelKey = new ChannelKey(ChannelType.MDCAS_CALLBACK_REMOVAL, userIor);
                EventChannelAdapterFactory.find().addChannelListener(this, callbackRemovalProcessor, channelKey);
            }
        } catch (Exception e)
        {
            Log.exception(this, "session : " + currentSession, e);
        }

    }

    public BookDepthStruct getBookDepth(String sessionName, int productKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException
    {
        return getUserMarketQuery().getBookDepth(sessionName, productKey);
    }

    /**
     * returns the market history for a given product key and date time.
     * This method will return X records at which point the caller will
     * need to request the next block by specifying a new time.
     *
     * @param productKey int
     * @param startTime DateTimeStruct
     * @return MarketDataHistoryStruct
     *
     */
    public MarketDataHistoryStruct getMarketDataHistoryByTime(String sessionName, int productKey, DateTimeStruct startTime, short direction)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        return getUserMarketQuery().getMarketDataHistoryByTime(sessionName, productKey, startTime, direction);
    }

    public BookDepthStructV2 getBookDepthDetails(String sessionName, int productKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException
    {
        return getUserMarketQuery().getBookDepthDetails(sessionName, productKey);
    }

//-----------gRECAP-----------------
    public void subscribeRecapForClassV2(String sessionName, int classKey, com.cboe.idl.cmiCallbackV2.CMIRecapConsumer clientListener, short actionOnQueue)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling subscribeRecapForClassV2 for " + currentSession);
        }
        try
        {
            getUserMarketQuery().verifyUserRecapEnablement(sessionName, classKey);
            com.cboe.idl.cmiCallbackV2.CMIRecapConsumer timeoutListener =
                            com.cboe.idl.cmiCallbackV2.CMIRecapConsumerHelper.narrow((org.omg.CORBA.Object) RemoteConnectionFactory.find().setRoundTripTimeout(clientListener, marketDataCallbackTimeout));
            ChannelListener proxyListener = ServicesHelper.getRecapV2ConsumerProxy(timeoutListener, currentSession, actionOnQueue);
            if (clientListener != null)
            {
                boolean isLocalOnlySession  = localFilterOnlySessionsList.contains(sessionName);
                boolean isRemoteOnlySession = remoteFilterOnlySessionsList.contains(sessionName);
                boolean doLocal  = (isLocalOnlySession  || !isRemoteOnlySession);
                boolean doRemote = (isRemoteOnlySession || !isLocalOnlySession);

                if (doLocal)
                {
                    int channelType = ChannelType.CB_RECAP_BY_CLASS_V2;
                    subscribeMarketDataForClassByType(sessionName, classKey, channelType, proxyListener);
                    publishMarketData(channelType,sessionName,classKey,timeoutListener,actionOnQueue);
                }

                if (doRemote)
                {
                    ServicesHelper.getMarketDataRequestPublisher().publishRecapSubscriptionV2(
                            this,
                            userId,
                            userIor,
                            sessionName,
                            classKey,
                            0,
                            proxyListener,
                            actionOnQueue
                    );
                }
            }
        }
        catch (NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }
    }

    public void subscribeRecapForClass(String sessionName, int classKey, com.cboe.idl.cmiCallback.CMIRecapConsumer clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling subscribeRecapForClass for " + currentSession);
        }
        try
        {
            getUserMarketQuery().verifyUserRecapEnablement(sessionName, classKey);
            com.cboe.idl.cmiCallback.CMIRecapConsumer timeoutListener =
                            com.cboe.idl.cmiCallback.CMIRecapConsumerHelper.narrow((org.omg.CORBA.Object) RemoteConnectionFactory.find().setRoundTripTimeout(clientListener, marketDataCallbackTimeout));
            ChannelListener proxyListener = ServicesHelper.getRecapConsumerProxy(timeoutListener, currentSession);
            if (clientListener != null)
            {
                boolean isLocalOnlySession  = localFilterOnlySessionsList.contains(sessionName);
                boolean isRemoteOnlySession = remoteFilterOnlySessionsList.contains(sessionName);
                boolean doLocal  = (isLocalOnlySession  || !isRemoteOnlySession);
                boolean doRemote = (isRemoteOnlySession || !isLocalOnlySession);

                if (doLocal)
                {
                    int channelType = ChannelType.CB_RECAP_BY_CLASS;
                    subscribeMarketDataForClassByType(sessionName, classKey, channelType, proxyListener);
                    publishMarketData(channelType,sessionName,classKey,timeoutListener,(short)0);
                }

                if (doRemote)
                {
                    ServicesHelper.getMarketDataRequestPublisher().publishRecapSubscription(
                            this,
                            userId,
                            userIor,
                            sessionName,
                            classKey,
                            0,
                            proxyListener
                    );
                }
            }
        }
        catch (NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }
    }


    public void subscribeRecapForProductV2(String sessionName, int productKey, com.cboe.idl.cmiCallbackV2.CMIRecapConsumer clientListener, short actionOnQueue)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling subscribeRecapForProductV2 for " + currentSession);
        }
        try
        {
            int classKey = getProduct(productKey).productKeys.classKey;
            //verify if it is enabled
            getUserMarketQuery().verifyUserRecapEnablement(sessionName, classKey);
            com.cboe.idl.cmiCallbackV2.CMIRecapConsumer timeoutListener =
                            com.cboe.idl.cmiCallbackV2.CMIRecapConsumerHelper.narrow((org.omg.CORBA.Object) RemoteConnectionFactory.find().setRoundTripTimeout(clientListener, marketDataCallbackTimeout));
            ChannelListener proxyListener = ServicesHelper.getRecapV2ConsumerProxy(timeoutListener, currentSession, actionOnQueue);
            if (clientListener != null)
            {
                boolean isLocalOnlySession  = localFilterOnlySessionsList.contains(sessionName);
                boolean isRemoteOnlySession = remoteFilterOnlySessionsList.contains(sessionName);
                boolean doLocal  = (isLocalOnlySession  || !isRemoteOnlySession);
                boolean doRemote = (isRemoteOnlySession || !isLocalOnlySession);

                if (doLocal)
                {
                    int channelType = ChannelType.CB_RECAP_BY_PRODUCT_V2;
                    subscribeMarketDataForProductByType(sessionName, classKey, productKey, channelType, proxyListener);
                    publishMarketData(channelType,sessionName,productKey,timeoutListener,actionOnQueue);
                }

                if (doRemote)
                {
                    ServicesHelper.getMarketDataRequestPublisher().publishRecapSubscriptionV2(
                            this,
                            userId,
                            userIor,
                            sessionName,
                            classKey,
                            productKey,
                            proxyListener,
                            actionOnQueue
                    );
                }
            }
        }
        catch (NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }
    }

    public void subscribeRecapForProduct(String sessionName, int productKey, com.cboe.idl.cmiCallback.CMIRecapConsumer clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling subscribeRecapForProduct for " + currentSession);
        }
        try
        {
            ProductStruct product = getProduct(productKey);
            int classKey = product.productKeys.classKey;
            //verify if it is enabled
            getUserMarketQuery().verifyUserRecapEnablement(sessionName, classKey);
            com.cboe.idl.cmiCallback.CMIRecapConsumer timeoutListener =
                            com.cboe.idl.cmiCallback.CMIRecapConsumerHelper.narrow((org.omg.CORBA.Object) RemoteConnectionFactory.find().setRoundTripTimeout(clientListener, marketDataCallbackTimeout));
            ChannelListener proxyListener = ServicesHelper.getRecapConsumerProxy(timeoutListener, currentSession);

            if (clientListener != null)
            {
                boolean isLocalOnlySession  = localFilterOnlySessionsList.contains(sessionName);
                boolean isRemoteOnlySession = remoteFilterOnlySessionsList.contains(sessionName);
                boolean doLocal  = (isLocalOnlySession  || !isRemoteOnlySession);
                boolean doRemote = (isRemoteOnlySession || !isLocalOnlySession);

                if (doLocal)
                {
                    int channelType = ChannelType.CB_RECAP_BY_PRODUCT;
                    subscribeMarketDataForProductByType(sessionName, classKey, productKey, channelType, proxyListener);
                    publishMarketData(channelType,sessionName,productKey,timeoutListener,(short)0);
                }

                if (doRemote)
                {
                    ServicesHelper.getMarketDataRequestPublisher().publishRecapSubscription(
                            this,
                            userId,
                            userIor,
                            sessionName,
                            classKey,
                            productKey,
                            proxyListener
                    );
                }
            }
        }
        catch (NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }
    }

    public void unsubscribeRecapForProductV2(String sessionName, int productKey, com.cboe.idl.cmiCallbackV2.CMIRecapConsumer clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling unsubscribeRecapForProductV2 for " + currentSession);
        }
        try
        {
            ProductStruct product = getProduct(productKey);
            int classKey = product.productKeys.classKey;
            //verify if it is enabled
            getUserMarketQuery().verifyUserRecapEnablement(sessionName, classKey);
            ChannelListener proxyListener = ServicesHelper.getRecapV2ConsumerProxy(clientListener, currentSession, (short) 0);
            boolean isLocalOnlySession  = localFilterOnlySessionsList.contains(sessionName);
            boolean isRemoteOnlySession = remoteFilterOnlySessionsList.contains(sessionName);
            boolean doLocal  = (isLocalOnlySession  || !isRemoteOnlySession);
            boolean doRemote = (isRemoteOnlySession || !isLocalOnlySession);

            if (doLocal)
            {
                int channelType = ChannelType.CB_RECAP_BY_PRODUCT_V2;
                unsubscribeMarketDataForProductByType(sessionName, classKey, productKey, channelType, proxyListener);
            }

            if (doRemote)
            {
                ServicesHelper.getMarketDataRequestPublisher().publishRecapUnSubscriptionV2(
                        this,
                        userId,
                        userIor,
                        sessionName,
                        classKey,
                        productKey,
                        proxyListener
                );
            }
        }
        catch (NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }
    }

    public void unsubscribeRecapForProduct(String sessionName, int productKey, com.cboe.idl.cmiCallback.CMIRecapConsumer clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling unsubscribeRecapForProduct for " + currentSession);
        }
        try
        {
            ProductStruct product = getProduct(productKey);
            int classKey = product.productKeys.classKey;
            //verify if it is enabled
            getUserMarketQuery().verifyUserRecapEnablement(sessionName, classKey);
            ChannelListener proxyListener = ServicesHelper.getRecapConsumerProxy(clientListener, currentSession);

            boolean isLocalOnlySession  = localFilterOnlySessionsList.contains(sessionName);
            boolean isRemoteOnlySession = remoteFilterOnlySessionsList.contains(sessionName);
            boolean doLocal  = (isLocalOnlySession  || !isRemoteOnlySession);
            boolean doRemote = (isRemoteOnlySession || !isLocalOnlySession);

            if (doLocal)
            {
                int channelType = ChannelType.CB_RECAP_BY_PRODUCT;
                unsubscribeMarketDataForProductByType(sessionName, classKey, productKey, channelType, proxyListener);
            }

            if (doRemote)
            {
                ServicesHelper.getMarketDataRequestPublisher().publishRecapUnSubscription(
                        this,
                        userId,
                        userIor,
                        sessionName,
                        classKey,
                        productKey,
                        proxyListener
                );
            }
        }
        catch (NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }
    }

    public void unsubscribeRecapForClassV2(String sessionName, int classKey, com.cboe.idl.cmiCallbackV2.CMIRecapConsumer clientListener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling unsubscribeRecapForClassV2 for " + currentSession);
        }
        try
        {
            getUserMarketQuery().verifyUserRecapEnablement(sessionName, classKey);
            ChannelListener proxyListener = ServicesHelper.getRecapV2ConsumerProxy(clientListener, currentSession, (short) 0);

            boolean isLocalOnlySession  = localFilterOnlySessionsList.contains(sessionName);
            boolean isRemoteOnlySession = remoteFilterOnlySessionsList.contains(sessionName);
            boolean doLocal  = (isLocalOnlySession  || !isRemoteOnlySession);
            boolean doRemote = (isRemoteOnlySession || !isLocalOnlySession);

            if (doLocal)
            {
                int channelType = ChannelType.CB_RECAP_BY_CLASS_V2;
                unsubscribeMarketDataForClassByType(sessionName, classKey, channelType, proxyListener);
            }

            if (doRemote)
            {
                ServicesHelper.getMarketDataRequestPublisher().publishRecapUnSubscriptionV2(
                        this,
                        userId,
                        userIor,
                        sessionName,
                        classKey,
                        0,
                        proxyListener
                );
            }
        }
        catch (NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }
    }

    public void unsubscribeRecapForClass(String sessionName, int classKey, com.cboe.idl.cmiCallback.CMIRecapConsumer clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling unsubscribeRecapForClass for " + currentSession);
        }
        try
        {
            getUserMarketQuery().verifyUserRecapEnablement(sessionName, classKey);
            ChannelListener proxyListener = ServicesHelper.getRecapConsumerProxy(clientListener, currentSession);

            boolean isLocalOnlySession  = localFilterOnlySessionsList.contains(sessionName);
            boolean isRemoteOnlySession = remoteFilterOnlySessionsList.contains(sessionName);
            boolean doLocal  = (isLocalOnlySession  || !isRemoteOnlySession);
            boolean doRemote = (isRemoteOnlySession || !isLocalOnlySession);

            if (doLocal)
            {
                int channelType = ChannelType.CB_RECAP_BY_CLASS;
                unsubscribeMarketDataForClassByType(sessionName, classKey, channelType, proxyListener);
            }

            if (doRemote)
            {
                ServicesHelper.getMarketDataRequestPublisher().publishRecapUnSubscription(
                        this,
                        userId,
                        userIor,
                        sessionName,
                        classKey,
                        0,
                        proxyListener
                );
            }
        }
        catch (NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }
    }

// CURRENT_MARKET

    public void subscribeCurrentMarketForProduct(String sessionName, int productKey, com.cboe.idl.cmiCallback.CMICurrentMarketConsumer clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling subscribeCurrentMarketForProduct for " + currentSession + " for productKey=" + productKey);
        }
        try
        {
            ProductStruct product = getProduct(productKey);
            int classKey = product.productKeys.classKey;
            //verify if it is enabled
            getUserMarketQuery().verifyUserCurrentMarketEnablement(sessionName, classKey);
            com.cboe.idl.cmiCallback.CMICurrentMarketConsumer timeoutListener =
                            com.cboe.idl.cmiCallback.CMICurrentMarketConsumerHelper.narrow((org.omg.CORBA.Object) RemoteConnectionFactory.find().setRoundTripTimeout(clientListener, marketDataCallbackTimeout));
            ChannelListener proxyListener = ServicesHelper.getCurrentMarketConsumerProxy(timeoutListener, currentSession);
            if (clientListener != null)
            {
                boolean isLocalOnlySession  = localFilterOnlySessionsList.contains(sessionName);
                boolean isRemoteOnlySession = remoteFilterOnlySessionsList.contains(sessionName);
                boolean doLocal  = (isLocalOnlySession  || !isRemoteOnlySession);
                boolean doRemote = (isRemoteOnlySession || !isLocalOnlySession);

                if (doLocal)
                {
                    int channelType = ChannelType.CB_CURRENT_MARKET_BY_PRODUCT;
                    subscribeMarketDataForProductByType(sessionName, classKey, productKey, channelType, proxyListener);
                    publishMarketData(channelType,sessionName,productKey,timeoutListener,(short)0);
                }

                if (doRemote)
                {
                    ServicesHelper.getMarketDataRequestPublisher().publishCurrentMarketSubscription(
                            this,
                            userId,
                            userIor,
                            sessionName,
                            classKey,
                            productKey,
                            proxyListener
                    );
                }
            }
        }
        catch (NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }
    }

    public void subscribeCurrentMarketForProductV2(String sessionName, int productKey, com.cboe.idl.cmiCallbackV2.CMICurrentMarketConsumer clientListener, short actionOnQueue)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling subscribeCurrentMarketForProductV2 for " + currentSession);
        }
        try
        {
            ProductStruct product = getProduct(productKey);
            int classKey = product.productKeys.classKey;
            //verify if it is enabled
            getUserMarketQuery().verifyUserCurrentMarketEnablement(sessionName, classKey);
            com.cboe.idl.cmiCallbackV2.CMICurrentMarketConsumer timeoutListener =
                            com.cboe.idl.cmiCallbackV2.CMICurrentMarketConsumerHelper.narrow((org.omg.CORBA.Object) RemoteConnectionFactory.find().setRoundTripTimeout(clientListener, marketDataCallbackTimeout));
            ChannelListener proxyListener = ServicesHelper.getCurrentMarketV2ConsumerProxy(timeoutListener, currentSession, actionOnQueue);
            if (clientListener != null)
            {
                boolean isLocalOnlySession  = localFilterOnlySessionsList.contains(sessionName);
                boolean isRemoteOnlySession = remoteFilterOnlySessionsList.contains(sessionName);
                boolean doLocal  = (isLocalOnlySession  || !isRemoteOnlySession);
                boolean doRemote = (isRemoteOnlySession || !isLocalOnlySession);

                if (doLocal)
                {
                    int channelType = ChannelType.CB_CURRENT_MARKET_BY_PRODUCT_V2;
                    subscribeMarketDataForProductByType(sessionName, classKey, productKey, channelType, proxyListener);
                    publishMarketData(channelType,sessionName,productKey,timeoutListener,actionOnQueue);
                }

                if (doRemote)
                {
                    ServicesHelper.getMarketDataRequestPublisher().publishCurrentMarketSubscriptionV2(
                            this,
                            userId,
                            userIor,
                            sessionName,
                            classKey,
                            productKey,
                            proxyListener,
                            actionOnQueue
                    );
                }
            }
        }
        catch (NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }
    }

    public void subscribeCurrentMarketForProductV3(String sessionName, int productKey, com.cboe.idl.cmiCallbackV3.CMICurrentMarketConsumer clientListener, short actionOnQueue)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling subscribeCurrentMarketForProductV3 for " + currentSession);
        }
        try
        {
            ProductStruct product = getProduct(productKey);
            int classKey = product.productKeys.classKey;
            //verify if it is enabled
            getUserMarketQuery().verifyUserCurrentMarketEnablement(sessionName, classKey);
            com.cboe.idl.cmiCallbackV3.CMICurrentMarketConsumer timeoutListener =
                            com.cboe.idl.cmiCallbackV3.CMICurrentMarketConsumerHelper.narrow((org.omg.CORBA.Object) RemoteConnectionFactory.find().setRoundTripTimeout(clientListener, marketDataCallbackTimeout));
            ChannelListener proxyListener = ServicesHelper.getCurrentMarketV3ConsumerProxy(timeoutListener, currentSession, actionOnQueue);
            if (clientListener != null)
            {
                boolean isLocalOnlySession  = localFilterOnlySessionsList.contains(sessionName);
                boolean isRemoteOnlySession = remoteFilterOnlySessionsList.contains(sessionName);
                boolean doLocal  = (isLocalOnlySession  || !isRemoteOnlySession);
                boolean doRemote = (isRemoteOnlySession || !isLocalOnlySession);

                if (doLocal)
                {
                    int channelType = ChannelType.CB_CURRENT_MARKET_BY_PRODUCT_V3;
                    subscribeMarketDataForProductByType(sessionName, classKey, productKey, channelType, proxyListener);
                    publishMarketData(channelType,sessionName,productKey,timeoutListener,actionOnQueue);
                }

                if (doRemote)
                {
                    ServicesHelper.getMarketDataRequestPublisher().publishCurrentMarketSubscriptionV3(
                            this,
                            userId,
                            userIor,
                            sessionName,
                            classKey,
                            productKey,
                            proxyListener,
                            actionOnQueue
                    );
                }
            }
        }
        catch (NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }
    }

    public void subscribeCurrentMarketForClass(String sessionName, int classKey, com.cboe.idl.cmiCallback.CMICurrentMarketConsumer clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling subscribeCurrentMarketForClass for " + currentSession);
        }
        try
        {
            if (clientListener != null)
            {
                getUserMarketQuery().verifyUserCurrentMarketEnablement(sessionName, classKey);
                com.cboe.idl.cmiCallback.CMICurrentMarketConsumer timeoutListener =
                                com.cboe.idl.cmiCallback.CMICurrentMarketConsumerHelper.narrow((org.omg.CORBA.Object) RemoteConnectionFactory.find().setRoundTripTimeout(clientListener, marketDataCallbackTimeout));
                ChannelListener proxyListener = ServicesHelper.getCurrentMarketConsumerProxy(timeoutListener, currentSession);
                boolean isLocalOnlySession  = localFilterOnlySessionsList.contains(sessionName);
                boolean isRemoteOnlySession = remoteFilterOnlySessionsList.contains(sessionName);
                boolean doLocal  = (isLocalOnlySession  || !isRemoteOnlySession);
                boolean doRemote = (isRemoteOnlySession || !isLocalOnlySession);

                if (doLocal)
                {
                    int channelType = ChannelType.CB_CURRENT_MARKET_BY_CLASS;
                    subscribeMarketDataForClassByType(sessionName, classKey, channelType, proxyListener);
                    publishMarketData(channelType,sessionName,classKey,timeoutListener,(short)0);
                }

                if (doRemote)
                {
                    ServicesHelper.getMarketDataRequestPublisher().publishCurrentMarketSubscription(
                            this,
                            userId,
                            userIor,
                            sessionName,
                            classKey,
                            0,
                            proxyListener);
                }
            }
        }
        catch (NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }
    }

    public void subscribeCurrentMarketForClassV2(String sessionName, int classKey, com.cboe.idl.cmiCallbackV2.CMICurrentMarketConsumer clientListener, short actionOnQueue)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling subscribeCurrentMarketForClassV2 for " + currentSession);
        }
        try
        {
            if (clientListener != null)
            {
                getUserMarketQuery().verifyUserCurrentMarketEnablement(sessionName, classKey);
                com.cboe.idl.cmiCallbackV2.CMICurrentMarketConsumer timeoutListener =
                                com.cboe.idl.cmiCallbackV2.CMICurrentMarketConsumerHelper.narrow((org.omg.CORBA.Object) RemoteConnectionFactory.find().setRoundTripTimeout(clientListener, marketDataCallbackTimeout));
                ChannelListener proxyListener = ServicesHelper.getCurrentMarketV2ConsumerProxy(timeoutListener, currentSession, actionOnQueue);
                boolean isLocalOnlySession  = localFilterOnlySessionsList.contains(sessionName);
                boolean isRemoteOnlySession = remoteFilterOnlySessionsList.contains(sessionName);
                boolean doLocal  = (isLocalOnlySession  || !isRemoteOnlySession);
                boolean doRemote = (isRemoteOnlySession || !isLocalOnlySession);

                if (doLocal)
                {
                    int channelType = ChannelType.CB_CURRENT_MARKET_BY_CLASS_V2;
                    subscribeMarketDataForClassByType(sessionName, classKey, channelType, proxyListener);
                    publishMarketData(channelType,sessionName,classKey,timeoutListener,actionOnQueue);
                }

                if (doRemote)
                {
                    ServicesHelper.getMarketDataRequestPublisher().publishCurrentMarketSubscriptionV2(
                            this,
                            userId,
                            userIor,
                            sessionName,
                            classKey,
                            0,
                            proxyListener,
                            actionOnQueue
                    );
                }
            }
        }
        catch (NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }
    }

    public MarketDataHistoryDetailStruct getDetailMarketDataHistoryByTime(String sessionName, int productKey, DateTimeStruct startTime, short direction)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        return getUserMarketQuery().getDetailMarketDataHistoryByTime(sessionName, productKey, startTime, direction);
    }

    public MarketDataHistoryDetailStruct getPriorityMarketDataHistoryByTime(String sessionName, int productKey, DateTimeStruct startTime, short direction)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        return getUserMarketQuery().getPriorityMarketDataHistoryByTime(sessionName, productKey, startTime, direction);
    }

    public void subscribeCurrentMarketForClassV3(String sessionName, int classKey, com.cboe.idl.cmiCallbackV3.CMICurrentMarketConsumer clientListener, short actionOnQueue)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling subscribeCurrentMarketForClass3 for " + currentSession);
        }
        try
        {
            if (clientListener != null)
            {
                getUserMarketQuery().verifyUserCurrentMarketEnablement(sessionName, classKey);
                com.cboe.idl.cmiCallbackV3.CMICurrentMarketConsumer timeoutListener =
                                com.cboe.idl.cmiCallbackV3.CMICurrentMarketConsumerHelper.narrow((org.omg.CORBA.Object) RemoteConnectionFactory.find().setRoundTripTimeout(clientListener, marketDataCallbackTimeout));
                ChannelListener proxyListener = ServicesHelper.getCurrentMarketV3ConsumerProxy(timeoutListener, currentSession, actionOnQueue);
                boolean isLocalOnlySession  = localFilterOnlySessionsList.contains(sessionName);
                boolean isRemoteOnlySession = remoteFilterOnlySessionsList.contains(sessionName);
                boolean doLocal  = (isLocalOnlySession  || !isRemoteOnlySession);
                boolean doRemote = (isRemoteOnlySession || !isLocalOnlySession);

                if (doLocal)
                {
                    int channelType = ChannelType.CB_CURRENT_MARKET_BY_CLASS_V3;
                    subscribeMarketDataForClassByType(sessionName, classKey, channelType, proxyListener);
                    publishMarketData(channelType,sessionName,classKey,timeoutListener,actionOnQueue);
                }

                if (doRemote)
                {
                    ServicesHelper.getMarketDataRequestPublisher().publishCurrentMarketSubscriptionV3(
                            this,
                            userId,
                            userIor,
                            sessionName,
                            classKey,
                            0,
                            proxyListener,
                            actionOnQueue
                    );
                }
            }
        }
        catch (NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }
    }

    public void unsubscribeCurrentMarketForProductV2(String sessionName, int productKey, com.cboe.idl.cmiCallbackV2.CMICurrentMarketConsumer clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling unsubscribeCurrentMarketForProductV2 for " + currentSession);
        }
        try
        {
            ProductStruct product = getProduct(productKey);
            int classKey = product.productKeys.classKey;
            //verify if it is enabled
            getUserMarketQuery().verifyUserCurrentMarketEnablement(sessionName, classKey);
            ChannelListener proxyListener = ServicesHelper.getCurrentMarketV2ConsumerProxy(clientListener, currentSession, (short) 0);
            boolean isLocalOnlySession  = localFilterOnlySessionsList.contains(sessionName);
            boolean isRemoteOnlySession = remoteFilterOnlySessionsList.contains(sessionName);
            boolean doLocal  = (isLocalOnlySession  || !isRemoteOnlySession);
            boolean doRemote = (isRemoteOnlySession || !isLocalOnlySession);

            if (doLocal)
            {
                int channelType = ChannelType.CB_CURRENT_MARKET_BY_PRODUCT_V2;
                unsubscribeMarketDataForProductByType(sessionName, classKey, productKey, channelType, proxyListener);
            }

            if (doRemote)
            {
                ServicesHelper.getMarketDataRequestPublisher().publishCurrentMarketUnSubscriptionV2(
                        this,
                        userId,
                        userIor,
                        sessionName,
                        classKey,
                        productKey,
                        proxyListener
                );
            }
        }
        catch (NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }
    }

    public void unsubscribeCurrentMarketForProductV3(String sessionName, int productKey, com.cboe.idl.cmiCallbackV3.CMICurrentMarketConsumer clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling unsubscribeCurrentMarketForProductV3 for " + currentSession);
        }
        try
        {
            ProductStruct product = getProduct(productKey);
            int classKey = product.productKeys.classKey;
            //verify if it is enabled
            getUserMarketQuery().verifyUserCurrentMarketEnablement(sessionName, classKey);
            ChannelListener proxyListener = ServicesHelper.getCurrentMarketV3ConsumerProxy(clientListener, currentSession, (short) 0);
            boolean isLocalOnlySession  = localFilterOnlySessionsList.contains(sessionName);
            boolean isRemoteOnlySession = remoteFilterOnlySessionsList.contains(sessionName);
            boolean doLocal  = (isLocalOnlySession  || !isRemoteOnlySession);
            boolean doRemote = (isRemoteOnlySession || !isLocalOnlySession);

            if (doLocal)
            {
                int channelType = ChannelType.CB_CURRENT_MARKET_BY_PRODUCT_V3;
                unsubscribeMarketDataForProductByType(sessionName, classKey, productKey, channelType, proxyListener);
            }

            if (doRemote)
            {
                ServicesHelper.getMarketDataRequestPublisher().publishCurrentMarketUnSubscriptionV3(
                        this,
                        userId,
                        userIor,
                        sessionName,
                        classKey,
                        productKey,
                        proxyListener
                );
            }
        }
        catch (NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }
    }

    public void unsubscribeCurrentMarketForProduct(String sessionName, int productKey, com.cboe.idl.cmiCallback.CMICurrentMarketConsumer clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling unsubscribeCurrentMarketForProduct for " + currentSession);
        }
        try
        {
            ProductStruct product = getProduct(productKey);
            int classKey = product.productKeys.classKey;
            //verify if it is enabled
            getUserMarketQuery().verifyUserCurrentMarketEnablement(sessionName, classKey);
            ChannelListener proxyListener = ServicesHelper.getCurrentMarketConsumerProxy(clientListener, currentSession);
            boolean isLocalOnlySession  = localFilterOnlySessionsList.contains(sessionName);
            boolean isRemoteOnlySession = remoteFilterOnlySessionsList.contains(sessionName);
            boolean doLocal  = (isLocalOnlySession  || !isRemoteOnlySession);
            boolean doRemote = (isRemoteOnlySession || !isLocalOnlySession);

            if (doLocal)
            {
                int channelType = ChannelType.CB_CURRENT_MARKET_BY_PRODUCT;
                unsubscribeMarketDataForProductByType(sessionName, classKey, productKey, channelType, proxyListener);
            }

            if (doRemote)
            {
                ServicesHelper.getMarketDataRequestPublisher().publishCurrentMarketUnSubscription(
                            this,
                            userId,
                            userIor,
                            sessionName,
                            classKey,
                            productKey,
                            proxyListener
                );
            }
        }
        catch (NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }
    }

    public void unsubscribeCurrentMarketForClassV2(String sessionName, int classKey, com.cboe.idl.cmiCallbackV2.CMICurrentMarketConsumer clientListener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling unsubscribeCurrentMarketForClassV2 for " + currentSession);
        }
        try
        {
            getUserMarketQuery().verifyUserCurrentMarketEnablement(sessionName, classKey);
            ChannelListener proxyListener = ServicesHelper.getCurrentMarketV2ConsumerProxy(clientListener, currentSession, (short) 0);
            boolean isLocalOnlySession  = localFilterOnlySessionsList.contains(sessionName);
            boolean isRemoteOnlySession = remoteFilterOnlySessionsList.contains(sessionName);
            boolean doLocal  = (isLocalOnlySession  || !isRemoteOnlySession);
            boolean doRemote = (isRemoteOnlySession || !isLocalOnlySession);

            if (doLocal)
            {
                int channelType = ChannelType.CB_CURRENT_MARKET_BY_CLASS_V2;
                unsubscribeMarketDataForClassByType(sessionName, classKey, channelType, proxyListener);
            }

            if (doRemote)
            {
                ServicesHelper.getMarketDataRequestPublisher().publishCurrentMarketUnSubscriptionV2(
                        this,
                        userId,
                        userIor,
                        sessionName,
                        classKey,
                        0,
                        proxyListener
                );
            }
        }
        catch (NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }
    }

    public void unsubscribeCurrentMarketForClassV3(String sessionName, int classKey, com.cboe.idl.cmiCallbackV3.CMICurrentMarketConsumer clientListener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling unsubscribeCurrentMarketForClassV3 for " + currentSession);
        }
        try
        {
            getUserMarketQuery().verifyUserCurrentMarketEnablement(sessionName, classKey);
            ChannelListener proxyListener = ServicesHelper.getCurrentMarketV3ConsumerProxy(clientListener, currentSession, (short) 0);
            boolean isLocalOnlySession  = localFilterOnlySessionsList.contains(sessionName);
            boolean isRemoteOnlySession = remoteFilterOnlySessionsList.contains(sessionName);
            boolean doLocal  = (isLocalOnlySession  || !isRemoteOnlySession);
            boolean doRemote = (isRemoteOnlySession || !isLocalOnlySession);

            if (doLocal)
            {
                int channelType = ChannelType.CB_CURRENT_MARKET_BY_CLASS_V3;
                unsubscribeMarketDataForClassByType(sessionName, classKey, channelType, proxyListener);
            }

            if (doRemote)
            {
                ServicesHelper.getMarketDataRequestPublisher().publishCurrentMarketUnSubscriptionV3(
                        this,
                        userId,
                        userIor,
                        sessionName,
                        classKey,
                        0,
                        proxyListener
                );
            }
        }
        catch (NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }
    }

    public void unsubscribeCurrentMarketForClass(String sessionName, int classKey, com.cboe.idl.cmiCallback.CMICurrentMarketConsumer clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling unsubscribeCurrentMarketForClass for " + currentSession);
        }
        try
        {
            getUserMarketQuery().verifyUserCurrentMarketEnablement(sessionName, classKey);
            ChannelListener proxyListener = ServicesHelper.getCurrentMarketConsumerProxy(clientListener, currentSession);
            boolean isLocalOnlySession  = localFilterOnlySessionsList.contains(sessionName);
            boolean isRemoteOnlySession = remoteFilterOnlySessionsList.contains(sessionName);
            boolean doLocal  = (isLocalOnlySession  || !isRemoteOnlySession);
            boolean doRemote = (isRemoteOnlySession || !isLocalOnlySession);

            if (doLocal)
            {
                int channelType = ChannelType.CB_CURRENT_MARKET_BY_CLASS;
                unsubscribeMarketDataForClassByType(sessionName, classKey, channelType, proxyListener);
            }

            if (doRemote)
            {
                ServicesHelper.getMarketDataRequestPublisher().publishCurrentMarketUnSubscription(
                            this,
                            userId,
                            userIor,
                            sessionName,
                            classKey,
                            0,
                            proxyListener
                );
            }
        }
        catch (NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }
    }

//-----NBBO-------

    public void subscribeNBBOForProductV2(String sessionName, int productKey, com.cboe.idl.cmiCallbackV2.CMINBBOConsumer clientListener, short actionOnQueue)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling subscribeNBBOForProductV2 for " + currentSession);
        }
        try
        {
            ProductStruct product = getProduct(productKey);
            int classKey = product.productKeys.classKey;
            //verify if it is enabled
            getUserMarketQuery().verifyUserNBBOEnablement(sessionName, classKey);
            com.cboe.idl.cmiCallbackV2.CMINBBOConsumer timeoutListener =
                            com.cboe.idl.cmiCallbackV2.CMINBBOConsumerHelper.narrow((org.omg.CORBA.Object) RemoteConnectionFactory.find().setRoundTripTimeout(clientListener, marketDataCallbackTimeout));
            ChannelListener proxyListener = ServicesHelper.getNBBOV2ConsumerProxy(timeoutListener, currentSession, actionOnQueue);
            if (clientListener != null)
            {
                boolean isLocalOnlySession  = localFilterOnlySessionsList.contains(sessionName);
                boolean isRemoteOnlySession = remoteFilterOnlySessionsList.contains(sessionName);
                boolean doLocal  = (isLocalOnlySession  || !isRemoteOnlySession);
                boolean doRemote = (isRemoteOnlySession || !isLocalOnlySession);

                if (doLocal)
                {
                    int channelType = ChannelType.CB_NBBO_BY_PRODUCT_V2;
                    subscribeMarketDataForProductByType(sessionName, classKey, productKey, channelType, proxyListener);
                    publishMarketData(channelType,sessionName,productKey,timeoutListener,actionOnQueue);
                }

                if (doRemote)
                {
                    ServicesHelper.getMarketDataRequestPublisher().publishNBBOSubscriptionV2(
                            this,
                            userId,
                            userIor,
                            sessionName,
                            classKey,
                            productKey,
                            proxyListener,
                            actionOnQueue
                    );
                }
            }
        }
        catch (NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }
    }

    public void subscribeNBBOForProduct(String sessionName, int productKey,com.cboe.idl.cmiCallback.CMINBBOConsumer clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling subscribeNBBOForProduct for " + currentSession);
        }
        try
        {
            ProductStruct product = getProduct(productKey);
            int classKey = product.productKeys.classKey;
            //verify if it is enabled
            getUserMarketQuery().verifyUserNBBOEnablement(sessionName, classKey);
            com.cboe.idl.cmiCallback.CMINBBOConsumer timeoutListener =
                            com.cboe.idl.cmiCallback.CMINBBOConsumerHelper.narrow((org.omg.CORBA.Object) RemoteConnectionFactory.find().setRoundTripTimeout(clientListener, marketDataCallbackTimeout));
            ChannelListener proxyListener = ServicesHelper.getNBBOConsumerProxy(timeoutListener, currentSession);
            if (clientListener != null)
            {
                boolean isLocalOnlySession  = localFilterOnlySessionsList.contains(sessionName);
                boolean isRemoteOnlySession = remoteFilterOnlySessionsList.contains(sessionName);
                boolean doLocal  = (isLocalOnlySession  || !isRemoteOnlySession);
                boolean doRemote = (isRemoteOnlySession || !isLocalOnlySession);

                if (doLocal)
                {
                    int channelType = ChannelType.CB_NBBO_BY_PRODUCT;
                    subscribeMarketDataForProductByType(sessionName, classKey, productKey, channelType, proxyListener);
                    publishMarketData(channelType,sessionName,productKey,timeoutListener,(short)0);
                }

                if (doRemote)
                {
                    ServicesHelper.getMarketDataRequestPublisher().publishNBBOSubscription(
                            this,
                            userId,
                            userIor,
                            sessionName,
                            classKey,
                            productKey,
                            proxyListener
                    );
                }
            }
        }
        catch (NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }
    }

    public void subscribeNBBOForClassV2(String sessionName, int classKey, com.cboe.idl.cmiCallbackV2.CMINBBOConsumer clientListener, short actionOnQueue)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling subscribeNBBOForClassV2 for " + currentSession);
        }
        try
        {
            if (clientListener != null)
            {
                getUserMarketQuery().verifyUserNBBOEnablement(sessionName, classKey);
                com.cboe.idl.cmiCallbackV2.CMINBBOConsumer timeoutListener =
                                com.cboe.idl.cmiCallbackV2.CMINBBOConsumerHelper.narrow((org.omg.CORBA.Object) RemoteConnectionFactory.find().setRoundTripTimeout(clientListener, marketDataCallbackTimeout));
                ChannelListener proxyListener = ServicesHelper.getNBBOV2ConsumerProxy(timeoutListener, currentSession, actionOnQueue);
                boolean isLocalOnlySession  = localFilterOnlySessionsList.contains(sessionName);
                boolean isRemoteOnlySession = remoteFilterOnlySessionsList.contains(sessionName);
                boolean doLocal  = (isLocalOnlySession  || !isRemoteOnlySession);
                boolean doRemote = (isRemoteOnlySession || !isLocalOnlySession);

                if (doLocal)
                {
                    int channelType = ChannelType.CB_NBBO_BY_CLASS_V2;
                    subscribeMarketDataForClassByType(sessionName, classKey, channelType, proxyListener);
                    publishMarketData(channelType,sessionName,classKey,timeoutListener,actionOnQueue);
                }

                if (doRemote)
                {
                    ServicesHelper.getMarketDataRequestPublisher().publishNBBOSubscriptionV2(
                            this,
                            userId,
                            userIor,
                            sessionName,
                            classKey,
                            0,
                            proxyListener,
                            actionOnQueue
                    );
                }
            }
        }
        catch (NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }
    }

    public void subscribeNBBOForClass(String sessionName, int classKey, com.cboe.idl.cmiCallback.CMINBBOConsumer clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling subscribeNBBOForClass for " + currentSession);
        }
        try
        {
            if (clientListener != null)
            {
                getUserMarketQuery().verifyUserNBBOEnablement(sessionName, classKey);
                com.cboe.idl.cmiCallback.CMINBBOConsumer timeoutListener =
                                com.cboe.idl.cmiCallback.CMINBBOConsumerHelper.narrow((org.omg.CORBA.Object) RemoteConnectionFactory.find().setRoundTripTimeout(clientListener, marketDataCallbackTimeout));
                ChannelListener proxyListener = ServicesHelper.getNBBOConsumerProxy(timeoutListener, currentSession);
                boolean isLocalOnlySession  = localFilterOnlySessionsList.contains(sessionName);
                boolean isRemoteOnlySession = remoteFilterOnlySessionsList.contains(sessionName);
                boolean doLocal  = (isLocalOnlySession  || !isRemoteOnlySession);
                boolean doRemote = (isRemoteOnlySession || !isLocalOnlySession);

                if (doLocal)
                {
                    int channelType = ChannelType.CB_NBBO_BY_CLASS;
                    subscribeMarketDataForClassByType(sessionName, classKey, channelType, proxyListener);
                    publishMarketData(channelType,sessionName,classKey,timeoutListener,(short)0);
                }

                if (doRemote)
                {
                    ServicesHelper.getMarketDataRequestPublisher().publishNBBOSubscription(
                            this,
                            userId,
                            userIor,
                            sessionName,
                            classKey,
                            0,
                            proxyListener
                    );
                }
            }
        }
        catch (NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }
    }

    public void unsubscribeNBBOForProductV2(String sessionName, int productKey, com.cboe.idl.cmiCallbackV2.CMINBBOConsumer clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling unsubscribeNBBOForProductV2 for " + currentSession);
        }
        try
        {
            ProductStruct product = getProduct(productKey);
            int classKey = product.productKeys.classKey;
            //verify if it is enabled
            getUserMarketQuery().verifyUserNBBOEnablement(sessionName, classKey);
            ChannelListener proxyListener = ServicesHelper.getNBBOV2ConsumerProxy(clientListener, currentSession, (short) 0);
            boolean isLocalOnlySession  = localFilterOnlySessionsList.contains(sessionName);
            boolean isRemoteOnlySession = remoteFilterOnlySessionsList.contains(sessionName);
            boolean doLocal  = (isLocalOnlySession  || !isRemoteOnlySession);
            boolean doRemote = (isRemoteOnlySession || !isLocalOnlySession);

            if (doLocal)
            {
                int channelType = ChannelType.CB_NBBO_BY_PRODUCT_V2;
                unsubscribeMarketDataForProductByType(sessionName, classKey, productKey, channelType, proxyListener);
            }

            if (doRemote)
            {
                ServicesHelper.getMarketDataRequestPublisher().publishNBBOUnSubscriptionV2(
                        this,
                        userId,
                        userIor,
                        sessionName,
                        classKey,
                        productKey,
                        proxyListener
                );
            }
        }
        catch (NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }
    }

    public void unsubscribeNBBOForProduct(String sessionName, int productKey, com.cboe.idl.cmiCallback.CMINBBOConsumer clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling unsubscribeNBBOForProduct for " + currentSession);
        }
        try
        {
            ProductStruct product = getProduct(productKey);
            int classKey = product.productKeys.classKey;
            //verify if it is enabled
            getUserMarketQuery().verifyUserNBBOEnablement(sessionName, classKey);
            ChannelListener proxyListener = ServicesHelper.getNBBOConsumerProxy(clientListener, currentSession);
            boolean isLocalOnlySession  = localFilterOnlySessionsList.contains(sessionName);
            boolean isRemoteOnlySession = remoteFilterOnlySessionsList.contains(sessionName);
            boolean doLocal  = (isLocalOnlySession  || !isRemoteOnlySession);
            boolean doRemote = (isRemoteOnlySession || !isLocalOnlySession);

            if (doLocal)
            {
                int channelType = ChannelType.CB_NBBO_BY_PRODUCT;
                unsubscribeMarketDataForProductByType(sessionName, classKey, productKey, channelType, proxyListener);
            }

            if (doRemote)
            {
                ServicesHelper.getMarketDataRequestPublisher().publishNBBOUnSubscription(
                        this,
                        userId,
                        userIor,
                        sessionName,
                        classKey,
                        productKey,
                        proxyListener
                );
            }
        }
        catch (NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }
    }

    public void unsubscribeNBBOForClassV2(String sessionName, int classKey, com.cboe.idl.cmiCallbackV2.CMINBBOConsumer clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling unsubscribeNBBOForClassV2 for " + currentSession);
        }
        try
        {
            getUserMarketQuery().verifyUserNBBOEnablement(sessionName, classKey);
            ChannelListener proxyListener = ServicesHelper.getNBBOV2ConsumerProxy(clientListener, currentSession, (short) 0);
            boolean isLocalOnlySession  = localFilterOnlySessionsList.contains(sessionName);
            boolean isRemoteOnlySession = remoteFilterOnlySessionsList.contains(sessionName);
            boolean doLocal  = (isLocalOnlySession  || !isRemoteOnlySession);
            boolean doRemote = (isRemoteOnlySession || !isLocalOnlySession);

            if (doLocal)
            {
                int channelType = ChannelType.CB_NBBO_BY_CLASS_V2;
                unsubscribeMarketDataForClassByType(sessionName, classKey, channelType, proxyListener);
            }

            if (doRemote)
            {
                ServicesHelper.getMarketDataRequestPublisher().publishNBBOUnSubscriptionV2(
                        this,
                        userId,
                        userIor,
                        sessionName,
                        classKey,
                        0,
                        proxyListener
                );
            }
        }
        catch (NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }
    }

    public void unsubscribeNBBOForClass(String sessionName, int classKey, com.cboe.idl.cmiCallback.CMINBBOConsumer clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling unsubscribeNBBOForClass for " + currentSession);
        }
        try
        {
            getUserMarketQuery().verifyUserNBBOEnablement(sessionName, classKey);
            ChannelListener proxyListener = ServicesHelper.getNBBOConsumerProxy(clientListener, currentSession);
            boolean isLocalOnlySession  = localFilterOnlySessionsList.contains(sessionName);
            boolean isRemoteOnlySession = remoteFilterOnlySessionsList.contains(sessionName);
            boolean doLocal  = (isLocalOnlySession  || !isRemoteOnlySession);
            boolean doRemote = (isRemoteOnlySession || !isLocalOnlySession);

            if (doLocal)
            {
                int channelType = ChannelType.CB_NBBO_BY_CLASS;
                unsubscribeMarketDataForClassByType(sessionName, classKey, channelType, proxyListener);
            }

            if (doRemote)
            {
                ServicesHelper.getMarketDataRequestPublisher().publishNBBOUnSubscription(
                        this,
                        userId,
                        userIor,
                        sessionName,
                        classKey,
                        0,
                        proxyListener
                );
            }
        }
        catch (NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }
    }

//----------BOOKDEPTH--------------

    public void subscribeBookDepthForClassV2(String sessionName, int classKey, com.cboe.idl.cmiCallbackV2.CMIOrderBookConsumer clientListener, short actionOnQueue)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling subscribeBookDepthForClassV2 for " + currentSession);
        }
        try
        {
            if (clientListener != null)
            {
                //verify if it is enabled
                getUserMarketQuery().verifyUserBookDepthEnablement(sessionName, classKey);
                com.cboe.idl.cmiCallbackV2.CMIOrderBookConsumer timeoutListener =
                                com.cboe.idl.cmiCallbackV2.CMIOrderBookConsumerHelper.narrow((org.omg.CORBA.Object) RemoteConnectionFactory.find().setRoundTripTimeout(clientListener, marketDataCallbackTimeout));
                ChannelListener proxyListener = ServicesHelper.getBookDepthV2ConsumerProxy(timeoutListener, currentSession, actionOnQueue);
                boolean isLocalOnlySession  = localFilterOnlySessionsList.contains(sessionName);
                boolean isRemoteOnlySession = remoteFilterOnlySessionsList.contains(sessionName);
                boolean doLocal  = (isLocalOnlySession  || !isRemoteOnlySession);
                boolean doRemote = (isRemoteOnlySession || !isLocalOnlySession);

                if (doLocal)
                {
                    int channelType = ChannelType.CB_BOOK_DEPTH_BY_CLASS_V2;
                    subscribeMarketDataForClassByType(sessionName, classKey, channelType, proxyListener);
                    publishMarketData(channelType,sessionName,classKey,timeoutListener,actionOnQueue);
                }

                if (doRemote)
                {
                    ServicesHelper.getMarketDataRequestPublisher().publishBookDepthSubscriptionV2(
                            this,
                            userId,
                            userIor,
                            sessionName,
                            classKey,
                            0,
                            proxyListener,
                            actionOnQueue
                    );
                }
            }
        }
        catch (NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }

    }

    public void subscribeBookDepthForProductV2(String sessionName, int productKey, com.cboe.idl.cmiCallbackV2.CMIOrderBookConsumer clientListener, short actionOnQueue)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling subscribeBookDepthForProductV2 for " + currentSession);
        }
        try
        {
            if (clientListener != null)
            {
                ProductStruct product = getProduct(productKey);
                int classKey = product.productKeys.classKey;
                //verify if it is enabled
                getUserMarketQuery().verifyUserBookDepthEnablement(sessionName, classKey);
                com.cboe.idl.cmiCallbackV2.CMIOrderBookConsumer timeoutListener =
                                com.cboe.idl.cmiCallbackV2.CMIOrderBookConsumerHelper.narrow((org.omg.CORBA.Object) RemoteConnectionFactory.find().setRoundTripTimeout(clientListener, marketDataCallbackTimeout));
                ChannelListener proxyListener = ServicesHelper.getBookDepthV2ConsumerProxy(timeoutListener, currentSession, actionOnQueue);
                boolean isLocalOnlySession  = localFilterOnlySessionsList.contains(sessionName);
                boolean isRemoteOnlySession = remoteFilterOnlySessionsList.contains(sessionName);
                boolean doLocal  = (isLocalOnlySession  || !isRemoteOnlySession);
                boolean doRemote = (isRemoteOnlySession || !isLocalOnlySession);

                if (doLocal)
                {
                    int channelType = ChannelType.CB_BOOK_DEPTH_BY_PRODUCT_V2;
                    subscribeMarketDataForProductByType(sessionName, classKey, productKey, channelType, proxyListener);
                    publishMarketData(channelType,sessionName,productKey,timeoutListener,actionOnQueue);
                }

                if (doRemote)
                {
                    ServicesHelper.getMarketDataRequestPublisher().publishBookDepthSubscriptionV2(
                            this,
                            userId,
                            userIor,
                            sessionName,
                            classKey,
                            productKey,
                            proxyListener,
                            actionOnQueue
                    );
                }
            }
        }
        catch (NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }
    }

    public void subscribeBookDepthUpdateForClassV2(String sessionName, int classKey, com.cboe.idl.cmiCallbackV2.CMIOrderBookUpdateConsumer cmiOrderBookUpdateConsumer, short i1)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling subscribeBookDepthUpdateForClassV2 for " + currentSession);
        }

        //verify if it is enabled
        getUserMarketQuery().verifyUserBookDepthEnablement(sessionName, classKey);

        // until server implements this, throw not impletmented;
        if (true)
        {
            throw ExceptionBuilder.authorizationException("subscribeBookDepthUpdateForClassV2 NOT IMPLETMENTED!", AuthenticationCodes.FUNCTION_NOT_IMPLEMENTED);
        }
    }

    public void subscribeBookDepthUpdateForProductV2(String sessionName, int productKey, com.cboe.idl.cmiCallbackV2.CMIOrderBookUpdateConsumer cmiOrderBookUpdateConsumer, short i1)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling subscribeBookDepthUpdateForProductV2 for " + currentSession);
        }
        try
        {
            ProductStruct product = currentSession.getProductQuery().getProductByKey(productKey);
            int classKey = product.productKeys.classKey;
            //verify if it is enabled
            getUserMarketQuery().verifyUserBookDepthEnablement(sessionName, classKey);
        } catch (NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }

        // until server implements this, throw not impletmented;
        if (true)
        {
            throw ExceptionBuilder.authorizationException("subscribeBookDepthUpdateForProductV2 NOT IMPLETMENTED!", AuthenticationCodes.FUNCTION_NOT_IMPLEMENTED);
        }
    }

    public void unsubscribeBookDepthForClassV2(String sessionName, int classKey, com.cboe.idl.cmiCallbackV2.CMIOrderBookConsumer clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling unsubscribeBookDepthForClassV2 for " + currentSession);
        }
        try
        {
            getUserMarketQuery().verifyUserBookDepthEnablement(sessionName, classKey);
            ChannelListener proxyListener = ServicesHelper.getBookDepthV2ConsumerProxy(clientListener, currentSession, (short) 0);
            boolean isLocalOnlySession  = localFilterOnlySessionsList.contains(sessionName);
            boolean isRemoteOnlySession = remoteFilterOnlySessionsList.contains(sessionName);
            boolean doLocal  = (isLocalOnlySession  || !isRemoteOnlySession);
            boolean doRemote = (isRemoteOnlySession || !isLocalOnlySession);
            if (doLocal)
            {
                int channelType = ChannelType.CB_BOOK_DEPTH_BY_CLASS_V2;
                unsubscribeMarketDataForClassByType(sessionName, classKey, channelType, proxyListener);
            }
            if (doRemote)
            {
                ServicesHelper.getMarketDataRequestPublisher().publishBookDepthUnSubscriptionV2(
                        this,
                        userId,
                        userIor,
                        sessionName,
                        classKey,
                        0,
                        proxyListener
                );
            }
        }
        catch (NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }
    }

    public void unsubscribeBookDepthForProductV2(String sessionName, int productKey, com.cboe.idl.cmiCallbackV2.CMIOrderBookConsumer clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling unsubscribeBookDepthForProductV2 for " + currentSession);
        }
        try
        {
            ProductStruct product = getProduct(productKey);
            int classKey = product.productKeys.classKey;
            //verify if it is enabled
            getUserMarketQuery().verifyUserBookDepthEnablement(sessionName, classKey);
            ChannelListener proxyListener = ServicesHelper.getBookDepthV2ConsumerProxy(clientListener, currentSession, (short) 0);

            boolean isLocalOnlySession  = localFilterOnlySessionsList.contains(sessionName);
            boolean isRemoteOnlySession = remoteFilterOnlySessionsList.contains(sessionName);
            boolean doLocal  = (isLocalOnlySession  || !isRemoteOnlySession);
            boolean doRemote = (isRemoteOnlySession || !isLocalOnlySession);

            if (doLocal)
            {
                int channelType = ChannelType.CB_BOOK_DEPTH_BY_PRODUCT_V2;
                unsubscribeMarketDataForProductByType(sessionName, classKey, productKey, channelType, proxyListener);
            }

            if (doRemote)
            {
                ServicesHelper.getMarketDataRequestPublisher().publishBookDepthUnSubscriptionV2(
                        this,
                        userId,
                        userIor,
                        sessionName,
                        classKey,
                        productKey,
                        proxyListener
                );
            }
        }
        catch (NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }
    }

    public void unsubscribeBookDepthUpdateForClassV2(String sessionName, int classKey, com.cboe.idl.cmiCallbackV2.CMIOrderBookUpdateConsumer cmiOrderBookUpdateConsumer)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling unsubscribeBookDepthUpdateForClassV2 for " + currentSession);
        }

        //verify if it is enabled
        getUserMarketQuery().verifyUserBookDepthEnablement(sessionName, classKey);

        // until server implements this, throw not impletmented;
        if (true)
        {
            throw ExceptionBuilder.authorizationException("unsubscribeBookDepthUpdateForClassV2 NOT IMPLETMENTED!", AuthenticationCodes.FUNCTION_NOT_IMPLEMENTED);
        }
    }

    public void unsubscribeBookDepthUpdateForProductV2(String sessionName, int productKey, com.cboe.idl.cmiCallbackV2.CMIOrderBookUpdateConsumer clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling unsubscribeBookDepthUpdateForProductV2 for " + currentSession);
        }
        try
        {
            ProductStruct product = currentSession.getProductQuery().getProductByKey(productKey);

            int classKey = product.productKeys.classKey;
            //verify if it is enabled
            getUserMarketQuery().verifyUserBookDepthEnablement(sessionName, classKey);

        } catch (NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }

        // until server implements this, throw not impletmented;
        if (true)
        {
            throw ExceptionBuilder.authorizationException("unsubscribeBookDepthUpdateForProductV2 NOT IMPLETMENTED!", AuthenticationCodes.FUNCTION_NOT_IMPLEMENTED);
        }
    }

    public void subscribeBookDepth(String sessionName, int productKey, com.cboe.idl.cmiCallback.CMIOrderBookConsumer clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling subscribeBookDepth for " + currentSession);
        }
        try
        {
            if (clientListener != null)
            {
                ProductStruct product = getProduct(productKey);
                int classKey = product.productKeys.classKey;
                //verify if it is enabled
                getUserMarketQuery().verifyUserBookDepthEnablement(sessionName, classKey);
                com.cboe.idl.cmiCallback.CMIOrderBookConsumer timeoutListener =
                                com.cboe.idl.cmiCallback.CMIOrderBookConsumerHelper.narrow((org.omg.CORBA.Object) RemoteConnectionFactory.find().setRoundTripTimeout(clientListener, marketDataCallbackTimeout));
                ChannelListener proxyListener = ServicesHelper.getBookDepthConsumerProxy(timeoutListener, currentSession);
                boolean isLocalOnlySession  = localFilterOnlySessionsList.contains(sessionName);
                boolean isRemoteOnlySession = remoteFilterOnlySessionsList.contains(sessionName);
                boolean doLocal  = (isLocalOnlySession  || !isRemoteOnlySession);
                boolean doRemote = (isRemoteOnlySession || !isLocalOnlySession);

                if (doLocal)
                {
                    int channelType = ChannelType.CB_BOOK_DEPTH_BY_PRODUCT;
                    subscribeMarketDataForProductByType(sessionName, classKey, productKey, channelType, proxyListener);
                    publishMarketData(channelType,sessionName,productKey,timeoutListener,(short)0);
                }

                if (doRemote)
                {
                    ServicesHelper.getMarketDataRequestPublisher().publishBookDepthSubscription(
                            this,
                            userId,
                            userIor,
                            sessionName,
                            classKey,
                            productKey,
                            proxyListener);
                }
            }
        }
        catch (NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }
    }

    public void subscribeBookDepthUpdate(String sessionName, int productKey, com.cboe.idl.cmiCallback.CMIOrderBookUpdateConsumer clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling subscribeBookDepthUpdate for " + currentSession);
        }
        try
        {
            ProductStruct product = currentSession.getProductQuery().getProductByKey(productKey);

            int classKey = product.productKeys.classKey;
            //verify if it is enabled
            getUserMarketQuery().verifyUserBookDepthEnablement(sessionName, classKey);
        } catch (NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }

        // until server implements this, throw not impletmented;
        if (true)
        {
            throw ExceptionBuilder.authorizationException("subscribeBookDepthUpdate NOT IMPLETMENTED!", AuthenticationCodes.FUNCTION_NOT_IMPLEMENTED);
        }
    }

    public void unsubscribeBookDepth(String sessionName, int productKey, com.cboe.idl.cmiCallback.CMIOrderBookConsumer clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling unsubscribeBookDepth for " + currentSession);
        }
        try
        {
            ProductStruct product = getProduct(productKey);
            int classKey = product.productKeys.classKey;
            //verify if it is enabled
            getUserMarketQuery().verifyUserBookDepthEnablement(sessionName, classKey);
            ChannelListener proxyListener = ServicesHelper.getBookDepthConsumerProxy(clientListener, currentSession);

            boolean isLocalOnlySession  = localFilterOnlySessionsList.contains(sessionName);
            boolean isRemoteOnlySession = remoteFilterOnlySessionsList.contains(sessionName);
            boolean doLocal  = (isLocalOnlySession  || !isRemoteOnlySession);
            boolean doRemote = (isRemoteOnlySession || !isLocalOnlySession);

            if (doLocal)
            {
                int channelType = ChannelType.CB_BOOK_DEPTH_BY_PRODUCT;
                unsubscribeMarketDataForProductByType(sessionName, classKey, productKey, channelType, proxyListener);
            }

            if (doRemote)
            {
                ServicesHelper.getMarketDataRequestPublisher().publishBookDepthUnSubscription(
                        this,
                        userId,
                        userIor,
                        sessionName,
                        classKey,
                        productKey,
                        proxyListener);
            }
        }
        catch (NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }
    }

    public void unsubscribeBookDepthUpdate(String sessionName, int productKey, com.cboe.idl.cmiCallback.CMIOrderBookUpdateConsumer clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling unsubscribeBookDepthUpdate for " + currentSession);
        }

        // until server implements this, throw not impletmented;
        if (true)
        {
            throw ExceptionBuilder.authorizationException("unsubscribeBookDepthUpdate NOT IMPLETMENTED!", AuthenticationCodes.FUNCTION_NOT_IMPLEMENTED);
        }
    }


//-----------TICKER--------------------


    public void subscribeTickerForClassV2(String sessionName, int classKey, com.cboe.idl.cmiCallbackV2.CMITickerConsumer clientListener, short actionOnQueue)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling subscribeTickerForClassV2 for " + currentSession);
        }
        try
        {
            if (clientListener != null)
            {
                getUserMarketQuery().verifyUserTickerEnablement(sessionName, classKey);
                com.cboe.idl.cmiCallbackV2.CMITickerConsumer timeoutListener =
                                com.cboe.idl.cmiCallbackV2.CMITickerConsumerHelper.narrow((org.omg.CORBA.Object) RemoteConnectionFactory.find().setRoundTripTimeout(clientListener, marketDataCallbackTimeout));
                ChannelListener proxyListener = ServicesHelper.getTickerV2ConsumerProxy(timeoutListener, currentSession, actionOnQueue);
                boolean isLocalOnlySession  = localFilterOnlySessionsList.contains(sessionName);
                boolean isRemoteOnlySession = remoteFilterOnlySessionsList.contains(sessionName);
                boolean doLocal  = (isLocalOnlySession  || !isRemoteOnlySession);
                boolean doRemote = (isRemoteOnlySession || !isLocalOnlySession);

                if (doLocal)
                {
                    int channelType = ChannelType.CB_TICKER_BY_CLASS_V2;
                    subscribeMarketDataForClassByType(sessionName, classKey, channelType, proxyListener);
                }

                if (doRemote)
                {
                    ServicesHelper.getMarketDataRequestPublisher().publishTickerSubscriptionV2(
                            this,
                            userId,
                            userIor,
                            sessionName,
                            classKey,
                            0,
                            proxyListener,
                            actionOnQueue
                    );
                }
            }
        }
        catch (NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }
    }

    public void subscribeTickerForProductV2(String sessionName, int productKey, com.cboe.idl.cmiCallbackV2.CMITickerConsumer clientListener, short actionOnQueue)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling subscribeTickerForProductV2 for " + currentSession);
        }
        try
        {
            ProductStruct product = getProduct(productKey);
            int classKey = product.productKeys.classKey;
            //verify if it is enabled
            getUserMarketQuery().verifyUserTickerEnablement(sessionName, classKey);
            com.cboe.idl.cmiCallbackV2.CMITickerConsumer timeoutListener =
                            com.cboe.idl.cmiCallbackV2.CMITickerConsumerHelper.narrow((org.omg.CORBA.Object) RemoteConnectionFactory.find().setRoundTripTimeout(clientListener, marketDataCallbackTimeout));
            ChannelListener proxyListener = ServicesHelper.getTickerV2ConsumerProxy(timeoutListener, currentSession, actionOnQueue);
            if (clientListener != null)
            {
                boolean isLocalOnlySession  = localFilterOnlySessionsList.contains(sessionName);
                boolean isRemoteOnlySession = remoteFilterOnlySessionsList.contains(sessionName);
                boolean doLocal  = (isLocalOnlySession  || !isRemoteOnlySession);
                boolean doRemote = (isRemoteOnlySession || !isLocalOnlySession);

                if (doLocal)
                {
                    int channelType = ChannelType.CB_TICKER_BY_PRODUCT_V2;
                    subscribeMarketDataForProductByType(sessionName, classKey, productKey, channelType, proxyListener);
                }

                if (doRemote)
                {
                    ServicesHelper.getMarketDataRequestPublisher().publishTickerSubscriptionV2(
                            this,
                            userId,
                            userIor,
                            sessionName,
                            classKey,
                            productKey,
                            proxyListener,
                            actionOnQueue
                    );
                }
            }
        }
        catch (NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }
    }

    public void unsubscribeTickerForClassV2(String sessionName, int classKey, com.cboe.idl.cmiCallbackV2.CMITickerConsumer clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling unsubscribeTickerForClassV2 for " + currentSession);
        }
        try
        {
            getUserMarketQuery().verifyUserTickerEnablement(sessionName, classKey);
            ChannelListener proxyListener = ServicesHelper.getTickerV2ConsumerProxy(clientListener, currentSession, (short) 0);
            boolean isLocalOnlySession  = localFilterOnlySessionsList.contains(sessionName);
            boolean isRemoteOnlySession = remoteFilterOnlySessionsList.contains(sessionName);
            boolean doLocal  = (isLocalOnlySession  || !isRemoteOnlySession);
            boolean doRemote = (isRemoteOnlySession || !isLocalOnlySession);

            if (doLocal)
            {
                int channelType = ChannelType.CB_TICKER_BY_CLASS_V2;
                unsubscribeMarketDataForClassByType(sessionName, classKey, channelType, proxyListener);
            }

            if (doRemote)
            {
                ServicesHelper.getMarketDataRequestPublisher().publishTickerUnSubscriptionV2(
                        this,
                        userId,
                        userIor,
                        sessionName,
                        classKey,
                        0,
                        proxyListener
                );
            }
        }
        catch (NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }
    }

    public void unsubscribeTickerForProductV2(String sessionName, int productKey, com.cboe.idl.cmiCallbackV2.CMITickerConsumer clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling unsubscribeTickerForProductV2 for " + currentSession);
        }
        try
        {
            ProductStruct product = getProduct(productKey);
            int classKey = product.productKeys.classKey;
            //verify if it is enabled
            getUserMarketQuery().verifyUserTickerEnablement(sessionName, classKey);
            ChannelListener proxyListener = ServicesHelper.getTickerV2ConsumerProxy(clientListener, currentSession, (short) 0);

            boolean isLocalOnlySession  = localFilterOnlySessionsList.contains(sessionName);
            boolean isRemoteOnlySession = remoteFilterOnlySessionsList.contains(sessionName);
            boolean doLocal  = (isLocalOnlySession  || !isRemoteOnlySession);
            boolean doRemote = (isRemoteOnlySession || !isLocalOnlySession);

            if (doLocal)
            {
                int channelType = ChannelType.CB_TICKER_BY_PRODUCT_V2;
                unsubscribeMarketDataForProductByType(sessionName, classKey, productKey, channelType, proxyListener);
            }

            if (doRemote)
            {
                ServicesHelper.getMarketDataRequestPublisher().publishTickerUnSubscriptionV2(
                        this,
                        userId,
                        userIor,
                        sessionName,
                        classKey,
                        productKey,
                        proxyListener
                );
            }
        }
        catch (NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }
    }

    public void subscribeTicker(String sessionName, int productKey, com.cboe.idl.cmiCallback.CMITickerConsumer clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling subscribeTicker for " + currentSession);
        }
        try
        {
            ProductStruct product = getProduct(productKey);
            int classKey = product.productKeys.classKey;
            //verify if it is enabled
            getUserMarketQuery().verifyUserTickerEnablement(sessionName, classKey);
            if (clientListener != null)
            {
                com.cboe.idl.cmiCallback.CMITickerConsumer timeoutListener =
                                com.cboe.idl.cmiCallback.CMITickerConsumerHelper.narrow((org.omg.CORBA.Object) RemoteConnectionFactory.find().setRoundTripTimeout(clientListener, marketDataCallbackTimeout));
                ChannelListener proxyListener = ServicesHelper.getTickerConsumerProxy(timeoutListener, currentSession);
                boolean isLocalOnlySession  = localFilterOnlySessionsList.contains(sessionName);
                boolean isRemoteOnlySession = remoteFilterOnlySessionsList.contains(sessionName);
                boolean doLocal  = (isLocalOnlySession  || !isRemoteOnlySession);
                boolean doRemote = (isRemoteOnlySession || !isLocalOnlySession);

                if (doLocal)
                {
                    int channelType = ChannelType.CB_TICKER;
                    subscribeMarketDataForProductByType(sessionName, classKey, productKey, channelType, proxyListener);
                }

                if (doRemote)
                {
                    ServicesHelper.getMarketDataRequestPublisher().publishTickerSubscription(
                            this,
                            userId,
                            userIor,
                            sessionName,
                            classKey,
                            productKey,
                            proxyListener
                    );
                }
            }
        }
        catch (NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }
    }

    public void unsubscribeTicker(String sessionName, int productKey, com.cboe.idl.cmiCallback.CMITickerConsumer clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling unsubscribeTicker for " + currentSession);
        }
        try
        {
            ProductStruct product = getProduct(productKey);
            int classKey = product.productKeys.classKey;
            //verify if it is enabled
            getUserMarketQuery().verifyUserTickerEnablement(sessionName, classKey);
            ChannelListener proxyListener = ServicesHelper.getTickerConsumerProxy(clientListener, currentSession);

            boolean isLocalOnlySession  = localFilterOnlySessionsList.contains(sessionName);
            boolean isRemoteOnlySession = remoteFilterOnlySessionsList.contains(sessionName);
            boolean doLocal  = (isLocalOnlySession  || !isRemoteOnlySession);
            boolean doRemote = (isRemoteOnlySession || !isLocalOnlySession);

            if (doLocal)
            {
                int channelType = ChannelType.CB_TICKER;
                unsubscribeMarketDataForProductByType(sessionName, classKey, productKey, channelType, proxyListener);
            }

            if (doRemote)
            {
                ServicesHelper.getMarketDataRequestPublisher().publishTickerUnSubscription(
                        this,
                        userId,
                        userIor,
                        sessionName,
                        classKey,
                        productKey,
                        proxyListener
                );
            }
        }
        catch (NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }
    }

//-----------EXPECTEDOPENINGPRICE----------------
    public void subscribeExpectedOpeningPriceForClassV2(String sessionName, int classKey, com.cboe.idl.cmiCallbackV2.CMIExpectedOpeningPriceConsumer clientListener, short actionOnQueue)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling subscribeExpectedOpeningPriceV2 for " + currentSession);
        }
        try
        {
            if (clientListener != null)
            {
                getUserMarketQuery().verifyUserExpectdOpeningPriceEnablement(sessionName, classKey);
                com.cboe.idl.cmiCallbackV2.CMIExpectedOpeningPriceConsumer timeoutListener =
                                com.cboe.idl.cmiCallbackV2.CMIExpectedOpeningPriceConsumerHelper.narrow((org.omg.CORBA.Object) RemoteConnectionFactory.find().setRoundTripTimeout(clientListener, marketDataCallbackTimeout));
                ChannelListener proxyListener = ServicesHelper.getExpectedOpeningPriceV2ConsumerProxy(timeoutListener, currentSession, actionOnQueue);
                boolean isLocalOnlySession  = localFilterOnlySessionsList.contains(sessionName);
                boolean isRemoteOnlySession = remoteFilterOnlySessionsList.contains(sessionName);
                boolean doLocal  = (isLocalOnlySession  || !isRemoteOnlySession);
                boolean doRemote = (isRemoteOnlySession || !isLocalOnlySession);

                if (doLocal)
                {
                    int channelType = ChannelType.CB_OPENING_PRICE_BY_CLASS_V2;
                    subscribeMarketDataForClassByType(sessionName, classKey, channelType, proxyListener);
                }

                if (doRemote)
                {
                    ServicesHelper.getMarketDataRequestPublisher().publishExpectedOpeningPriceSubscriptionV2(
                            this,
                            userId,
                            userIor,
                            sessionName,
                            classKey,
                            0,
                            proxyListener,
                            actionOnQueue
                    );
                }
            }
        }
        catch(NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }
    }

    public void subscribeExpectedOpeningPrice(String sessionName, int classKey, com.cboe.idl.cmiCallback.CMIExpectedOpeningPriceConsumer clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling subscribeExpectedOpeningPrice for " + currentSession);
        }
        try
        {
            if (clientListener != null)
            {
                getUserMarketQuery().verifyUserExpectdOpeningPriceEnablement(sessionName, classKey);
                com.cboe.idl.cmiCallback.CMIExpectedOpeningPriceConsumer timeoutListener =
                                com.cboe.idl.cmiCallback.CMIExpectedOpeningPriceConsumerHelper.narrow((org.omg.CORBA.Object) RemoteConnectionFactory.find().setRoundTripTimeout(clientListener, marketDataCallbackTimeout));
                ChannelListener proxyListener = ServicesHelper.getExpectedOpeningPriceConsumerProxy(timeoutListener, currentSession);
                boolean isLocalOnlySession  = localFilterOnlySessionsList.contains(sessionName);
                boolean isRemoteOnlySession = remoteFilterOnlySessionsList.contains(sessionName);
                boolean doLocal  = (isLocalOnlySession  || !isRemoteOnlySession);
                boolean doRemote = (isRemoteOnlySession || !isLocalOnlySession);

                if (doLocal)
                {
                    int channelType = ChannelType.CB_EXPECTED_OPENING_PRICE;
                    subscribeMarketDataForClassByType(sessionName, classKey, channelType, proxyListener);
                }
                if (doRemote)
                {
                    ServicesHelper.getMarketDataRequestPublisher().publishExpectedOpeningPriceSubscription(
                            this,
                            userId,
                            userIor,
                            sessionName,
                            classKey,
                            proxyListener
                    );
                }
            }
        } catch (NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }
    }

    public void subscribeExpectedOpeningPriceForProductV2(String sessionName, int productKey, com.cboe.idl.cmiCallbackV2.CMIExpectedOpeningPriceConsumer clientListener, short actionOnQueue)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling subscribeExpectedOpeningPriceV2 for " + currentSession);
        }
        try
        {
            ProductStruct product = getProduct(productKey);
            int classKey = product.productKeys.classKey;
            //verify if it is enabled
            getUserMarketQuery().verifyUserExpectdOpeningPriceEnablement(sessionName, classKey);
            com.cboe.idl.cmiCallbackV2.CMIExpectedOpeningPriceConsumer timeoutListener =
                            com.cboe.idl.cmiCallbackV2.CMIExpectedOpeningPriceConsumerHelper.narrow((org.omg.CORBA.Object) RemoteConnectionFactory.find().setRoundTripTimeout(clientListener, marketDataCallbackTimeout));
            ChannelListener proxyListener = ServicesHelper.getExpectedOpeningPriceV2ConsumerProxy(timeoutListener, currentSession, actionOnQueue);
            if (clientListener != null)
            {
                boolean isLocalOnlySession  = localFilterOnlySessionsList.contains(sessionName);
                boolean isRemoteOnlySession = remoteFilterOnlySessionsList.contains(sessionName);
                boolean doLocal  = (isLocalOnlySession  || !isRemoteOnlySession);
                boolean doRemote = (isRemoteOnlySession || !isLocalOnlySession);

                if (doLocal)
                {
                    int channelType = ChannelType.CB_OPENING_PRICE_BY_PRODUCT_V2;
                    subscribeMarketDataForProductByType(sessionName, classKey, productKey, channelType, proxyListener);
                }

                if (doRemote)
                {
                    ServicesHelper.getMarketDataRequestPublisher().publishExpectedOpeningPriceSubscriptionV2(
                            this,
                            userId,
                            userIor,
                            sessionName,
                            classKey,
                            productKey,
                            proxyListener,
                            actionOnQueue
                    );
                }
            }
        }
        catch(NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }
    }

    public void unsubscribeExpectedOpeningPriceForClassV2(String sessionName, int classKey, com.cboe.idl.cmiCallbackV2.CMIExpectedOpeningPriceConsumer clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling unsubscribeExpectedOpeningPriceForClassV2 for " + currentSession);
        }
        try
        {
            getUserMarketQuery().verifyUserExpectdOpeningPriceEnablement(sessionName, classKey);
            ChannelListener proxyListener = ServicesHelper.getExpectedOpeningPriceV2ConsumerProxy(clientListener, currentSession, (short) 0);
            boolean isLocalOnlySession  = localFilterOnlySessionsList.contains(sessionName);
            boolean isRemoteOnlySession = remoteFilterOnlySessionsList.contains(sessionName);
            boolean doLocal  = (isLocalOnlySession  || !isRemoteOnlySession);
            boolean doRemote = (isRemoteOnlySession || !isLocalOnlySession);

            if (doLocal)
            {
                int channelType = ChannelType.CB_OPENING_PRICE_BY_CLASS_V2;
                unsubscribeMarketDataForClassByType(sessionName, classKey, channelType, proxyListener);
            }

            if (doRemote)
            {
                ServicesHelper.getMarketDataRequestPublisher().publishExpectedOpeningPriceUnSubscriptionV2(
                        this,
                        userId,
                        userIor,
                        sessionName,
                        classKey,
                        0,
                        proxyListener
                );
            }
        }
        catch (NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }
    }

    public void unsubscribeExpectedOpeningPriceForProductV2(String sessionName, int productKey, com.cboe.idl.cmiCallbackV2.CMIExpectedOpeningPriceConsumer clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling unsubscribeExpectedOpeningPriceForProductV2 for " + currentSession);
        }
        try
        {
            ProductStruct product = getProduct(productKey);
            int classKey = product.productKeys.classKey;
            //verify if it is enabled
            getUserMarketQuery().verifyUserExpectdOpeningPriceEnablement(sessionName, classKey);
            ChannelListener proxyListener = ServicesHelper.getExpectedOpeningPriceV2ConsumerProxy(clientListener, currentSession, (short) 0);
            boolean isLocalOnlySession  = localFilterOnlySessionsList.contains(sessionName);
            boolean isRemoteOnlySession = remoteFilterOnlySessionsList.contains(sessionName);
            boolean doLocal  = (isLocalOnlySession  || !isRemoteOnlySession);
            boolean doRemote = (isRemoteOnlySession || !isLocalOnlySession);

            if (doLocal)
            {
                int channelType = ChannelType.CB_OPENING_PRICE_BY_PRODUCT_V2;
                unsubscribeMarketDataForProductByType(sessionName, classKey, productKey, channelType, proxyListener);
            }

            if (doRemote)
            {
                ServicesHelper.getMarketDataRequestPublisher().publishExpectedOpeningPriceUnSubscriptionV2(
                        this,
                        userId,
                        userIor,
                        sessionName,
                        classKey,
                        productKey,
                        proxyListener
                );
            }
        }
        catch (NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }
    }

    public void unsubscribeExpectedOpeningPrice(String sessionName, int classKey, com.cboe.idl.cmiCallback.CMIExpectedOpeningPriceConsumer clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling unsubscribeExpectedOpeningPrice for " + currentSession);
        }
        try
        {
            getUserMarketQuery().verifyUserExpectdOpeningPriceEnablement(sessionName, classKey);
            ChannelListener proxyListener = ServicesHelper.getExpectedOpeningPriceConsumerProxy(clientListener, currentSession);
            boolean isLocalOnlySession  = localFilterOnlySessionsList.contains(sessionName);
            boolean isRemoteOnlySession = remoteFilterOnlySessionsList.contains(sessionName);
            boolean doLocal  = (isLocalOnlySession  || !isRemoteOnlySession);
            boolean doRemote = (isRemoteOnlySession || !isLocalOnlySession);

            if (doLocal)
            {
                int channelType = ChannelType.CB_EXPECTED_OPENING_PRICE;
                unsubscribeMarketDataForClassByType(sessionName, classKey, channelType, proxyListener);
            }

            if (doRemote)
            {
                ServicesHelper.getMarketDataRequestPublisher().publishExpectedOpeningPriceUnSubscription(
                        this,
                        userId,
                        userIor,
                        sessionName,
                        classKey,
                        proxyListener
                );
            }
        }
        catch (NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }
    }


    private ProductStruct getProduct(int productKey)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        return currentSession.getProductQuery().getProductByKey(productKey);
    }

    protected ProductConfigurationService getProductConfigurationService()
    {
        if (productConfigurationService == null )
        {
            productConfigurationService = ServicesHelper.getProductConfigurationService();
        }
        return productConfigurationService;
    }

    protected UserMarketDataService getUserMarketQuery()
    {
        if(userMarketQuery == null)
        {
            userMarketQuery = ServicesHelper.getUserMarketDataService(currentSession);
        }
        return userMarketQuery;
    }

    public void acceptUserSessionLogout()
    {
        super.acceptUserSessionLogout();
        callbackRemovalProcessor.setParent(null);
        callbackRemovalProcessor = null;
        EventChannelAdapterFactory.find().removeChannelListener(callbackRemovalProcessor);
    }
	
	public void subscribeLastSaleByClass(String sessionName, int classKey, TickerConsumer clientListener, short actionOnQueue) throws SystemException, CommunicationException, DataValidationException, NotFoundException, AuthorizationException
    {
		if (Log.isDebugOn())
        {
            Log.debug(this, "calling unsubscribeLastSaleForClass for " + currentSession);
        }
        try
        {
            // No requirement for user enablement
        	// Wrap TickerConsumer in LargeTradetLastSaleConsumerProxy
            ChannelListener proxyListener = ServicesHelper.getLargeTradetLastSaleConsumerProxy(clientListener, currentSession, actionOnQueue);
            boolean isLocalOnlySession  = localFilterOnlySessionsList.contains(sessionName);
            boolean isRemoteOnlySession = remoteFilterOnlySessionsList.contains(sessionName);
            boolean doLocal  = (isLocalOnlySession  || !isRemoteOnlySession);
            boolean doRemote = (isRemoteOnlySession || !isLocalOnlySession);

            if (doLocal)
            {
                int channelType = ChannelType.CB_LARGE_TRADE_LAST_SALE_BY_CLASS;
                subscribeMarketDataForClassByType(sessionName, classKey, channelType, proxyListener);
            }
            
            // TBD
            if (doRemote)
            {
            	// get the publisher and publish to MDCAS through event channel
                ServicesHelper.getMarketDataRequestPublisher().publishLargeTradeLastSaleSubscription(
                        this,
                        userId,
                        userIor,
                        sessionName,
                        classKey,
                        0,
                        proxyListener,
                        actionOnQueue);
            }
        }
        catch (NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }
	}
	
	public void unsubscribeLastSaleByClass(String sessionName, int classKey, TickerConsumer clientListener) throws SystemException, CommunicationException, DataValidationException, NotFoundException, AuthorizationException
    {
		 if (Log.isDebugOn())
         {
             Log.debug(this, "calling subscribeLastSaleByClass for " + currentSession);
         }
	        try
	        {
	        	//	No requirement for user enablement
	            ChannelListener proxyListener = ServicesHelper.getLargeTradetLastSaleConsumerProxy(clientListener, currentSession, (short) 0);
	            boolean isLocalOnlySession  = localFilterOnlySessionsList.contains(sessionName);
	            boolean isRemoteOnlySession = remoteFilterOnlySessionsList.contains(sessionName);
	            boolean doLocal  = (isLocalOnlySession  || !isRemoteOnlySession);
	            boolean doRemote = (isRemoteOnlySession || !isLocalOnlySession);

	            if (doLocal)
	            {
	                int channelType = ChannelType.CB_LARGE_TRADE_LAST_SALE_BY_CLASS;
	                unsubscribeMarketDataForClassByType(sessionName, classKey, channelType, proxyListener);
	            }
	            
	            
	            if (doRemote)
	            {
	            	//	get the publisher and publish to MDCAS through event channel
	                ServicesHelper.getMarketDataRequestPublisher().publishLargeTradeLastSaleUnSubscription(
	                        this,
	                        userId,
	                        userIor,
	                        sessionName,
	                        classKey,
	                        0,
	                        proxyListener
	                );
	            }
	        }
	        catch (NotFoundException e)
	        {
	            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
	        }
		
	}

}
