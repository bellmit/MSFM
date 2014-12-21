//
// -----------------------------------------------------------------------------------
// Source file: AbstractSessionMarketDataCache.java
//
// PACKAGE: com.cboe.presentation.api.marketDataCache
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.api.marketDataCache;

import java.util.*;
import java.util.concurrent.*;

import org.omg.CORBA.UserException;

import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;

import com.cboe.interfaces.domain.SessionKeyWrapper;
import com.cboe.interfaces.presentation.api.marketDataCache.SessionMarketDataCache;
import com.cboe.interfaces.presentation.api.marketDataCache.MarketDataCacheClient;
import com.cboe.interfaces.presentation.product.SessionProduct;
import com.cboe.interfaces.presentation.product.SessionProductClass;
import com.cboe.interfaces.presentation.product.Product;

import com.cboe.util.ChannelKey;

import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;
import com.cboe.presentation.common.exceptionHandling.DefaultExceptionHandlerHome;
import com.cboe.presentation.api.APIHome;
import com.cboe.presentation.api.marketDataCache.test.TestMarketDataGeneratorTask;

import com.cboe.domain.util.SessionKeyContainer;

/**
 * A session-based cache of market data that listens on the IEC for a specific
 * type of market data and caches it by SessionProduct.
 *
 * The generic 'T' is the type of market data object received on the IEC.
 * SessionKeyWrapper represents the SessionProducts that will server as the
 * unique keys that the data will be stored by.
 */
public abstract class AbstractSessionMarketDataCache<T> extends AbstractMarketDataCache<T>
        implements SessionMarketDataCache<T>
{
    private static final String TEST_MODE_PROP_NAME = "MARKET_DATA_TEST";
    private static final String TEST_MODE_INTERVAL_PROP_NAME = TEST_MODE_PROP_NAME + "_INTERVAL";

    protected final String sessionName;
    private final Map<Integer, Map<Integer, T>> allMarketDataMap;

    private final Map<MarketDataCacheClient, Map<SessionKeyWrapper, Set<SessionKeyWrapper>>> updatedProductMaps;

    protected AbstractSessionMarketDataCache(String sessionName)
    {
        this.sessionName = sessionName;
        allMarketDataMap = new HashMap<Integer, Map<Integer, T>>(20);
        updatedProductMaps = new ConcurrentHashMap<MarketDataCacheClient, Map<SessionKeyWrapper, Set<SessionKeyWrapper>>>(10);
    }

    protected abstract void initializeMarketDataCache(int productKey) throws SystemException,
            CommunicationException, AuthorizationException, DataValidationException,
            NotFoundException;

    public String getSessionName()
    {
        return sessionName;
    }

    public T getMarketDataForProduct(int classKey, int productKey) throws
            SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        return getMarketDataForProduct(classKey, productKey, true);
    }

    public T getMarketDataForProduct(int classKey, int productKey, boolean lazyInitCache)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        T marketData;
        Map<Integer, T> marketDataForClass = allMarketDataMap.get(classKey);
        synchronized(marketDataForClass)
        {
            marketData = marketDataForClass.get(productKey);
            if(marketData == null && lazyInitCache)
            {
                initializeMarketDataCache(productKey);
                marketData = marketDataForClass.get(productKey);
            }
        }

        return marketData;
    }

    public void publishMarketDataSnapshot(int classKey)
    {
        GUILoggerHome.find().information(getClass().getName() + ".publishMarketDataSnapshot()",
                                         GUILoggerBusinessProperty.MARKET_QUERY, "Publishing events for all products for session="+
                                                                                 getSessionName()+" classKey=" + classKey);
        try
        {
            Product[] products = productQueryDelegate.getAllProductsForClass(classKey, true);
            for(Product product : products)
            {
                publishMarketDataEvent(getMarketDataForProduct(classKey, product.getProductKey()), classKey, product.getProductKey());
            }
        }
        catch(UserException e)
        {
            GUILoggerHome.find().exception(
                    getLoggingPrefix() + ".publishMarketDataSnapshot(" + classKey + ")",
                    "Exception calling ProductQuery.getAllProductsForClass()", e);
        }
    }

    /**
     * Return a Set of Products that the MarketDataCache has received updates for.  A Set will be
     * maintained by the cache for each MarketDataCacheClient that has been been registered with
     * maintainUpdatedProductsList().  Each time a MarketDataCacheClient calls this method, the
     * cache will clear out the Set for the client.
     * @param cacheUser that has registered with maintainUpdatedProductsList()
     * @return Set<SessionKeyWrapper> that this cache has received market data updates for since the
     *         last time this MarketDataCacheClient has called getUpdatedProductsList()
     * @throws IllegalArgumentException if maintainUpdatedProductsList() hasn't been called for this
     * MarketDataCacheClient
     */
    public Set<SessionKeyWrapper> getUpdatedProductsForClass(MarketDataCacheClient cacheUser,
                                                             SessionKeyWrapper classKeyContainer)
            throws IllegalArgumentException
    {
        Set<SessionKeyWrapper> retVal;
        Map<SessionKeyWrapper, Set<SessionKeyWrapper>> updatedProductsForClient = updatedProductMaps.get(cacheUser);
        if(updatedProductsForClient == null)
        {
            throw new IllegalArgumentException("MarketDataCacheClient '" + cacheUser +
                                               "' has not been registered with this cache to maintain a list of updated Products");
        }

        Set<SessionKeyWrapper> cachedSet = updatedProductsForClient.get(classKeyContainer);
        if(cachedSet != null)
        {
            retVal = new HashSet<SessionKeyWrapper>(cachedSet.size());
            // sync on the Set while (1) getting all entries and (2) then
            // clearing the Set, so any updates that are received between
            // those two steps won't be accidentally cleared
            synchronized(cachedSet)
            {
                // dump contents of the cached Set to the new Set, and clear the cached Set
                retVal.addAll(cachedSet);
                cachedSet.clear();
            }
        }
        else
        {
            retVal = new HashSet<SessionKeyWrapper>(0);
        }
        return retVal;
    }

    /**
     * Instruct the cache to maintain a list of products that have received updates since the last
     * time the MarketDataCacheClient called getUpdatedProductsList()
     */
    public void maintainUpdatedProductsList(MarketDataCacheClient cacheUser)
    {
        Map<SessionKeyWrapper, Set<SessionKeyWrapper>> updatedProductsForClient = new HashMap<SessionKeyWrapper, Set<SessionKeyWrapper>>();
        updatedProductMaps.put(cacheUser, updatedProductsForClient);
        synchronized(subscriptionLock)
        {
            // create Maps to track updated products for this cacheUser for all subscribedClasses
            for(int classKey : subscribedClasses)
            {
                addUpdatedProductsMapsForClass(cacheUser, getClassSessionKeyWrapper(getSessionName(), classKey));
            }
        }
    }

    /**
     * Adds the supplied market data object to the cache for the productKey.
     */
    protected void addMarketDataByProduct(int classKey, int productKey, T marketData)
    {
        Map<Integer, T> marketDataForClass = allMarketDataMap.get(classKey);
        synchronized(marketDataForClass)
        {
            marketDataForClass.put(productKey, marketData);
        }
    }

    /**
     * Removes the cached market data for the classKey.
     * @param classKey
     */
    protected void removeMarketDataByClass(int classKey)
    {
        Map<Integer, T> marketDataForClass = allMarketDataMap.get(classKey);
        synchronized(marketDataForClass)
        {
            marketDataForClass.clear();
        }

        // also clear all pending updates for the classKey for all registered cache clients...
        SessionKeyWrapper classKeyContainer = getClassSessionKeyWrapper(getSessionName(), classKey);
        // iterate through the Maps of all registered cacheUsers
        for(Map<SessionKeyWrapper, Set<SessionKeyWrapper>> updatedProductsForClient : updatedProductMaps.values())
        {
            // get the Set of updated Products for the productClass for the cacheUser
            Set<SessionKeyWrapper> updatedProductsForClass = updatedProductsForClient.get(classKeyContainer);

            // remove all pending updates
            synchronized(updatedProductsForClass)
            {
                updatedProductsForClass.clear();
            }
        }
    }

    // add a Map for this ProductClass, for the MarketDataCacheClient
    private void addUpdatedProductsMapsForClass(MarketDataCacheClient cacheUser, SessionKeyWrapper classKeyContainer)
    {
        Map<SessionKeyWrapper, Set<SessionKeyWrapper>> updatedProductsForClient = updatedProductMaps.get(cacheUser);
        addUpdatedProductsMap(updatedProductsForClient, classKeyContainer);
    }

    // add a Map for this ProductClass, for ALL registered MarketDataCacheClients
    private void addUpdatedProductsMapsForClass(SessionKeyWrapper classKeyContainer)
    {
        for(Map<SessionKeyWrapper, Set<SessionKeyWrapper>> updatedProductsForClient : updatedProductMaps.values())
        {
            addUpdatedProductsMap(updatedProductsForClient, classKeyContainer);
        }
    }

    private void addUpdatedProductsMap(Map<SessionKeyWrapper, Set<SessionKeyWrapper>> updatedProductsForClient,
                                       SessionKeyWrapper classKeyContainer)
    {
        synchronized(updatedProductsForClient)
        {
            // get the Set of updates for the productClass; create a new Set if necessary
            if(!updatedProductsForClient.containsKey(classKeyContainer))
            {
                Set<SessionKeyWrapper> updatedProducts = new HashSet<SessionKeyWrapper>();
                updatedProductsForClient.put(classKeyContainer, updatedProducts);
            }
        }
    }

    /**
     * Adds the product to the lists of products that have received market data updates.
     */
    protected void registerUpdate(SessionKeyWrapper productKeyContainer, SessionKeyWrapper classKeyContainer)
    {
        // register the updated Product for each registered cacheUser
        for(Map<SessionKeyWrapper, Set<SessionKeyWrapper>> updatedProductsForClient : updatedProductMaps.values())
        {
            // get the Set of updated Products for the productClass
            Set<SessionKeyWrapper> updatedProductsForClass = updatedProductsForClient.get(classKeyContainer);

            // register the update
            synchronized(updatedProductsForClass)
            {
                updatedProductsForClass.add(productKeyContainer);
            }
        }
    }

    protected void internalSubscribeIEC(int classKey)
    {
        //create a local Map to cache the updates received off the IEC for this classKey
        allMarketDataMap.put(classKey, new HashMap<Integer, T>());

        addUpdatedProductsMapsForClass(getClassSessionKeyWrapper(getSessionName(), classKey));
        ChannelKey key = new ChannelKey(getChannelTypeForSubscribeByClassKey(),
                                        new SessionKeyContainer(sessionName, classKey));
        eventChannel.addChannelListener(eventChannel, this, key);
    }

    protected void internalUnsubscribeIEC(int classKey)
    {
        ChannelKey key = new ChannelKey(getChannelTypeForSubscribeByClassKey(),
                                        new SessionKeyContainer(sessionName, classKey));
        eventChannel.removeChannelListener(eventChannel, this, key);
    }

    protected void publishMarketDataEvent(T eventData, int classKey, int productKey)
    {
        if(getChannelTypeForPublishByProductKey() > 0)
        {
            dispatchEvent(getChannelTypeForPublishByProductKey(),
                          new SessionKeyContainer(sessionName, productKey), eventData);
        }

        if(getChannelTypeForPublishByClassKey() > 0)
        {
            dispatchEvent(getChannelTypeForPublishByClassKey(),
                          new SessionKeyContainer(sessionName, classKey), eventData);
        }
    }

    /**
     * Convenience method to get the SessionKeyWrapper that uniquely identifies a SessionProduct
     * @param session
     * @param productKey
     * @return SessionKeyWrapper
     */
    protected SessionKeyWrapper getProductSessionKeyWrapper(String session, int productKey)
    {
        SessionKeyWrapper sessionKeyWrapper = null;
        try
        {
            SessionProduct product = APIHome.findProductQueryAPI().getProductByKeyForSession(session,
                                                                                             productKey);
            sessionKeyWrapper = product.getSessionKeyWrapper();
        }
        catch(UserException e)
        {
            GUILoggerHome.find().exception(getLoggingPrefix() + ".getProductSessionKeyWrapper()",
                                           "Unable to get SessionProduct. Session:" + session +
                                           ", Key:" + productKey, e);
        }
        return sessionKeyWrapper;
    }

    /**
     * Convenience method to get the SessionKeyWrapper that uniquely identifies a SessionProductClass
     * @param session
     * @param classKey
     * @return SessionKeyWrapper
     */
    protected SessionKeyWrapper getClassSessionKeyWrapper(String session, int classKey)
    {
        SessionKeyWrapper sessionKeyWrapper;
        try
        {
            SessionProductClass productClass = APIHome.findProductQueryAPI().getClassByKeyForSession(session, classKey);
            sessionKeyWrapper = productClass.getSessionKeyWrapper();
        }
        catch(UserException e)
        {
            // return a new impl of SessionKeyContainer
            sessionKeyWrapper = new SessionKeyContainer(session, classKey);
            GUILoggerHome.find().exception(getLoggingPrefix() + ".getClassSessionKeyWrapper()",
                                           "Unable to get SessionProductClass. Session:" + session +
                                           ", Key:" + classKey, e);
        }
        return sessionKeyWrapper;
    }

    protected String getTestModePropName()
    {
        return TEST_MODE_PROP_NAME;
    }

    protected String getTestModeIntervalPropName()
    {
        return TEST_MODE_INTERVAL_PROP_NAME;
    }

    //
    // for temporary testing purposes only -- the TestMarketDataGenerators will
    // publish fake MD events on the IEC, as if the events were being dispatched
    // from the callback consumers
    //
    protected abstract TestMarketDataGeneratorTask<T> createNewTestTimerTask(SessionProduct[] products);

    private static int timerTaskNumber = 0;
    private static final int NUM_THREADS_PER_CLASS = 1;
    Map<Integer, List<Timer>> timersByClass = new HashMap<Integer, List<Timer>>(10);
    @Override
    public void subscribeMarketData(int classKey)
    {
        super.subscribeMarketData(classKey);

        if(isTestMode())
        {
            try
            {
                SessionProduct[] products = APIHome.findProductQueryAPI()
                        .getProductsForSession(getSessionName(), classKey, null);

                List<Timer> timerList = new ArrayList<Timer>(NUM_THREADS_PER_CLASS);
                timersByClass.put(classKey, timerList);
                GUILoggerHome.find().information(getClass().getName() + ".subscribeMarketData()",
                                                 GUILoggerBusinessProperty.MARKET_QUERY,
                                                 "Starting "+NUM_THREADS_PER_CLASS+" test market data generators for session=" +
                                                 getSessionName() + " classKey=" + classKey);
                for(int i = 0; i < NUM_THREADS_PER_CLASS; i++)
                {
                    timerList.add(new Timer(getClass().getName()+"_Test_" + timerTaskNumber++));
                    timerList.get(i).scheduleAtFixedRate(createNewTestTimerTask(products), 50, getTestModeDataPublishInterval());
                }
            }
            catch(Exception e)
            {
                DefaultExceptionHandlerHome.find().process(e);
            }
        }
    }

    public void unsubscribeMarketData(int classKey)
    {
        super.unsubscribeMarketData(classKey);

        if(isTestMode())
        {
            GUILoggerHome.find().information(getClass().getName() + ".unsubscribeMarketData()",
                                             GUILoggerBusinessProperty.MARKET_QUERY,
                                             "Stopping " + NUM_THREADS_PER_CLASS +
                                             " test market data generators for session=" +
                                             getSessionName() + " classKey=" + classKey);
            List<Timer> timerList = timersByClass.get(classKey);
            for(Timer t : timerList)
            {
                t.cancel();
            }
        }
    }
}
