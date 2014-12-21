//
// -----------------------------------------------------------------------------------
// Source file: NBBOProductCacheImpl.java
//
// PACKAGE: com.cboe.presentation.api.productcache
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2009 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.api.productcache;

import com.cboe.idl.cmiMarketData.NBBOStructV4;
import com.cboe.interfaces.presentation.api.productcache.NBBOProductCache;
import com.cboe.util.channel.ChannelEvent;

/**
 * Concrete implementation of the NBBOProductCache. 
 * This class listens to the IEC and store/invalidate/retreive the 
 * data from cache.
 * 
 * This class is Thread safe.
 * 
 * @author Eric Maheo
 *
 */
public class NBBOProductCacheImpl extends AbstractProductCache<NBBOStructV4, NBBOCacheElement> 
                                    implements NBBOProductCache<NBBOStructV4>
{

    /**
     * Create a NBBOProductCache element to store in product cache.
     */
    public NBBOProductCacheImpl(){}

    /**
     * {@inheritDoc}.
     */
    @Override
    public void publishMarketDataSnapshot(int productKey)
    {
        NBBOStructV4[] cmAry =  getProductCache(productKey);
        for (NBBOStructV4 cm : cmAry){
            dispatchEvent(getChannelTypeForPublishByProductKey(), cm.productKey, cm );
        }
    }
    
    /**
     * {@inheritDoc}.
     */
    @Override
    public void addProductCache(int productKey, NBBOStructV4 struct)
    {
       NBBOCacheElement element = new NBBOCacheElement(struct);
       
       try {
           lockUpdates.lock();
           table.put(productKey, element);
           updates.add(struct);
       }
       finally{
           lockUpdates.unlock();
       }
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void channelUpdate(ChannelEvent event)
    {
        NBBOStructV4 nbbo = (NBBOStructV4)event.getEventData();
        int productKey = nbbo.productKey;

        NBBOCacheElement prevCacheElement;
        NBBOStructV4 prevNbbo = null;

        /**
         * Get previous NBBO data and compare time stamp to see if this event is more recent.
         */
        prevCacheElement = table.get(productKey);
        if(prevCacheElement != null)
        {
            prevNbbo = (prevCacheElement.getProductForAllExchangeMarket())[0];
        }

        if (prevNbbo == null || prevNbbo.sentTime < nbbo.sentTime)
        {
            addProductCache(productKey, nbbo);
        }
    }
    
    /**
     * {@inheritDoc}.
     */
    @Override
    protected int getChannelTypeForPublishByProductKey()
    {
        return ProductCacheAPI.PUBLISH_NBBO_BY_PRODUCT;
    }
    
    /**
     * @return the classname.
     */
    protected String getLoggingPrefix()
    {
        return "NBBOProductCacheImpl";
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    protected NBBOStructV4[] initTemplateArray()
    {
        return new NBBOStructV4[]{};
    }
}