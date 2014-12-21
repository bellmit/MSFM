//
// -----------------------------------------------------------------------------------
// Source file: NBBOV2CacheImpl.java
//
// PACKAGE: com.cboe.presentation.api.marketDataCache
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.api.marketDataCache;

import com.cboe.idl.cmiMarketData.NBBOStruct;
import com.cboe.idl.cmiProduct.ProductKeysStruct;

import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;

import com.cboe.interfaces.domain.SessionKeyWrapper;

import com.cboe.interfaces.presentation.api.marketDataCache.NBBOV2Cache;
import com.cboe.interfaces.presentation.product.SessionProduct;

import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;

import com.cboe.presentation.api.marketDataCache.test.NBBOV2GeneratorTask;

import com.cboe.domain.util.MarketDataStructBuilder;

public class NBBOV2CacheImpl extends AbstractSessionMarketDataCache<NBBOStruct>
    implements NBBOV2Cache
{
    private static final int SUBSCRIPTION_KEY = -1 * ChannelType.CB_NBBO_BY_CLASS;
    private String loggingPrefix;

    protected NBBOV2CacheImpl(String sessionName)
    {
        super(sessionName);
        loggingPrefix = "NBBOV2CacheImpl["+sessionName+"]";
    }

    protected String getLoggingPrefix()
    {
        return loggingPrefix;
    }

    protected void initializeMarketDataCache(int productKey) throws SystemException,
            CommunicationException, AuthorizationException, DataValidationException,
            NotFoundException
    {
        ProductKeysStruct productKeys = productQueryDelegate.getProductByKey(productKey).getProductKeysStruct();
        NBBOStruct nbbo = MarketDataStructBuilder.buildNBBOStruct(productKeys);
        nbbo.sessionName = getSessionName();
        addMarketDataByProduct(productKeys.classKey, productKeys.productKey, nbbo);

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
        return ChannelType.CB_NBBO_BY_CLASS;
    }

    /**
     * Returns the ChannelType constant for the events that will be published on the IEC
     * @return -1 if no events will be published by productKey, else returns a ChannelType constant
     */
    protected int getChannelTypeForPublishByProductKey()
    {
        return ChannelType.CB_NBBO_BY_PRODUCT;
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
        int channelType = ((ChannelKey)event.getChannel()).channelType;

        switch(channelType)
        {
            case SUBSCRIPTION_KEY:
                NBBOStruct nbbo = (NBBOStruct)event.getEventData();
                SessionKeyWrapper productKeyObj = getProductSessionKeyWrapper(nbbo.sessionName, nbbo.productKeys.productKey);
                SessionKeyWrapper classKeyObj = getClassSessionKeyWrapper(nbbo.sessionName, nbbo.productKeys.classKey);
                if (productKeyObj != null && classKeyObj != null)
                {
                    addMarketDataByProduct(nbbo.productKeys.classKey, nbbo.productKeys.productKey, nbbo);
                    registerUpdate(productKeyObj, classKeyObj);
                }

                publishMarketDataEvent(nbbo, nbbo.productKeys.classKey, nbbo.productKeys.productKey);
                break;
        }
    }

    // for temporary testing purposes only...
    protected NBBOV2GeneratorTask createNewTestTimerTask(SessionProduct[] products)
    {
        return new NBBOV2GeneratorTask(products);
    }
}
