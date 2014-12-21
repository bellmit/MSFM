//
// -----------------------------------------------------------------------------------
// Source file: RecapProductCacheImpl.java
//
// PACKAGE: com.cboe.presentation.api.productcache
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2009 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.api.productcache;

import com.cboe.interfaces.presentation.api.productcache.RecapProductCache;
import com.cboe.interfaces.presentation.marketData.express.RecapV4;
import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.util.channel.ChannelEvent;

/**
 * Concrete implementation of the RecapProductCache. 
 * This class listens to the IEC and store/invalidate/retreive the 
 * data from cache.
 * This class is Thread safe.
 * 
 * @author Eric Maheo
 *
 */
public class RecapProductCacheImpl extends AbstractProductCache<RecapV4, RecapCacheElement> 
                implements RecapProductCache<RecapV4>
{

    /**
     * Construct this object.
     */
    public RecapProductCacheImpl(){
        super();
    }
    
    @Override
    protected int getChannelTypeForPublishByProductKey()
    {
        return ProductCacheAPI.PUBLISH_RECAP_BY_PRODUCT;
    }

    @Override
    public void addProductCache(int productKey, RecapV4 struct)
    {
        lockUpdates.lock();
        try {
            RecapCacheElement element = table.get(productKey);

            if (element == null){
                element = new RecapCacheElement(struct);
                element.addProductForExchangeMarket(struct.getExchange(), struct);
                table.put(productKey, element);
                updates.add(struct);
            }
            else {
                int prevSeqNum = -1;
                RecapV4 prevMarketData = element.getProductForExchangeMarket(struct.getExchange());
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
        finally {
            lockUpdates.unlock();
        }
    }

    /**
     * Publish the market snapshot for the product productKey stored into the cache.
     * If productKey isn't in cache not publish occurs.
     */
    public void publishMarketDataSnapshot(int productKey){
        RecapV4[] cmAry =  getProductCache(productKey);
        for (RecapV4 cm : cmAry){
            dispatchEvent(getChannelTypeForPublishByProductKey(), cm.getProductKey(), cm );
        }
    }

    @Override
    public void channelUpdate(ChannelEvent event)
    {
        final RecapV4 recap = (RecapV4)event.getEventData();
        int productKey = recap.getProductKey();
        addProductCache(productKey, recap);
    }
    
    /**
     * @return the classname.
     */
    protected String getLoggingPrefix()
    {
        return "RecapProductCacheImpl";
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    protected RecapV4[] initTemplateArray()
    {
        return new RecapV4[]{};
    }
}
