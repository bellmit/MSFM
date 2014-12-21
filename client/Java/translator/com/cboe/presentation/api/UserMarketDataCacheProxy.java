//
// -----------------------------------------------------------------------------------
// Source file: UserMarketDataCacheProxy.java
//
// PACKAGE: com.cboe.presentation.api;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2001 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.api;

import java.util.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.Timer;

import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;

import com.cboe.idl.cmiMarketData.CurrentMarketStruct;
import com.cboe.idl.cmiMarketData.NBBOStruct;
import com.cboe.idl.cmiMarketData.RecapStruct;
import com.cboe.idl.cmiProduct.ProductKeysStruct;

import com.cboe.interfaces.domain.CurrentMarketProductContainer;

import com.cboe.util.event.EventChannelListener;
import com.cboe.util.event.EventChannelAdapterFactory;
import com.cboe.util.ChannelType;
import com.cboe.util.ChannelKey;
import com.cboe.util.channel.ChannelEvent;

import com.cboe.interfaces.presentation.api.TraderAPI;
import com.cboe.interfaces.presentation.api.ProductQueryAPI;
import com.cboe.interfaces.presentation.api.MarketQueryV3API;
import com.cboe.interfaces.presentation.marketData.UserMarketDataStruct;
import com.cboe.interfaces.presentation.marketData.PersonalBestBook;
import com.cboe.interfaces.presentation.product.Product;
import com.cboe.interfaces.presentation.product.SessionProduct;
import com.cboe.interfaces.presentation.product.SessionProductClass;
import com.cboe.interfaces.domain.SessionKeyWrapper;

import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;
import com.cboe.presentation.common.properties.AppPropertiesFileFactory;
import com.cboe.presentation.common.exceptionHandling.DefaultExceptionHandlerHome;
import com.cboe.interfaces.presentation.api.marketDataCache.CurrentMarketV3Cache;
import com.cboe.interfaces.presentation.api.marketDataCache.MarketDataCacheClient;
import com.cboe.interfaces.presentation.api.marketDataCache.RecapV2Cache;
import com.cboe.interfaces.presentation.api.marketDataCache.NBBOV2Cache;
import com.cboe.presentation.api.marketDataCache.MarketDataCacheFactory;

import org.omg.CORBA.UserException;

import com.cboe.domain.util.SessionKeyContainer;
import com.cboe.domain.util.MarketDataStructBuilder;

//======================================================================
//          Class Definition / Implementation
//======================================================================
//======================================================================
//      CLASS:      UserMarketDataCacheProxy
/**
 * This class is a helper that provides limited user market query interfaces to facilitate
 * caching of products, recap, current market,personal best book for given class.
 * It will keep a cached collection of these so that round trips to the CAS are not always required.
 * All collections used are non-synchronized. This codebase handles all synchronization
 * on a larger granular level. Care must be taken in rearranging or changing code
 * fragments, as you may disturb the synchronization.
 *
 * @version 11/4/1999
 * @author Mike Pyatetsky
 */
//======================================================================
public class UserMarketDataCacheProxy implements EventChannelListener, ActionListener, MarketDataCacheClient
{
    private final ProductQueryAPI productQueryDelegate;
    private final MarketQueryV3API marketQueryDelegate;

    private final Map<Integer, UserMarketDataStruct> userMarketDataByProduct;
    private final Map<SessionKeyWrapper, Set<SessionKeyWrapper>> updatedPBBProducts;
    private final String sessionName;
    private final String Category = getClass().getName();
    protected static final String PROPERTIES_SECTION_NAME = "Timers";
    protected static final String MD_CACHE_LEVEL_KEY_NAME = "MarketDataCacheMillis";

    protected static final int MIN_UPDATE_DELAY = 100;
    protected static final int MAX_UPDATE_DELAY = 3000;
    protected static final int DEFAULT_UPDATE_DELAY = MIN_UPDATE_DELAY;

    private final Set<SessionKeyWrapper> subscribedClasses;
    protected Timer timer = null;

    private final CurrentMarketV3Cache cmV3Cache;
    private final RecapV2Cache recapV2Cache;
    private final NBBOV2Cache nbboV2Cache;

    protected UserMarketDataCacheProxy(String sessionName, TraderAPI delegate)
    {
        this(sessionName, delegate, delegate);
    }

    protected UserMarketDataCacheProxy(String sessionName, ProductQueryAPI productQueryDelegate, MarketQueryV3API marketQueryDelegate)
    {
        this.productQueryDelegate = productQueryDelegate;
        this.marketQueryDelegate = marketQueryDelegate;

        userMarketDataByProduct = new HashMap<Integer, UserMarketDataStruct>();
        updatedPBBProducts = new HashMap<SessionKeyWrapper, Set<SessionKeyWrapper>>();
        subscribedClasses = new HashSet<SessionKeyWrapper>(20);

        // for convenience, keep a reference to each of the caches
        cmV3Cache = MarketDataCacheFactory.findCurrentMarketV3Cache(sessionName);
        recapV2Cache = MarketDataCacheFactory.findRecapV2Cache(sessionName);
        nbboV2Cache = MarketDataCacheFactory.findNBBOV2Cache(sessionName);
        // instruct each cache to maintain a list of products that have
        // received any MD updates since the last time this UMDCP has gotten
        // the updated products
        cmV3Cache.maintainUpdatedProductsList(this);
        recapV2Cache.maintainUpdatedProductsList(this);
        nbboV2Cache.maintainUpdatedProductsList(this);

        getTimer().start();

        this.sessionName = sessionName;
    }

    /**
     * This method used to extract UserMarketDataStruct from userMarketDataByProduct
     * collection using productKey as a key
     * @param productKey
     * @return UserMarketDataStruct
     */
    protected UserMarketDataStruct getUserMarketDataForProduct(int classKey, int productKey) throws
            SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        return getUserMarketDataForProduct(classKey, productKey, true);
    }

    /**
     * This method used to extract UserMarketDataStruct from userMarketDataByProduct
     * collection using productKey as a key
     *
     * If lazyInitCache is false and there is no market data cached for the product, the
     * returned struct's md values will be zeroed out.
     *
     * @param productKey
     * @return UserMarketDataStruct
     */
    protected UserMarketDataStruct getUserMarketDataForProduct(int classKey, int productKey, boolean lazyInitCache)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        UserMarketDataStruct userMarketData;

        synchronized(userMarketDataByProduct)
        {
            userMarketData = userMarketDataByProduct.get(productKey);

            if ( userMarketData == null ) 
            {
                Product product = productQueryDelegate.getProductByKey(productKey);
                SessionKeyWrapper classContainer = new SessionKeyContainer(sessionName, classKey);
                userMarketData = new UserMarketDataStruct();
                userMarketData.productKeys = product.getProductKeysStruct();
                // only subscribe if we should lazily initialize the cache
                if(lazyInitCache)
                {
                    // subscribe for Market Data if the cache isn't already subscribed
                    subscribeUserMarketDataForClass(classContainer);
                    userMarketDataByProduct.put(productKey, userMarketData);
                }
            }
        }

        CurrentMarketStruct currentMarket = null;
        CurrentMarketStruct currentMarketPublic = null;
        RecapStruct recap;
        NBBOStruct nbbo;

        // md caches will return null if no market data is cached for the product and lazyInitCache is false
        CurrentMarketProductContainer currentMarketContainer = cmV3Cache.getMarketDataForProduct(classKey, productKey, lazyInitCache);
        if(currentMarketContainer != null)
        {
            currentMarket = currentMarketContainer.getBestMarket();
            currentMarketPublic = currentMarketContainer.getBestPublicMarketAtTop();
        }
        recap = recapV2Cache.getMarketDataForProduct(classKey, productKey, lazyInitCache);
        nbbo = nbboV2Cache.getMarketDataForProduct(classKey, productKey, lazyInitCache);

        ProductKeysStruct productKeys = userMarketData.productKeys;
        if(currentMarket != null)
        {
            userMarketData.currentMarket = currentMarket;
            // no currentMarketPublic is significant, so do not test for null
            userMarketData.currentMarketPublic = currentMarketPublic;
        }
        else
        {
            userMarketData.currentMarket = MarketDataStructBuilder.buildCurrentMarketStruct(productKeys);
            userMarketData.currentMarketPublic = MarketDataStructBuilder.buildCurrentMarketStruct(productKeys);
        }
        
        if(recap != null)
        {
            userMarketData.recap = recap;
        }
        else
        {
            userMarketData.recap = MarketDataStructBuilder.buildRecapStruct(productKeys);
        }

        if(nbbo != null)
        {
            userMarketData.NBBO = nbbo;
        }
        else
        {
            userMarketData.NBBO = MarketDataStructBuilder.buildNBBOStruct(productKeys);
        }

        // personalBestBook isn't cached
        userMarketData.personalBestBook = marketQueryDelegate.getPersonalBestBookByProduct(sessionName, productKey, null);

        return userMarketData;
    }

    protected void removeAllInterestForClass(int classKey)
    {
        SessionKeyWrapper classContainer = new SessionKeyContainer(sessionName, classKey);
        synchronized(subscribedClasses)
        {
            if(subscribedClasses.contains(classContainer))
            {
                subscribedClasses.remove(classContainer);

                try
                {
                    marketQueryDelegate.unsubscribePersonalBestBookByClass(sessionName, classKey, this);
                }
                catch(UserException e)
                {
                    GUILoggerHome.find().exception(Category + ".unsubscribePersonalBestBookForClass()",
                                                   "Exception trying to unsubscribe for PersonalBestBook for session=" +
                                                   sessionName + " classKey=" + classKey, e);

                }

                // call the unsubscribe methods in the translator so the count
                // of listeners for each type of Market Data will be decremented
                try
                {
                    marketQueryDelegate.unsubscribeCurrentMarketForClassV3(sessionName, classKey, null);
                }
                catch(UserException e)
                {
                    GUILoggerHome.find().exception(Category + ".removeAllInterestForClass()",
                                                   "Exception trying to unsubscribe for CurrentMarketV3 for session=" +
                                                   sessionName + " classKey=" + classKey, e);
                }

                try
                {
                    marketQueryDelegate.unsubscribeRecapForClassV2(sessionName, classKey, null);
                }
                catch(UserException e)
                {
                    GUILoggerHome.find().exception(Category + ".removeAllInterestForClass()",
                                                   "Exception trying to unsubscribe for RecapV2 for session=" +
                                                   sessionName + " classKey=" + classKey, e);
                }

                try
                {
                    marketQueryDelegate.unsubscribeNBBOForClassV2(sessionName, classKey, null);
                }
                catch(UserException e)
                {
                    GUILoggerHome.find().exception(Category + ".removeAllInterestForClass()",
                                                   "Exception trying to unsubscribe for NBBOV2 for session=" +
                                                   sessionName + " classKey=" + classKey, e);
                }
            }
        }
        //remove all the cached structs for the ProductClass
        try
        {
            Product[] products = productQueryDelegate.getAllProductsForClass(classKey, true);
            synchronized(userMarketDataByProduct)
            {
                for(Product product : products)
                {
                    userMarketDataByProduct.remove(product.getProductKey());
                }
            }
        }
        catch(UserException e)
        {
            GUILoggerHome.find().exception(Category + ".removeAllInterestForClass()",
                                           "Exception during cache cleanup, trying to get all products for classKey '" +
                                           classKey + "'", e);
        }
    }

    /**
    * This method builds an array of all UserMarketDataStruct which are extracted from
    * userMarketDataByProduct collection
    * @return UserMarketDataStruct[] Array of all UserMarketDataStruct for the class
    */
    protected UserMarketDataStruct[] getUserMarketDataForClass(int classKey)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        UserMarketDataStruct[] userMarketDataStructs = null;
        try 
        {
            SessionProduct[] products = productQueryDelegate.getProductsForSession(sessionName, classKey, this);
            userMarketDataStructs = new UserMarketDataStruct[products.length];

            SessionKeyWrapper classContainer = new SessionKeyContainer(sessionName, classKey);
            subscribeUserMarketDataForClass(classContainer);
            for ( int i = 0; i < products.length; i++ )
            {
                userMarketDataStructs[i] = getUserMarketDataForProduct(classKey, products[i].getProductKey());
            }
        } 
        catch (NotFoundException e)
        {
            GUILoggerHome.find().exception(Category+".getUserMarketDataForClass()","",e);
        }
        return userMarketDataStructs;
    }

    private void subscribeUserMarketDataForClass(SessionKeyWrapper classContainer)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        synchronized(subscribedClasses)
        {
            if(!subscribedClasses.contains(classContainer))
            {
                //subscribe for PBB
                marketQueryDelegate.getPersonalBestBookByClass(sessionName, classContainer.getKey(), this);
                //passing null clientListener because we want to trigger
                // the translator to subscribe to the CAS for events, but
                // this UMDCP isn't listening for the events on the IEC
                marketQueryDelegate.subscribeCurrentMarketForClassV3(sessionName, classContainer.getKey(), null);
                marketQueryDelegate.subscribeRecapForClassV2(sessionName, classContainer.getKey(), null);
                marketQueryDelegate.subscribeNBBOForClassV2(sessionName, classContainer.getKey(), null);
                subscribedClasses.add(classContainer);
            }
        }
    }

    /**
    * This method is implementation of EventChannelListener interface and allows
    * catching and processing all the events <code>this</code> object is subscribed for
    * @param event Object of type ChannelEvent
    */
    public void channelUpdate(ChannelEvent event)
    {
        SessionKeyWrapper productKeyObj;
        SessionKeyWrapper classKeyObj;

        int channelType = ((ChannelKey)event.getChannel()).channelType;

        switch(channelType)
        {
            case ChannelType.CB_PRODUCT_UPDATE:
            case ChannelType.CB_PRODUCT_UPDATE_BY_CLASS:
                SessionProduct product = (SessionProduct)event.getEventData();
                productKeyObj = product.getSessionKeyWrapper();
                classKeyObj = getClassSessionKeyWrapper(product.getTradingSessionName(), product.getProductKeysStruct().classKey);
                // don't need cache action
                generateUserMarketDataEvent(classKeyObj, productKeyObj);
                break;
            case ChannelType.CB_PERSONAL_BEST_BOOK:
                PersonalBestBook bestBook = (PersonalBestBook)event.getEventData();
                productKeyObj = getProductSessionKeyWrapper(bestBook.getSessionName(), bestBook.getProductKey());
                classKeyObj = getClassSessionKeyWrapperByProductKey(bestBook.getSessionName(), bestBook.getProductKey());
                if (productKeyObj != null && classKeyObj != null)
                {
                    registerPersonalBestBookUpdate(productKeyObj, classKeyObj);
                }
                break;
            default:
                // No action
                break;
        }
    }

    /**
     * This method creates and dispatches CB_UserMarketData event to all listeners of it.
     * The event object contains eventData object in type of array of UserMarketDataStruct
     *
     * The cache will be lazily initialized if it's not currently subscribed for the class.
     */
    private void generateUserMarketDataEvent(SessionKeyWrapper classKeyContainer, SessionKeyWrapper productKeyContainer)
    {
        generateUserMarketDataEvent(classKeyContainer, productKeyContainer, true);
    }

    /**
     * This method creates and dispatches CB_UserMarketData event to all listeners of it.
     * The event object contains eventData object in type of array of UserMarketDataStruct.
     *
     * If lazyInitCache is false and there is no market data cached for the product, the
     * published struct's md values will be zeroed out.
     */
    private void generateUserMarketDataEvent(SessionKeyWrapper classKeyContainer, SessionKeyWrapper productKeyContainer, boolean lazyInitCache)
    {
        try
        {
            int productKey = productKeyContainer.getKey();
            UserMarketDataStruct dataStruct = getUserMarketDataForProduct(classKeyContainer.getKey(), productKey, lazyInitCache);

            ChannelKey key = new ChannelKey(ChannelType.CB_USER_MARKET_DATA, classKeyContainer);
            ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, key, dataStruct);
            EventChannelAdapterFactory.find().dispatch(event);

            key = new ChannelKey(ChannelType.CB_USER_MARKET_DATA_BY_PRODUCT, productKeyContainer);
            event = EventChannelAdapterFactory.find().getChannelEvent(this, key, dataStruct);
            EventChannelAdapterFactory.find().dispatch(event);
        }
        catch (UserException e)
        {
            DefaultExceptionHandlerHome.find().process(e, "Could not dispatch market data event on IEC. Session:" +
                                                          classKeyContainer.getSessionName() +
                                                          ", classKey:" + classKeyContainer.getKey());
        }
    }

    private Timer getTimer()
    {
        if (timer == null)
        {
            int updateDelay = getMDUpdateDelay();
            timer = new Timer(updateDelay, this);
        }
        return timer;
    }

    private int getMDUpdateDelay()
    {
        int updateDelay;
        if (AppPropertiesFileFactory.isAppPropertiesAvailable())
        {
            String value = AppPropertiesFileFactory.find().getValue(PROPERTIES_SECTION_NAME, MD_CACHE_LEVEL_KEY_NAME);

            if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.COMMON))
            {
                GUILoggerHome.find().debug(Category + ".getMDUpdateDelay", GUILoggerBusinessProperty.COMMON,
                                           MD_CACHE_LEVEL_KEY_NAME + '=' + value);
            }

            try
            {
                updateDelay = Integer.parseInt(value);
                updateDelay = Math.max(updateDelay, MIN_UPDATE_DELAY);
                updateDelay = Math.min(updateDelay, MAX_UPDATE_DELAY);
            }
            catch(NumberFormatException e)
            {
                GUILoggerHome.find().exception(Category + ".getMDUpdateDelay",
                                               "Error parsing " + MD_CACHE_LEVEL_KEY_NAME + ", value =" + value, e);
                updateDelay = DEFAULT_UPDATE_DELAY;
            }
        }
        else
        {
            updateDelay = DEFAULT_UPDATE_DELAY;
        }

        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.COMMON))
        {
            GUILoggerHome.find().debug(Category + ".getMDUpdateDelay",
                                       GUILoggerBusinessProperty.COMMON, "updateDelay = " + updateDelay);
        }

        return updateDelay;
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == getTimer())
        {
            generateEventsForClasses();
        }
    }

    private void registerPersonalBestBookUpdate(SessionKeyWrapper productKeyContainer, SessionKeyWrapper classKeyContainer)
    {
        Set<SessionKeyWrapper> products = getUpdatedPBBProductsForClass(classKeyContainer);
        synchronized(products)
        {
            products.add(productKeyContainer);
        }
    }

    private Set<SessionKeyWrapper> getUpdatedPBBProductsForClass(SessionKeyWrapper classContainer)
    {
        Set<SessionKeyWrapper> products;
        // get PersonalBestBook updated products
        synchronized(updatedPBBProducts)
        {
            products = updatedPBBProducts.get(classContainer);

            if(products == null)
            {
                products = new HashSet<SessionKeyWrapper>();
                {
                    updatedPBBProducts.put(classContainer, products);
                }
            }
        }

        return products;
    }

    /**
     * Get a set of all the products that have received market data updates since the last time the timer fired.
     * @param classContainer
     * @return
     */
    private Set<SessionKeyWrapper> getUpdatedProductsForClass(SessionKeyWrapper classContainer)
    {
        Set<SessionKeyWrapper> updatedProducts = new HashSet<SessionKeyWrapper>();
        Set<SessionKeyWrapper> products = getUpdatedPBBProductsForClass(classContainer);
        synchronized(products)
        {
            updatedProducts.addAll(products);
            products.clear();
        }
        updatedProducts.addAll(cmV3Cache.getUpdatedProductsForClass(this, classContainer));
        updatedProducts.addAll(recapV2Cache.getUpdatedProductsForClass(this, classContainer));
        updatedProducts.addAll(nbboV2Cache.getUpdatedProductsForClass(this, classContainer));

        return updatedProducts;
    }

    private void generateEventsForClasses()
    {
        Set<SessionKeyWrapper> tmpSet;
        synchronized(subscribedClasses)
        {
            tmpSet = new HashSet<SessionKeyWrapper>(subscribedClasses);
        }
        for(SessionKeyWrapper classContainer : tmpSet)
        {
            generateEventsForClass(classContainer);
        }
    }

    private void generateEventsForClass(SessionKeyWrapper classContainer)
    {
        Set<SessionKeyWrapper> products = getUpdatedProductsForClass(classContainer);

        for(SessionKeyWrapper productContainer : products)
        {
            generateUserMarketDataEvent(classContainer, productContainer, false);
        }
    }

    private SessionKeyWrapper getProductSessionKeyWrapper(String session, int key)
    {
        SessionKeyWrapper sessionKeyWrapper = null;
        try
        {
            SessionProduct product = APIHome.findProductQueryAPI().getProductByKeyForSession(session, key);
            sessionKeyWrapper = product.getSessionKeyWrapper();
        }
        catch(UserException e)
        {
            GUILoggerHome.find().exception(Category+".getProductSessionKeyWrapper()", "Unable to get SessionProduct. Session:"+session+", Key:"+key, e);
        }
        return sessionKeyWrapper;
    }

    private SessionKeyWrapper getClassSessionKeyWrapper(String session, int key)
    {
        SessionKeyWrapper sessionKeyWrapper = null;
        try
        {
            SessionProductClass productClass = APIHome.findProductQueryAPI().getClassByKeyForSession(session, key);
            sessionKeyWrapper = productClass.getSessionKeyWrapper();
        }
        catch(UserException e)
        {
            GUILoggerHome.find().exception(Category+".getClassSessionKeyWrapper()", "Unable to get SessionProductClass. Session:"+session+", Key:"+key, e);
        }
        return sessionKeyWrapper;
    }

    private SessionKeyWrapper getClassSessionKeyWrapperByProductKey(String session, int productKey)
    {
        SessionKeyWrapper sessionKeyWrapper = null;
        SessionProduct product = null;
        try
        {
            product = APIHome.findProductQueryAPI().getProductByKeyForSession(session, productKey);
        }
        catch (UserException e)
        {
            GUILoggerHome.find().exception(Category + ".getClassSessionKeyWrapperByProductKey()", "Unable to get SessionProduct. Session:" + session + ", Key:" + productKey, e);
        }

        if (product != null)
        {
            int classKey = product.getProductKeysStruct().classKey;
            try
            {
                SessionProductClass productClass = APIHome.findProductQueryAPI().getClassByKeyForSession(session, classKey);
                sessionKeyWrapper = productClass.getSessionKeyWrapper();
            }
            catch (UserException e)
            {
                GUILoggerHome.find().exception(Category + ".getClassSessionKeyWrapperByProductKey()", "Unable to get SessionProductClass. Session:" + session + ", Key:" + classKey, e);
            }
        }

        return sessionKeyWrapper;
    }

    /**
     * Implement equals() of MarketDataCacheClient
     * @param otherCache
     * @return true if the otherCache is the same instance as this
     */
    public boolean equals(Object otherCache)
    {
        return this == otherCache;
    }
}
