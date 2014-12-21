//
// -----------------------------------------------------------------------------------
// Source file: CurrentMarketProductCacheImpl.java
//
// PACKAGE: com.cboe.presentation.api.productcache
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2009 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.api.productcache;

import com.cboe.interfaces.presentation.api.productcache.CurrentMarketProductCache;
import com.cboe.interfaces.presentation.marketData.express.CurrentMarketV4ProductContainer;
import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.util.channel.ChannelEvent;

/**
 * Concrete implementation of the CurrentMarketProductCache. 
 * This class listens to the IEC and store/invalidate/retreive the 
 * data from cache.
 * This class is Thread safe.
 * 
 * @author Eric Maheo
 *
 */
public class CurrentMarketProductCacheImpl extends AbstractProductCache<CurrentMarketV4ProductContainer, CurrentMarketCacheElement> 
                                    implements CurrentMarketProductCache<CurrentMarketV4ProductContainer> 
{
    /**
     * Construct this object.
     */
    public CurrentMarketProductCacheImpl(){
        super();
    }
        
    /**
     * {@inheritDoc}.
     */
    @Override
    public void addProductCache(int productKey, CurrentMarketV4ProductContainer struct)
    {
        lockUpdates.lock();
        try {
            CurrentMarketCacheElement element = table.get(productKey);
            if (element == null){
                element = new CurrentMarketCacheElement(struct);
                element.addProductForExchangeMarket(struct.getExchange(), struct);
                table.put(productKey, element);
                updates.add(struct);
            }
            else {
                int prevSeqNum = -1;
                CurrentMarketV4ProductContainer prevMarketData = element.getProductForExchangeMarket(struct.getExchange());
                if (prevMarketData != null)
                {
                    prevSeqNum = prevMarketData.getMessageSequenceNumber();
                }

                if(struct.getMessageSequenceNumber() > prevSeqNum || struct.getMessageSequenceNumber() == 0)
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
        finally {
            lockUpdates.unlock();
        }
    }

    /**
     * Publish the market snapshot for the product productKey stored into the cache.
     * If productKey isn't in cache not publish occurs.
     */
    public void publishMarketDataSnapshot(int productKey){
        CurrentMarketV4ProductContainer[] cmAry =  getProductCache(productKey);
        for (CurrentMarketV4ProductContainer cm : cmAry){
            dispatchEvent(getChannelTypeForPublishByProductKey(), cm.getProductKey(), cm );
        }
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void channelUpdate(ChannelEvent event)
    {
        final CurrentMarketV4ProductContainer currentMarket = (CurrentMarketV4ProductContainer)event.getEventData();
        int productKey = currentMarket.getProductKey();
        addProductCache(productKey, currentMarket);
    }
    /**
     * {@inheritDoc}.
     */
    @Override
    protected int getChannelTypeForPublishByProductKey()
    {
        return ProductCacheAPI.PUBLISH_CURRENT_MARKET_BY_PRODUCT;
    }
    /**
     * @return the classname.
     */
    protected String getLoggingPrefix()
    {
        return "CurrentMarketProductCacheImpl";
    }
    /**
     * {@inheritDoc}.
     */
    @Override
    protected CurrentMarketV4ProductContainer[] initTemplateArray()
    {
        return new CurrentMarketV4ProductContainer[]{};
    }
}
