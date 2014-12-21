//
// -----------------------------------------------------------------------------------
// Source file: TickerProductCacheImpl.java
//
// PACKAGE: com.cboe.presentation.api.productcache
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2009 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.api.productcache;

import com.cboe.interfaces.presentation.marketData.express.TickerV4;

import com.cboe.interfaces.presentation.api.productcache.TickerProductCache;
import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.util.channel.ChannelEvent;

/**
 * Concrete implementation of the TickerProductCache. 
 * This class listens to the IEC and store/invalidate/retreive the 
 * data from cache.
 * This class is Thread safe.
 * 
 * @author Eric Maheo
 * 
 */
public class TickerProductCacheImpl extends AbstractProductCache<TickerV4, TickerV4CacheElement>
    implements TickerProductCache<TickerV4>
{
    
    /**
     * Create a TickerProductCache impl.
     */
    public TickerProductCacheImpl(){
        super();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    protected int getChannelTypeForPublishByProductKey()
    {
        return ProductCacheAPI.PUBLISH_TICKER_BY_PRODUCT;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void addProductCache(int productKey, TickerV4 struct)
    {
        lockUpdates.lock();
        try {
            TickerV4CacheElement ticker = table.get(productKey);
            if (ticker == null){
                ticker = new TickerV4CacheElement(struct);
                ticker.addProductForExchangeMarket(struct.getExchange(), struct);
                table.put(ticker.getProductKey(), ticker);
                updates.add(struct);
            }
            else {
                int prevSeqNum = -1;
                TickerV4 prevMarketData = ticker.getProductForExchangeMarket(struct.getExchange());
                if (prevMarketData != null)
                {
                    prevSeqNum = prevMarketData.getMessageSequenceNumber();
                }

                if (struct.getMessageSequenceNumber() > prevSeqNum || struct.getMessageSequenceNumber() == 0)
                {
                    ticker.addProductForExchangeMarket(struct.getExchange(), struct);
                    updates.add(struct);
                }
                else
                {
                    GUILoggerHome.find().debug(getLoggingPrefix() + ".addProductCache()",
                            GUILoggerBusinessProperty.MARKET_QUERY, "dropping out-of-sequence message for productKey=" + productKey + " exchange=" +
                            struct.getExchange() + " -- new seqNum=" + struct.getMessageSequenceNumber() + " previous seqNum=" + prevSeqNum);
                }
            }
        }
        finally{
            lockUpdates.unlock();
        }
    }

    /**
     * Publish the ticker snapshot for the product productKey stored into the cache.
     * If productKey isn't in cache not publish occurs.
     */
    public void publishMarketDataSnapshot(int productKey){
        TickerV4[] cmAry =  getProductCache(productKey);
        for (TickerV4 cm : cmAry){
            dispatchEvent(getChannelTypeForPublishByProductKey(), cm.getProductKey(), cm );
        }
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void channelUpdate(ChannelEvent event)
    {
        final TickerV4 ticker = (TickerV4)event.getEventData();
        int productKey = ticker.getProductKey();
        addProductCache(productKey, ticker);
    }
    
    /**
     * Returns the class name of this class.
     * @return classname.
     */
    protected String getLoggingPrefix(){
        return "TickerProductCacheImpl";
    }

    @Override
    protected TickerV4[] initTemplateArray()
    {
        return new TickerV4[]{};
    }
}
