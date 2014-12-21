//
// -----------------------------------------------------------------------------------
// Source file: CurrentMarketV3CacheImpl.java
//
// PACKAGE: com.cboe.presentation.api.marketDataCache
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.api.marketDataCache;

import com.cboe.idl.cmiMarketData.CurrentMarketStruct;
import com.cboe.idl.cmiProduct.ProductKeysStruct;

import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;

import com.cboe.interfaces.domain.CurrentMarketProductContainer;
import com.cboe.interfaces.domain.SessionKeyWrapper;

import com.cboe.interfaces.presentation.api.marketDataCache.CurrentMarketV3Cache;
import com.cboe.interfaces.presentation.product.SessionProduct;

import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;

import com.cboe.presentation.api.marketDataCache.test.CurrentMarketV3GeneratorTask;

import com.cboe.domain.util.MarketDataStructBuilder;
import com.cboe.domain.util.CurrentMarketProductContainerImpl;

public class CurrentMarketV3CacheImpl extends AbstractSessionMarketDataCache<CurrentMarketProductContainer>
    implements CurrentMarketV3Cache
{
    private static final int SUBSCRIPTION_KEY = -1 * ChannelType.CB_CURRENT_MARKET_BY_CLASS_V3;
    private String loggingPrefix;

    protected CurrentMarketV3CacheImpl(String sessionName)
    {
        super(sessionName);
        loggingPrefix = "CurrentMarketV3CacheImpl["+sessionName+"]";
    }

    protected String getLoggingPrefix()
    {
        return loggingPrefix;
    }

    /**
     * Returns the ChannelType constant for the events that will be published on the IEC
     * @return -1 if no events will be published by classKey, else returns a ChannelType constant
     */
    protected int getChannelTypeForPublishByClassKey()
    {
        return ChannelType.CB_CURRENT_MARKET_BY_CLASS_V3;
    }

    /**
     * Returns the ChannelType constant for the events that will be published on the IEC
     * @return -1 if no events will be published by productKey, else returns a ChannelType constant
     */
    protected int getChannelTypeForPublishByProductKey()
    {
        return ChannelType.CB_CURRENT_MARKET_BY_PRODUCT_V3;
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

    /*
    * NOTE:
    * This method is only called from getMarketDataForProduct() within a synchronized block.
    * No synchronization is needed here.
    */
    protected void initializeMarketDataCache(int productKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
            NotFoundException
    {
        CurrentMarketStruct currentMarket;
        CurrentMarketStruct bestPublicMarketAtTop;
        CurrentMarketProductContainer currentMarketContainer;

        ProductKeysStruct productKeys = productQueryDelegate.getProductByKey(productKey).getProductKeysStruct();
        currentMarket = MarketDataStructBuilder.buildCurrentMarketStruct(productKeys);
        currentMarket.sessionName = getSessionName();
        bestPublicMarketAtTop = MarketDataStructBuilder.buildCurrentMarketStruct(productKeys);
        currentMarketContainer = new CurrentMarketProductContainerImpl(currentMarket, bestPublicMarketAtTop);

        int classKey = productKeys.classKey;
        addMarketDataByProduct(classKey, productKey, currentMarketContainer);

        if(!isSubscribedForClass(classKey))
        {
            subscribeMarketData(classKey);
        }
    }

    public void channelUpdate(ChannelEvent event)
    {
        int channelType = ((ChannelKey) event.getChannel()).channelType;

        switch(channelType)
        {
            case SUBSCRIPTION_KEY:
                CurrentMarketProductContainer currentMarket = (CurrentMarketProductContainer) event.getEventData();
                SessionKeyWrapper productKeyObj = getProductSessionKeyWrapper(currentMarket.getBestMarket().sessionName,
                                                                              currentMarket.getBestMarket().productKeys.productKey);
                SessionKeyWrapper classKeyObj = getClassSessionKeyWrapper(currentMarket.getBestMarket().sessionName,
                                                                          currentMarket.getBestMarket().productKeys.classKey);
                if(productKeyObj != null && classKeyObj != null)
                {
                    addMarketDataByProduct(currentMarket.getBestMarket().productKeys.classKey,
                                           currentMarket.getBestMarket().productKeys.productKey,
                                           currentMarket);
                    registerUpdate(productKeyObj, classKeyObj);
                }

                publishMarketDataEvent(currentMarket,
                                       currentMarket.getBestMarket().productKeys.classKey,
                                       currentMarket.getBestMarket().productKeys.productKey);
                break;
            default:
                break;
        }
    }


    // for temporary testing purposes only...
    protected CurrentMarketV3GeneratorTask createNewTestTimerTask(SessionProduct[] products)
    {
        return new CurrentMarketV3GeneratorTask(products);
    }
}
