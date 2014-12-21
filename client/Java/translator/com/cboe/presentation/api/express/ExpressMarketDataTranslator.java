//
// -----------------------------------------------------------------------------------
// Source file: ExpressMarketDataTranslator.java
//
// PACKAGE: com.cboe.presentation.api.express
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.api.express;

import java.util.*;

import com.cboe.idl.cmiConstants.ExchangeStrings;
import com.cboe.idl.cmiConstants.PriceTypes;
import com.cboe.idl.cmiMarketData.CurrentMarketStruct;
import com.cboe.idl.cmiMarketData.CurrentMarketStructV4;
import com.cboe.idl.cmiMarketData.LastSaleStructV4;
import com.cboe.idl.cmiMarketData.NBBOStruct;
import com.cboe.idl.cmiMarketData.NBBOStructV4;
import com.cboe.idl.cmiMarketData.RecapStruct;
import com.cboe.idl.cmiMarketData.RecapStructV4;
import com.cboe.idl.cmiMarketData.TickerStruct;
import com.cboe.idl.cmiMarketData.TickerStructV4;
import com.cboe.idl.cmiProduct.ProductNameStruct;

import com.cboe.interfaces.domain.CurrentMarketProductContainer;
import com.cboe.interfaces.presentation.marketData.express.CurrentMarketV4ProductContainer;
import com.cboe.interfaces.presentation.marketData.express.LastSaleV4;
import com.cboe.interfaces.presentation.marketData.express.RecapV4;
import com.cboe.interfaces.presentation.marketData.express.TickerV4;
import com.cboe.interfaces.presentation.product.Product;
import com.cboe.interfaces.presentation.product.ProductClass;

import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.event.EventChannelAdapter;
import com.cboe.util.event.EventChannelAdapterFactory;
import com.cboe.util.event.EventChannelListener;

import com.cboe.presentation.common.formatters.DisplayPriceFactory;
import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.properties.AppPropertiesFileFactory;
import com.cboe.presentation.marketData.MarketVolumeStructHelper;
import com.cboe.presentation.marketData.express.ExpressMDTimeHelper;
import com.cboe.presentation.product.ProductHelper;

import com.cboe.domain.util.CurrentMarketProductContainerImpl;
import com.cboe.domain.util.PriceFactory;
import com.cboe.domain.util.SessionKeyContainer;
import com.cboe.domain.util.StructBuilder;

/**
 * This subscribes to the event channel for V4 (MDX) market data for CurrentMarket,
 * Recap/LastSale, and Ticker.  When it receives the V4 events, it translates them
 * into the corresponding V2 or V3 events and republishes them onto the event channel.
 *
 * The events this receives and the corresponding events that are republished are:
 *
 *     CB_CURRENT_MARKET_BY_CLASS_V4  ->  CB_CURRENT_MARKET_BY_CLASS_V3
 *
 *     CB_RECAP_BY_CLASS_V4           ->  CB_RECAP_BY_CLASS
 *     CB_RECAP_BY_CLASS_V4           ->  CB_RECAP_BY_PRODUCT
 *
 *     CB_LAST_SALE_BY_CLASS_V4       ->  CB_RECAP_BY_CLASS
 *     CB_LAST_SALE_BY_CLASS_V4       ->  CB_RECAP_BY_PRODUCT
 *
 *     CB_TICKER_BY_CLASS_V4          ->  CB_TICKER_BY_CLASS_V2
 *     CB_TICKER_BY_CLASS_V4          ->  CB_TICKER
 *
 */
public class ExpressMarketDataTranslator implements EventChannelListener
{
    protected static final String DEFAULT_PROPERTY_SECTION = "Defaults";
    protected static final String MDX_SUPPORTED_SESSIONS_PROPERTY = "MDXSupportedSessions";
    protected static final String EXCHANGE_STR_PROPERTY_SECTION = "MDXExchangeStrings";
    protected static final String DEFAULT_EXCHANGE = ExchangeStrings.CBOE;
    private Map<String, String[]> sessionExchanges;

    private static ExpressMarketDataTranslator instance;

    private EventChannelAdapter eventChannel;

    // when a session-based (i.e., non-MDX) subscription is received, we need to cache
    // the relation of the classKey to the sessionName, so we can translate/republish the
    // sessionless MDX data for the correct session
    private final Map<Integer, Set<String>> sessionsByClassKey;
    // in order to publish data on the IEC for Recap/LastSale for only a ProductClass'
    // primary exchange, we need to maintain a map of the translation between CMI primary
    // exchanges (long names, e.g., "NYSE") and the TIPS/MDX exchanges (e.g., "N")
    private Map<String, String> exchangeIdMap;
    // since it could be possible for productKeys to be used by multiple sessions (e.g., multiple
    // Equity sessions), we need to cache the structs by session, and productKey
    private final Map<String, Map<Integer, RecapStruct>> recapStructBySessionMap;
    // cache the list of sessions that MDX subscriptions will be supported for
    private final Set<String> mdxSupportedSessions = new TreeSet<String>();

    protected static synchronized ExpressMarketDataTranslator find()
    {
        if(instance == null)
        {
            instance = new ExpressMarketDataTranslator();
        }
        return instance;
    }

    private ExpressMarketDataTranslator()
    {
        eventChannel = EventChannelAdapterFactory.find();
        sessionsByClassKey = new HashMap<Integer, Set<String>>(10);
        sessionExchanges = new HashMap<String, String[]>(10);
        recapStructBySessionMap = new HashMap<String, Map<Integer, RecapStruct>>(2000);
        createTipsToCmiExchangeIdMapping();
    }

    public void subscribeCurrentMarketV4ForClass(String session, int classKey)
    {
        if(isMDXSupportedSession(session))
        {
            // initialize default exchanges for this session from app properties
            getDefaultExchanges(session);
            cacheSessionForClassKey(session, classKey);
            subscribeCurrentMarketV4ForClass(classKey);
        }
        else
        {
            throw new IllegalArgumentException("Trading session '" + session +
                                               "' is not supported by MDX (supported sessions: " +
                                               getMDXSupportedSessions().toString() + ")");
        }
    }

    public void subscribeNBBOV4ForClass(String session, int classKey)
    {
        getDefaultExchanges(session);
        cacheSessionForClassKey(session, classKey);
        ChannelKey key = new ChannelKey(ChannelType.CB_NBBO_BY_CLASS_V4, classKey);
        eventChannel.addChannelListener(eventChannel, this, key);
    }

    public void unsubscribeNBBOV4ForClass(int classKey)
    {
        ChannelKey key = new ChannelKey(ChannelType.CB_NBBO_BY_CLASS_V4, classKey);
        eventChannel.removeChannelListener(eventChannel, this, key);
    }

    // we don't want to expose this because we require subscriptions to include the trading session,
    // as they would for a standard V3 subscription, so they don't need access to this classKey-only-method
    private void subscribeCurrentMarketV4ForClass(int classKey)
    {
        ChannelKey key = new ChannelKey(ChannelType.CB_CURRENT_MARKET_BY_CLASS_V4, classKey);
        eventChannel.addChannelListener(eventChannel, this, key);
    }

    public void unsubscribeCurrentMarketV4ForClass(int classKey)
    {
        ChannelKey key = new ChannelKey(ChannelType.CB_CURRENT_MARKET_BY_CLASS_V4, classKey);
        eventChannel.removeChannelListener(eventChannel, this, key);
    }

    public void channelUpdate(ChannelEvent event)
    {
        int channelType = ((ChannelKey) event.getChannel()).channelType;
        ChannelEvent translatedEvent;

        Integer classKey;
        Set<String> sessions;
        switch(channelType)
        {
            case ChannelType.CB_CURRENT_MARKET_BY_CLASS_V4:
                CurrentMarketV4ProductContainer expressCurrentMarket =
                        (CurrentMarketV4ProductContainer) event.getEventData();
                classKey = (Integer) ((ChannelKey) event.getChannel()).key;
                // look up which trading sessions this classKey belongs to
                sessions = getSessionsForClassKey(classKey);
                // only publish data on the IEC if the event is for the "default" exchange for this trading session
                if(sessions != null && sessions.size() > 0)
                {
                    for(String session : sessions)
                    {
                        if(publishForExchange(expressCurrentMarket.getExchange(), session,
                                              classKey))
                        {
                            //convert the CurrentMarketV4 data to CurrentMarketV3
                            CurrentMarketProductContainer v3CurrentMarket = new CurrentMarketProductContainerImpl();

                            CurrentMarketStruct convertedStruct1 = convertCurrentMarketV4Struct(expressCurrentMarket
                                            .getBestMarket().getCurrentMarketStructV4(), session);
                            v3CurrentMarket.setBestMarket(convertedStruct1);

                            CurrentMarketStruct convertedStruct2 = convertCurrentMarketV4Struct(expressCurrentMarket
                                            .getBestPublicMarketAtTop().getCurrentMarketStructV4(), session);
                            v3CurrentMarket.setBestPublicMarketAtTop(convertedStruct2);

                            Object key = new SessionKeyContainer(session, classKey);

                            ChannelKey v3ChannelKey = new ChannelKey(-1 * ChannelType.CB_CURRENT_MARKET_BY_CLASS_V3, key);
                            translatedEvent = EventChannelAdapterFactory.find()
                                    .getChannelEvent(this, v3ChannelKey, v3CurrentMarket);
                            publishEvent(translatedEvent);

                            //also publish a CB_CURRENT_MARKET_BY_PRODUCT_V3 event
                            key = new SessionKeyContainer(session, expressCurrentMarket.getProductKey());
                            v3ChannelKey = new ChannelKey(-1 * ChannelType.CB_CURRENT_MARKET_BY_PRODUCT_V3, key);
                            translatedEvent = EventChannelAdapterFactory.find().getChannelEvent(this, v3ChannelKey, v3CurrentMarket);
                            publishEvent(translatedEvent);
                        }
                    }
                }
                break;
            case ChannelType.CB_RECAP_BY_CLASS_V4:
                RecapV4 recapWrapper = (RecapV4) event.getEventData();
                classKey = (Integer) ((ChannelKey) event.getChannel()).key;
                // look up which trading sessions this classKey belongs to
                sessions = getSessionsForClassKey(classKey);
                // only publish data on the IEC if the event is for the "default" exchange for this trading session
                if(sessions != null && sessions.size() > 0)
                {
                    for(String session : sessions)
                    {
                        if(publishForExchange(recapWrapper.getExchange(), session, classKey))
                        {
                            //convert the RecapV4 data to RecapV2
                            RecapStruct v2Struct =
                                    convertRecapV4Struct(recapWrapper.getRecapStructV4(), session);

                            ChannelKey v2ChannelKey = new ChannelKey(-1 * ChannelType.CB_RECAP_BY_CLASS,
                                                                     new SessionKeyContainer(
                                                                             session, classKey));
                            translatedEvent = EventChannelAdapterFactory.find()
                                    .getChannelEvent(this, v2ChannelKey, v2Struct);
                            publishEvent(translatedEvent);

                            v2ChannelKey = new ChannelKey(-1 * ChannelType.CB_RECAP_BY_PRODUCT,
                                                          new SessionKeyContainer(session,
                                                                                  v2Struct.productKeys.productKey));
                            translatedEvent = EventChannelAdapterFactory.find()
                                    .getChannelEvent(this, v2ChannelKey, v2Struct);
                            publishEvent(translatedEvent);
                        }
                    }
                }
                break;
            case ChannelType.CB_LAST_SALE_BY_CLASS_V4:
                LastSaleV4 lastSaleWrapper = (LastSaleV4) event.getEventData();
                classKey = (Integer) ((ChannelKey) event.getChannel()).key;
                // look up which trading sessions this classKey belongs to
                sessions = getSessionsForClassKey(classKey);
                // only publish data on the IEC if the event is for the "default" exchange for this trading session
                if(sessions != null && sessions.size() > 0)
                {
                    for(String session : sessions)
                    {
                        if(publishForExchange(lastSaleWrapper.getExchange(), session, classKey))
                        {
                            //convert the LastSaleV4 data to RecapV2
                            RecapStruct v2Struct = convertLastSaleV4Struct(
                                    lastSaleWrapper.getLastSaleStructV4(), session);

                            ChannelKey v2ChannelKey = new ChannelKey(-1 * ChannelType.CB_RECAP_BY_CLASS,
                                                                     new SessionKeyContainer(
                                                                             session, classKey));
                            translatedEvent = EventChannelAdapterFactory.find()
                                    .getChannelEvent(this, v2ChannelKey, v2Struct);
                            publishEvent(translatedEvent);

                            v2ChannelKey = new ChannelKey(-1 * ChannelType.CB_RECAP_BY_PRODUCT,
                                                          new SessionKeyContainer(session,
                                                                                  v2Struct.productKeys.productKey));
                            translatedEvent = EventChannelAdapterFactory.find()
                                    .getChannelEvent(this, v2ChannelKey, v2Struct);
                            publishEvent(translatedEvent);
                        }
                    }
                }
                break;
            case ChannelType.CB_TICKER_BY_CLASS_V4:
                TickerV4 tickerWrapper = (TickerV4) event.getEventData();
                classKey = (Integer) ((ChannelKey) event.getChannel()).key;
                // look up which trading sessions this classKey belongs to
                sessions = getSessionsForClassKey(classKey);
                // only publish data on the IEC if the event is for the "default" exchange for this trading session
                if(sessions != null && sessions.size() > 0)
                {
                    for(String session : sessions)
                    {
                        if(publishForExchange(tickerWrapper.getExchange(), session, classKey))
                        {
                            //convert the TickerStructV4 data to TickerStruct
                            TickerStruct v2Struct = convertTickerV4Struct(tickerWrapper.getTickerStructV4(), session);

                            ChannelKey v2ChannelKey = new ChannelKey(ChannelType.CB_TICKER,
                                                                     new SessionKeyContainer(session,
                                                                             v2Struct.productKeys.productKey));
                            translatedEvent = EventChannelAdapterFactory.find()
                                    .getChannelEvent(this, v2ChannelKey, v2Struct);
                            publishEvent(translatedEvent);

                            v2ChannelKey = new ChannelKey(ChannelType.CB_TICKER_BY_CLASS_V2,
                                                          new SessionKeyContainer(session,
                                                                                  classKey));
                            translatedEvent = EventChannelAdapterFactory.find()
                                    .getChannelEvent(this, v2ChannelKey, v2Struct);
                            publishEvent(translatedEvent);
                        }
                    }
                }
                break;
            case ChannelType.CB_NBBO_BY_CLASS_V4:
                NBBOStructV4 v4Struct = (NBBOStructV4) event.getEventData();
                classKey = v4Struct.classKey;
                // look up which trading sessions this classKey belongs to
                sessions = getSessionsForClassKey(classKey);
                // only publish data on the IEC if the event is for the "default" exchange for this trading session
                if(sessions != null && sessions.size() > 0)
                {
                    for(String session : sessions)
                    {
                        NBBOStruct struct = convertNBBOV4Struct(v4Struct, session);

                        ChannelKey key = new ChannelKey(-1 * ChannelType.CB_NBBO_BY_CLASS, new SessionKeyContainer(struct.sessionName, struct.productKeys.classKey));
                        translatedEvent = EventChannelAdapterFactory.find().getChannelEvent(this, key, struct);
                        publishEvent(translatedEvent);

                        key = new ChannelKey(-1 * ChannelType.CB_NBBO_BY_PRODUCT, new SessionKeyContainer(struct.sessionName, struct.productKeys.productKey));
                        translatedEvent = EventChannelAdapterFactory.find().getChannelEvent(this, key, struct);
                        publishEvent(translatedEvent);
                    }
                }
                break;
        }
    }

    /**
     * MDX will not provide data for some sessions (e.g., any Futures sessions).
     */
    public boolean isMDXSupportedSession(String session)
    {
        return getMDXSupportedSessions().contains(session);
    }

    public Set<String> getMDXSupportedSessions()
    {
        if(mdxSupportedSessions.size() == 0)
        {
            synchronized(mdxSupportedSessions)
            {
                String propVal = AppPropertiesFileFactory.find()
                        .getValue(DEFAULT_PROPERTY_SECTION, MDX_SUPPORTED_SESSIONS_PROPERTY);
                if (propVal != null)
                {
                    String[] sessions = propVal.split(",");
                    for(String s : sessions)
                    {
                        mdxSupportedSessions.add(s);
                    }
                }    
            }
        }
        return mdxSupportedSessions;
    }

    /**
     * Returns true if (based on applicatin properties) the MDX data should be translated
     * and republished for the given exchange and session.
     */
    protected boolean publishForExchange(String exchange, String tradingSession, int classKey)
    {
        boolean retVal = false;
        if(isMDXSupportedSession(tradingSession))
        {
            String[] exchanges = getDefaultExchanges(tradingSession);
            if(exchanges != null)
            {
                // if wildcard "*", then publish for all exchanges
                if(exchanges.length == 1 && "*".equals(exchanges[0]))
                {
                    retVal = true;
                }
                // if "PRIMARY", then publish for only the ProductClass' primary exchange
                else if(exchanges.length == 1 && "PRIMARY".equals(exchanges[0]))
                {
                    ProductClass productClass = ProductHelper.getProductClass(classKey);
                    if(productClass != null)
                    {
                        // check if exchange matches the ProductClass' primary exchg
                        if(primaryExchangeEqualsMDXExchange(productClass.getPrimaryExchange(), exchange))
                        {
                            retVal = true;
                        }
                    }
                }
                // else, publish only for exchanges that are exact matches for the designated default exchanges for the session
                else
                {
                    for(String tmpExchange : exchanges)
                    {
                        if(exchange.equals(tmpExchange))
                        {
                            retVal = true;
                            break;
                        }
                    }
                }
            }
            else
            {
                // if there were no exchanges designated as defaults in the properties file for an MDX-supported
                // trading session, then it may be a miss in the properties file
                retVal = true;
            }
        }
        return retVal;
    }

    protected synchronized String[] getDefaultExchanges(String tradingSession)
    {
        String[] exchanges = sessionExchanges.get(tradingSession);
        if(exchanges == null)
        {
            String propValue = AppPropertiesFileFactory.find().getValue(EXCHANGE_STR_PROPERTY_SECTION, tradingSession);
            if(propValue != null && propValue.length() > 0)
            {
                if(GUILoggerHome.find().isDebugOn() &&
                   GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
                {
                    GUILoggerHome.find().debug(getClass().getName() + ".getDefaultExchanges()",
                                               GUILoggerBusinessProperty.MARKET_QUERY,
                                               "Property (section:name) '" +
                                               EXCHANGE_STR_PROPERTY_SECTION + ":" +
                                               tradingSession + "' = '" + propValue + "'");
                }
                exchanges = propValue.split(",");
            }
            else
            {
                GUILoggerHome.find().alarm(getClass().getName() + ".getDefaultExchanges()",
                                           "Property (section:name) '" + EXCHANGE_STR_PROPERTY_SECTION + ":" +
                                           tradingSession + "' not found -- using default '" +
                                           DEFAULT_EXCHANGE + "'");
                exchanges = new String[]{DEFAULT_EXCHANGE};
            }
            sessionExchanges.put(tradingSession, exchanges);
        }
        return exchanges;
    }

    private CurrentMarketStruct convertCurrentMarketV4Struct(CurrentMarketStructV4 v4MarketData, String session)
    {
        CurrentMarketStruct v3MarketData = new CurrentMarketStruct();

        Product product = ProductHelper.getProduct(v4MarketData.productKey);
        v3MarketData.productKeys = product.getProductKeysStruct();
        v3MarketData.sessionName = session;
        v3MarketData.exchange = v4MarketData.exchange;
        v3MarketData.bidPrice = DisplayPriceFactory.create(v4MarketData.bidPrice, v4MarketData.priceScale).toStruct();
//        v3MarketData.bidPrice.type = PriceTypes.VALUED;    this messes up NO_PRICE type
        v3MarketData.bidSizeSequence = MarketVolumeStructHelper.convertV4MarketVolumeStructsToV3(v4MarketData.bidSizeSequence);

        v3MarketData.askPrice = DisplayPriceFactory.create(v4MarketData.askPrice, v4MarketData.priceScale).toStruct();
//        v3MarketData.askPrice.type = PriceTypes.VALUED;   this messes up NO_PRICE type
        v3MarketData.askSizeSequence = MarketVolumeStructHelper.convertV4MarketVolumeStructsToV3(v4MarketData.askSizeSequence);

        // v4MarketData.sentTime is millis since midnight
        v3MarketData.sentTime = ExpressMDTimeHelper.convertMillisSinceMidnightToTime(v4MarketData.sentTime);

        return v3MarketData;
    }

    protected NBBOStruct convertNBBOV4Struct(NBBOStructV4 v4Struct, String session)
    {
        NBBOStruct struct = new NBBOStruct();
        struct.productKeys = ProductHelper.getProduct(v4Struct.productKey).getProductKeysStruct();
        struct.askExchangeVolume = v4Struct.askExchangeVolume;
        struct.askPrice = DisplayPriceFactory.create(v4Struct.askPrice, v4Struct.priceScale).toStruct();

        struct.bidExchangeVolume = v4Struct.bidExchangeVolume;
        struct.bidPrice = DisplayPriceFactory.create(v4Struct.bidPrice, v4Struct.priceScale).toStruct();

        struct.sentTime = ExpressMDTimeHelper.convertMillisSinceMidnightToTime(v4Struct.sentTime);
        struct.sessionName = session;
        return struct;
    }

    private Map<Integer, RecapStruct> getRecapStructCacheMap(String session, int classKey)
    {
        Map<Integer, RecapStruct> sessionClassMap;
        //this is the only place where the structure of the recapStructBySessionMap is changed
        synchronized(recapStructBySessionMap)
        {
            String mapKey = new StringBuilder(session).append(classKey).toString();
            sessionClassMap = recapStructBySessionMap.get(mapKey);
            if(sessionClassMap == null)
            {
                sessionClassMap = new HashMap<Integer, RecapStruct>(200);
                recapStructBySessionMap.put(mapKey, sessionClassMap);
            }
        }
        return sessionClassMap;
    }

    private RecapStruct getCachedV2RecapStruct(String session, int classKey, int productKey)
    {
        Map<Integer, RecapStruct> sessionClassMap = getRecapStructCacheMap(session, classKey);
        RecapStruct retVal;
        synchronized(sessionClassMap)
        {
            retVal = sessionClassMap.get(productKey);
            if(retVal == null)
            {
                // create/cache a new RecapStruct for this product
                retVal = new RecapStruct();
                Product product = ProductHelper.getProduct(productKey);
                retVal.productKeys = product.getProductKeysStruct();
                retVal.productInformation = product.getProductNameStruct();
                retVal.sessionName = session;
                //initialize empty fields
                retVal.askPrice = PriceFactory.getNoPrice().toStruct();
                retVal.askTime = StructBuilder.buildTimeStruct();
                retVal.bidPrice = PriceFactory.getNoPrice().toStruct();
                retVal.bidTime = StructBuilder.buildTimeStruct();
                retVal.closePrice = PriceFactory.getNoPrice().toStruct();
                retVal.highPrice = PriceFactory.getNoPrice().toStruct();
                retVal.lastSalePrice = PriceFactory.getNoPrice().toStruct();
                retVal.lowPrice = PriceFactory.getNoPrice().toStruct();
                retVal.netChange = PriceFactory.getNoPrice().toStruct();
                retVal.openPrice = PriceFactory.getNoPrice().toStruct();
                retVal.previousClosePrice = PriceFactory.getNoPrice().toStruct();
                retVal.tick = PriceFactory.getNoPrice().toStruct();
                retVal.tradeTime = StructBuilder.buildTimeStruct();

                retVal.lastSaleVolume = 0;
                retVal.totalVolume = 0;
                retVal.tickDirection = ' ';
                retVal.netChangeDirection = ' ';
                retVal.bidDirection = ' ';
                retVal.bidSize = 0;
                retVal.askSize = 0;
                retVal.recapPrefix = "";
                retVal.openInterest = 0;
                retVal.isOTC = false;

                sessionClassMap.put(productKey, retVal);
            }
        }
        return retVal;
    }

    // get the cached v2 RecapStruct for the Product and update the fields in the struct that come from the RecapStructV4.
    private RecapStruct convertRecapV4Struct(RecapStructV4 v4Struct, String session)
    {
        RecapStruct clonedStruct;
        RecapStruct v2Struct = getCachedV2RecapStruct(session, v4Struct.classKey, v4Struct.productKey);
        // synchronize on the cached struct to guard against multiple threads trying to update the same struct at the same
        // time -- that could cause the fields of the struct to contain data from different events
        synchronized(v2Struct)
        {
            v2Struct.lowPrice = DisplayPriceFactory.create(v4Struct.lowPrice, v4Struct.priceScale).toStruct();
            v2Struct.highPrice = DisplayPriceFactory.create(v4Struct.highPrice, v4Struct.priceScale).toStruct();
            v2Struct.openPrice = DisplayPriceFactory.create(v4Struct.openPrice, v4Struct.priceScale).toStruct();
            v2Struct.previousClosePrice = DisplayPriceFactory.create(v4Struct.previousClosePrice, v4Struct.priceScale).toStruct();
            clonedStruct = cloneRecapStruct(v2Struct);
        }

        return clonedStruct;
    }

    // get the cached v2 RecapStruct for the Product and update the fields in the struct that come from the LastSaleStructV4.
    private RecapStruct convertLastSaleV4Struct(LastSaleStructV4 v4Struct, String session)
    {
        RecapStruct clonedStruct;
        RecapStruct v2Struct = getCachedV2RecapStruct(session, v4Struct.classKey, v4Struct.productKey);
        // synchronize on the cached struct to guard against multiple threads trying to update the same struct at the same
        // time -- that could cause the fields of the struct to contain data from different events
        synchronized(v2Struct)
        {
            v2Struct.lastSalePrice = DisplayPriceFactory.create(v4Struct.lastSalePrice, v4Struct.priceScale).toStruct();
            v2Struct.tradeTime = ExpressMDTimeHelper.convertMillisSinceMidnightToTime(v4Struct.lastSaleTime);
            v2Struct.lastSaleVolume = v4Struct.lastSaleVolume;
            v2Struct.totalVolume = v4Struct.totalVolume;
            v2Struct.tickDirection = v4Struct.tickDirection;
            v2Struct.netChange = DisplayPriceFactory.create(v4Struct.netPriceChange, v4Struct.priceScale).toStruct();

            v2Struct.bidTime = v2Struct.tradeTime;
            v2Struct.askTime = v2Struct.tradeTime;
            clonedStruct = cloneRecapStruct(v2Struct);
        }

        return clonedStruct;
    }

    private TickerStruct convertTickerV4Struct(TickerStructV4 v4Struct, String session)
    {
        TickerStruct v2Struct = new TickerStruct();
        v2Struct.productKeys = ProductHelper.getProduct(v4Struct.productKey).getProductKeysStruct();
        v2Struct.sessionName = session;
        v2Struct.exchangeSymbol = v4Struct.exchange;
        v2Struct.salePrefix = v4Struct.salePrefix;
        v2Struct.lastSalePrice = DisplayPriceFactory.create(v4Struct.tradePrice, v4Struct.priceScale).toStruct();
        v2Struct.lastSaleVolume = v4Struct.tradeVolume;
        v2Struct.salePostfix = v4Struct.salePostfix;
        return v2Struct;
    }

    private void publishEvent(ChannelEvent event)
    {
        if(event != null)
        {
            eventChannel.dispatch(event);
        }
    }

    public void subscribeRecapLastSaleV4ForClass(String session, int classKey)
    {
        if(isMDXSupportedSession(session))
        {
            // initialize default exchanges for this session from app properties
            getDefaultExchanges(session);
            cacheSessionForClassKey(session, classKey);
            subscribeRecapLastSaleV4ForClass(classKey);
        }
        else
        {
            throw new IllegalArgumentException("Trading session '" + session +
                                               "' is not supported by MDX (supported sessions: " +
                                               getMDXSupportedSessions().toString() + ")");
        }
    }

    // we don't want to expose this because we require subscriptions to include the trading session,
    // as they would for a standard V2 subscription, so they don't need access to this classKey-only-method
    private void subscribeRecapLastSaleV4ForClass(int classKey)
    {
        ChannelKey key = new ChannelKey(ChannelType.CB_RECAP_BY_CLASS_V4, classKey);
        eventChannel.addChannelListener(eventChannel, this, key);

        key = new ChannelKey(ChannelType.CB_LAST_SALE_BY_CLASS_V4, classKey);
        eventChannel.addChannelListener(eventChannel, this, key);
    }

    public void unsubscribeRecapLastSaleV4ForClass(int classKey)
    {
        ChannelKey key = new ChannelKey(ChannelType.CB_RECAP_BY_CLASS_V4, classKey);
        eventChannel.removeChannelListener(eventChannel, this, key);

        key = new ChannelKey(ChannelType.CB_LAST_SALE_BY_CLASS_V4, classKey);
        eventChannel.removeChannelListener(eventChannel, this, key);

        for(String session : getSessionsForClassKey(classKey))
        {
            Map<Integer, RecapStruct> sessionClassMap = getRecapStructCacheMap(session, classKey);
            synchronized(sessionClassMap)
            {
                sessionClassMap.clear();
            }
        }
    }

    private RecapStruct cloneRecapStruct(RecapStruct struct)
    {
        RecapStruct clonedStruct = new RecapStruct();

        clonedStruct.productKeys = struct.productKeys;
        clonedStruct.sessionName = struct.sessionName;

        clonedStruct.productInformation = new ProductNameStruct();
        clonedStruct.productInformation.reportingClass = struct.productInformation.reportingClass;
        clonedStruct.productInformation.exercisePrice = StructBuilder.clonePrice(struct.productInformation.exercisePrice);
        clonedStruct.productInformation.expirationDate = StructBuilder.cloneDate(struct.productInformation.expirationDate);
        clonedStruct.productInformation.optionType = struct.productInformation.optionType;
        clonedStruct.productInformation.productSymbol = struct.productInformation.productSymbol;

        clonedStruct.lastSalePrice = StructBuilder.clonePrice(struct.lastSalePrice);
        clonedStruct.tradeTime = StructBuilder.cloneTime(struct.tradeTime);
        clonedStruct.lastSaleVolume = struct.lastSaleVolume;
        clonedStruct.totalVolume = struct.totalVolume;
        clonedStruct.tickDirection = struct.tickDirection;
        clonedStruct.netChangeDirection = struct.netChangeDirection;
        clonedStruct.bidDirection = struct.bidDirection;
        clonedStruct.netChange = StructBuilder.clonePrice(struct.netChange);
        clonedStruct.bidPrice = StructBuilder.clonePrice(struct.bidPrice);
        clonedStruct.bidSize = struct.bidSize;
        clonedStruct.bidTime = StructBuilder.cloneTime(struct.bidTime);
        clonedStruct.askPrice = StructBuilder.clonePrice(struct.askPrice);
        clonedStruct.askSize = struct.askSize;
        clonedStruct.askTime = StructBuilder.cloneTime(struct.askTime);
        clonedStruct.recapPrefix = struct.recapPrefix;
        clonedStruct.tick = StructBuilder.clonePrice(struct.tick);
        clonedStruct.lowPrice = StructBuilder.clonePrice(struct.lowPrice);
        clonedStruct.highPrice = StructBuilder.clonePrice(struct.highPrice);
        clonedStruct.openPrice = StructBuilder.clonePrice(struct.openPrice);
        clonedStruct.closePrice = StructBuilder.clonePrice(struct.closePrice);
        clonedStruct.openInterest = struct.openInterest;
        clonedStruct.previousClosePrice = StructBuilder.clonePrice(struct.previousClosePrice);
        clonedStruct.isOTC = struct.isOTC;

        return clonedStruct;
    }

    public void subscribeTickerV4ForClass(String session, int classKey)
    {
        if(isMDXSupportedSession(session))
        {
            // initialize default exchanges for this session from app properties
            getDefaultExchanges(session);
            cacheSessionForClassKey(session, classKey);
            subscribeTickerV4ForClass(classKey);
        }
        else
        {
            throw new IllegalArgumentException("Trading session '" + session +
                                               "' is not supported by MDX (supported sessions: " +
                                               getMDXSupportedSessions().toString() + ")");
        }
    }

    // we don't want to expose this because we require subscriptions to include the trading session,
    // as they would for a standard V2 subscription, so they don't need access to this classKey-only-method
    private void subscribeTickerV4ForClass(int classKey)
    {
        ChannelKey key = new ChannelKey(ChannelType.CB_TICKER_BY_CLASS_V4, classKey);
        eventChannel.addChannelListener(eventChannel, this, key);
    }

    public void unsubscribeTickerV4ForClass(int classKey)
    {
        ChannelKey key = new ChannelKey(ChannelType.CB_TICKER_BY_CLASS_V4, classKey);
        eventChannel.removeChannelListener(eventChannel, this, key);
    }

    private boolean primaryExchangeEqualsMDXExchange(String cmiPrimaryExchange, String mdxExchange)
    {
        String cmiExchange = getCmiExchangeId(mdxExchange);
        boolean retVal = cmiExchange.equals(cmiPrimaryExchange);
        return retVal;
    }

    //maintain a set of all valid sessions for each classKey
    private void cacheSessionForClassKey(String session, int classKey)
    {
        synchronized(sessionsByClassKey)
        {
            Set<String> sessions = sessionsByClassKey.get(classKey);
            if(sessions == null)
            {
                sessions = new TreeSet<String>();
                sessionsByClassKey.put(classKey, sessions);
            }
            if(!sessions.contains(session))
            {
                sessions.add(session);
            }
        }
    }

    private Set<String> getSessionsForClassKey(int classKey)
    {
        Set<String> sessions;
        synchronized(sessionsByClassKey)
        {
            sessions = sessionsByClassKey.get(classKey);
        }
        return sessions;
    }

    /*
     * (NOTE: this mapping came from /vobs/dte/mdx/mdxTipsAdapter/Java/impls/com/cboe/mdx/mdxTipsAdapter/TipsMdxUpdateImpl.java)
     *
     * TIPS   CMI Mapping
     * ------ ------------------------------------------------------------------------
     * A      AMEX
     * B      BSE
     * W      CBOE
     * BF     CBOT
     * M      CHX
     * MF     CME
     * C      CSE
     * I      ISE
     * none   LIFFE
     * Q or T NASD    Q if stock is NASDAQ stock (I.E MSFT, DELL, GOOG, etc.),
     *                 T if NYSE/AMEX listed stock (I.E. IBM, GM, etc.)
     * none   NYME
     * N      NYSE
     * EU     ONE      Comes in as "E" but to make it distinct we append "U" as in fUtures.
     * X      PHLX
     * P      PSE
     * none   NQLX
     * BO     BOX
     * CU     CFE
     * D     NASD Alternative Display Facility (NOT the same as NASD Q or T)
     * DJ    Dow Jones Indices
     * AO    AMEX Options (Appears on AMEX indices reported via OPRA)
     * CO    CBOE Options (Appears on CBOE indices reported via OPRA)
     * IO    ISE Options (Appears on ISE indices reported via OPRA)
     * PO    Pacific / Arca-NYSE (Appears on PSE/Arca/NYSE indices reported via OPRA)
     * XO    PHLX Options (Appears on PHLX indices reported via OPRA)
     * NO    NYSE Options (PO will be migrating to "NO" sometime in early 2007)
     */
    private void createTipsToCmiExchangeIdMapping()
    {
        HashMap<String, String> map = new HashMap<String, String>(30);

        map.put("A", ExchangeStrings.AMEX);
        map.put("B", ExchangeStrings.BSE);
        map.put("W", ExchangeStrings.CBOE);
        map.put("BF", ExchangeStrings.CBOT);
        map.put("M", ExchangeStrings.CHX);
        map.put("MF", ExchangeStrings.CME);
        map.put("C", ExchangeStrings.CSE);
        map.put("I", ExchangeStrings.ISE);
        map.put("Q", ExchangeStrings.NASD);
        map.put("T", ExchangeStrings.NASD);
        map.put("N", ExchangeStrings.NYSE);
        map.put("EU", ExchangeStrings.ONE);
        map.put("X", ExchangeStrings.PHLX);
        map.put("P", ExchangeStrings.PSE);
        map.put("BO", ExchangeStrings.BOX);
        map.put("CU", ExchangeStrings.CFE);
        map.put("D", ExchangeStrings.NASD);
        map.put("DJ", "DJ");
        map.put("AO", ExchangeStrings.AMEX);
        map.put("CO", ExchangeStrings.CBOE);
        map.put("IO", ExchangeStrings.ISE);
        map.put("PO", ExchangeStrings.PSE);
        map.put("AO", ExchangeStrings.AMEX);
        map.put("NO", ExchangeStrings.NYSE);
        map.put("W", ExchangeStrings.CBOE2);
        map.put("Z", ExchangeStrings.BATS);
        map.put("g", "EDGA");
        map.put("h", "EDGX");

        exchangeIdMap = Collections.unmodifiableMap(map);
    }

    protected String getCmiExchangeId(String tipsMDXExchangeId)
    {
        String id = exchangeIdMap.get(tipsMDXExchangeId);
        return (id != null) ? id : tipsMDXExchangeId;
    }
}
