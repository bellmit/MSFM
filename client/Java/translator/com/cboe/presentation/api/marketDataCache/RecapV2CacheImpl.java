//
// -----------------------------------------------------------------------------------
// Source file: RecapV2CacheImpl.java
//
// PACKAGE: com.cboe.presentation.api.marketDataCache
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.api.marketDataCache;

import com.cboe.idl.cmiMarketData.RecapStruct;
import com.cboe.idl.cmiProduct.ProductKeysStruct;

import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;

import com.cboe.interfaces.domain.SessionKeyWrapper;

import com.cboe.interfaces.presentation.api.marketDataCache.RecapV2Cache;
import com.cboe.interfaces.presentation.product.SessionProduct;

import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.ChannelType;
import com.cboe.util.ChannelKey;

import com.cboe.presentation.api.marketDataCache.test.RecapV2GeneratorTask;

import com.cboe.domain.util.MarketDataStructBuilder;

public class RecapV2CacheImpl extends AbstractSessionMarketDataCache<RecapStruct>
    implements RecapV2Cache
{
    private static final int SUBSCRIPTION_KEY = -1 * ChannelType.CB_RECAP_BY_CLASS;
    private String loggingPrefix;

    protected RecapV2CacheImpl(String sessionName)
    {
        super(sessionName);
        loggingPrefix = "RecapV2CacheImpl[" + sessionName + "]";
    }

    protected String getLoggingPrefix()
    {
        return loggingPrefix;
    }

    /*
    * NOTE:
    * This method is only called from getMarketDataForProduct() within a synchronized block.
    * No synchronization is needed here.
    */
    protected void initializeMarketDataCache(int productKey) throws SystemException,
            CommunicationException, AuthorizationException, DataValidationException,
            NotFoundException
    {
        ProductKeysStruct productKeys = productQueryDelegate.getProductByKey(productKey).getProductKeysStruct();
        RecapStruct recap = MarketDataStructBuilder.buildRecapStruct(productKeys);
        recap.sessionName = getSessionName();
        addMarketDataByProduct(productKeys.classKey, productKeys.productKey, recap);
        if(!isSubscribedForClass(productKeys.classKey))
        {
            subscribeMarketData(productKeys.classKey);
        }
    }

    /**
     * Returns the ChannelType constant for the events that will be published on the IEC
     * @return -1 if no events will be published by classKey, else returns a ChannelType constant
     */
    protected int getChannelTypeForPublishByClassKey()
    {
        return ChannelType.CB_RECAP_BY_CLASS;
    }

    /**
     * Returns the ChannelType constant for the events that will be published on the IEC
     * @return -1 if no events will be published by productKey, else returns a ChannelType constant
     */
    protected int getChannelTypeForPublishByProductKey()
    {
        return ChannelType.CB_RECAP_BY_PRODUCT;
    }

    /**
     * Returns the ChannelType constant that this cache will use to subscribe for events on the IEC
     * Subscriptions are class-based, but events may be re-published by both product and class.
     * @return ChannelType constant
     */
    protected int getChannelTypeForSubscribeByClassKey()
    {
        return SUBSCRIPTION_KEY;
    }

    /**
     * channelUpdate is called by the event channel adapter when it dispatches an event to the
     * registered listeners.
     */
    public void channelUpdate(ChannelEvent event)
    {
        int channelType = ((ChannelKey) event.getChannel()).channelType;

        switch(channelType)
        {
            case SUBSCRIPTION_KEY:
                RecapStruct recap = (RecapStruct)event.getEventData();
                SessionKeyWrapper productKeyObj = getProductSessionKeyWrapper(recap.sessionName, recap.productKeys.productKey);
                SessionKeyWrapper classKeyObj = getClassSessionKeyWrapper(recap.sessionName, recap.productKeys.classKey);
                if (productKeyObj != null && classKeyObj != null)
                {
                    addMarketDataByProduct(recap.productKeys.classKey, recap.productKeys.productKey, recap);
                    registerUpdate(productKeyObj, classKeyObj);
                }

                publishMarketDataEvent(recap, recap.productKeys.classKey, recap.productKeys.productKey);
                break;
        }
    }

    // for temporary testing purposes only...
    protected RecapV2GeneratorTask createNewTestTimerTask(SessionProduct[] products)
    {
        return new RecapV2GeneratorTask(products);
    }
}
