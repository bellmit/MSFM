package com.cboe.remoteApplication.marketData;

import com.cboe.application.marketData.MarketQueryBaseImpl;
import com.cboe.application.shared.RemoteConnectionFactory;
import com.cboe.application.shared.ServicesHelper;
import com.cboe.application.shared.consumer.UserSessionLogoutProcessorFactory;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;
import com.cboe.idl.cmiErrorCodes.DataValidationCodes;
import com.cboe.idl.consumers.TickerConsumer;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.remoteApplication.RemoteCASMarketDataService;
import com.cboe.interfaces.remoteApplication.RemoteCASSessionManager;
import com.cboe.interfaces.domain.instrumentedChannel.InstrumentedChannelListener;
import com.cboe.util.ChannelType;
import com.cboe.util.ExceptionBuilder;
import com.cboe.util.channel.ChannelListener;
import com.cboe.util.event.EventChannelAdapterFactory;


/**
 * @author Jing Chen
 */
public class RemoteCASMarketDataServiceImpl extends MarketQueryBaseImpl implements RemoteCASMarketDataService
{
    public RemoteCASMarketDataServiceImpl( RemoteCASSessionManager sessionManager, int callabackTimeout)
    {
        super();
        baseSession = sessionManager;
        marketDataCallbackTimeout = callabackTimeout;
        localMarketDataService = false;
        try
        {
            this.userId = sessionManager.getUserId();
            logoutProcessor = UserSessionLogoutProcessorFactory.create(this);
            EventChannelAdapterFactory.find().addChannelListener(this, logoutProcessor, baseSession);
            initialize();
        }
         catch (Exception e)
        {
            Log.exception(this, "session : " + sessionManager, e);
        }
    }

    public void create(String name)
    {
        super.create(name);
    }

    public void subscribeRecapForClass(String sessionName, int classKey, com.cboe.idl.cmiCallback.CMIRecapConsumer clientListener)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling subscribeRecapForClass for session:" + baseSession + " classKey=" + classKey);
        }
        try
        {
            if (clientListener != null)
            {
                int channelType = ChannelType.CB_RECAP_BY_CLASS;
                com.cboe.idl.cmiCallback.CMIRecapConsumer listener =
                        com.cboe.idl.cmiCallback.CMIRecapConsumerHelper.narrow((org.omg.CORBA.Object) RemoteConnectionFactory.find().setRoundTripTimeout(clientListener, marketDataCallbackTimeout));
                ChannelListener proxyListener = ServicesHelper.getRecapConsumerProxy(listener, baseSession);
                subscribeMarketDataForClassByType(sessionName, classKey, channelType, proxyListener);
                publishMarketData(channelType, sessionName, classKey, listener, (short)0);
            }
        }
        catch (NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }
    }

    public void subscribeRecapForClassV2(String sessionName, int classKey, com.cboe.idl.cmiCallbackV2.CMIRecapConsumer clientListener, short actionOnQueue)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling subscribeRecapForClassV2 for session:" + baseSession + " classKey=" + classKey + " action=" + actionOnQueue);
        }
        try
        {
            if (clientListener != null)
            {
                int channelType = ChannelType.CB_RECAP_BY_CLASS_V2;
                com.cboe.idl.cmiCallbackV2.CMIRecapConsumer listener =
                        com.cboe.idl.cmiCallbackV2.CMIRecapConsumerHelper.narrow((org.omg.CORBA.Object) RemoteConnectionFactory.find().setRoundTripTimeout(clientListener, marketDataCallbackTimeout));
                ChannelListener proxyListener = ServicesHelper.getRecapV2ConsumerProxy(listener, baseSession, actionOnQueue);
                subscribeMarketDataForClassByType(sessionName, classKey, channelType, proxyListener);
                publishMarketData(ChannelType.CB_RECAP_BY_CLASS_V2, sessionName, classKey, listener, (short)0);
            }
        }
        catch (NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }
    }

    public void unsubscribeRecapForClassV2(String sessionName, int classKey, com.cboe.idl.cmiCallbackV2.CMIRecapConsumer clientListener)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling unsubscribeRecapForClassV2 for " + baseSession + " classKey=" + classKey);
        }
        try
        {
            int channelType = ChannelType.CB_RECAP_BY_CLASS_V2;
            ChannelListener proxyListener = ServicesHelper.getRecapV2ConsumerProxy(clientListener, baseSession, (short)0);
            unsubscribeMarketDataForClassByType(sessionName, classKey, channelType, proxyListener);
        }
        catch (NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }
    }

    public void unsubscribeRecapForClass(String sessionName, int classKey, com.cboe.idl.cmiCallback.CMIRecapConsumer clientListener)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling unsubscribeRecapForClass for " + baseSession + " classKey=" + classKey);
        }
        try
        {
            int channelType = ChannelType.CB_RECAP_BY_CLASS;
            ChannelListener proxyListener = ServicesHelper.getRecapConsumerProxy(clientListener, baseSession);
            unsubscribeMarketDataForClassByType(sessionName, classKey, channelType, proxyListener);
        }
        catch (NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }
    }


    public void subscribeRecapForProductV2(String sessionName, int classKey, int productKey, com.cboe.idl.cmiCallbackV2.CMIRecapConsumer clientListener, short actionOnQueue)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling subscribeRecapForProductV2 for " + baseSession + " classKey=" + classKey + " action=" + actionOnQueue);
        }
        try
        {
            if (clientListener != null)
            {
                int channelType = ChannelType.CB_RECAP_BY_PRODUCT_V2;
                com.cboe.idl.cmiCallbackV2.CMIRecapConsumer listener =
                        com.cboe.idl.cmiCallbackV2.CMIRecapConsumerHelper.narrow((org.omg.CORBA.Object) RemoteConnectionFactory.find().setRoundTripTimeout(clientListener, marketDataCallbackTimeout));
                ChannelListener proxyListener = ServicesHelper.getRecapV2ConsumerProxy(listener, baseSession, actionOnQueue);
                subscribeMarketDataForProductByType(sessionName, classKey, productKey, channelType, proxyListener);
                publishMarketData(channelType,sessionName,productKey,listener,actionOnQueue);
            }
        }
        catch (NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }
    }

    public void subscribeRecapForProduct(String sessionName, int classKey, int productKey, com.cboe.idl.cmiCallback.CMIRecapConsumer clientListener)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling subscribeRecapForProduct for " + baseSession + " classKey=" + classKey);
        }
        try
        {
            if (clientListener != null)
            {
                int channelType = ChannelType.CB_RECAP_BY_PRODUCT;
                com.cboe.idl.cmiCallback.CMIRecapConsumer listener =
                        com.cboe.idl.cmiCallback.CMIRecapConsumerHelper.narrow((org.omg.CORBA.Object) RemoteConnectionFactory.find().setRoundTripTimeout(clientListener, marketDataCallbackTimeout));
                ChannelListener proxyListener = ServicesHelper.getRecapConsumerProxy(listener, baseSession);
                subscribeMarketDataForProductByType(sessionName, classKey, productKey, channelType, proxyListener);
                publishMarketData(channelType,sessionName,productKey,listener,(short)0);
            }
        }
        catch (NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }
    }

    public void unsubscribeRecapForProductV2(String sessionName, int classKey, int productKey, com.cboe.idl.cmiCallbackV2.CMIRecapConsumer clientListener)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling unsubscribeRecapForProductV2 for " + baseSession + " classKey=" + classKey);
        }
        try
        {
            int channelType = ChannelType.CB_RECAP_BY_PRODUCT_V2;
            ChannelListener proxyListener = ServicesHelper.getRecapV2ConsumerProxy(clientListener, baseSession, (short)0);
            unsubscribeMarketDataForProductByType(sessionName, classKey, productKey, channelType, proxyListener);
        }
        catch (NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }
    }

    public void unsubscribeRecapForProduct(String sessionName, int classKey, int productKey, com.cboe.idl.cmiCallback.CMIRecapConsumer clientListener)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling unsubscribeRecapForProduct for " + baseSession + " classKey=" + classKey);
        }
        try
        {
            int channelType = ChannelType.CB_RECAP_BY_PRODUCT;
            ChannelListener proxyListener = ServicesHelper.getRecapConsumerProxy(clientListener, baseSession);
            unsubscribeMarketDataForProductByType(sessionName, classKey, productKey, channelType, proxyListener);
        }
        catch (NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }
    }

    public void subscribeCurrentMarketForClassV2(String sessionName, int classKey, com.cboe.idl.cmiCallbackV2.CMICurrentMarketConsumer clientListener, short actionOnQueue)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling subscribeCurrentMarketForClassV2 for " + baseSession + " classKey=" + classKey + " action=" + actionOnQueue);
        }
        try
        {
            if (clientListener != null)
            {
                int channelType = ChannelType.CB_CURRENT_MARKET_BY_CLASS_V2;
                com.cboe.idl.cmiCallbackV2.CMICurrentMarketConsumer listener =
                        com.cboe.idl.cmiCallbackV2.CMICurrentMarketConsumerHelper.narrow((org.omg.CORBA.Object) RemoteConnectionFactory.find().setRoundTripTimeout(clientListener, marketDataCallbackTimeout));
                ChannelListener proxyListener = ServicesHelper.getCurrentMarketV2ConsumerProxy(listener, baseSession, actionOnQueue);
                subscribeMarketDataForClassByType(sessionName, classKey, channelType, proxyListener);
                publishMarketData(channelType,sessionName,classKey,listener,actionOnQueue);
            }
        }
        catch (NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }
    }

    public void subscribeCurrentMarketForClassV3(String sessionName, int classKey, com.cboe.idl.cmiCallbackV3.CMICurrentMarketConsumer clientListener, short actionOnQueue)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling subscribeCurrentMarketForClassV3 for " + baseSession + " classKey=" + classKey + " action=" + actionOnQueue);
        }
        try
        {
            if (clientListener != null)
            {
                int channelType = ChannelType.CB_CURRENT_MARKET_BY_CLASS_V3;
                com.cboe.idl.cmiCallbackV3.CMICurrentMarketConsumer listener =
                        com.cboe.idl.cmiCallbackV3.CMICurrentMarketConsumerHelper.narrow((org.omg.CORBA.Object) RemoteConnectionFactory.find().setRoundTripTimeout(clientListener, marketDataCallbackTimeout));
                ChannelListener proxyListener = ServicesHelper.getCurrentMarketV3ConsumerProxy(listener, baseSession, actionOnQueue);
                subscribeMarketDataForClassByType(sessionName, classKey, channelType, proxyListener);
                publishMarketData(channelType,sessionName,classKey,listener,actionOnQueue);
            }
        }
        catch (NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }
    }

    public void subscribeCurrentMarketForClass(String sessionName, int classKey, com.cboe.idl.cmiCallback.CMICurrentMarketConsumer clientListener)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling subscribeCurrentMarketForClass for " + baseSession + " classKey=" + classKey);
        }
        try
        {
            if (clientListener != null)
            {
                int channelType = ChannelType.CB_CURRENT_MARKET_BY_CLASS;
                com.cboe.idl.cmiCallback.CMICurrentMarketConsumer listener =
                        com.cboe.idl.cmiCallback.CMICurrentMarketConsumerHelper.narrow((org.omg.CORBA.Object) RemoteConnectionFactory.find().setRoundTripTimeout(clientListener, marketDataCallbackTimeout));
                ChannelListener proxyListener = ServicesHelper.getCurrentMarketConsumerProxy(listener, baseSession);
                subscribeMarketDataForClassByType(sessionName, classKey, channelType, proxyListener);
                publishMarketData(channelType,sessionName,classKey,listener,(short)0);
            }
        }
        catch (NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }
    }

    public void unsubscribeCurrentMarketForClassV2(String sessionName, int classKey, com.cboe.idl.cmiCallbackV2.CMICurrentMarketConsumer clientListener)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling unsubscribeCurrentMarketForClassV2 for " + baseSession + " classKey=" + classKey);
        }
        try
        {
            int channelType = ChannelType.CB_CURRENT_MARKET_BY_CLASS_V2;
            ChannelListener proxyListener = ServicesHelper.getCurrentMarketV2ConsumerProxy(clientListener, baseSession, (short)0);
            unsubscribeMarketDataForClassByType(sessionName, classKey, channelType, proxyListener);
        }
        catch (NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }
    }

    public void unsubscribeCurrentMarketForClassV3(String sessionName, int classKey, com.cboe.idl.cmiCallbackV3.CMICurrentMarketConsumer clientListener)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling unsubscribeCurrentMarketForClassV3 for " + baseSession + " classKey=" + classKey);
        }
        try
        {
            int channelType = ChannelType.CB_CURRENT_MARKET_BY_CLASS_V3;
            ChannelListener proxyListener = ServicesHelper.getCurrentMarketV3ConsumerProxy(clientListener, baseSession, (short)0);
            unsubscribeMarketDataForClassByType(sessionName, classKey, channelType, proxyListener);
        }
        catch (NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }
    }

    public void unsubscribeCurrentMarketForClass(String sessionName, int classKey, com.cboe.idl.cmiCallback.CMICurrentMarketConsumer clientListener)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling unsubscribeCurrentMarketForClass for " + baseSession + " classKey=" + classKey);
        }
        try
        {
            int channelType = ChannelType.CB_CURRENT_MARKET_BY_CLASS;
            ChannelListener proxyListener = ServicesHelper.getCurrentMarketConsumerProxy(clientListener, baseSession);
            unsubscribeMarketDataForClassByType(sessionName, classKey, channelType, proxyListener);
        }
        catch (NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }
    }

    public void subscribeCurrentMarketForProductV2(String sessionName, int classKey, int productKey, com.cboe.idl.cmiCallbackV2.CMICurrentMarketConsumer clientListener, short actionOnQueue)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling subscribeCurrentMarketForProductV2 for " + baseSession + " classKey=" + classKey + " action=" + actionOnQueue);
        }
        try
        {
            if (clientListener != null)
            {
                int channelType = ChannelType.CB_CURRENT_MARKET_BY_PRODUCT_V2;
                com.cboe.idl.cmiCallbackV2.CMICurrentMarketConsumer listener =
                        com.cboe.idl.cmiCallbackV2.CMICurrentMarketConsumerHelper.narrow((org.omg.CORBA.Object) RemoteConnectionFactory.find().setRoundTripTimeout(clientListener, marketDataCallbackTimeout));
                ChannelListener proxyListener = ServicesHelper.getCurrentMarketV2ConsumerProxy(listener, baseSession, actionOnQueue);
                subscribeMarketDataForProductByType(sessionName, classKey, productKey, channelType, proxyListener);
                publishMarketData(channelType,sessionName,productKey,listener,actionOnQueue);
            }
        }
        catch (NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }

    }

    public void subscribeCurrentMarketForProductV3(String sessionName, int classKey, int productKey, com.cboe.idl.cmiCallbackV3.CMICurrentMarketConsumer clientListener, short actionOnQueue)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling subscribeCurrentMarketForProductV3 for " + baseSession + " classKey=" + classKey + " action=" + actionOnQueue);
        }
        try
        {
            if (clientListener != null)
            {
                int channelType = ChannelType.CB_CURRENT_MARKET_BY_PRODUCT_V3;
                com.cboe.idl.cmiCallbackV3.CMICurrentMarketConsumer listener =
                        com.cboe.idl.cmiCallbackV3.CMICurrentMarketConsumerHelper.narrow((org.omg.CORBA.Object) RemoteConnectionFactory.find().setRoundTripTimeout(clientListener, marketDataCallbackTimeout));
                ChannelListener proxyListener = ServicesHelper.getCurrentMarketV3ConsumerProxy(listener, baseSession, actionOnQueue);
                subscribeMarketDataForProductByType(sessionName, classKey, productKey, channelType, proxyListener);
                publishMarketData(channelType,sessionName,productKey,listener,actionOnQueue);
            }
        }
        catch (NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }

    }

   public void subscribeCurrentMarketForProduct(String sessionName, int classKey, int productKey, com.cboe.idl.cmiCallback.CMICurrentMarketConsumer clientListener)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling subscribeCurrentMarketForProduct for " + baseSession + " classKey=" + classKey);
        }
        try
        {
            if (clientListener != null)
            {

                int channelType = ChannelType.CB_CURRENT_MARKET_BY_PRODUCT;
                com.cboe.idl.cmiCallback.CMICurrentMarketConsumer listener =
                        com.cboe.idl.cmiCallback.CMICurrentMarketConsumerHelper.narrow((org.omg.CORBA.Object) RemoteConnectionFactory.find().setRoundTripTimeout(clientListener, marketDataCallbackTimeout));
                ChannelListener proxyListener = ServicesHelper.getCurrentMarketConsumerProxy(listener, baseSession);
                subscribeMarketDataForProductByType(sessionName, classKey, productKey, channelType, proxyListener);
                publishMarketData(channelType,sessionName,productKey,listener,(short)0);
            }
        }
        catch (NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }

    }

    public void unsubscribeCurrentMarketForProduct(String sessionName, int classKey, int productKey, com.cboe.idl.cmiCallback.CMICurrentMarketConsumer clientListener)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling unsubscribeCurrentMarketForProduct for " + baseSession + " classKey=" + classKey);
        }
        try
        {
            int channelType = ChannelType.CB_CURRENT_MARKET_BY_PRODUCT;
            ChannelListener proxyListener = ServicesHelper.getCurrentMarketConsumerProxy(clientListener, baseSession);
            unsubscribeMarketDataForProductByType(sessionName, classKey, productKey, channelType, proxyListener);
        }
        catch (NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }

    }

    public void unsubscribeCurrentMarketForProductV2(String sessionName, int classKey, int productKey, com.cboe.idl.cmiCallbackV2.CMICurrentMarketConsumer clientListener)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling unsubscribeCurrentMarketForProductV2 for " + baseSession + " classKey=" + classKey);
        }
        try
        {
            int channelType = ChannelType.CB_CURRENT_MARKET_BY_PRODUCT_V2;
            ChannelListener proxyListener = ServicesHelper.getCurrentMarketV2ConsumerProxy(clientListener, baseSession,(short)0);
            unsubscribeMarketDataForProductByType(sessionName, classKey, productKey, channelType, proxyListener);
        }
        catch (NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }

    }

    public void unsubscribeCurrentMarketForProductV3(String sessionName, int classKey, int productKey, com.cboe.idl.cmiCallbackV3.CMICurrentMarketConsumer clientListener)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling unsubscribeCurrentMarketForProductV3 for " + baseSession + " classKey=" + classKey);
        }
        try
        {
            int channelType = ChannelType.CB_CURRENT_MARKET_BY_PRODUCT_V3;
            ChannelListener proxyListener = ServicesHelper.getCurrentMarketV3ConsumerProxy(clientListener, baseSession,(short)0);
            unsubscribeMarketDataForProductByType(sessionName, classKey, productKey, channelType, proxyListener);
        }
        catch (NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }

    }

    public void subscribeNBBOForClassV2(String sessionName, int classKey, com.cboe.idl.cmiCallbackV2.CMINBBOConsumer clientListener, short actionOnQueue)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling subscribeNBBOForClassV2 for " + baseSession + " classKey=" + classKey + " action=" + actionOnQueue);
        }
        try
        {
            if (clientListener != null)
            {
                int channelType = ChannelType.CB_NBBO_BY_CLASS_V2;
                com.cboe.idl.cmiCallbackV2.CMINBBOConsumer listener =
                        com.cboe.idl.cmiCallbackV2.CMINBBOConsumerHelper.narrow((org.omg.CORBA.Object) RemoteConnectionFactory.find().setRoundTripTimeout(clientListener, marketDataCallbackTimeout));
                ChannelListener proxyListener = ServicesHelper.getNBBOV2ConsumerProxy(listener, baseSession,actionOnQueue);
                subscribeMarketDataForClassByType(sessionName, classKey, channelType, proxyListener);
                publishMarketData(channelType,sessionName,classKey,listener,actionOnQueue);
            }
        }
        catch (NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }
    }

    public void subscribeNBBOForClass(String sessionName, int classKey, com.cboe.idl.cmiCallback.CMINBBOConsumer clientListener)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling subscribeNBBOForClass for " + baseSession + " classKey=" + classKey);
        }
        try
        {
            if (clientListener != null)
            {
                int channelType = ChannelType.CB_NBBO_BY_CLASS;
                com.cboe.idl.cmiCallback.CMINBBOConsumer listener =
                        com.cboe.idl.cmiCallback.CMINBBOConsumerHelper.narrow((org.omg.CORBA.Object) RemoteConnectionFactory.find().setRoundTripTimeout(clientListener, marketDataCallbackTimeout));
                ChannelListener proxyListener = ServicesHelper.getNBBOConsumerProxy(listener, baseSession);
                subscribeMarketDataForClassByType(sessionName, classKey, channelType, proxyListener);
                publishMarketData(channelType,sessionName,classKey,listener,(short)0);
            }
        }
        catch (NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }
    }

    public void unsubscribeNBBOForClassV2(String sessionName, int classKey, com.cboe.idl.cmiCallbackV2.CMINBBOConsumer clientListener)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling unsubscribeNBBOForClassV2 for " + baseSession + " classKey=" + classKey);
        }
        try
        {
            int channelType = ChannelType.CB_NBBO_BY_CLASS_V2;
            ChannelListener proxyListener = ServicesHelper.getNBBOV2ConsumerProxy(clientListener, baseSession, (short)0);
            unsubscribeMarketDataForClassByType(sessionName, classKey, channelType, proxyListener);
        }
        catch (NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }

    }

    public void unsubscribeNBBOForClass(String sessionName, int classKey, com.cboe.idl.cmiCallback.CMINBBOConsumer clientListener)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling unsubscribeNBBOForClass for " + baseSession + " classKey=" + classKey);
        }
        try
        {
            int channelType = ChannelType.CB_NBBO_BY_CLASS;
            ChannelListener proxyListener = ServicesHelper.getNBBOConsumerProxy(clientListener, baseSession);
            unsubscribeMarketDataForClassByType(sessionName, classKey, channelType, proxyListener);
        }
        catch (NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }

    }

    public void subscribeNBBOForProductV2(String sessionName, int classKey, int productKey, com.cboe.idl.cmiCallbackV2.CMINBBOConsumer clientListener, short actionOnQueue)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling subscribeNBBOForProductV2 for " + baseSession + " classKey=" + classKey + " action=" + actionOnQueue);
        }
        try
        {
            if (clientListener != null)
            {
                int channelType = ChannelType.CB_NBBO_BY_PRODUCT_V2;
                com.cboe.idl.cmiCallbackV2.CMINBBOConsumer listener =
                        com.cboe.idl.cmiCallbackV2.CMINBBOConsumerHelper.narrow((org.omg.CORBA.Object) RemoteConnectionFactory.find().setRoundTripTimeout(clientListener, marketDataCallbackTimeout));
                ChannelListener proxyListener = ServicesHelper.getNBBOV2ConsumerProxy(listener, baseSession, actionOnQueue);
                subscribeMarketDataForProductByType(sessionName, classKey, productKey, channelType, proxyListener);
                publishMarketData(channelType,sessionName,productKey,listener,actionOnQueue);
            }
        }
        catch (NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }

    }


    public void subscribeNBBOForProduct(String sessionName, int classKey, int productKey, com.cboe.idl.cmiCallback.CMINBBOConsumer clientListener)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling subscribeNBBOForProduct for " + baseSession + " classKey=" + classKey);
        }
        try
        {
            if (clientListener != null)
            {
                int channelType = ChannelType.CB_NBBO_BY_PRODUCT;
                com.cboe.idl.cmiCallback.CMINBBOConsumer listener =
                        com.cboe.idl.cmiCallback.CMINBBOConsumerHelper.narrow((org.omg.CORBA.Object) RemoteConnectionFactory.find().setRoundTripTimeout(clientListener, marketDataCallbackTimeout));
                ChannelListener proxyListener = ServicesHelper.getNBBOConsumerProxy(listener, baseSession);
                subscribeMarketDataForProductByType(sessionName, classKey, productKey, channelType, proxyListener);
                publishMarketData(channelType,sessionName,productKey,listener,(short)0);
            }
        }
        catch (NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }

    }

    public void unsubscribeNBBOForProductV2(String sessionName, int classKey, int productKey, com.cboe.idl.cmiCallbackV2.CMINBBOConsumer clientListener)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling unsubscribeNBBOForProductV2 for " + baseSession + " classKey=" + classKey);
        }
        try
        {
            int channelType = ChannelType.CB_NBBO_BY_PRODUCT_V2;
            ChannelListener proxyListener = ServicesHelper.getNBBOV2ConsumerProxy(clientListener, baseSession, (short)0);
            unsubscribeMarketDataForProductByType(sessionName, classKey, productKey, channelType, proxyListener);
        }
        catch (NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }
    }

    public void unsubscribeNBBOForProduct(String sessionName, int classKey, int productKey, com.cboe.idl.cmiCallback.CMINBBOConsumer clientListener)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling unsubscribeNBBOForProduct for " + baseSession + " classKey=" + classKey);
        }
        try
        {
            int channelType = ChannelType.CB_NBBO_BY_PRODUCT;
            ChannelListener proxyListener = ServicesHelper.getNBBOConsumerProxy(clientListener, baseSession);
            unsubscribeMarketDataForProductByType(sessionName, classKey, productKey, channelType, proxyListener);
        }
        catch (NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }
    }

    public void subscribeTickerForProductV2(String sessionName, int classKey, int productKey, com.cboe.idl.cmiCallbackV2.CMITickerConsumer clientListener, short actionOnQueue)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling subscribeTickerForProductV2 for " + baseSession + " classKey=" + classKey + " action=" + actionOnQueue);
        }
        try
        {
            if (clientListener != null)
            {
                int channelType = ChannelType.CB_TICKER_BY_PRODUCT_V2;
                com.cboe.idl.cmiCallbackV2.CMITickerConsumer listener =
                        com.cboe.idl.cmiCallbackV2.CMITickerConsumerHelper.narrow((org.omg.CORBA.Object) RemoteConnectionFactory.find().setRoundTripTimeout(clientListener, marketDataCallbackTimeout));
                ChannelListener proxyListener = ServicesHelper.getTickerV2ConsumerProxy(listener, baseSession, actionOnQueue);
                subscribeMarketDataForProductByType(sessionName, classKey, productKey, channelType, proxyListener);
            }
        }
        catch (NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }
    }

    public void subscribeTickerForProduct(String sessionName, int classKey, int productKey, com.cboe.idl.cmiCallback.CMITickerConsumer clientListener)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling subscribeTickerForProduct for " + baseSession + " classKey=" + classKey);
        }
        try
        {
            if (clientListener != null)
            {
                int channelType = ChannelType.CB_TICKER;
                com.cboe.idl.cmiCallback.CMITickerConsumer listener =
                        com.cboe.idl.cmiCallback.CMITickerConsumerHelper.narrow((org.omg.CORBA.Object) RemoteConnectionFactory.find().setRoundTripTimeout(clientListener, marketDataCallbackTimeout));
                ChannelListener proxyListener = ServicesHelper.getTickerConsumerProxy(listener, baseSession);
                subscribeMarketDataForProductByType(sessionName, classKey, productKey, channelType, proxyListener);
            }
        }
        catch (NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }
    }


    public void unsubscribeTickerForProductV2(String sessionName, int classKey, int productKey, com.cboe.idl.cmiCallbackV2.CMITickerConsumer clientListener)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling unsubscribeTickerForProductV2 for " + baseSession + " classKey=" + classKey);
        }
        try
        {
            int channelType = ChannelType.CB_TICKER_BY_PRODUCT_V2;
            ChannelListener proxyListener = ServicesHelper.getTickerV2ConsumerProxy(clientListener, baseSession, (short)0);
            unsubscribeMarketDataForProductByType(sessionName, classKey, productKey, channelType, proxyListener);
        }
        catch (NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }
    }

    public void unsubscribeTickerForProduct(String sessionName, int classKey, int productKey, com.cboe.idl.cmiCallback.CMITickerConsumer clientListener)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling unsubscribeTickerForProduct for " + baseSession + " classKey=" + classKey);
        }
        try
        {
            int channelType = ChannelType.CB_TICKER;
            ChannelListener proxyListener = ServicesHelper.getTickerConsumerProxy(clientListener, baseSession);
            unsubscribeMarketDataForProductByType(sessionName, classKey, productKey, channelType, proxyListener);
        }
        catch (NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }
    }

    public void subscribeTickerForClassV2(String sessionName, int classKey, com.cboe.idl.cmiCallbackV2.CMITickerConsumer clientListener, short actionOnQueue)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling subscribeTickerForClassV2 for " + baseSession + " classKey=" + classKey + " action=" + actionOnQueue);
        }
        try
        {
            if (clientListener != null)
            {
                int channelType = ChannelType.CB_TICKER_BY_CLASS_V2;
                com.cboe.idl.cmiCallbackV2.CMITickerConsumer listener =
                        com.cboe.idl.cmiCallbackV2.CMITickerConsumerHelper.narrow((org.omg.CORBA.Object) RemoteConnectionFactory.find().setRoundTripTimeout(clientListener, marketDataCallbackTimeout));
                ChannelListener proxyListener = ServicesHelper.getTickerV2ConsumerProxy(listener, baseSession, actionOnQueue);
                subscribeMarketDataForClassByType(sessionName, classKey, channelType, proxyListener);
            }
        }
        catch (NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }
    }
    
    public void subscribeLargeTradeLastSaleForClass(String sessionName, 
    		int classKey, 
    		com.cboe.idl.consumers.TickerConsumer clientListener, 
    		short actionOnQueue)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException
    {
      if (Log.isDebugOn())
      {
          Log.debug(this, "calling subscribeLargeTradeLastSaleForClass for " + baseSession + " classKey=" + classKey + " action=" + actionOnQueue);
      }
      try
      {
          if (clientListener != null)
          {
              int channelType = ChannelType.CB_LARGE_TRADE_LAST_SALE_BY_CLASS;
              com.cboe.idl.consumers.TickerConsumer listener =
                      com.cboe.idl.consumers.TickerConsumerHelper.narrow((org.omg.CORBA.Object) RemoteConnectionFactory.find().setRoundTripTimeout(clientListener, marketDataCallbackTimeout));
              ChannelListener proxyListener = ServicesHelper.getLargeTradetLastSaleConsumerProxy(listener, baseSession, actionOnQueue);
              subscribeMarketDataForClassByType(sessionName, classKey, channelType, proxyListener);
          }
      }
      catch (NotFoundException e)
      {
          throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
      }
  }
    

    public void unsubscribeTickerForClassV2(String sessionName, int classKey, com.cboe.idl.cmiCallbackV2.CMITickerConsumer clientListener)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling unsubscribeTickerForClassV2 for " + baseSession + " classKey=" + classKey);
        }
        try
        {
            int channelType = ChannelType.CB_TICKER_BY_CLASS_V2;
            ChannelListener proxyListener = ServicesHelper.getTickerV2ConsumerProxy(clientListener, baseSession, (short)0);
            unsubscribeMarketDataForClassByType(sessionName, classKey, channelType, proxyListener);
        }
        catch (NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }
    }
    
    public void unsubscribeLargeTradeLastSaleForClass(String sessionName, int classKey, TickerConsumer clientListener) throws SystemException, CommunicationException, AuthorizationException, DataValidationException {
    	if (Log.isDebugOn())
        {
            Log.debug(this, "calling unsubscribeLargeTradeLastSaleForClass for " + baseSession + " classKey=" + classKey);
        }
        try
        {
            int channelType = ChannelType.CB_LARGE_TRADE_LAST_SALE_BY_CLASS;
            ChannelListener proxyListener = ServicesHelper.getLargeTradetLastSaleConsumerProxy(clientListener, baseSession, (short)0);
            unsubscribeMarketDataForClassByType(sessionName, classKey, channelType, proxyListener);
        }
        catch (NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }
		
	}

    public void subscribeExpectedOpeningPriceForProductV2(String sessionName, int classKey, int productKey, com.cboe.idl.cmiCallbackV2.CMIExpectedOpeningPriceConsumer clientListener, short actionOnQueue)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling subscribeExpectedOpeningPriceForProductV2 for " + baseSession + " classKey=" + classKey + " action=" + actionOnQueue);
        }
        try
        {
            if (clientListener != null)
            {
                int channelType = ChannelType.CB_OPENING_PRICE_BY_PRODUCT_V2;
                com.cboe.idl.cmiCallbackV2.CMIExpectedOpeningPriceConsumer listener =
                        com.cboe.idl.cmiCallbackV2.CMIExpectedOpeningPriceConsumerHelper.narrow((org.omg.CORBA.Object) RemoteConnectionFactory.find().setRoundTripTimeout(clientListener, marketDataCallbackTimeout));
                ChannelListener proxyListener = ServicesHelper.getExpectedOpeningPriceV2ConsumerProxy(listener, baseSession, actionOnQueue);
                subscribeMarketDataForProductByType(sessionName, classKey, productKey, channelType, proxyListener);
            }
        }
        catch(NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }
    }

    public void unsubscribeExpectedOpeningPriceForProductV2(String sessionName, int classKey, int productKey, com.cboe.idl.cmiCallbackV2.CMIExpectedOpeningPriceConsumer clientListener)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling unsubscribeExpectedOpeningPriceForProductV2 for " + baseSession + " classKey=" + classKey);
        }
        try
        {
            int channelType = ChannelType.CB_OPENING_PRICE_BY_PRODUCT_V2;
            ChannelListener proxyListener = ServicesHelper.getExpectedOpeningPriceV2ConsumerProxy(clientListener, baseSession, (short)0);
            unsubscribeMarketDataForProductByType(sessionName, classKey, productKey, channelType, proxyListener);
        }
        catch (NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }
    }

    public void subscribeExpectedOpeningPriceForClassV2(String sessionName, int classKey, com.cboe.idl.cmiCallbackV2.CMIExpectedOpeningPriceConsumer clientListener, short actionOnQueue)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling subscribeExpectedOpeningPriceForClassV2 for " + baseSession + " classKey=" + classKey);
        }
        try
        {
            if (clientListener != null)
            {
                int channelType = ChannelType.CB_OPENING_PRICE_BY_CLASS_V2;
                com.cboe.idl.cmiCallbackV2.CMIExpectedOpeningPriceConsumer listener =
                        com.cboe.idl.cmiCallbackV2.CMIExpectedOpeningPriceConsumerHelper.narrow((org.omg.CORBA.Object) RemoteConnectionFactory.find().setRoundTripTimeout(clientListener, marketDataCallbackTimeout));
                ChannelListener proxyListener = ServicesHelper.getExpectedOpeningPriceV2ConsumerProxy(listener, baseSession, actionOnQueue);
                subscribeMarketDataForClassByType(sessionName, classKey, channelType, proxyListener);
            }
        }
        catch(NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }
    }

    public void subscribeExpectedOpeningPriceForClass(String sessionName, int classKey, com.cboe.idl.cmiCallback.CMIExpectedOpeningPriceConsumer clientListener)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling subscribeExpectedOpeningPriceForClass for " + baseSession + " classKey=" + classKey);
        }
        try
        {
            if (clientListener != null)
            {
                int channelType = ChannelType.CB_EXPECTED_OPENING_PRICE;
                com.cboe.idl.cmiCallback.CMIExpectedOpeningPriceConsumer listener =
                        com.cboe.idl.cmiCallback.CMIExpectedOpeningPriceConsumerHelper.narrow((org.omg.CORBA.Object) RemoteConnectionFactory.find().setRoundTripTimeout(clientListener, marketDataCallbackTimeout));
                ChannelListener proxyListener = ServicesHelper.getExpectedOpeningPriceConsumerProxy(listener, baseSession);
                subscribeMarketDataForClassByType(sessionName, classKey, channelType, proxyListener);
            }
        }
        catch(NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }
    }

    public void unsubscribeExpectedOpeningPriceForClassV2(String sessionName, int classKey, com.cboe.idl.cmiCallbackV2.CMIExpectedOpeningPriceConsumer clientListener)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling unsubscribeExpectedOpeningPriceForClassV2 for " + baseSession + " classKey=" + classKey);
        }
        try
        {
            int channelType = ChannelType.CB_OPENING_PRICE_BY_CLASS_V2;
            ChannelListener proxyListener = ServicesHelper.getExpectedOpeningPriceV2ConsumerProxy(clientListener, baseSession, (short)0);
            unsubscribeMarketDataForClassByType(sessionName, classKey, channelType, proxyListener);
        }
        catch (NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }
    }

    public void unsubscribeExpectedOpeningPriceForClass(String sessionName, int classKey, com.cboe.idl.cmiCallback.CMIExpectedOpeningPriceConsumer clientListener)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling unsubscribeExpectedOpeningPriceForClass for " + baseSession + " classKey=" + classKey);
        }
        try
        {
            int channelType = ChannelType.CB_EXPECTED_OPENING_PRICE;
            ChannelListener proxyListener = ServicesHelper.getExpectedOpeningPriceConsumerProxy(clientListener, baseSession);
            unsubscribeMarketDataForClassByType(sessionName, classKey, channelType, proxyListener);
        }
        catch (NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }
    }

    public void subscribeBookDepthForClassV2(String sessionName, int classKey, com.cboe.idl.cmiCallbackV2.CMIOrderBookConsumer clientListener, short actionOnQueue)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling subscribeBookDepthForClassV2 for " + baseSession + " classKey=" + classKey + " action=" + actionOnQueue);
        }
        try
        {
            if (clientListener != null)
            {
                int channelType = ChannelType.CB_BOOK_DEPTH_BY_CLASS_V2;
                com.cboe.idl.cmiCallbackV2.CMIOrderBookConsumer listener =
                        com.cboe.idl.cmiCallbackV2.CMIOrderBookConsumerHelper.narrow((org.omg.CORBA.Object) RemoteConnectionFactory.find().setRoundTripTimeout(clientListener, marketDataCallbackTimeout));
                ChannelListener proxyListener = ServicesHelper.getBookDepthV2ConsumerProxy(listener, baseSession, actionOnQueue);
                subscribeMarketDataForClassByType(sessionName, classKey, channelType, proxyListener);
                publishMarketData(channelType,sessionName,classKey,listener,actionOnQueue);
            }
        }
        catch (NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }
    }

    public void unsubscribeBookDepthForClassV2(String sessionName, int classKey, com.cboe.idl.cmiCallbackV2.CMIOrderBookConsumer clientListener)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling unsubscribeBookDepthForClassV2 for " + baseSession + " classKey=" + classKey);
        }
        try
        {
            int channelType = ChannelType.CB_BOOK_DEPTH_BY_CLASS_V2;
            ChannelListener proxyListener = ServicesHelper.getBookDepthV2ConsumerProxy(clientListener, baseSession, (short)0);
            unsubscribeMarketDataForClassByType(sessionName, classKey, channelType, proxyListener);
        }
        catch (NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }
    }

    public void subscribeBookDepthForProductV2(String sessionName, int classKey, int productKey, com.cboe.idl.cmiCallbackV2.CMIOrderBookConsumer clientListener, short actionOnQueue)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling subscribeBookDepthForProductV2 for " + baseSession + " classKey=" + classKey + " action=" + actionOnQueue);
        }
        try
        {
            if (clientListener != null)
            {
                int channelType = ChannelType.CB_BOOK_DEPTH_BY_PRODUCT_V2;
                com.cboe.idl.cmiCallbackV2.CMIOrderBookConsumer listener =
                        com.cboe.idl.cmiCallbackV2.CMIOrderBookConsumerHelper.narrow((org.omg.CORBA.Object) RemoteConnectionFactory.find().setRoundTripTimeout(clientListener, marketDataCallbackTimeout));
                ChannelListener proxyListener = ServicesHelper.getBookDepthV2ConsumerProxy(listener, baseSession, actionOnQueue);
                subscribeMarketDataForProductByType(sessionName, classKey, productKey, channelType, proxyListener);
                publishMarketData(channelType,sessionName,productKey,listener,actionOnQueue);
            }
        }
        catch (NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }
    }

    public void subscribeBookDepthForProduct(String sessionName, int classKey, int productKey, com.cboe.idl.cmiCallback.CMIOrderBookConsumer clientListener)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling subscribeBookDepthForProduct for " + baseSession + " classKey=" + classKey);
        }
        try
        {
            if (clientListener != null)
            {
                int channelType = ChannelType.CB_BOOK_DEPTH_BY_PRODUCT;
                com.cboe.idl.cmiCallback.CMIOrderBookConsumer listener =
                        com.cboe.idl.cmiCallback.CMIOrderBookConsumerHelper.narrow((org.omg.CORBA.Object) RemoteConnectionFactory.find().setRoundTripTimeout(clientListener, marketDataCallbackTimeout));
                ChannelListener proxyListener = ServicesHelper.getBookDepthConsumerProxy(listener, baseSession);
                subscribeMarketDataForProductByType(sessionName, classKey, productKey, channelType, proxyListener);
                publishMarketData(channelType,sessionName,productKey,listener,(short)0);
            }
        }
        catch (NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }
    }

    public void unsubscribeBookDepthForProductV2(String sessionName, int classKey, int productKey, com.cboe.idl.cmiCallbackV2.CMIOrderBookConsumer clientListener)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling unsubscribeBookDepthForProductV2 for " + baseSession + " classKey=" + classKey);
        }
        try
        {
            int channelType = ChannelType.CB_BOOK_DEPTH_BY_PRODUCT_V2;
            ChannelListener proxyListener = ServicesHelper.getBookDepthV2ConsumerProxy(clientListener, baseSession, (short)0);
            unsubscribeMarketDataForProductByType(sessionName, classKey, productKey, channelType, proxyListener);
        }
        catch (NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }
    }

    public void unsubscribeBookDepthForProduct(String sessionName, int classKey, int productKey, com.cboe.idl.cmiCallback.CMIOrderBookConsumer clientListener)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling unsubscribeBookDepthForProduct for " + baseSession + " classKey=" + classKey);
        }
        try
        {
            int channelType = ChannelType.CB_BOOK_DEPTH_BY_PRODUCT;
            ChannelListener proxyListener = ServicesHelper.getBookDepthConsumerProxy(clientListener, baseSession);
            unsubscribeMarketDataForProductByType(sessionName, classKey, productKey, channelType, proxyListener);
        }
        catch (NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }
    }

    public void subscribeBookDepthUpdateForClassV2(String sessionName, int classKey, com.cboe.idl.cmiCallbackV2.CMIOrderBookUpdateConsumer clientListener, short actionOnQueue)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException
    {
//   TO BE IMPLEMENTED IN THE FUTURE
    }

    public void unsubscribeBookDepthUpdateForClassV2(String sessionName, int classKey, com.cboe.idl.cmiCallbackV2.CMIOrderBookUpdateConsumer clientListener)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException
    {
//   TO BE IMPLEMENTED IN THE FUTURE
    }

    public void subscribeBookDepthUpdateForProductV2(String sessionName, int classKey, int productKey, com.cboe.idl.cmiCallbackV2.CMIOrderBookUpdateConsumer clientListener, short actionOnQueue)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException
    {
//   TO BE IMPLEMENTED IN THE FUTURE
    }

    public void unsubscribeBookDepthUpdateForProductV2(String sessionName, int classKey, int productKey, com.cboe.idl.cmiCallbackV2.CMIOrderBookUpdateConsumer clientListener)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException
    {
//   TO BE IMPLEMENTED IN THE FUTURE
    }

    public void subscribeBookDepthUpdateForProduct(String sessionName, int classKey, int productKey, com.cboe.idl.cmiCallback.CMIOrderBookUpdateConsumer clientListener)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException
    {
//   TO BE IMPLEMENTED IN THE FUTURE
    }

    public void unsubscribeBookDepthUpdateForProduct(String sessionName, int classKey, int productKey, com.cboe.idl.cmiCallback.CMIOrderBookUpdateConsumer clientListener)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException
    {
//   TO BE IMPLEMENTED IN THE FUTURE
    }

	


}
