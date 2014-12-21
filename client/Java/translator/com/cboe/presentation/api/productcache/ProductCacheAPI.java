//
// -----------------------------------------------------------------------------------
// Source file: ProductCacheAPI.java
//
// PACKAGE: com.cboe.presentation.api.productcache
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2009 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.api.productcache;

import com.cboe.consumers.callback.SubscriptionManagerFactory;
import com.cboe.idl.cmiCallbackV4.CMINBBOConsumer;
import com.cboe.idl.cmiCallbackV4.CMIRecapConsumer;
import com.cboe.idl.cmiCallbackV4.CMITickerConsumer;
import com.cboe.idl.cmiMarketData.NBBOStructV4;
import com.cboe.idl.internalCallback.CurrentMarketManualQuoteConsumer;
import com.cboe.interfaces.presentation.api.productcache.*;
import com.cboe.interfaces.presentation.marketData.express.CurrentMarketV4ProductContainer;
import com.cboe.interfaces.presentation.marketData.express.LastSaleV4;
import com.cboe.interfaces.presentation.marketData.express.RecapV4;
import com.cboe.interfaces.presentation.marketData.express.TickerV4;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.event.EventChannelListener;

/**
 * API that query the Cache for the CurrentMarketProductCache, 
 * LastSaleProductCache, NBBOProductCache, RecapProductCache and TickerProductCache.
 * All products contained in those caches are V4 and can have several exchange market for 
 * each product.
 * 
 * The class is Thread safe.
 * 
 * @author Eric Maheo
 *
 */
public final class ProductCacheAPI
{

    /**
     * Eager instantiation of the singleton cache factory that ensures Thread safety.
     */
    private static final ProductCacheAPI THIS = new ProductCacheAPI();

    private final CurrentMarketProductCache<CurrentMarketV4ProductContainer> currentMarketProductCache;
    private final LastSaleProductCache<LastSaleV4> lastSaleProductCache;
    private final NBBOProductCache<NBBOStructV4> nbboProductCache;
    private final RecapProductCache<RecapV4> recapProductCache;
    private final TickerProductCache<TickerV4> tickerProductCache;
    
    /** ChannelType for the Current market by product. Its value is {@value}. */
    public static final int CURRENT_MARKET_BY_PRODUCT = ChannelType.CB_CURRENT_MARKET_BY_PRODUCT_V4;
    /** ChannelType used to publish the cache current market by product. Its value is {@value}. */
    public static final int PUBLISH_CURRENT_MARKET_BY_PRODUCT = ChannelType.DISPATCH_CURRENT_MARKET_BY_PRODUCT_V4;
    
    /** ChannelType for the NBBO by product. Its value is {@value}. */
    public static final int NBBO_BY_PRODUCT = ChannelType.CB_NBBO_BY_PRODUCT_V4;
    /** ChannelType used to publish the cache NBBO for a product. Its value is {@value}. */
    public static final int PUBLISH_NBBO_BY_PRODUCT = ChannelType.DISPATCH_NBBO_BY_PRODUCT_V4;
    
    /** ChannelType for the Ticker by product. Its value is {@value}. */
    public static final int TICKER_BY_PRODUCT = ChannelType.CB_TICKER_BY_PRODUCT_V4;
    /** ChannelType used to publish the cache Ticker for a product. Its value is {@value}. */
    public static final int PUBLISH_TICKER_BY_PRODUCT = ChannelType.DISPATCH_TICKER_BY_PRODUCT_V4;
    
    /** ChannelType for the recap by product. Its value is {@value}. */
    public static final int RECAP_BY_PRODUT = ChannelType.CB_RECAP_BY_PRODUCT_V4;
    /** ChannelType used to publish the cache last sale for a product. Its value is {@value}. */
    public static final int PUBLISH_RECAP_BY_PRODUCT = ChannelType.DISPATCH_RECAP_BY_PRODUCT_V4;
    
    /** ChannelType for the last sale by product. Its value is {@value}. */
    public static final int LAST_SALE_BY_PRODUCT = ChannelType.CB_LAST_SALE_BY_PRODUCT_V4;
    /** ChannelType used to publish the last sale for a product. Its value is {@value}. */
    public static final int PUBLISH_LAST_SALE_PRODUCT = ChannelType.DISPATCH_LAST_SALE_BY_PRODUCT_V4;
    
    /**
     * Prevent to call the constructor from an external class.
     */
    private ProductCacheAPI(){
        currentMarketProductCache = new CurrentMarketProductCacheImpl();
        lastSaleProductCache = new LastSaleProductCacheImpl();
        nbboProductCache = new NBBOProductCacheImpl();
        recapProductCache = new RecapProductCacheImpl();
        tickerProductCache = new TickerProductCacheImpl();
    }
    
    /**
     * Gets the instance of this class in a thread safe way.
     * 
     * @return this object.
     */
    public static ProductCacheAPI getInstance(){
        return THIS;
    }
    
    /**
     * Force a current market publish to the IEC for the productKey.
     * 
     * @param productKey to publish.
     */
    public void publishCurrentMarketProduct(int productKey){
        currentMarketProductCache.publishMarketDataSnapshot(productKey);
    }
    /**
     * Force a NBBO publish to the IEC for the productKey.
     * 
     * @param productKey to publish.
     */
    public void publishNBBOProduct(int productKey){
        nbboProductCache.publishMarketDataSnapshot(productKey);
    }
    /**
     * Force a recap publish to the IEC for the productKey.
     * 
     * @param productKey to publish.
     */
    public void publishRecapProduct(int productKey){
        recapProductCache.publishMarketDataSnapshot(productKey);
    }
    /**
     * Force a last sale publish to the IEC for the productKey.
     * 
     * @param productKey to publish.
     */
    public void publishLastSaleProduct(int productKey){
        lastSaleProductCache.publishMarketDataSnapshot(productKey);
    }
    /**
     * Force a ticker publish to the IEC for the productKey.
     * 
     * @param productKey to publish.
     */
    public void publishTickerProduct(int productKey){
        tickerProductCache.publishMarketDataSnapshot(productKey);
    }
    
    /**
     * Subscribe the cache with the consumer to the CAS. It also subscribes the client to the 
     * dispatcher policy to receive the latest update on a different channel {@link ChannelType#CURRENT_MARKET_BY_PRODUCT} 
     * at fix interval.
     * 
     * @param productKey to subscribe.
     * @param clientListener that subscribe to the dispatcher policy manager to get updated product value a fix interval.
     * @param currentMarketConsumerV5 consumer for the current market for a product.
     * @return the subscrition count maintained by the SubscriptionManagerFactory.
     */
    public int subscribeCurrentMarketProductCache(int productKey, EventChannelListener clientListener, CurrentMarketManualQuoteConsumer currentMarketConsumerV5){
        ChannelKey keyCache = new ChannelKey(CURRENT_MARKET_BY_PRODUCT , productKey);
        //register to the dispather policy
        getCurrentMarketProductCache().subscribeProduct(PUBLISH_CURRENT_MARKET_BY_PRODUCT, productKey, clientListener);
        //register to the cas
        int count = SubscriptionManagerFactory.find().subscribe(keyCache,getCurrentMarketProductCache(), currentMarketConsumerV5);
        return count;
    }
    /**
     * Unsubscribe client from the dispatcher and possibly the cache from the CAS if the count reach 0.
     * 
     * @param productKey
     * @param clientListener
     * @param currentMarketConsumerV5
     * @return the count of subscribe client for this product.
     */
    public int unsubscribeCurrentMarketProductCache(int productKey, EventChannelListener clientListener, CurrentMarketManualQuoteConsumer currentMarketConsumerV5){
        ChannelKey keyCache = new ChannelKey(CURRENT_MARKET_BY_PRODUCT , productKey);
        //unregister to the cas
        int count = SubscriptionManagerFactory.find().unsubscribe(keyCache,getCurrentMarketProductCache() , currentMarketConsumerV5);
        //unregister to the dispather policy
        getCurrentMarketProductCache().unsubscribeProduct(PUBLISH_CURRENT_MARKET_BY_PRODUCT, productKey, clientListener);
        return count;
    }
    /**
     * Subscribe the cache with the consumer to the CAS. It also subscribes the client to the 
     * dispatcher policy to receive the latest update on a different channel {@link ChannelType#CURRENT_MARKET_BY_PRODUCT} 
     * at fix interval.
     * 
     * @param productKey to subscribe.
     * @param clientListener that subscribe to the dispatcher policy manager to get updated product value a fix interval.
     * @param cmiNBBOConsumer consumer for the current market for a product.
     * @return the subscrition count maintained by the SubscriptionManagerFactory.
     */
    public int subscribeNBBOProductCache(int productKey, EventChannelListener clientListener, CMINBBOConsumer cmiNBBOConsumer){
        ChannelKey keyCache = new ChannelKey(NBBO_BY_PRODUCT , productKey);
        //register to the dispather policy
        getNBBOProductCache().subscribeProduct(PUBLISH_NBBO_BY_PRODUCT, productKey, clientListener);
        //register to the cas
        int count = SubscriptionManagerFactory.find().subscribe(keyCache, getNBBOProductCache(), cmiNBBOConsumer);
        return count;
    }
    /**
     * Unsubscribe client from the dispatcher and possibly the cache from the CAS if the count reach 0.
     * 
     * @param productKey
     * @param clientListener
     * @param cmiNBBOConsumer
     * @return the count of subscribe client for this product.
     */
    public int unsubscribeNBBOProductCache(int productKey, EventChannelListener clientListener, CMINBBOConsumer cmiNBBOConsumer){
        ChannelKey keyCache = new ChannelKey(NBBO_BY_PRODUCT , productKey);
        //unregister to the cas
        int count = SubscriptionManagerFactory.find().unsubscribe(keyCache, getNBBOProductCache(), cmiNBBOConsumer);
        //unregister to the dispather policy
        getNBBOProductCache().unsubscribeProduct(PUBLISH_NBBO_BY_PRODUCT, productKey, clientListener);
        return count;
    }
    /**
     * Subscribe the cache with the consumer to the CAS. It also subscribes the client to the 
     * dispatcher policy to receive the latest update on a different channel {@link ChannelType#CURRENT_MARKET_BY_PRODUCT} 
     * at fix interval.
     * 
     * @param productKey to subscribe.
     * @param clientListener that subscribe to the dispatcher policy manager to get updated product value a fix interval.
     * @param recapConsumerV4 consumer for the current market for a product.
     * @return the subscrition count maintained by the SubscriptionManagerFactory.
     */
    public int subscribeRecapLastSaleProductCache(int productKey, EventChannelListener clientListener, CMIRecapConsumer recapConsumerV4){
        ChannelKey keyCacheRecap = new ChannelKey(RECAP_BY_PRODUT , productKey);
        ChannelKey keyCacheLastSale = new ChannelKey(LAST_SALE_BY_PRODUCT , productKey);
        //register to the dispather policy
        getRecapProductCache().subscribeProduct(PUBLISH_RECAP_BY_PRODUCT, productKey, clientListener);
        getLastSaleProductCache().subscribeProduct(PUBLISH_LAST_SALE_PRODUCT, productKey, clientListener);
        //register to the cas
        int count = SubscriptionManagerFactory.find().subscribe(keyCacheRecap, getRecapProductCache(), recapConsumerV4);
        SubscriptionManagerFactory.find().subscribe(keyCacheLastSale, getLastSaleProductCache(), recapConsumerV4);
        return count;
    }
    /**
     * Unsubscribe client from the dispatcher and possibly the cache from the CAS if the count reach 0.
     * 
     * @param productKey
     * @param clientListener
     * @param recapConsumerV4
     * @return the count of subscribe client for this product.
     */
    public int unsubscribeRecapLastSaleProductCache(int productKey, EventChannelListener clientListener, CMIRecapConsumer recapConsumerV4){
        ChannelKey keyCacheRecap = new ChannelKey(RECAP_BY_PRODUT , productKey);
        ChannelKey keyCacheLastSale = new ChannelKey(LAST_SALE_BY_PRODUCT , productKey);
        //unregister to the cas
        int count = SubscriptionManagerFactory.find().unsubscribe(keyCacheRecap, getRecapProductCache(), recapConsumerV4);
        SubscriptionManagerFactory.find().unsubscribe(keyCacheLastSale, getLastSaleProductCache(), recapConsumerV4);
        //unregister to the dispather policy
        getRecapProductCache().unsubscribeProduct(PUBLISH_RECAP_BY_PRODUCT, productKey, clientListener);
        getLastSaleProductCache().unsubscribeProduct(PUBLISH_LAST_SALE_PRODUCT, productKey, clientListener);
        return count;
    }
    /**
     * Subscribe the cache with the consumer to the CAS. It also subscribes the client to the 
     * dispatcher policy to receive the latest update on a different channel {@link ChannelType#CURRENT_MARKET_BY_PRODUCT} 
     * at fix interval.
     * 
     * @param productKey to subscribe.
     * @param clientListener that subscribe to the dispatcher policy manager to get updated product value a fix interval.
     * @param ticketConsumerV4 consumer for the current market for a product.
     * @return the subscrition count maintained by the SubscriptionManagerFactory.
     */
    public int subscribeTickerProductCache(int productKey, EventChannelListener clientListener, CMITickerConsumer ticketConsumerV4){
        ChannelKey keyCache = new ChannelKey(TICKER_BY_PRODUCT , productKey);
        //register to the dispather policy
        getTickerProductCache().subscribeProduct(PUBLISH_TICKER_BY_PRODUCT, productKey, clientListener);
        //register to the cas
        int count = SubscriptionManagerFactory.find().subscribe(keyCache, getTickerProductCache(), ticketConsumerV4);
        return count;
    }
    /**
     * Unsubscribe client from the dispatcher and possibly the cache from the CAS if the count reach 0.
     * 
     * @param productKey
     * @param clientListener
     * @param ticketConsumerV4
     * @return the count of subscribe client for this product.
     */
    public int unsubscribeTickerProductCache(int productKey, EventChannelListener clientListener, CMITickerConsumer ticketConsumerV4){
        ChannelKey keyCache = new ChannelKey(TICKER_BY_PRODUCT , productKey);
        //unregister to the cas
        int count = SubscriptionManagerFactory.find().unsubscribe(keyCache, getTickerProductCache(), ticketConsumerV4);
        //unregister to the dispather policy
        getTickerProductCache().unsubscribeProduct(PUBLISH_TICKER_BY_PRODUCT, productKey, clientListener);
        return count;
    }
    
    /**
     * Gets the currentMarketProductCache that contains the products V4.
     * @return the cache instance.
     */
    CurrentMarketProductCache<CurrentMarketV4ProductContainer> getCurrentMarketProductCache(){
        return currentMarketProductCache;
    }
    /**
     * Gets the lastSaleProductCache that contains the lastSale products V4.
     * @return the cache instance.
     */
    LastSaleProductCache<LastSaleV4> getLastSaleProductCache(){
        return lastSaleProductCache;
    }
    /**
     * Gets the NBBOProductCache that contains the nbbo product V4.
     * @return the cache instance.
     */
    NBBOProductCache<NBBOStructV4> getNBBOProductCache(){
        return nbboProductCache;
    }
    /**
     * Gets the recapProductCache that contains the recap products V4.
     * @return the cache instance.
     */
    RecapProductCache<RecapV4> getRecapProductCache(){
        return recapProductCache;
    }
    /**
     * Gets the TickerProductCache that contains the ticker products V4.
     * @return the cache instance.
     */
    TickerProductCache<TickerV4> getTickerProductCache(){
        return tickerProductCache;
    }
}
