//
// -----------------------------------------------------------------------------------
// Source file: AbstractExchangeMarketDataCache.java
//
// PACKAGE: com.cboe.presentation.api.marketDataCache
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.api.marketDataCache;

import java.util.*;

import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;

import com.cboe.interfaces.presentation.api.marketDataCache.ExchangeMarketDataCache;
import com.cboe.interfaces.presentation.product.Product;

import com.cboe.util.ChannelKey;

import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;
import com.cboe.presentation.common.exceptionHandling.DefaultExceptionHandlerHome;
import com.cboe.presentation.api.marketDataCache.test.TestMarketDataGeneratorTask;
import com.cboe.presentation.api.APIHome;

// T is the type of Market Data received on the IEC
public abstract class AbstractExchangeMarketDataCache<T> extends AbstractMarketDataCache<T>
        implements ExchangeMarketDataCache<T>
{
    private static final String TEST_MODE_PROP_NAME = "MDX_MARKET_DATA_TEST";
    private static final String TEST_MODE_INTERVAL_PROP_NAME = TEST_MODE_PROP_NAME + "_INTERVAL";

    // classKey, exchange, productKey, T
    protected final Map<Integer, Map<String, Map<Integer, T>>> allCachedMarketData;

    protected AbstractExchangeMarketDataCache()
    {
        allCachedMarketData = new HashMap<Integer, Map<String, Map<Integer, T>>>();
    }

    protected void internalSubscribeIEC(int classKey)
    {
        // create a Map for this classKey before adding listener to the IEC
        addMarketDataMapForClass(classKey);

        ChannelKey key = new ChannelKey(getChannelTypeForSubscribeByClassKey(), classKey);
        eventChannel.addChannelListener(eventChannel, this, key);
    }

    protected void internalUnsubscribeIEC(int classKey)
    {
        ChannelKey key = new ChannelKey(getChannelTypeForSubscribeByClassKey(), classKey);
        eventChannel.removeChannelListener(eventChannel, this, key);
    }

    public Map<String, T> getMarketDataForProduct(int productKey)
            throws SystemException, CommunicationException, AuthorizationException,
            DataValidationException, NotFoundException
    {
        Map<String, T> exchangeProductData;
        Product product = productQueryDelegate.getProductByKey(productKey);
        if(product != null)
        {
            int classKey = product.getProductKeysStruct().classKey;

            Map<String, Map<Integer, T>> productClassMap = allCachedMarketData.get(classKey);
            synchronized(productClassMap)
            {
                // initialize the productData size list to the number of exchanges in the productClassMap
                exchangeProductData = new HashMap<String, T>(productClassMap.size());
                for(String exchange : productClassMap.keySet())
                {
                    Map<Integer, T> exchangeMap = productClassMap.get(exchange);
                    synchronized(exchangeMap)
                    {
                        T marketData = exchangeMap.get(productKey);
                        exchangeProductData.put(exchange, marketData);
                    }
                }
            }
        }
        else
        {
            exchangeProductData = new HashMap<String, T>(0);
        }
        return exchangeProductData;
    }

    public void publishMarketDataSnapshot(int classKey)
    {
        List<ProductMarketData<T>> marketDataList = new ArrayList<ProductMarketData<T>>();
        // build a collection of market data received for the class for all exchanges
        if(allCachedMarketData.containsKey(classKey))
        {
            Map<String, Map<Integer, T>> productClassMap = allCachedMarketData.get(classKey);
            synchronized(productClassMap)
            {
                for(Map<Integer, T> exchangeMap : productClassMap.values())
                {
                    synchronized(exchangeMap)
                    {
                        for(Map.Entry<Integer, T> mapEntry : exchangeMap.entrySet())
                        {
                            marketDataList.add(new ProductMarketData<T>(mapEntry.getKey(), mapEntry.getValue()));
                        }
                    }
                }
            }
        }

        GUILoggerHome.find().information(getClass().getName() + ".publishMarketDataSnapshot()", GUILoggerBusinessProperty.MARKET_QUERY,
                                         "Publishing "+marketDataList.size()+" cached events for classKey="+classKey);
        // publish all the market data events on the IEC
        for(ProductMarketData<T> marketData : marketDataList)
        {
            publishMarketDataEvent(marketData.getMarketData(), classKey, marketData.getProductKey());
        }
    }

    protected void publishMarketDataEvent(T eventData, int classKey, int productKey)
    {
        if(getChannelTypeForPublishByClassKey() != -1)
        {
            dispatchEvent(getChannelTypeForPublishByClassKey(), classKey, eventData);
        }
        if(getChannelTypeForPublishByProductKey() != -1)
        {
            dispatchEvent(getChannelTypeForPublishByProductKey(), productKey, eventData);
        }
    }

    /**
     * Remove market data from the cache for the classKey for all exchanges
     * @param classKey
     */
    protected void removeMarketDataByClass(int classKey)
    {
        Map<String, Map<Integer, T>> productClassMap = allCachedMarketData.get(classKey);
        if(productClassMap != null)
        {
            synchronized(productClassMap)
            {
                //attempt to remove data for this productKey for each exchange
                for(Map<Integer, T> exchangeMap : productClassMap.values())
                {
                    synchronized(exchangeMap)
                    {
                        exchangeMap.clear();
                    }
                }
            }
        }
    }

    /**
     * Get the local Map being used to store market data received on the IEC for this exchange/classKey
     * @param exchange
     * @param classKey
     * @return Map of market data where the keys are productKeys and values are market data objects of generic type T
     */
    protected Map<Integer, T> getExchangeMapForClass(String exchange, int classKey)
    {
        Map<Integer, T> exchangeMap;
        Map<String, Map<Integer, T>> productClassMap = allCachedMarketData.get(classKey);
        synchronized(productClassMap)
        {
            exchangeMap = productClassMap.get(exchange);
            if(exchangeMap == null)
            {
                exchangeMap = new HashMap<Integer, T>();
                productClassMap.put(exchange, exchangeMap);
            }
        }
        return exchangeMap;
    }

    private void addMarketDataMapForClass(int classKey)
    {
        synchronized(allCachedMarketData)
        {
            // create a Map for this classKey
            allCachedMarketData.put(classKey, new HashMap<String, Map<Integer, T>>());
        }
    }

    protected class ProductMarketData<T>
    {
        private int productKey;
        private T marketData;

        protected ProductMarketData(int productKey, T marketData)
        {
            this.productKey = productKey;
            this.marketData = marketData;
        }

        protected int getProductKey()
        {
            return productKey;
        }

        protected T getMarketData()
        {
            return marketData;
        }
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
    protected abstract TestMarketDataGeneratorTask<T> createNewTestTimerTask(Product[] products);

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
                Product[] products = APIHome.findProductQueryAPI().getAllProductsByClass(classKey, true, null);

                List<Timer> timerList = new ArrayList<Timer>(NUM_THREADS_PER_CLASS);
                timersByClass.put(classKey, timerList);
                GUILoggerHome.find().information(getClass().getName() + ".subscribeMarketData()",
                                                 GUILoggerBusinessProperty.MARKET_QUERY,
                                                 "Starting " + NUM_THREADS_PER_CLASS +
                                                 " test market data generators for classKey=" +
                                                 classKey);
                for(int i = 0; i < NUM_THREADS_PER_CLASS; i++)
                {
                    timerList.add(new Timer(getClass().getName() + "_Test_" + timerTaskNumber++));
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
                                             " test market data generators for classKey=" +
                                             classKey);
            List<Timer> timerList = timersByClass.get(classKey);
            for(Timer t : timerList)
            {
                t.cancel();
            }
        }
    }
}
