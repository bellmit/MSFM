package com.cboe.consumers.callback;

import com.cboe.interfaces.consumers.callback.*;
import com.cboe.util.event.EventChannelAdapter;

import com.cboe.presentation.common.properties.AppPropertiesFileFactory;
import com.cboe.presentation.common.logging.GUILoggerHome;

/**
 * Created by IntelliJ IDEA.
 * User: HallB
 * Date: Apr 4, 2003
 * Time: 8:30:33 AM
 * To change this template use Options | File Templates.
 */
public class CMICallbackV2ConsumerCacheFactoryImpl implements CallbackV2ConsumerCacheFactory
{
    private static final String PROPERTY_SECTION = "Defaults";
    private static final String EOP_PROPERTY_NAME = "NumEOPV2Consumers";

    EventChannelAdapter eventChannel;
    CMICurrentMarketV2ConsumerCache currentMarketCache;
    CMIRecapV2ConsumerCache recapCache;
    CMINBBOV2ConsumerCache nbboCache;
    CMIOrderBookV2ConsumerCache bookDepthCache;
    CMIExpectedOpeningPriceV2ConsumerCache eopCache;

    public CMICallbackV2ConsumerCacheFactoryImpl(EventChannelAdapter eventChannel)
    {
        if(eventChannel == null)
        {
            throw new IllegalArgumentException("EventChannelAdapter cannot be null.");
        }
        this.eventChannel = eventChannel;
        // creating the caches in the constructor to avoid possible synchronization issues when creating them lazily
        currentMarketCache = new CMICurrentMarketV2ConsumerCacheImpl(eventChannel);
        recapCache = new CMIRecapV2ConsumerCacheImpl(eventChannel);
        nbboCache = new CMINBBOV2ConsumerCacheImpl(eventChannel);
        bookDepthCache = new CMIOrderBookV2ConsumerCacheImpl(eventChannel);

        int numEOPConsumers = CMIExpectedOpeningPriceV2ConsumerCacheImpl.DEFAULT_MAX_EOP_CONSUMERS;
        if(AppPropertiesFileFactory.isAppPropertiesAvailable())
        {
            String numEOPConsumersStr = AppPropertiesFileFactory.find().getValue(PROPERTY_SECTION, EOP_PROPERTY_NAME);
            try
            {
                numEOPConsumers = Integer.parseInt(numEOPConsumersStr);
            }
            catch(NumberFormatException e)
            {
                GUILoggerHome.find().alarm(getClass().getName(), "Warning: Properties file contained invalid int value '"+
                                                                 numEOPConsumersStr+"' for Property '"+
                                                                 EOP_PROPERTY_NAME+"'; will use default "+
                                                                 CMIExpectedOpeningPriceV2ConsumerCacheImpl.DEFAULT_MAX_EOP_CONSUMERS);
            }
        }
        eopCache = new CMIExpectedOpeningPriceV2ConsumerCacheImpl(eventChannel, numEOPConsumers);
    }

    public CMICurrentMarketV2ConsumerCache getCurrentMarketConsumerCache()
    {
        return currentMarketCache;
    }

    public CMIRecapV2ConsumerCache getRecapConsumerCache()
    {
        return recapCache;
    }

    public CMINBBOV2ConsumerCache getNBBOConsumerCache()
    {
        return nbboCache;
    }

    public CMIOrderBookV2ConsumerCache getBookDepthConsumerCache()
    {
        return bookDepthCache;
    }

    public CMIExpectedOpeningPriceV2ConsumerCache getEOPConsumerCache()
    {
        return eopCache;
    }
}
