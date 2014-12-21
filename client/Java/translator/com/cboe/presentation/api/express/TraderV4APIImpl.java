//
// -----------------------------------------------------------------------------------
// Source file: TraderV4APIImpl.java
//
// PACKAGE: com.cboe.presentation.api.express
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.api.express;

import org.omg.CORBA.UserException;

import com.cboe.consumers.callback.CMICallbackV4ConsumerCacheFactoryImpl;
import com.cboe.consumers.callback.CallbackV5ConsumerCacheFactoryImpl;
import com.cboe.consumers.callback.SubscriptionManagerFactory;
import com.cboe.domain.util.SessionKeyContainer;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;
import com.cboe.idl.cmiCallback.CMIUserSessionAdmin;
import com.cboe.idl.cmiCallbackV4.CMICurrentMarketConsumer;
import com.cboe.idl.cmiCallbackV4.CMINBBOConsumer;
import com.cboe.idl.cmiCallbackV4.CMIRecapConsumer;
import com.cboe.idl.cmiCallbackV4.CMITickerConsumer;
import com.cboe.idl.cmiMarketData.NBBOStruct;
import com.cboe.idl.cmiV4.MarketQuery;
import com.cboe.idl.cmiV4.UserSessionManagerV4;
import com.cboe.idl.cmiV5.UserSessionManagerV5;
import com.cboe.idl.cmiV9.UserSessionManagerV9;
import com.cboe.idl.floorApplication.FloorSessionManager;
import com.cboe.idl.floorApplication.MarketQueryV5;
import com.cboe.idl.floorApplication.MarketQueryV5Helper;
import com.cboe.idl.floorApplication.NBBOService;
import com.cboe.idl.floorApplication.NBBOServiceHelper;
import com.cboe.idl.internalCallback.CurrentMarketManualQuoteConsumer;
import com.cboe.interfaces.consumers.callback.CallbackV4ConsumerCacheFactory;
import com.cboe.interfaces.consumers.callback.CallbackV5ConsumerCacheFactory;
import com.cboe.interfaces.presentation.api.MarketQueryV3API;
import com.cboe.interfaces.presentation.api.TimedOutException;
import com.cboe.interfaces.presentation.api.TraderV4API;
import com.cboe.interfaces.presentation.api.marketDataCache.NBBOV2Cache;
import com.cboe.interfaces.presentation.product.SessionProduct;
import com.cboe.presentation.api.NbboEventChannelSnapshot;
import com.cboe.presentation.api.TraderAPIImpl;
import com.cboe.presentation.api.UserAccessFloorFactory;
import com.cboe.presentation.api.marketDataCache.MarketDataCacheFactory;
import com.cboe.presentation.api.productcache.ProductCacheAPI;
import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.properties.AppPropertiesFileFactory;
import com.cboe.presentation.product.ProductHelper;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.event.EventChannelListener;

/**
 * Overrides the Market Query [un]subscription methods, but actually subscribes
 * to the CAS for MDX/V4 data.  The EventTranslator converts the V4 data to the
 * old-style structs and republishes it on the IEC, so the old-style IEC listeners
 * work as expected.
 */
public class TraderV4APIImpl extends TraderAPIImpl implements TraderV4API
{
    public static final transient String V4_TO_V3_MD_PROP_KEY = "V4ToV3ConversionEnabled";
 
    private static final String Category = "TraderV4APIImpl";
    protected static final transient String V4NBBO_SERVICE_PROP_KEY = "V4NBBOService";
    protected static final transient String PROPERTIES_SECTION_NAME = "Defaults";
    protected UserSessionManagerV4 sessionManagerV4;
    protected UserSessionManagerV5 sessionManagerV5;
    
    protected MarketQueryV3API marketQueryV3Delegate;
    protected MarketQuery marketQueryV4;
    private MarketQueryV5 marketQueryV5;
    protected NBBOService nbboService;
    protected FloorSessionManager floorSessionManager;
    protected FloorSessionManager floorSessionManager2;

    private ExpressMarketDataTranslator eventTranslator;

    protected CallbackV4ConsumerCacheFactory cmiConsumerCacheFactoryV4;
    protected CallbackV5ConsumerCacheFactory cmiConsumerCacheFactoryV5;

    protected TraderV4APIImpl() 
    {
    }

    public TraderV4APIImpl(UserSessionManagerV9 sessionMgr,
    		CMIUserSessionAdmin userListener, EventChannelListener clientListener,
    		boolean gmd)
    {
    	super(sessionMgr, userListener, clientListener, gmd );
    	setSessionManagerV4(sessionMgr);
    	setSessionManagerV5(sessionMgr);
    	setSessionManagerV6(sessionMgr);
    	setSessionManagerV7(sessionMgr);
    	setSessionManagerV9(sessionMgr);
    }

    public void cleanUp()
    {
        super.cleanUp();
        cleanupCallbackV4Consumers();
        cleanupSessionManagerV4Interfaces();
        cleanupSessionManagerV5Interfaces();
    }

    public void initialize()
            throws Exception
    {
        super.initialize();
        eventTranslator = ExpressMarketDataTranslator.find();
        initializeCallbackV4Consumers();
        initializeCallbackV5Consumers();
        initializeSessionManagerV4Interfaces();
        initializeNBBOService();
        initializeMarketQueryV5Service();
    }

    protected void initializeNBBOService()
            throws SystemException, CommunicationException, AuthorizationException,
            NotFoundException
    {
        floorSessionManager =
                UserAccessFloorFactory.getUserSessionManager(sessionManagerV4);

        if(floorSessionManager != null)
        {
            nbboService = NBBOServiceHelper.narrow(floorSessionManager.getService(NBBOServiceHelper.id()));
        }
    }

    private void initializeMarketQueryV5Service()
    	throws SystemException, CommunicationException, AuthorizationException, 
    	NotFoundException
    	{
    		if (floorSessionManager2 == null){
    			floorSessionManager2 = UserAccessFloorFactory.getUserSessionManager(sessionManagerV5);
    		}
    		if (floorSessionManager2 != null){
    			marketQueryV5 = MarketQueryV5Helper.narrow(floorSessionManager2.getService(MarketQueryV5Helper.id()));
    		}
    	}
    
    protected void cleanupCallbackV4Consumers()
    {
        cmiConsumerCacheFactoryV4.getCurrentMarketConsumerCache().cleanupCallbackConsumers();
        cmiConsumerCacheFactoryV4.getRecapConsumerCache().cleanupCallbackConsumers();
        cmiConsumerCacheFactoryV4.getTickerConsumerCache().cleanupCallbackConsumers();
    }

    protected void cleanupSessionManagerV4Interfaces()
    {
        marketQueryV4 = null;
    }

    private void cleanupSessionManagerV5Interfaces(){
    	marketQueryV5 = null;
    }
    
    protected void setSessionManagerV4(UserSessionManagerV4 sessionManagerV4)
    {
        this.sessionManagerV4 = sessionManagerV4;
    }

    protected void initializeSessionManagerV4Interfaces()
            throws SystemException, CommunicationException, AuthorizationException
    {
        marketQueryV4 = sessionManagerV4.getMarketQueryV4();
    }

//    protected void initializeSessionManagerV5Interfaces() 
//    throws SystemException, CommunicationException, AuthorizationException
//    {
//    	//marketQueryV5 = sessionManagerV5.getMar
//    	marketQueryV5 = 
//    }
    
    protected void initializeCallbackV4Consumers()
    {
        cmiConsumerCacheFactoryV4 = new CMICallbackV4ConsumerCacheFactoryImpl(eventChannel);
    }

    protected void initializeCallbackV5Consumers()
    {
        cmiConsumerCacheFactoryV5 = new CallbackV5ConsumerCacheFactoryImpl(eventChannel);
    }
    
    public boolean isMDXSupportedSession(String session)
    {
        return ExpressMarketDataTranslator.find().isMDXSupportedSession(session);
    }

    public boolean isV4ToV3MDConversionEnabled()
    {
        String enableV4ToV3Conversion = "false";
        if(AppPropertiesFileFactory.isAppPropertiesAvailable())
        {
            enableV4ToV3Conversion = AppPropertiesFileFactory.find().getValue(ExpressMarketDataTranslator.DEFAULT_PROPERTY_SECTION, V4_TO_V3_MD_PROP_KEY);
            if(enableV4ToV3Conversion == null || enableV4ToV3Conversion.length() == 0)
            {
                enableV4ToV3Conversion = System.getProperty(V4_TO_V3_MD_PROP_KEY);
            }
        }

        return Boolean.parseBoolean(enableV4ToV3Conversion);
    }

    public void subscribeCurrentMarketForProductV3(String session, int productKey,
                                                   EventChannelListener clientListener) throws
            SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if(!ExpressMarketDataTranslator.find().isMDXSupportedSession(session) || !isV4ToV3MDConversionEnabled())
        {
            super.subscribeCurrentMarketForProductV3(session, productKey, clientListener);
        }
        else
        {
            //todo: should do something similar to ticker? -- subscribe by classKey, since that's all MDX supports (no product-based subscriptions)
            ChannelKey v3ProductChannelKey = new ChannelKey(ChannelType.CB_CURRENT_MARKET_BY_PRODUCT_V3,
                                                            new SessionKeyContainer(session, productKey));
            if(GUILoggerHome.find().isDebugOn() &&
               GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
            {
                Object[] argObj = new Object[4];
                argObj[0] = session;
                argObj[1] = productKey;
                argObj[2] = clientListener;

                GUILoggerHome.find().debug(Category + ": subscribeCurrentMarketForProductV3",
                                           GUILoggerBusinessProperty.MARKET_QUERY, argObj);
            }

            if(clientListener != null)
            {
                eventChannel.addChannelListener(eventChannel, clientListener, v3ProductChannelKey);
            }

            int classKey = ProductHelper.getProduct(productKey).getProductKeysStruct().classKey;
            try
            {
                //MDX doesn't provide product-based subscriptions, so we'll have to subscribe for entire class's CurrentMarket
                subscribeCurrentMarketForClassV3(session, classKey, null);
            }
            catch(AuthorizationException e)
            {
                if(clientListener != null)
                {
                    eventChannel.removeChannelListener(eventChannel, clientListener,
                                                       v3ProductChannelKey);
                }
                throw e;
            }
            catch(SystemException e)
            {
                if(clientListener != null)
                {
                    eventChannel.removeChannelListener(eventChannel, clientListener,
                                                       v3ProductChannelKey);
                }
                throw e;
            }
            catch(DataValidationException e)
            {
                if(clientListener != null)
                {
                    eventChannel.removeChannelListener(eventChannel, clientListener,
                                                       v3ProductChannelKey);
                }
                throw e;
            }
            catch(CommunicationException e)
            {
                if(clientListener != null)
                {
                    eventChannel.removeChannelListener(eventChannel, clientListener,
                                                       v3ProductChannelKey);
                }
                throw e;
            }
        }
    }

    public void unsubscribeCurrentMarketForProductV3(String session, int productKey,
                                                     EventChannelListener clientListener) throws
            SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if(!ExpressMarketDataTranslator.find().isMDXSupportedSession(session) ||
           !isV4ToV3MDConversionEnabled())
        {
            super.unsubscribeCurrentMarketForProductV3(session, productKey, clientListener);
        }
        else
        {
            ChannelKey v3ProductChannelKey = new ChannelKey(ChannelType.CB_CURRENT_MARKET_BY_PRODUCT_V3,
                                                            new SessionKeyContainer(session, productKey));
            if(GUILoggerHome.find().isDebugOn() &&
               GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
            {
                Object[] argObj = new Object[3];
                argObj[0] = session;
                argObj[1] = productKey;
                argObj[2] = clientListener;
                GUILoggerHome.find().debug(Category + ": unsubscribeCurrentMarketForProductV3",
                                           GUILoggerBusinessProperty.MARKET_QUERY, argObj);
            }

            if(clientListener != null)
            {
                eventChannel.removeChannelListener(eventChannel, clientListener, v3ProductChannelKey);
            }

            int classKey = ProductHelper.getProduct(productKey).getProductKeysStruct().classKey;
            //MDX doesn't provide product-based subscriptions, so we'll have to subscribe for entire class's CurrentMarket
            unsubscribeCurrentMarketForClassV3(session, classKey, null);
        }
    }

    public void subscribeCurrentMarketForClassV3(String session, int classKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if(!ExpressMarketDataTranslator.find().isMDXSupportedSession(session) || !isV4ToV3MDConversionEnabled())
        {
            super.subscribeCurrentMarketForClassV3(session, classKey, clientListener);
        }
        else
        {
            if(GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
            {
                Object[] argObj = new Object[3];
                argObj[0] = session;
                argObj[1] = classKey;
                argObj[2] = clientListener;
                GUILoggerHome.find().debug(Category + ": subscribeCurrentMarketForClassV3", GUILoggerBusinessProperty.MARKET_QUERY, argObj);
            }
            CMICurrentMarketConsumer currentMarketConsumerV4 =
                    cmiConsumerCacheFactoryV4.getCurrentMarketConsumerCache().getCurrentMarketConsumer(classKey);

            // subscribe clientListener to the IEC for V3 events
            ChannelKey v3Key = new ChannelKey(ChannelType.CB_CURRENT_MARKET_BY_CLASS_V3, new SessionKeyContainer(session, classKey));
            int v3SubCount = SubscriptionManagerFactory.find().subscribe(v3Key, clientListener, currentMarketConsumerV4);
            // if this is the first subscription, also subscribe the ExpressMarketDataTranslator
            if(v3SubCount == 1)
            {
                eventTranslator.subscribeCurrentMarketV4ForClass(session, classKey);
                MarketDataCacheFactory.findCurrentMarketV3Cache(session).subscribeMarketData(classKey);
            }
            else
            {
                // request the V3 CM cache to republish its data (not necessary for the V4 cache to
                // republish, because that will just result in all the V4 events being translated again to V3 events)
                MarketDataCacheFactory.findCurrentMarketV3Cache(session).publishMarketDataSnapshot(classKey);
            }

            // pass null clientListener because we just need to maintain the sub. count, without adding an additional listener to the IEC
            try
            {
                internalSubscribeCurrentMarketV4("subscribeCurrentMarketForClassV3", classKey, null, currentMarketConsumerV4);
            }
            catch(AuthorizationException e)
            {
                // also unsubscribe the V3 IEC listener
                v3SubCount = SubscriptionManagerFactory.find().unsubscribe(v3Key, clientListener, currentMarketConsumerV4);
                if(v3SubCount == 0)
                {
                    eventTranslator.unsubscribeCurrentMarketV4ForClass(classKey);
                    MarketDataCacheFactory.findCurrentMarketV3Cache(session).unsubscribeMarketData(classKey);
                }
                throw e;
            }
            catch(SystemException e)
            {
                // also unsubscribe the V3 IEC listener
                v3SubCount = SubscriptionManagerFactory.find().unsubscribe(v3Key, clientListener, currentMarketConsumerV4);
                if(v3SubCount == 0)
                {
                    eventTranslator.unsubscribeCurrentMarketV4ForClass(classKey);
                    MarketDataCacheFactory.findCurrentMarketV3Cache(session).unsubscribeMarketData(classKey);
                }
                throw e;
            }
            catch(DataValidationException e)
            {
                // also unsubscribe the V3 IEC listener
                v3SubCount = SubscriptionManagerFactory.find().unsubscribe(v3Key, clientListener, currentMarketConsumerV4);
                if(v3SubCount == 0)
                {
                    eventTranslator.unsubscribeCurrentMarketV4ForClass(classKey);
                    MarketDataCacheFactory.findCurrentMarketV3Cache(session).unsubscribeMarketData(classKey);
                }
                throw e;
            }
            catch(CommunicationException e)
            {
                // also unsubscribe the V3 IEC listener
                v3SubCount = SubscriptionManagerFactory.find().unsubscribe(v3Key, clientListener, currentMarketConsumerV4);
                if(v3SubCount == 0)
                {
                    eventTranslator.unsubscribeCurrentMarketV4ForClass(classKey);
                    MarketDataCacheFactory.findCurrentMarketV3Cache(session).unsubscribeMarketData(classKey);
                }
                throw e;
            }
        }
    }

    public void unsubscribeCurrentMarketForClassV3(String session, int classKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if(!ExpressMarketDataTranslator.find().isMDXSupportedSession(session) || !isV4ToV3MDConversionEnabled())
        {
            super.unsubscribeCurrentMarketForClassV3(session, classKey, clientListener);
        }
        else
        {
            if(GUILoggerHome.find().isDebugOn() &&
               GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
            {
                Object[] argObj = new Object[3];
                argObj[0] = session;
                argObj[1] = classKey;
                argObj[2] = clientListener;
                GUILoggerHome.find().debug(Category + ": unsubscribeCurrentMarketForClassV3",
                                           GUILoggerBusinessProperty.MARKET_QUERY, argObj);
            }
            CMICurrentMarketConsumer currentMarketConsumerV4 = cmiConsumerCacheFactoryV4.getCurrentMarketConsumerCache().getCurrentMarketConsumer(classKey);

            // unsubscribe clientListener from the IEC for V3 events
            ChannelKey v3Key = new ChannelKey(ChannelType.CB_CURRENT_MARKET_BY_CLASS_V3, new SessionKeyContainer(session, classKey));
            int v3SubCount = SubscriptionManagerFactory.find().unsubscribe(v3Key, clientListener, currentMarketConsumerV4);
            // if this was the last remaining subscription, also unsubscribe the ExpressMarketDataTranslator
            if(v3SubCount == 0)
            {
                eventTranslator.unsubscribeCurrentMarketV4ForClass(classKey);
                MarketDataCacheFactory.findCurrentMarketV3Cache(session).unsubscribeMarketData(classKey);
            }

            ChannelKey v4Key = new ChannelKey(ChannelType.CB_CURRENT_MARKET_BY_CLASS_V4, classKey);
            // pass null clientListener because we just need to maintain the sub. count, without removing a listener from the IEC
            int subCount = SubscriptionManagerFactory.find().unsubscribe(v4Key, null, currentMarketConsumerV4);
            if(subCount == 0)
            {
                MarketDataCacheFactory.findCurrentMarketV4Cache().unsubscribeMarketData(classKey);
                GUILoggerHome.find().debug(Category + ": unsubscribeCurrentMarketForClassV3",
                                           GUILoggerBusinessProperty.MARKET_QUERY,
                                           "unsubscribing translator to CAS for MDX CurrentMarket V4 for classKey=" +
                                           classKey);
                marketQueryV4.unsubscribeCurrentMarket(classKey, currentMarketConsumerV4);
            }
        }
    }

    public void subscribeRecapForClassV2(String session, int classKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if(!ExpressMarketDataTranslator.find().isMDXSupportedSession(session) || !isV4ToV3MDConversionEnabled())
        {
            super.subscribeRecapForClassV2(session, classKey, clientListener);
        }
        else
        {
            if(GUILoggerHome.find().isDebugOn() &&
               GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
            {
                Object[] argObj = new Object[3];
                argObj[0] = session;
                argObj[1] = classKey;
                argObj[2] = clientListener;
                GUILoggerHome.find().debug(Category + ": subscribeRecapForClassV2",
                                           GUILoggerBusinessProperty.MARKET_QUERY, argObj);
            }
            CMIRecapConsumer recapConsumerV4 = cmiConsumerCacheFactoryV4.getRecapConsumerCache().getRecapConsumer(classKey);

            // subscribe clientListener to the IEC for V2 events
            ChannelKey v2Key = new ChannelKey(ChannelType.CB_RECAP_BY_CLASS, new SessionKeyContainer(session, classKey));
            int v2SubCount = SubscriptionManagerFactory.find().subscribe(v2Key, clientListener, recapConsumerV4);
            // if this is the first subscription, also subscribe the ExpressMarketDataTranslator to the IEC for v4 events
            if(v2SubCount == 1)
            {
                eventTranslator.subscribeRecapLastSaleV4ForClass(session, classKey);
                MarketDataCacheFactory.findRecapV2Cache(session).subscribeMarketData(classKey);
            }
            else
            {
                // request the V2 cache to republish its data (not necessary for the V4 cache to
                // republish, because that will just result in all the V4 events being translated again to V2 events)
                MarketDataCacheFactory.findRecapV2Cache(session).publishMarketDataSnapshot(classKey);
            }

            // pass null clientListener because we just need to maintain the sub. count, without adding an additional listener to the IEC
            try
            {
                internalSubscribeRecapLastSaleV4("subscribeRecapForClassV2", classKey, null, recapConsumerV4);
            }
            // also unsubscribe the V2 IEC listener if there was an exception subscribing
            catch(AuthorizationException e)
            {
                v2SubCount = SubscriptionManagerFactory.find().unsubscribe(v2Key, clientListener, recapConsumerV4);
                if(v2SubCount == 0)
                {
                    eventTranslator.unsubscribeRecapLastSaleV4ForClass(classKey);
                    MarketDataCacheFactory.findRecapV2Cache(session).unsubscribeMarketData(classKey);
                }
                throw e;
            }
            catch(SystemException e)
            {
                v2SubCount = SubscriptionManagerFactory.find().unsubscribe(v2Key, clientListener, recapConsumerV4);
                if(v2SubCount == 0)
                {
                    eventTranslator.unsubscribeRecapLastSaleV4ForClass(classKey);
                    MarketDataCacheFactory.findRecapV2Cache(session).unsubscribeMarketData(classKey);
                }
                throw e;
            }
            catch(CommunicationException e)
            {
                v2SubCount = SubscriptionManagerFactory.find().unsubscribe(v2Key, clientListener, recapConsumerV4);
                if(v2SubCount == 0)
                {
                    eventTranslator.unsubscribeRecapLastSaleV4ForClass(classKey);
                    MarketDataCacheFactory.findRecapV2Cache(session).unsubscribeMarketData(classKey);
                }
                throw e;
            }
            catch(DataValidationException e)
            {
                v2SubCount = SubscriptionManagerFactory.find().unsubscribe(v2Key, clientListener, recapConsumerV4);
                if(v2SubCount == 0)
                {
                    eventTranslator.unsubscribeRecapLastSaleV4ForClass(classKey);
                    MarketDataCacheFactory.findRecapV2Cache(session).unsubscribeMarketData(classKey);
                }
                throw e;
            }
        }
    }

    public void unsubscribeRecapForClassV2(String session, int classKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if(!ExpressMarketDataTranslator.find().isMDXSupportedSession(session) || !isV4ToV3MDConversionEnabled())
        {
            super.unsubscribeRecapForClassV2(session, classKey, clientListener);
        }
        else
        {
            if(GUILoggerHome.find().isDebugOn() &&
               GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
            {
                Object[] argObj = new Object[3];
                argObj[0] = session;
                argObj[1] = classKey;
                argObj[2] = clientListener;
                GUILoggerHome.find().debug(Category + ": unsubscribeRecapForClassV2",
                                           GUILoggerBusinessProperty.MARKET_QUERY, argObj);
            }
            CMIRecapConsumer recapConsumerV4 = cmiConsumerCacheFactoryV4.getRecapConsumerCache().getRecapConsumer(classKey);

            // unsubscribe clientListener from the IEC for V2 events
            ChannelKey v2Key = new ChannelKey(ChannelType.CB_RECAP_BY_CLASS, new SessionKeyContainer(session, classKey));
            int v2SubCount = SubscriptionManagerFactory.find().unsubscribe(v2Key, clientListener, recapConsumerV4);
            // if this was the last remaining subscription, also unsubscribe the ExpressMarketDataTranslator
            if(v2SubCount == 0)
            {
                eventTranslator.unsubscribeRecapLastSaleV4ForClass(classKey);
                MarketDataCacheFactory.findRecapV2Cache(session).unsubscribeMarketData(classKey);
            }

            ChannelKey v4Key = new ChannelKey(ChannelType.CB_RECAP_BY_CLASS_V4, classKey);
            // pass null clientListener because we just need to maintain the sub. count, without removing a listener from the IEC
            int subCount = SubscriptionManagerFactory.find().unsubscribe(v4Key, null, recapConsumerV4);
            if(subCount == 0)
            {
                MarketDataCacheFactory.findRecapV4Cache().unsubscribeMarketData(classKey);
                MarketDataCacheFactory.findLastSaleV4Cache().unsubscribeMarketData(classKey);
                GUILoggerHome.find().debug(Category + ": unsubscribeRecapForClassV2",
                                           GUILoggerBusinessProperty.MARKET_QUERY,
                                           "unsubscribing translator to CAS for MDX Recap/LastSale V4 for classKey=" +
                                           classKey);
                marketQueryV4.unsubscribeRecap(classKey, recapConsumerV4);
            }
        }
    }

    public void subscribeRecapForProductV2(String sessionName, int productKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if(!ExpressMarketDataTranslator.find().isMDXSupportedSession(sessionName) || !isV4ToV3MDConversionEnabled())
        {
            super.subscribeRecapForProductV2(sessionName, productKey, clientListener);
        }
        else
        {
            ChannelKey v2ProductChannelKey = new ChannelKey(ChannelType.CB_RECAP_BY_PRODUCT, new SessionKeyContainer(sessionName, productKey));
            if(GUILoggerHome.find().isDebugOn() &&
               GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
            {
                Object[] argObj = new Object[4];
                argObj[0] = sessionName;
                argObj[1] = productKey;
                argObj[2] = clientListener;
                argObj[3] = DEFAULT_QUEUE_ACTION;

                GUILoggerHome.find().debug(Category + ": subscribeRecapForProductV2",
                                           GUILoggerBusinessProperty.MARKET_QUERY, argObj);
            }

            if(clientListener != null)
            {
                eventChannel.addChannelListener(eventChannel, clientListener, v2ProductChannelKey);
            }

            int classKey = ProductHelper.getProduct(productKey).getProductKeysStruct().classKey;
            try
            {
                subscribeRecapForClassV2(sessionName, classKey, null);
            }
            catch(AuthorizationException e)
            {
                if(clientListener != null)
                {
                    eventChannel.removeChannelListener(eventChannel, clientListener, v2ProductChannelKey);
                }
                throw e;
            }
            catch(SystemException e)
            {
                if(clientListener != null)
                {
                    eventChannel.removeChannelListener(eventChannel, clientListener, v2ProductChannelKey);
                }
                throw e;
            }
            catch(DataValidationException e)
            {
                if(clientListener != null)
                {
                    eventChannel.removeChannelListener(eventChannel, clientListener, v2ProductChannelKey);
                }
                throw e;
            }
            catch(CommunicationException e)
            {
                if(clientListener != null)
                {
                    eventChannel.removeChannelListener(eventChannel, clientListener, v2ProductChannelKey);
                }
                throw e;
            }
        }
    }

    public void unsubscribeRecapForProductV2(String sessionName, int productKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if(!ExpressMarketDataTranslator.find().isMDXSupportedSession(sessionName) || !isV4ToV3MDConversionEnabled())
        {
            super.unsubscribeRecapForProductV2(sessionName, productKey, clientListener);
        }
        else
        {
            ChannelKey v2ProductChannelKey = new ChannelKey(ChannelType.CB_RECAP_BY_PRODUCT,
                                                            new SessionKeyContainer(sessionName, productKey));
            if(GUILoggerHome.find().isDebugOn() &&
               GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
            {
                Object[] argObj = new Object[3];
                argObj[0] = sessionName;
                argObj[1] = productKey;
                argObj[2] = clientListener;
                GUILoggerHome.find().debug(Category + ": unsubscribeRecapForProductV2",
                                           GUILoggerBusinessProperty.MARKET_QUERY, argObj);
            }

            if(clientListener != null)
            {
                eventChannel.removeChannelListener(eventChannel, clientListener, v2ProductChannelKey);
            }

            int classKey = ProductHelper.getProduct(productKey).getProductKeysStruct().classKey;
            unsubscribeRecapForClassV2(sessionName, classKey, null);
        }
    }

    public void subscribeTickerForProductV2(String sessionName, int productKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if(!ExpressMarketDataTranslator.find().isMDXSupportedSession(sessionName) || !isV4ToV3MDConversionEnabled())
        {
            super.subscribeTickerForProductV2(sessionName, productKey, clientListener);
        }
        else
        {
            ChannelKey v2ProductChannelKey = new ChannelKey(ChannelType.CB_TICKER,
                                                            new SessionKeyContainer(sessionName, productKey));
            if(GUILoggerHome.find().isDebugOn() &&
               GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
            {
                Object[] argObj = new Object[4];
                argObj[0] = sessionName;
                argObj[1] = productKey;
                argObj[2] = clientListener;
                argObj[3] = DEFAULT_QUEUE_ACTION;

                GUILoggerHome.find().debug(Category + ": subscribeTickerForProductV2",
                                           GUILoggerBusinessProperty.MARKET_QUERY, argObj);
            }

            if(clientListener != null)
            {
                eventChannel.addChannelListener(eventChannel, clientListener, v2ProductChannelKey);
            }

            int classKey = ProductHelper.getProduct(productKey).getProductKeysStruct().classKey;
            try
            {
                subscribeTickerForClassV2(sessionName, classKey, null);
            }
            catch(AuthorizationException e)
            {
                if(clientListener != null)
                {
                    eventChannel.removeChannelListener(eventChannel, clientListener, v2ProductChannelKey);
                }
                throw e;
            }
            catch(SystemException e)
            {
                if(clientListener != null)
                {
                    eventChannel.removeChannelListener(eventChannel, clientListener, v2ProductChannelKey);
                }
                throw e;
            }
            catch(DataValidationException e)
            {
                if(clientListener != null)
                {
                    eventChannel.removeChannelListener(eventChannel, clientListener, v2ProductChannelKey);
                }
                throw e;
            }
            catch(CommunicationException e)
            {
                if(clientListener != null)
                {
                    eventChannel.removeChannelListener(eventChannel, clientListener, v2ProductChannelKey);
                }
                throw e;
            }
        }
    }

    public void unsubscribeTickerForProductV2(String sessionName, int productKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if(!ExpressMarketDataTranslator.find().isMDXSupportedSession(sessionName) || !isV4ToV3MDConversionEnabled())
        {
            super.unsubscribeTickerForProductV2(sessionName, productKey, clientListener);
        }
        else
        {
            ChannelKey v2ProductChannelKey = new ChannelKey(ChannelType.CB_TICKER,
                                                            new SessionKeyContainer(sessionName,
                                                                                    productKey));
            if(GUILoggerHome.find().isDebugOn() &&
               GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
            {
                Object[] argObj = new Object[3];
                argObj[0] = sessionName;
                argObj[1] = productKey;
                argObj[2] = clientListener;
                GUILoggerHome.find().debug(Category + ": unsubscribeTickerForProductV2",
                                           GUILoggerBusinessProperty.MARKET_QUERY, argObj);
            }

            if(clientListener != null)
            {
                eventChannel.removeChannelListener(eventChannel, clientListener, v2ProductChannelKey);
            }

            int classKey = ProductHelper.getProduct(productKey).getProductKeysStruct().classKey;
            unsubscribeTickerForClassV2(sessionName, classKey, null);
        }
    }

    public void subscribeTickerForClassV2(String sessionName, int classKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if(!ExpressMarketDataTranslator.find().isMDXSupportedSession(sessionName) || !isV4ToV3MDConversionEnabled())
        {
            super.subscribeTickerForClassV2(sessionName, classKey, clientListener);
        }
        else
        {
            if(GUILoggerHome.find().isDebugOn() &&
               GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
            {
                Object[] argObj = new Object[3];
                argObj[0] = sessionName;
                argObj[1] = classKey;
                argObj[2] = clientListener;
                GUILoggerHome.find().debug(Category + ": subscribeTickerForClassV2",
                                           GUILoggerBusinessProperty.MARKET_QUERY, argObj);
            }
            CMITickerConsumer tickerConsumerV4 = cmiConsumerCacheFactoryV4.getTickerConsumerCache().getTickerConsumer(classKey);

            // subscribe clientListener to the IEC for V2 events
            ChannelKey v2Key = new ChannelKey(ChannelType.CB_TICKER_BY_CLASS_V2,
                                              new SessionKeyContainer(sessionName, classKey));
            int v2SubCount = SubscriptionManagerFactory.find().subscribe(v2Key, clientListener, tickerConsumerV4);
            // if this is the first subscription, also subscribe the ExpressMarketDataTranslator to the IEC for v4 events
            if(v2SubCount == 1)
            {
                eventTranslator.subscribeTickerV4ForClass(sessionName, classKey);
            }

            // pass null clientListener because we just need to maintain the sub. count, without adding an additional listener to the IEC
            try
            {
                internalSubscribeTickerV4("subscribeTickerForClassV2", classKey, null, tickerConsumerV4);
            }
            // also unsubscribe the V2 IEC listener if there was an exception subscribing
            catch(AuthorizationException e)
            {
                v2SubCount = SubscriptionManagerFactory.find().unsubscribe(v2Key, clientListener, tickerConsumerV4);
                if(v2SubCount == 0)
                {
                    eventTranslator.unsubscribeTickerV4ForClass(classKey);
                }
                throw e;
            }
            catch(SystemException e)
            {
                v2SubCount = SubscriptionManagerFactory.find().unsubscribe(v2Key, clientListener, tickerConsumerV4);
                if(v2SubCount == 0)
                {
                    eventTranslator.unsubscribeTickerV4ForClass(classKey);
                }
                throw e;
            }
            catch(DataValidationException e)
            {
                v2SubCount = SubscriptionManagerFactory.find().unsubscribe(v2Key, clientListener, tickerConsumerV4);
                if(v2SubCount == 0)
                {
                    eventTranslator.unsubscribeTickerV4ForClass(classKey);
                }
                throw e;
            }
            catch(CommunicationException e)
            {
                v2SubCount = SubscriptionManagerFactory.find().unsubscribe(v2Key, clientListener, tickerConsumerV4);
                if(v2SubCount == 0)
                {
                    eventTranslator.unsubscribeTickerV4ForClass(classKey);
                }
                throw e;
            }
        }
    }

    public void unsubscribeTickerForClassV2(String sessionName, int classKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if(!ExpressMarketDataTranslator.find().isMDXSupportedSession(sessionName) || !isV4ToV3MDConversionEnabled())
        {
            super.unsubscribeTickerForClassV2(sessionName, classKey, clientListener);
        }
        else
        {
            if(GUILoggerHome.find().isDebugOn() &&
               GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
            {
                Object[] argObj = new Object[3];
                argObj[0] = sessionName;
                argObj[1] = classKey;
                argObj[2] = clientListener;
                GUILoggerHome.find().debug(Category + ": unsubscribeTickerForClassV2",
                                           GUILoggerBusinessProperty.MARKET_QUERY, argObj);
            }
            CMITickerConsumer tickerConsumerV4 = cmiConsumerCacheFactoryV4.getTickerConsumerCache().getTickerConsumer(classKey);

            // unsubscribe clientListener from the IEC for V2 events
            ChannelKey v2Key = new ChannelKey(ChannelType.CB_TICKER_BY_CLASS_V2, new SessionKeyContainer(sessionName, classKey));
            int v2SubCount = SubscriptionManagerFactory.find().unsubscribe(v2Key, clientListener, tickerConsumerV4);
            // if this was the last remaining subscription, also unsubscribe the ExpressMarketDataTranslator
            if(v2SubCount == 0)
            {
                eventTranslator.unsubscribeTickerV4ForClass(classKey);
            }

            ChannelKey v4Key = new ChannelKey(ChannelType.CB_TICKER_BY_CLASS_V4, classKey);
            // pass null clientListener because we just need to maintain the sub. count, without removing a listener from the IEC
            int subCount = SubscriptionManagerFactory.find().unsubscribe(v4Key, null, tickerConsumerV4);
            if(subCount == 0)
            {
                MarketDataCacheFactory.findTickerV4Cache().unsubscribeMarketData(classKey);
                GUILoggerHome.find().debug(Category + ": unsubscribeTickerForClassV2",
                                           GUILoggerBusinessProperty.MARKET_QUERY,
                                           "unsubscribing translator to CAS for MDX Ticker V4 for classKey=" +
                                           classKey);
                marketQueryV4.unsubscribeTicker(classKey, tickerConsumerV4);
            }
        }
    }

    //MarketQueryV4API
    public void subscribeCurrentMarketV4(int classKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if(GUILoggerHome.find().isDebugOn() && 
           GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
        {
            Object[] argObj = new Object[2];
            argObj[0] = classKey;
            argObj[1] = clientListener;

            GUILoggerHome.find().debug(Category + ": subscribeCurrentMarketV4", GUILoggerBusinessProperty.MARKET_QUERY,
                                       argObj);
        }

        if(clientListener == null)
        {
            throw new IllegalArgumentException(Category + ".subscribeCurrentMarketV4() -- EventChannelListener can't be null");
        }
        CMICurrentMarketConsumer currentMarketConsumerV4 =
                cmiConsumerCacheFactoryV4.getCurrentMarketConsumerCache().getCurrentMarketConsumer(classKey);

        internalSubscribeCurrentMarketV4("subscribeCurrentMarketV4", classKey, clientListener, currentMarketConsumerV4);
    }

    public void unsubscribeCurrentMarketV4(int classKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
        {
            Object[] argObj = new Object[2];
            argObj[0] = classKey;
            argObj[1] = clientListener;

            GUILoggerHome.find().debug(Category + ": unsubscribeCurrentMarketV4", GUILoggerBusinessProperty.MARKET_QUERY, argObj);
        }

        ChannelKey key = new ChannelKey(ChannelType.CB_CURRENT_MARKET_BY_CLASS_V4, classKey);
        CMICurrentMarketConsumer currentMarketConsumerV4 =
                cmiConsumerCacheFactoryV4.getCurrentMarketConsumerCache().getCurrentMarketConsumer(classKey);

        int subCount = SubscriptionManagerFactory.find().unsubscribe(key, clientListener, currentMarketConsumerV4);
        if(subCount == 0)
        {
            MarketDataCacheFactory.findCurrentMarketV4Cache().unsubscribeMarketData(classKey);
            GUILoggerHome.find().debug(Category + ": unsubscribeCurrentMarketV4", GUILoggerBusinessProperty.MARKET_QUERY,
                                       "unsubscribing translator to CAS for MDX CurrentMarket V4 for classKey=" + classKey);
            marketQueryV4.unsubscribeCurrentMarket(classKey, currentMarketConsumerV4);
        }
    }

    /**
     * Subscribes the EventChannelListener for both CB_LAST_SALE_BY_CLASS_V4 and CB_RECAP_BY_CLASS_V4 events.
     *
     * @param classKey
     * @param clientListener
     * @throws SystemException
     * @throws CommunicationException
     * @throws AuthorizationException
     * @throws DataValidationException
     */
    public void subscribeRecapLastSaleV4(int classKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
        {
            Object[] argObj = new Object[2];
            argObj[0] = classKey;
            argObj[1] = clientListener;

            GUILoggerHome.find()
                    .debug(Category + ": subscribeRecapLastSaleV4", GUILoggerBusinessProperty.MARKET_QUERY,
                           argObj);
        }

        if(clientListener == null)
        {
            throw new IllegalArgumentException(Category + ".subscribeRecapLastSaleV4() -- EventChannelListener can't be null");
        }
        CMIRecapConsumer recapConsumerV4 =
                cmiConsumerCacheFactoryV4.getRecapConsumerCache().getRecapConsumer(classKey);

        // need to subscribe clientListener to eventChannel for LAST_SALE_V4 and RECAP_V4
        internalSubscribeRecapLastSaleV4("subscribeRecapLastSaleV4", classKey, clientListener, recapConsumerV4);
    }

    /**
     * Unsubscribes the EventChannelListener for both CB_LAST_SALE_BY_CLASS_V4 and CB_RECAP_BY_CLASS_V4 events.
     *
     * @param classKey
     * @param clientListener
     * @throws SystemException
     * @throws CommunicationException
     * @throws AuthorizationException
     * @throws DataValidationException
     */
    public void unsubscribeRecapLastSaleV4(int classKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
        {
            Object[] argObj = new Object[2];
            argObj[0] = classKey;
            argObj[1] = clientListener;

            GUILoggerHome.find().debug(Category + ": unsubscribeRecapLastSaleV4",
                                       GUILoggerBusinessProperty.MARKET_QUERY, argObj);
        }
        CMIRecapConsumer recapConsumerV4 =
                cmiConsumerCacheFactoryV4.getRecapConsumerCache().getRecapConsumer(classKey);

        // need to unsubscribe clientListener from eventChannel for LAST_SALE_V4 and RECAP_V4
        ChannelKey key = new ChannelKey(ChannelType.CB_LAST_SALE_BY_CLASS_V4, classKey);
        SubscriptionManagerFactory.find().unsubscribe(key, clientListener, recapConsumerV4);

        key = new ChannelKey(ChannelType.CB_RECAP_BY_CLASS_V4, classKey);
        int subCount = SubscriptionManagerFactory.find().unsubscribe(key, clientListener, recapConsumerV4);
        if(subCount == 0)
        {
            MarketDataCacheFactory.findRecapV4Cache().unsubscribeMarketData(classKey);
            MarketDataCacheFactory.findLastSaleV4Cache().unsubscribeMarketData(classKey);
            GUILoggerHome.find().debug(Category + ": unsubscribeRecapLastSaleV4", GUILoggerBusinessProperty.MARKET_QUERY,
                                       "unsubscribing translator to CAS for MDX Recap/LastSale V4 for classKey=" + classKey);
            marketQueryV4.unsubscribeRecap(classKey, recapConsumerV4);
        }
    }

    public void subscribeTickerV4(int classKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
        {
            Object[] argObj = new Object[2];
            argObj[0] = classKey;
            argObj[1] = clientListener;

            GUILoggerHome.find().debug(Category + ": subscribeTickerV4", GUILoggerBusinessProperty.MARKET_QUERY, argObj);
        }

        if(clientListener == null)
        {
            throw new IllegalArgumentException(
                    Category + ".subscribeTickerV4() -- EventChannelListener can't be null");
        }
        CMITickerConsumer tickerConsumerV4 =
                cmiConsumerCacheFactoryV4.getTickerConsumerCache().getTickerConsumer(classKey);

        internalSubscribeTickerV4("subscribeTickerV4", classKey, clientListener, tickerConsumerV4);
    }

    public void unsubscribeTickerV4(int classKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
        {
            Object[] argObj = new Object[2];
            argObj[0] = classKey;
            argObj[1] = clientListener;

            GUILoggerHome.find().debug(Category + ": unsubscribeTickerV4",
                                       GUILoggerBusinessProperty.MARKET_QUERY, argObj);
        }
        CMITickerConsumer tickerConsumerV4 =
                cmiConsumerCacheFactoryV4.getTickerConsumerCache().getTickerConsumer(classKey);

        ChannelKey key = new ChannelKey(ChannelType.CB_TICKER_BY_CLASS_V4, classKey);
        int subCount = SubscriptionManagerFactory.find().unsubscribe(key, clientListener, tickerConsumerV4);
        if(subCount == 0)
        {
            MarketDataCacheFactory.findTickerV4Cache().unsubscribeMarketData(classKey);
            GUILoggerHome.find().debug(Category + ": unsubscribeTickerV4", GUILoggerBusinessProperty.MARKET_QUERY,
                                       "unsubscribing translator to CAS for MDX Ticker V4 for classKey=" + classKey);
            marketQueryV4.unsubscribeTicker(classKey, tickerConsumerV4);
        }
    }

    /**
     * Convenience method; this will attempt to subscribe to the CAS, and if the user isn't authorized, the clientListener will be unsub'd from the IEC for the channelKey
     */
    private void internalSubscribeCurrentMarketV4(String methodName, int classKey,
                                                  EventChannelListener clientListener,
                                                  CMICurrentMarketConsumer currentMarketConsumerV4)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        ChannelKey key = new ChannelKey(ChannelType.CB_CURRENT_MARKET_BY_CLASS_V4, classKey);
        int subCount = SubscriptionManagerFactory.find().subscribe(key, clientListener, currentMarketConsumerV4);
        if(subCount == 1)
        {
            MarketDataCacheFactory.findCurrentMarketV4Cache().subscribeMarketData(classKey);
            GUILoggerHome.find().debug(Category + ": " + methodName, GUILoggerBusinessProperty.MARKET_QUERY,
                                   "subscribing translator to CAS for MDX CurrentMarket V4 for classKey=" + classKey);
            try
            {
                marketQueryV4.subscribeCurrentMarket(classKey, currentMarketConsumerV4, DEFAULT_QUEUE_ACTION);
            }
            catch(AuthorizationException e)
            {
                GUILoggerHome.find().information(Category, GUILoggerBusinessProperty.MARKET_QUERY,
                                                 "Received an AuthorizationException trying to subscribe for CurrentMarketV4");
                SubscriptionManagerFactory.find().unsubscribe(key, clientListener, currentMarketConsumerV4);
                MarketDataCacheFactory.findCurrentMarketV4Cache().unsubscribeMarketData(classKey);
                throw e;
            }
            catch(SystemException e)
            {
                GUILoggerHome.find().information(Category, GUILoggerBusinessProperty.MARKET_QUERY,
                                                 "Received a SystemException trying to subscribe for CurrentMarketV4");
                SubscriptionManagerFactory.find().unsubscribe(key, clientListener, currentMarketConsumerV4);
                MarketDataCacheFactory.findCurrentMarketV4Cache().unsubscribeMarketData(classKey);
                throw e;
            }
            catch(DataValidationException e)
            {
                GUILoggerHome.find().information(Category, GUILoggerBusinessProperty.MARKET_QUERY,
                                                 "Received a DataValidationException trying to subscribe for CurrentMarketV4");
                SubscriptionManagerFactory.find().unsubscribe(key, clientListener, currentMarketConsumerV4);
                MarketDataCacheFactory.findCurrentMarketV4Cache().unsubscribeMarketData(classKey);
                throw e;
            }
            catch(CommunicationException e)
            {
                GUILoggerHome.find().information(Category, GUILoggerBusinessProperty.MARKET_QUERY,
                                                 "Received a CommunicationException trying to subscribe for CurrentMarketV4");
                SubscriptionManagerFactory.find().unsubscribe(key, clientListener, currentMarketConsumerV4);
                MarketDataCacheFactory.findCurrentMarketV4Cache().unsubscribeMarketData(classKey);
                throw e;
            }
        }
        else
        {
            MarketDataCacheFactory.findCurrentMarketV4Cache().publishMarketDataSnapshot(classKey);
        }
    }

    /**
     * Convenience method; this will attempt to subscribe to the CAS, and if the user isn't
     * authorized, the clientListener will be unsub'd from the IEC for the recap and lastSale channel keys.
     */
    private void internalSubscribeRecapLastSaleV4(String methodName, int classKey,
                                                  EventChannelListener clientListener,
                                                  CMIRecapConsumer recapConsumerV4)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        ChannelKey lastSaleKey = new ChannelKey(ChannelType.CB_LAST_SALE_BY_CLASS_V4, classKey);
        ChannelKey recapKey = new ChannelKey(ChannelType.CB_RECAP_BY_CLASS_V4, classKey);
        // add the clientListener to the IEC for both channelKeys
        SubscriptionManagerFactory.find().subscribe(lastSaleKey, clientListener, recapConsumerV4);
        int subCount = SubscriptionManagerFactory.find().subscribe(recapKey, clientListener, recapConsumerV4);
        // if it was the first subscription, also subscribe to the CAS
        if(subCount == 1)
        {
            MarketDataCacheFactory.findRecapV4Cache().subscribeMarketData(classKey);
            MarketDataCacheFactory.findLastSaleV4Cache().subscribeMarketData(classKey);
            GUILoggerHome.find().debug(Category + ": " + methodName, GUILoggerBusinessProperty.MARKET_QUERY,
                                   "subscribing translator to CAS for MDX Recap/LastSale V4 for classKey=" +
                                   classKey);
            try
            {
                marketQueryV4.subscribeRecap(classKey, recapConsumerV4, DEFAULT_QUEUE_ACTION);
            }
            catch(SystemException e)
            {
                GUILoggerHome.find().information(Category, GUILoggerBusinessProperty.MARKET_QUERY,
                                                 "Received a SystemException trying to subscribe for RecapV4");
                SubscriptionManagerFactory.find().unsubscribe(recapKey, clientListener, recapConsumerV4);
                SubscriptionManagerFactory.find().unsubscribe(lastSaleKey, clientListener, recapConsumerV4);
                MarketDataCacheFactory.findRecapV4Cache().unsubscribeMarketData(classKey);
                MarketDataCacheFactory.findLastSaleV4Cache().unsubscribeMarketData(classKey);
                throw e;
            }
            catch(CommunicationException e)
            {
                GUILoggerHome.find().information(Category, GUILoggerBusinessProperty.MARKET_QUERY,
                                                 "Received a CommunicationException trying to subscribe for RecapV4");
                SubscriptionManagerFactory.find().unsubscribe(recapKey, clientListener, recapConsumerV4);
                SubscriptionManagerFactory.find().unsubscribe(lastSaleKey, clientListener, recapConsumerV4);
                MarketDataCacheFactory.findRecapV4Cache().unsubscribeMarketData(classKey);
                MarketDataCacheFactory.findLastSaleV4Cache().unsubscribeMarketData(classKey);
                throw e;
            }
            catch(AuthorizationException e)
            {
                GUILoggerHome.find().information(Category, GUILoggerBusinessProperty.MARKET_QUERY,
                                                 "Received an AuthorizationException trying to subscribe for RecapV4");
                SubscriptionManagerFactory.find().unsubscribe(recapKey, clientListener, recapConsumerV4);
                SubscriptionManagerFactory.find().unsubscribe(lastSaleKey, clientListener, recapConsumerV4);
                MarketDataCacheFactory.findRecapV4Cache().unsubscribeMarketData(classKey);
                MarketDataCacheFactory.findLastSaleV4Cache().unsubscribeMarketData(classKey);
                throw e;
            }
            catch(DataValidationException e)
            {
                GUILoggerHome.find().information(Category, GUILoggerBusinessProperty.MARKET_QUERY,
                                                 "Received a DataValidationException trying to subscribe for RecapV4");
                SubscriptionManagerFactory.find().unsubscribe(recapKey, clientListener, recapConsumerV4);
                SubscriptionManagerFactory.find().unsubscribe(lastSaleKey, clientListener, recapConsumerV4);
                MarketDataCacheFactory.findRecapV4Cache().unsubscribeMarketData(classKey);
                MarketDataCacheFactory.findLastSaleV4Cache().unsubscribeMarketData(classKey);
                throw e;
            }
        }
        else
        {
            MarketDataCacheFactory.findRecapV4Cache().publishMarketDataSnapshot(classKey);
            MarketDataCacheFactory.findLastSaleV4Cache().publishMarketDataSnapshot(classKey);
        }
    }
    
    /**
     * subscribe to new NBBOService(MDX) - checkProperty for Old/New sub NBBO data
     *
     * @param sessionName
     * @param classKey
     * @param clientListener
     * @throws SystemException
     * @throws CommunicationException
     * @throws AuthorizationException
     * @throws DataValidationException
     */
    public void subscribeNBBOForClassV2(String sessionName, int classKey,
                                        EventChannelListener clientListener) throws SystemException,
            CommunicationException, AuthorizationException, DataValidationException
    {
        if(!isV4NBBOEnabled())
        {
            super.subscribeNBBOForClassV2(sessionName, classKey, clientListener);
        }
        else
        {
            if(GUILoggerHome.find().isDebugOn() &&
               GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
            {
                Object[] argObj = new Object[4];
                argObj[0] = sessionName;
                argObj[1] = classKey;
                argObj[2] = clientListener;
                argObj[3] = DEFAULT_QUEUE_ACTION;
                GUILoggerHome.find().debug(Category + ": subscribeNBBOForClassV2",
                                           GUILoggerBusinessProperty.MARKET_QUERY, argObj);
            }

            CMINBBOConsumer nbboConsumerV4 = cmiConsumerCacheFactoryV4.getNBBOConsumerCache().getNBBOConsumer(classKey);

            // subscribe clientListener to the IEC for V2 events
            ChannelKey v2Key = new ChannelKey(ChannelType.CB_NBBO_BY_CLASS, new SessionKeyContainer(sessionName, classKey));
            int v2SubCount = SubscriptionManagerFactory.find().subscribe(v2Key, clientListener, nbboConsumerV4);
            // if this is the first subscription, also subscribe the ExpressMarketDataTranslator
            if(v2SubCount == 1)
            {
                eventTranslator.subscribeNBBOV4ForClass(sessionName, classKey);
                MarketDataCacheFactory.findNBBOV2Cache(sessionName).subscribeMarketData(classKey);
            }
            else
            {
                MarketDataCacheFactory.findNBBOV2Cache(sessionName).publishMarketDataSnapshot(classKey);
            }

            ChannelKey v4Key = new ChannelKey(ChannelType.CB_NBBO_BY_CLASS_V4, classKey);
            if(SubscriptionManagerFactory.find().subscribe(v4Key, clientListener, nbboConsumerV4) == 1)
            {
                //subscribing translator to CAS for MDX CurrentMarket V4 for classKey="
                GUILoggerHome.find().debug(Category + ": subscribing translator to CAS for MDX NBBO V4 for classKey="+classKey,  //V4/MDX subscriptions to the CAS",
                                           GUILoggerBusinessProperty.MARKET_QUERY,
                                           "subscribing CAS for NBBO for session='" +
                                           sessionName + "' classKey=" + classKey);
                try
                {
                    nbboService.subscribeNBBO(classKey, nbboConsumerV4, DEFAULT_QUEUE_ACTION);
                }
                catch(SystemException e)
                {
                    GUILoggerHome.find().information(Category, GUILoggerBusinessProperty.MARKET_QUERY,
                                                     "Received a SystemException trying to subscribe for NBBOV4");
                    SubscriptionManagerFactory.find().unsubscribe(v4Key, clientListener, nbboConsumerV4);
                    // also unsubscribe the V2 IEC listener
                    int subCount = SubscriptionManagerFactory.find().unsubscribe(v2Key, clientListener, nbboConsumerV4);
                    // if that was the last listener for this key/consumer, then unsubscribe the eventTranslator from the IEC
                    if(subCount == 0)
                    {
                        eventTranslator.unsubscribeNBBOV4ForClass(classKey);
                        MarketDataCacheFactory.findNBBOV2Cache(sessionName).unsubscribeMarketData(classKey);
                    }
                    throw e;
                }
                catch(CommunicationException e)
                {
                    GUILoggerHome.find().information(Category, GUILoggerBusinessProperty.MARKET_QUERY,
                                                     "Received a CommunicationException trying to subscribe for NBBOV4");
                    SubscriptionManagerFactory.find().unsubscribe(v4Key, clientListener, nbboConsumerV4);
                    // also unsubscribe the V2 IEC listener
                    int subCount = SubscriptionManagerFactory.find().unsubscribe(v2Key, clientListener, nbboConsumerV4);
                    // if that was the last listener for this key/consumer, then unsubscribe the eventTranslator from the IEC
                    if(subCount == 0)
                    {
                        eventTranslator.unsubscribeNBBOV4ForClass(classKey);
                        MarketDataCacheFactory.findNBBOV2Cache(sessionName).unsubscribeMarketData(classKey);
                    }
                    throw e;
                }
                catch(AuthorizationException e)
                {
                    GUILoggerHome.find().information(Category, GUILoggerBusinessProperty.MARKET_QUERY,
                                                     "Received a AuthorizationException trying to subscribe for NBBOV4");
                    SubscriptionManagerFactory.find().unsubscribe(v4Key, clientListener, nbboConsumerV4);
                    // also unsubscribe the V2 IEC listener
                    int subCount = SubscriptionManagerFactory.find().unsubscribe(v2Key, clientListener, nbboConsumerV4);
                    // if that was the last listener for this key/consumer, then unsubscribe the eventTranslator from the IEC
                    if(subCount == 0)
                    {
                        eventTranslator.unsubscribeNBBOV4ForClass(classKey);
                        MarketDataCacheFactory.findNBBOV2Cache(sessionName).unsubscribeMarketData(classKey);
                    }
                    throw e;
                }
                catch(DataValidationException e)
                {
                    GUILoggerHome.find().information(Category, GUILoggerBusinessProperty.MARKET_QUERY,
                                                     "Received a DataValidationException trying to subscribe for NBBOV4");
                    SubscriptionManagerFactory.find().unsubscribe(v4Key, clientListener, nbboConsumerV4);
                    // also unsubscribe the V2 IEC listener
                    int subCount = SubscriptionManagerFactory.find().unsubscribe(v2Key, clientListener, nbboConsumerV4);
                    // if that was the last listener for this key/consumer, then unsubscribe the eventTranslator from the IEC
                    if(subCount == 0)
                    {
                        eventTranslator.unsubscribeNBBOV4ForClass(classKey);
                        MarketDataCacheFactory.findNBBOV2Cache(sessionName).unsubscribeMarketData(classKey);
                    }
                    throw e;
                }
            }
        }
    }

    /**
     * Check for the property SubUnSubNBBOService in SbtTraderGUI.properties
     * @return
     */
    private boolean isV4NBBOEnabled()
    {
        boolean actionKeys = false;
        if(AppPropertiesFileFactory.isAppPropertiesAvailable())
        {
            actionKeys = Boolean.valueOf(AppPropertiesFileFactory.find().getValue(
                    PROPERTIES_SECTION_NAME, V4NBBO_SERVICE_PROP_KEY));
        }
        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
        {
            GUILoggerHome.find().debug(Category + ": nbboService enabled "+actionKeys,
                                       GUILoggerBusinessProperty.MARKET_QUERY);
        }
        return actionKeys;
    }


    /**
     * unsubscribe from new NBBOService(MDX) - checkProperty for Old/New unsub NBBO data
     *
     * @param sessionName
     * @param classKey
     * @param clientListener
     * @throws SystemException
     * @throws CommunicationException
     * @throws AuthorizationException
     * @throws DataValidationException
     */
    public void unsubscribeNBBOForClassV2(String sessionName, int classKey,
                                          EventChannelListener clientListener) throws
            SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if(!isV4NBBOEnabled())
        {
            super.subscribeNBBOForClassV2(sessionName, classKey, clientListener);
        }
        else
        {
            if(GUILoggerHome.find().isDebugOn() &&
               GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
            {
                Object[] argObj = new Object[4];
                argObj[0] = sessionName;
                argObj[1] = classKey;
                argObj[2] = clientListener;

                GUILoggerHome.find().debug(Category + ": V4/MDX unsubscribeNBBOForClass",
                                           GUILoggerBusinessProperty.MARKET_QUERY, argObj);
            }

            CMINBBOConsumer nbboConsumerV4 = cmiConsumerCacheFactoryV4.getNBBOConsumerCache().getNBBOConsumer(classKey);

            //unsubscribe the v2 NBBO Consumer from the eventChannel, and if that was the last subsciption for the v2Key, unsub the eventTranslator
            ChannelKey v2Key = new ChannelKey(ChannelType.CB_NBBO_BY_CLASS, new SessionKeyContainer(sessionName, classKey));
            // also unsubscribe the V2 IEC listener
            int subCount = SubscriptionManagerFactory.find().unsubscribe(v2Key, clientListener, nbboConsumerV4);
            // if that was the last listener for this key/consumer, then unsubscribe the eventTranslator from the IEC
            if(subCount == 0)
            {
                eventTranslator.unsubscribeNBBOV4ForClass(classKey);
                MarketDataCacheFactory.findNBBOV2Cache(sessionName).unsubscribeMarketData(classKey);
            }

            //unsubscribe the v4 NBBO Consumer, and if that was the last subscription for the v4Key, unsub from the CAS
            ChannelKey v4Key = new ChannelKey(ChannelType.CB_NBBO_BY_CLASS_V4, classKey);
            if(SubscriptionManagerFactory.find()
                    .unsubscribe(v4Key, clientListener, nbboConsumerV4) == 0)
            {
                GUILoggerHome.find().debug(Category + ": V4/MDX unsubscribeNBBOForClass",
                                           GUILoggerBusinessProperty.MARKET_QUERY,
                                           "unsubscribing CAS for NBBO for session='" +
                                           sessionName + "' classKey=" + classKey);
                nbboService.unsubscribeNBBO(classKey, nbboConsumerV4);
            }
        }
    }

    private void internalSubscribeTickerV4(String methodName, int classKey,
                                           EventChannelListener clientListener,
                                           CMITickerConsumer tickerConsumerV4)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        ChannelKey tickerKey = new ChannelKey(ChannelType.CB_TICKER_BY_CLASS_V4, classKey);
        // add the clientListener to the IEC
        int subCount = SubscriptionManagerFactory.find().subscribe(tickerKey, clientListener, tickerConsumerV4);
        // if it was the first subscription, also subscribe to the CAS
        if(subCount == 1)
        {
            MarketDataCacheFactory.findTickerV4Cache().subscribeMarketData(classKey);
            GUILoggerHome.find().debug(Category + ": " + methodName,
                                       GUILoggerBusinessProperty.MARKET_QUERY,
                                       "subscribing translator to CAS for MDX Ticker V4 for classKey=" +
                                       classKey);
            try
            {
                marketQueryV4.subscribeTicker(classKey, tickerConsumerV4, DEFAULT_QUEUE_ACTION);
            }
            catch(AuthorizationException e)
            {
                GUILoggerHome.find().information(Category, GUILoggerBusinessProperty.MARKET_QUERY,
                                                 "Received an AuthorizationException trying to subscribe for TickerV4");
                SubscriptionManagerFactory.find().unsubscribe(tickerKey, clientListener, tickerConsumerV4);
                MarketDataCacheFactory.findTickerV4Cache().unsubscribeMarketData(classKey);
                throw e;
            }
            catch(SystemException e)
            {
                GUILoggerHome.find().information(Category, GUILoggerBusinessProperty.MARKET_QUERY,
                                                 "Received a SystemException trying to subscribe for TickerV4");
                SubscriptionManagerFactory.find().unsubscribe(tickerKey, clientListener, tickerConsumerV4);
                MarketDataCacheFactory.findTickerV4Cache().unsubscribeMarketData(classKey);
                throw e;
            }
            catch(DataValidationException e)
            {
                GUILoggerHome.find().information(Category, GUILoggerBusinessProperty.MARKET_QUERY,
                                                 "Received a DataValidationException trying to subscribe for TickerV4");
                SubscriptionManagerFactory.find().unsubscribe(tickerKey, clientListener, tickerConsumerV4);
                MarketDataCacheFactory.findTickerV4Cache().unsubscribeMarketData(classKey);
                throw e;
            }
            catch(CommunicationException e)
            {
                GUILoggerHome.find().information(Category, GUILoggerBusinessProperty.MARKET_QUERY,
                                                 "Received a CommunicationException trying to subscribe for TickerV4");
                SubscriptionManagerFactory.find().unsubscribe(tickerKey, clientListener, tickerConsumerV4);
                MarketDataCacheFactory.findTickerV4Cache().unsubscribeMarketData(classKey);
                throw e;
            }
        }
        else
        {
            MarketDataCacheFactory.findTickerV4Cache().publishMarketDataSnapshot(classKey);
        }
    }

    /**
     * Get the most recent NBBO for a session product
     */
    @SuppressWarnings({"DuplicateThrows"})
    public NBBOStruct getNbboSnapshotForProduct(String sessionName, int productKey) throws UserException
    {
        return getNbboSnapshotForProduct(0, sessionName, productKey);
    }

    /**
     * Get the most recent NBBO for a session product
     */
    @SuppressWarnings({"DuplicateThrows"})
    public NBBOStruct getNbboSnapshotForProduct(int timeout, String sessionName, int productKey)
            throws UserException
    {
        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
        {
            Object[] argObj = {timeout, sessionName, productKey};
            GUILoggerHome.find().debug(Category + ": getNbboSnapshotForProduct",
                                       GUILoggerBusinessProperty.MARKET_QUERY, argObj);
        }

        SessionProduct product = getProductByKeyForSession(sessionName, productKey);
        int classKey = product.getProductKeysStruct().classKey;
        NBBOV2Cache cache = MarketDataCacheFactory.findNBBOV2Cache(sessionName);
        NBBOStruct retVal = null;

        // if there wasn't already a subscription for CurrentMarket, get a "snapshot".
        if(!cache.isSubscribedForClass(classKey))
        {
            NbboEventChannelSnapshot listener =
                    new NbboEventChannelSnapshot(timeout, sessionName, productKey);
            try
            {
                retVal = (NBBOStruct) listener.getEventChannelData();

                if(GUILoggerHome.find().isDebugOn() &&
                   GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
                {
                    GUILoggerHome.find().debug(Category + ": NBBO returned from CAS: ",
                                               GUILoggerBusinessProperty.MARKET_QUERY, retVal);
                }
            }
            catch(TimedOutException e)
            {
                // Will throw a TimedOutException if the snapshot didn't get an event from
                // the CAS before the predetermined timeout period expired.
                // This isn't necessaruly a useful exception to show the user;
                // it only means that an event wasn't received within the
                //    pre-determined CurrentMarketEventChannelSnapshot.TIME_OUT period
                GUILoggerHome.find().exception(e.getMessage(), e);
            }
        }
        else
        {
            retVal = cache.getMarketDataForProduct(classKey, productKey);
            GUILoggerHome.find().debug(Category + ": NBBO found in cache: ",
                                       GUILoggerBusinessProperty.MARKET_QUERY, retVal);
        }

        return retVal;
    }
    /**
     * 
     * Subscribe to the current market for a product using the MaketQueryV5.<br>
     * Subscribe to the EventChannelListener to listen to the channel {@link ChannelType#CURRENT_MARKET_BY_GROUP_AND_TYPE}.
     * 
     */
	@Override
    public void subscribeCurrentMarketByProductV5(int classKey, int productKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
		if(GUILoggerHome.find().isDebugOn() &&
		           GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
		        {
		            Object[] argObj = new Object[2];
		            argObj[0] = classKey;
		            argObj[1] = clientListener;

		            GUILoggerHome.find().debug(Category + ": subscribeCurrentMarketV5", GUILoggerBusinessProperty.MARKET_QUERY,
		                                       argObj);
		        }

		        if(clientListener == null)
		        {
		            throw new IllegalArgumentException(Category + ".subscribeCurrentMarketV5() -- EventChannelListener can't be null");
		        }
		        CurrentMarketManualQuoteConsumer currentMarketConsumerV5 = 
		                cmiConsumerCacheFactoryV5.getCurrentMarketManualQuoteConsumerCache().getCurrentMarketManualQuoteConsumer(productKey);
		        internalSubscribeCurrentMarketV5("subscribeCurrentMarketV5", classKey, productKey, clientListener, currentMarketConsumerV5);
    }

	/**
	 * Subscribe to the NBBO for a product using the MarketQueryV5.<br>
	 * Subscribe to the EventChannelListener to listen to the channel {@link ChannelType#CURRENT_MARKET_BY_GROUP_AND_TYPE }.
	 */
	@Override
    public void subscribeNBBOByProductV5(String session, int classKey, int productKey, EventChannelListener clientListener) throws SystemException,
            CommunicationException, AuthorizationException, DataValidationException
    {
		if(GUILoggerHome.find().isDebugOn() && 
		           GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
		        {
		            Object[] argObj = new Object[2];
		            argObj[0] = classKey;
		            argObj[1] = clientListener;

		            GUILoggerHome.find().debug(Category + ": subscribeNBBOByProductV5", GUILoggerBusinessProperty.MARKET_QUERY,
		                                       argObj);
		        }

		        if(clientListener == null)
		        {
		            throw new IllegalArgumentException(Category + ".subscribeNBBOByProductV5() -- EventChannelListener can't be null");
		        }
		        CMINBBOConsumer cmiNBBOConsumer = 
	                cmiConsumerCacheFactoryV5.getNBBOConsumerCache().getNBBOConsumer(productKey);
		        
		        int subCount = ProductCacheAPI.getInstance().subscribeNBBOProductCache(productKey, clientListener, cmiNBBOConsumer);
		        
		        if(subCount == 1)
		        {
		            GUILoggerHome.find().debug(Category + ": subscr ibeNBBOByProductV5", GUILoggerBusinessProperty.MARKET_QUERY,
		                                   "subscribing translator to CAS for MDX CurrentMarketByProduct V5 for classKey=" + classKey + ", productKey=" + productKey);
		            try
		            {
		                marketQueryV5.subscribeNBBOByProduct(classKey, productKey, cmiNBBOConsumer, DEFAULT_QUEUE_ACTION);
		            }
		            catch(AuthorizationException e)
		            {
		                GUILoggerHome.find().information(Category, GUILoggerBusinessProperty.MARKET_QUERY,
		                                                 "Received an AuthorizationException trying to subscribe for NBBOByProductV5");
		                ProductCacheAPI.getInstance().unsubscribeNBBOProductCache(productKey, clientListener, cmiNBBOConsumer);		                
		                throw e;
		            }
		            catch(CommunicationException e){
	                      GUILoggerHome.find().information(Category, GUILoggerBusinessProperty.MARKET_QUERY,
                                                          "Received an AuthorizationException trying to subscribe for NBBOByProductV5");
	                      ProductCacheAPI.getInstance().unsubscribeNBBOProductCache(productKey, clientListener, cmiNBBOConsumer);
                          throw e;
		            }
		            catch(DataValidationException e){
                        GUILoggerHome.find().information(Category, GUILoggerBusinessProperty.MARKET_QUERY,
                                                          "Received an AuthorizationException trying to subscribe for NBBOByProductV5");
                        ProductCacheAPI.getInstance().unsubscribeNBBOProductCache(productKey, clientListener, cmiNBBOConsumer);
                        throw e;
		            }
		            catch(SystemException e){
                        GUILoggerHome.find().information(Category, GUILoggerBusinessProperty.MARKET_QUERY,
                                                        "Received an AuthorizationException trying to subscribe for NBBOByProductV5");
                        ProductCacheAPI.getInstance().unsubscribeNBBOProductCache(productKey, clientListener, cmiNBBOConsumer);
                        throw e;
		            }
		        }
		        else {
		            ProductCacheAPI.getInstance().publishNBBOProduct(productKey);
		        }
    }

	/**
     * Subscribe to the Recap last sale by product using the MarketQueryV5.<br>
     * Subscribe to the EventChannelListener to listen to the channel {@link ChannelType#CB_LAST_SALE_BY_CLASS_V4} and 
     * {@link ChannelType#CB_RECAP_BY_PRODUCT}.
     */
	@Override
    public void subscribeRecapLastSaleByProductV5(int classKey, int productKey, EventChannelListener clientListener) throws SystemException,
            CommunicationException, AuthorizationException, DataValidationException
    {
        if(GUILoggerHome.find().isDebugOn() &&
                GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
             {
                 Object[] argObj = new Object[2];
                 argObj[0] = classKey;
                 argObj[1] = clientListener;

                 GUILoggerHome.find()
                         .debug(Category + ": subscribeRecapLastSaleByProductV5", GUILoggerBusinessProperty.MARKET_QUERY,
                                argObj);
             }

             if(clientListener == null)
             {
                 throw new IllegalArgumentException(Category + ".subscribeRecapLastSaleByProductV5() -- EventChannelListener can't be null");
             }
             CMIRecapConsumer recapConsumerV4 =
                     cmiConsumerCacheFactoryV5.getRecapConsumerCache().getRecapConsumer(productKey);
             
             internalSubscribeRecapLastSaleV5("subscribeRecapLastSaleByProductV5", classKey, productKey, clientListener, recapConsumerV4);
    }
	
	@Override
	public void subscribeTickerByProductV5(int classKey, int productKey, EventChannelListener clientListener)
	    throws SystemException, CommunicationException, AuthorizationException, DataValidationException {
	    if(GUILoggerHome.find().isDebugOn() &&
                GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
             {
                 Object[] argObj = new Object[2];
                 argObj[0] = classKey;
                 argObj[1] = clientListener;

                 GUILoggerHome.find()
                         .debug(Category + ": subscribeTickerByProductV5", GUILoggerBusinessProperty.MARKET_QUERY,
                                argObj);
             }

             if(clientListener == null)
             {
                 throw new IllegalArgumentException(Category + ".subscribeTickerByProductV5() -- EventChannelListener can't be null");
             }
             CMITickerConsumer ticketConsumerV4 = cmiConsumerCacheFactoryV5.getTickerConsumerCache().getTickerConsumer(productKey);
             int subCount = ProductCacheAPI.getInstance().subscribeTickerProductCache(productKey, clientListener, ticketConsumerV4);
             
             // if it was the first subscription, also subscribe to the CAS
             if(subCount == 1)
             {
                 try
                 {
                     GUILoggerHome.find().debug(Category + ": subscribeTickerByProductV5" , GUILoggerBusinessProperty.MARKET_QUERY,
                             "subscribing translator to CAS for MDX Ticker for productKey=" +
                             productKey);
                     marketQueryV5.subscribeTickerByProduct(classKey, productKey, ticketConsumerV4, DEFAULT_QUEUE_ACTION);
                 }
                 catch(SystemException e)
                 {
                     GUILoggerHome.find().information(Category, GUILoggerBusinessProperty.MARKET_QUERY,
                                                      "Received a SystemException trying to subscribe for subscribeTickerByProductV5");
                     ProductCacheAPI.getInstance().unsubscribeTickerProductCache(productKey, clientListener, ticketConsumerV4);
                     throw e;
                 }
                 catch(CommunicationException e){
                     GUILoggerHome.find().information(Category, GUILoggerBusinessProperty.MARKET_QUERY,
                     "Received a SystemException trying to subscribe for subscribeTickerByProductV5");
                     ProductCacheAPI.getInstance().unsubscribeTickerProductCache(productKey, clientListener, ticketConsumerV4);
                    throw e;
                 }
                 catch(AuthorizationException e){
                     GUILoggerHome.find().information(Category, GUILoggerBusinessProperty.MARKET_QUERY,
                     "Received a SystemException trying to subscribe for subscribeTickerByProductV5");
                     ProductCacheAPI.getInstance().unsubscribeTickerProductCache(productKey, clientListener, ticketConsumerV4);
                     throw e;
                 }
                 catch(DataValidationException e){
                     GUILoggerHome.find().information(Category, GUILoggerBusinessProperty.MARKET_QUERY,
                     "Received a SystemException trying to subscribe for subscribeTickerByProductV5");
                     ProductCacheAPI.getInstance().unsubscribeTickerProductCache(productKey, clientListener, ticketConsumerV4);
                     throw e;            
                 }
             }
             else{
                 ProductCacheAPI.getInstance().publishTickerProduct(productKey);
             }
	}
	
	@Override
    public void unsubscribeTickerByProductV5(int classKey, int productKey, EventChannelListener clientListener)
	    throws SystemException,CommunicationException, AuthorizationException, DataValidationException {
	    if(GUILoggerHome.find().isDebugOn() &&
                GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
             {
                 Object[] argObj = new Object[2];
                 argObj[0] = classKey;
                 argObj[1] = clientListener;

                 GUILoggerHome.find().debug(Category + ": unsubscribeTickerByProductV5", GUILoggerBusinessProperty.MARKET_QUERY, argObj);
             }
        CMITickerConsumer ticketConsumerV4 = cmiConsumerCacheFactoryV5.getTickerConsumerCache().getTickerConsumer(productKey);
        
        int subCount = ProductCacheAPI.getInstance().unsubscribeTickerProductCache(productKey, clientListener, ticketConsumerV4);
        if(subCount == 0)
        {
            GUILoggerHome.find().debug(Category + ": unsubscribeTickerByProductV5", GUILoggerBusinessProperty.MARKET_QUERY,
                                       "unsubscribing translator to CAS for MDX Ticker V5 for productKey=" + productKey);
            marketQueryV5.unsubscribeTickerByProduct(classKey, productKey, ticketConsumerV4);
        }
    }
	
	@Override
    public void unsubscribeCurrentMarketByProductV5(int classKey, int productKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
		if(GUILoggerHome.find().isDebugOn() &&
		           GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
		        {
		            Object[] argObj = new Object[2];
		            argObj[0] = classKey;
		            argObj[1] = clientListener;

		            GUILoggerHome.find().debug(Category + ": unsubscribeCurrentMarketByProductV5", GUILoggerBusinessProperty.MARKET_QUERY, argObj);
		        }

		        CurrentMarketManualQuoteConsumer currentMarketConsumerV5 =
		        	cmiConsumerCacheFactoryV5.getCurrentMarketManualQuoteConsumerCache().getCurrentMarketManualQuoteConsumer(productKey);
		        int subCount = ProductCacheAPI.getInstance().unsubscribeCurrentMarketProductCache(productKey, clientListener, currentMarketConsumerV5);
		        if(subCount == 0)
		        {
		            GUILoggerHome.find().debug(Category + ": unsubscribeCurrentMarketByProductV5", GUILoggerBusinessProperty.MARKET_QUERY,
		                                       "unsubscribing translator to CAS for MDX CurrentMarket V5 for productKey=" + productKey);
		            marketQueryV5.unsubscribeCurrentMarketByProduct(classKey, productKey, currentMarketConsumerV5);
		        }
    }
	
	@Override
    public void unsubscribeNBBOByProductV5(String session, int classKey, int productKey, EventChannelListener clientListener) throws SystemException,
            CommunicationException, AuthorizationException, DataValidationException
    {
	    if(GUILoggerHome.find().isDebugOn() &&
                GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
             {
                 Object[] argObj = new Object[2];
                 argObj[0] = classKey;
                 argObj[1] = clientListener;

                 GUILoggerHome.find().debug(Category + ": unsubscribeNBBOByProductV5", GUILoggerBusinessProperty.MARKET_QUERY, argObj);
             }

             CMINBBOConsumer cmiNBBOConsumer = 
                 cmiConsumerCacheFactoryV5.getNBBOConsumerCache().getNBBOConsumer(productKey);
             
             int subCount = ProductCacheAPI.getInstance().unsubscribeNBBOProductCache(productKey, clientListener, cmiNBBOConsumer);
             if(subCount == 0)
             {
                 GUILoggerHome.find().debug(Category + ": unsubscribeNBBOByProductV5", GUILoggerBusinessProperty.MARKET_QUERY,
                                            "unsubscribing translator to CAS for MDX NBBOByProduct V5 for productKey=" + productKey);
                 marketQueryV5.unsubscribeNBBOByProduct(classKey, productKey, cmiNBBOConsumer);
             }
    }

	@Override
    public void unsubscribeRecapLastSaleByProductV5(int classKey, int productKey, EventChannelListener clientListener) throws SystemException,
            CommunicationException, AuthorizationException, DataValidationException
    {
        if(GUILoggerHome.find().isDebugOn() &&
                GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
             {
                 Object[] argObj = new Object[2];
                 argObj[0] = classKey;
                 argObj[1] = clientListener;

                 GUILoggerHome.find().debug(Category + ": unsubscribeRecapLastSaleByProductV5",
                                            GUILoggerBusinessProperty.MARKET_QUERY, argObj);
             }

        CMIRecapConsumer recapConsumerV4 =
            cmiConsumerCacheFactoryV5.getRecapConsumerCache().getRecapConsumer(productKey);
        int subCount = ProductCacheAPI.getInstance().unsubscribeRecapLastSaleProductCache(productKey, clientListener, recapConsumerV4);
        
         if(subCount == 0)
         {
             GUILoggerHome.find().debug(Category + ": unsubscribeRecapLastSaleByProductV5", GUILoggerBusinessProperty.MARKET_QUERY,
                                        "unsubscribing translator to CAS for MDX Recap/LastSale V5 for classKey=" + classKey + ", productKey="+productKey);
             marketQueryV5.unsubscribeRecapByProduct(classKey, productKey, recapConsumerV4);
         }
    }
	
	 /**
     * Convenience method; this will attempt to subscribe to the CAS, and if the user isn't authorized,
     *  the clientListener will be unsub'd from the IEC for the channelKey.
     */
    private void internalSubscribeCurrentMarketV5(String methodName, int classKey, int productKey,
                                                  EventChannelListener clientListener,
                                                  CurrentMarketManualQuoteConsumer currentMarketConsumerV5)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
    	int subCount = ProductCacheAPI.getInstance().subscribeCurrentMarketProductCache(productKey, clientListener, currentMarketConsumerV5);
    	
        if(subCount == 1)
        {
            GUILoggerHome.find().debug(Category + ": " + methodName, GUILoggerBusinessProperty.MARKET_QUERY,
                                   "subscribing translator to CAS for MDX CurrentMarketByProduct V5 for classKey=" + classKey + ", productKey=" + productKey);
            try
            {
            	marketQueryV5.subscribeCurrentMarketByProduct(classKey, productKey, currentMarketConsumerV5, DEFAULT_QUEUE_ACTION);
            }
            catch(AuthorizationException e)
            {
                GUILoggerHome.find().information(Category, GUILoggerBusinessProperty.MARKET_QUERY,
                                                 "Received an AuthorizationException trying to subscribe for CurrentMarketV5");
                ProductCacheAPI.getInstance().unsubscribeCurrentMarketProductCache(productKey, clientListener, currentMarketConsumerV5);
                throw e;
            }
            catch(SystemException e)
            {
                GUILoggerHome.find().information(Category, GUILoggerBusinessProperty.MARKET_QUERY,
                                                 "Received a SystemException trying to subscribe for CurrentMarketV5");
                ProductCacheAPI.getInstance().unsubscribeCurrentMarketProductCache(productKey, clientListener, currentMarketConsumerV5);
                throw e;
            }
            catch(DataValidationException e)
            {
                GUILoggerHome.find().information(Category, GUILoggerBusinessProperty.MARKET_QUERY,
                                                 "Received a DataValidationException trying to subscribe for CurrentMarketV5");
                ProductCacheAPI.getInstance().unsubscribeCurrentMarketProductCache(productKey, clientListener, currentMarketConsumerV5);
                throw e;
            }
            catch(CommunicationException e)
            {
                GUILoggerHome.find().information(Category, GUILoggerBusinessProperty.MARKET_QUERY,
                                                 "Received a CommunicationException trying to subscribe for CurrentMarketV5");
                ProductCacheAPI.getInstance().unsubscribeCurrentMarketProductCache(productKey, clientListener, currentMarketConsumerV5);
                throw e;
            }
        }
        else
        {
            ProductCacheAPI.getInstance().publishCurrentMarketProduct(productKey);
        }
    }

    /**
     * Convenience method; this will attempt to subscribe to the CAS, and if the user isn't
     * authorized, the clientListener will be unsub'd from the IEC for the recap and lastSale channel keys.
     */
    private void internalSubscribeRecapLastSaleV5(String methodName, int classKey, int productKey,
                                                  EventChannelListener clientListener,
                                                  CMIRecapConsumer recapConsumerV4)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        int subCount = ProductCacheAPI.getInstance().subscribeRecapLastSaleProductCache(productKey, clientListener, recapConsumerV4);
        // if it was the first subscription, also subscribe to the CAS
        if(subCount == 1)
        {
            GUILoggerHome.find().debug(Category + ": " + methodName, GUILoggerBusinessProperty.MARKET_QUERY,
                                   "subscribing translator to CAS for MDX Recap/LastSale V5 for classKey=" +
                                   classKey + " productKey=" + productKey);
            try
            {
                marketQueryV5.subscribeRecapByProduct(classKey, productKey, recapConsumerV4, DEFAULT_QUEUE_ACTION);
            }
            catch(SystemException e)
            {
                GUILoggerHome.find().information(Category, GUILoggerBusinessProperty.MARKET_QUERY,
                                                 "Received a SystemException trying to subscribe for RecapV5");
                ProductCacheAPI.getInstance().unsubscribeRecapLastSaleProductCache(productKey, clientListener, recapConsumerV4);
                throw e;
            }
            catch(CommunicationException e)
            {
                GUILoggerHome.find().information(Category, GUILoggerBusinessProperty.MARKET_QUERY,
                                                 "Received a CommunicationException trying to subscribe for RecapV5");
                ProductCacheAPI.getInstance().unsubscribeRecapLastSaleProductCache(productKey, clientListener, recapConsumerV4);
                throw e;
            }
            catch(AuthorizationException e)
            {
                GUILoggerHome.find().information(Category, GUILoggerBusinessProperty.MARKET_QUERY,
                                                 "Received an AuthorizationException trying to subscribe for RecapV5");
                ProductCacheAPI.getInstance().unsubscribeRecapLastSaleProductCache(productKey, clientListener, recapConsumerV4);
                throw e;
            }
            catch(DataValidationException e)
            {
                GUILoggerHome.find().information(Category, GUILoggerBusinessProperty.MARKET_QUERY,
                                                 "Received a DataValidationException trying to subscribe for RecapV5");
                ProductCacheAPI.getInstance().unsubscribeRecapLastSaleProductCache(productKey, clientListener, recapConsumerV4);
                throw e;
            }
        }
        else
        {
            ProductCacheAPI.getInstance().publishLastSaleProduct(productKey);
            ProductCacheAPI.getInstance().publishRecapProduct(productKey);
        }
    }
}
