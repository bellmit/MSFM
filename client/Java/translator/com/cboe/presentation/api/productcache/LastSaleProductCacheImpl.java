//
// -----------------------------------------------------------------------------------
// Source file: LastSaleProductCacheImpl.java
//
// PACKAGE: com.cboe.presentation.api.productcache
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2009 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.api.productcache;

import com.cboe.interfaces.presentation.api.productcache.LastSaleProductCache;

import com.cboe.interfaces.presentation.marketData.express.LastSaleV4;
import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.util.channel.ChannelEvent;

/**
 * Defines the API for the LastSaleProductCache.
 * This interface extends ProductCache interface which defines a more 
 * generic API for the product cache.
 * 
 * @author Eric Maheo
 *
 */
public class LastSaleProductCacheImpl extends AbstractProductCache<LastSaleV4, LastSaleCacheElement>
            implements LastSaleProductCache<LastSaleV4>
{

    /**
     * Construct this object.
     */
    public LastSaleProductCacheImpl(){
        super();
    }
    
    @Override
    protected int getChannelTypeForPublishByProductKey()
    {
        return ProductCacheAPI.PUBLISH_LAST_SALE_PRODUCT;
    }

    @Override
    public void addProductCache(int productKey, LastSaleV4 struct)
    {
        lockUpdates.lock();
        try{
            LastSaleCacheElement element = table.get(productKey);

            if (element == null){
                element = new LastSaleCacheElement(struct);
                element.addProductForExchangeMarket(struct.getExchange(), struct);
                table.put(productKey, element);
                updates.add(struct);
            }
            else {
                int prevSeqNum = -1;
                LastSaleV4 prevMarketData = element.getProductForExchangeMarket(struct.getExchange());
                if (prevMarketData != null)
                {
                    prevSeqNum = prevMarketData.getMessageSequenceNumber();
                }

                if (struct.getMessageSequenceNumber() > prevSeqNum || struct.getMessageSequenceNumber() == 0)
                {
                    element.addProductForExchangeMarket(struct.getExchange(), struct);
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

    @Override
    public void channelUpdate(ChannelEvent event)
    {
        final LastSaleV4 lastSale = (LastSaleV4)event.getEventData();
        int productKey = lastSale.getProductKey();
        addProductCache(productKey, lastSale);
    }

    /**
     * Publish the market snapshot for the product productKey stored into the cache.
     * If productKey isn't in cache not publish occurs.
     */
    public void publishMarketDataSnapshot(int productKey){
        LastSaleV4[] cmAry =  getProductCache(productKey);
        for (LastSaleV4 cm : cmAry){
            dispatchEvent(getChannelTypeForPublishByProductKey(), cm.getProductKey(), cm );
        }
    }

    /**
     * @return the classname.
     */
    protected String getLoggingPrefix()
    {
        return "LastSaleProductCacheImpl";
    }
    /**
     * {@inheritDoc}.
     */
    @Override
    protected LastSaleV4[] initTemplateArray()
    {
        return new LastSaleV4[]{};
    }
}
