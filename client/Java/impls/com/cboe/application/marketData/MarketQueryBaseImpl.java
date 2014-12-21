package com.cboe.application.marketData;

import com.cboe.application.shared.ServicesHelper;
import com.cboe.application.shared.consumer.*;
import com.cboe.application.supplier.*;
import com.cboe.application.util.MarketDataHelper;
import com.cboe.domain.iec.ClientIECFactory;
import com.cboe.domain.iec.ConcurrentEventChannelAdapter;
import com.cboe.domain.logout.LogoutServiceFactory;
import com.cboe.domain.supplier.proxy.BaseSupplierProxy;
import com.cboe.domain.util.*;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiMarketData.*;
import com.cboe.idl.marketData.InternalCurrentMarketStruct;
import com.cboe.idl.marketData.InternalTickerDetailStruct;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.*;
import com.cboe.interfaces.application.subscription.SubscriptionService;
import com.cboe.interfaces.domain.CurrentMarketContainer;
import com.cboe.interfaces.domain.instrumentedChannel.InstrumentedChannelListenerProxy;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.UserDataTypes;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.channel.ChannelListener;
import com.cboe.util.channel.ChannelListenerProxy;
import com.cboe.util.event.EventChannelAdapterFactory;
import com.cboe.util.event.EventChannelListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Collections;

/**
 * @author Jing Chen
 */
public class MarketQueryBaseImpl extends BObject implements RecapCollector, TickerCollector, CurrentMarketCollector, ExpectedOpeningPriceCollector, BookDepthCollector, UserSessionLogoutCollector, SubscriptionFields, EventChannelListener{
    //--------------------------------------------------------------------------
    // data members
    //--------------------------------------------------------------------------
    protected BaseSessionManager baseSession;
    private ConcurrentEventChannelAdapter internalEventChannel;

    // Callback Suppliers
    protected CurrentMarketSupplier currentMarketSupplier;
    protected BookDepthSupplier bookDepthSupplier;
    protected NBBOSupplier theNBBOSupplier;
    protected RecapSupplier recapSupplier;
    protected TickerSupplier tickerSupplier;
    protected ExpectedOpeningPriceSupplier openingPriceSupplier;
    protected CurrentMarketV2Supplier currentMarketV2Supplier;
    protected CurrentMarketV3Supplier currentMarketV3Supplier;
    protected BookDepthV2Supplier bookDepthV2Supplier;
    protected NBBOV2Supplier theNBBOV2Supplier;
    protected RecapV2Supplier recapV2Supplier;
    protected TickerV2Supplier tickerV2Supplier;
    protected ExpectedOpeningPriceV2Supplier openingPriceV2Supplier;
    protected LargeTradeLastSaleSupplier largeTradeLastSaleSupplier;
    
    // Event Channel Processors
    protected UserSessionLogoutProcessor logoutProcessor;
    protected ExpectedOpeningPriceProcessor expectedOpeningPriceProcessor;
    protected RecapProcessor recapProcessor = null;
    protected TickerProcessor tickerProcessor = null;
    protected CurrentMarketProcessor currentMarketProcessor = null;
    protected BookDepthProcessor bookDepthProcessor = null;
    protected String userId;
    protected SubscriptionService subscriptionService;
    protected boolean localMarketDataService;
    // These maps are really not maps.  Huh?  They are used as sets that simply
    // contain the class keys of classes that this user session is interested
    // in.  The purpose of them is to allow the code to optimize the publishing
    // of market data based on whether or not this user session is interested in
    // the data on a class-level or only on a product-level.  If the session is
    // interested in class-level marker data AND product-level data for that
    // same class, we don't need to double publish the data, since the product-
    // level notifications will be covered by the class-level notifications.
    //
    // Note: There are no maps for EOP, ticker, or book depth class interest for
    // version 1.  This is because the version 1 CMI callbacks only get passed
    // one item at a time for these types of market data, as opposed to sequences.
    //
    // The reason that we're using maps instead of sets is simply because the
    // standard java.util.* set implementations just use HashMap objects to do
    // the actual storage anyway, and adding another layer (for the set objects)
    // on top of the HashMap objects would just be less efficient.
    protected Map recapClassInterests;
    protected Map recapClassInterestsV2;
    protected Map currentMarketClassInterests;
    protected Map currentMarketClassInterestsV2;
    protected Map currentMarketClassInterestsV3;
    protected Map NBBOClassInterests;
    protected Map NBBOClassInterestsV2;
    protected Map eopClassInterestsV2;
    protected Map tickerClassInterestsV2;
    protected Map largeTradeLastSaleClassInterests;
    protected Map bookDepthClassInterestsV2;

    protected static int marketDataCallbackTimeout;

    protected void publishMarketData(int channelType, String sessionName, int key, Object listener, short actionOnQueue)
    {
        Object[] publishInfo = new Object[QUEUE_ACTION_FIELD + 1];
        publishInfo[CHANNEL_FIELD] = Integer.valueOf(channelType);
        publishInfo[SESSION_FIELD] = sessionName;
        publishInfo[KEY_FIELD] = Integer.valueOf(key);
        publishInfo[LISTENER_FIELD] = listener;
        publishInfo[QUEUE_ACTION_FIELD] = Short.valueOf(actionOnQueue);
        ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, this, publishInfo);
        EventChannelAdapterFactory.find().dispatch(event);
    }

    public MarketQueryBaseImpl() {
        super();
        try
        {
            internalEventChannel = ClientIECFactory.findConcurrentEventChannelAdapter(ClientIECFactory.MARKETDATA_INSTRUMENTED_IEC);
        }
        catch (Exception e)
        {
        	Log.exception("Exception getting MARKETDATA_INSTRUMENTED_IEC!", e);
        }
    }

    public void initialize() throws Exception
    {
        // CBOE Event Channel Processors
        expectedOpeningPriceProcessor = ExpectedOpeningPriceProcessorFactory.create(this);
        currentMarketProcessor = CurrentMarketProcessorFactory.create(this);
        bookDepthProcessor = BookDepthProcessorFactory.create(this);
        recapProcessor = RecapProcessorFactory.create(this);
        tickerProcessor = TickerProcessorFactory.create(this);

        // Callback Suppliers
        currentMarketSupplier = CurrentMarketSupplierFactory.find(baseSession);
        bookDepthSupplier = BookDepthSupplierFactory.find(baseSession);
        theNBBOSupplier = NBBOSupplierFactory.find(baseSession);
        recapSupplier = RecapSupplierFactory.find(baseSession);
        tickerSupplier = TickerSupplierFactory.find(baseSession);
        openingPriceSupplier = ExpectedOpeningPriceSupplierFactory.find(baseSession);

        // Make Sure all channels are dynamic
        currentMarketSupplier.setDynamicChannels(true);
        recapSupplier.setDynamicChannels(true);
        tickerSupplier.setDynamicChannels(true);
        bookDepthSupplier.setDynamicChannels(true);
        openingPriceSupplier.setDynamicChannels(true);
        theNBBOSupplier.setDynamicChannels(true);

        // Callback Suppliers
        currentMarketV2Supplier = CurrentMarketV2SupplierFactory.find(baseSession);
        currentMarketV3Supplier = CurrentMarketV3SupplierFactory.find(baseSession);
        bookDepthV2Supplier = BookDepthV2SupplierFactory.find(baseSession);
        theNBBOV2Supplier = NBBOV2SupplierFactory.find(baseSession);
        recapV2Supplier = RecapV2SupplierFactory.find(baseSession);
        tickerV2Supplier = TickerV2SupplierFactory.find(baseSession);
        // VTATS
        largeTradeLastSaleSupplier = LargeTradeLastSaleSupplierFactory.find(baseSession);
        openingPriceV2Supplier = ExpectedOpeningPriceV2SupplierFactory.find(baseSession);
        

        // Make Sure all channels are dynamic
        currentMarketV2Supplier.setDynamicChannels(true);
        currentMarketV3Supplier.setDynamicChannels(true);
        recapV2Supplier.setDynamicChannels(true);
        tickerV2Supplier.setDynamicChannels(true);
        //  VTATS
        largeTradeLastSaleSupplier.setDynamicChannels(true);
        bookDepthV2Supplier.setDynamicChannels(true);
        openingPriceV2Supplier.setDynamicChannels(true);
        theNBBOV2Supplier.setDynamicChannels(true);
        subscriptionService = ServicesHelper.getSubscriptionService(baseSession);

        // Class-level (versus product-leve) interest maps.  These maps are used
        // internally by this class as sets, because no values are stored in the
        // maps, only keys.  And the set of keys is the only data ever extracted
        // from these maps and actually used.
        recapClassInterests = Collections.synchronizedMap(new HashMap(11));
        recapClassInterestsV2 = Collections.synchronizedMap(new HashMap(11));
        currentMarketClassInterests = Collections.synchronizedMap(new HashMap(11));
        currentMarketClassInterestsV2 = Collections.synchronizedMap(new HashMap(11));
        currentMarketClassInterestsV3 = Collections.synchronizedMap(new HashMap(11));
        NBBOClassInterests = Collections.synchronizedMap(new HashMap(11));
        NBBOClassInterestsV2 = Collections.synchronizedMap(new HashMap(11));
        eopClassInterestsV2 = Collections.synchronizedMap(new HashMap(11));
        tickerClassInterestsV2 = Collections.synchronizedMap(new HashMap(11));
        // VTATS
        largeTradeLastSaleClassInterests  = Collections.synchronizedMap(new HashMap(11));
        bookDepthClassInterestsV2 = Collections.synchronizedMap(new HashMap(11));
        EventChannelAdapterFactory.find().addChannelListener(this, this, this);
    }

    public void create(String name)
    {
        super.create(name);
    }

    public void acceptRecapsForClass(RecapStruct[] recaps)
    {
        ChannelKey channelKey;
        ChannelEvent event;
        SessionKeyContainer productKey;
        int length = recaps.length;

        if (length > 0)
        {
            // create channel key for recap by class and dispatch new recap data
            SessionKeyContainer classKey = new SessionKeyContainer(recaps[0].sessionName, recaps[0].productKeys.classKey);
            Integer classId = recaps[0].productKeys.classKey;
            boolean publishByClassV1 = recapClassInterests.get(classId)!=null;
            boolean publishByClassV2 = recapClassInterestsV2.get(classId)!=null;

            if (publishByClassV1)
            {
                channelKey = new ChannelKey(ChannelType.CB_RECAP_BY_CLASS, classKey);
                event = internalEventChannel.getChannelEvent(this, channelKey, recaps);
                recapSupplier.dispatch(event);
            }

            if (publishByClassV2)
            {
                channelKey = new ChannelKey(ChannelType.CB_RECAP_BY_CLASS_V2, classKey);
                event = internalEventChannel.getChannelEvent(this, channelKey, recaps);
                recapV2Supplier.dispatch(event);
            }

            if (!publishByClassV1 && !publishByClassV2)
            {
                RecapStruct[] recap = new RecapStruct[1];

                for(int i=0; i<length; i++)
                {
                    productKey = new SessionKeyContainer(recaps[i].sessionName, recaps[i].productKeys.productKey);
                    recap[0] = recaps[i];
                    channelKey = new ChannelKey(ChannelType.CB_RECAP_BY_PRODUCT, productKey);
                    event = internalEventChannel.getChannelEvent(this, channelKey, recap);
                    recapSupplier.dispatch(event);

                    channelKey = new ChannelKey(ChannelType.CB_RECAP_BY_PRODUCT_V2, productKey);
                    event = internalEventChannel.getChannelEvent(this, channelKey, recap);
                    recapV2Supplier.dispatch(event);
                }
            }
        }
    }

    public void acceptCurrentMarketsForClass(CurrentMarketContainer marketBestWithPublic)
    {
        CurrentMarketStruct bestMarket[] = marketBestWithPublic.getBestMarkets();
        CurrentMarketStruct bestPublicMarket[] = marketBestWithPublic.getBestPublicMarketsAtTop();
        int bestMarketlength = bestMarket.length;
        if (bestMarketlength > 0)
        {
            if (!publishMarketDataByClass(bestMarket, marketBestWithPublic))
            {
                publishMarketDataByProduct(bestMarket, bestPublicMarket);
            }
        }
    }

    private void publishMarketDataByProduct(CurrentMarketStruct[] bestMarket, CurrentMarketStruct[] bestPublicMarket)
    {
        int bestMarketlength = bestMarket.length;
        int bestPublicMarketlength = bestPublicMarket.length;
        ChannelKey channelKey;
        ChannelEvent event;
        SessionKeyContainer productKey = null;

        CurrentMarketStruct[] currentMarket = new CurrentMarketStruct[1];
        CurrentMarketStruct[] bestPublicCurrentMarket = new CurrentMarketStruct[1];

        for(int i=0; i<bestMarketlength; i++)
        {
            productKey = new SessionKeyContainer(bestMarket[i].sessionName, bestMarket[i].productKeys.productKey);

            currentMarket[0] = bestMarket[i];
            bestPublicCurrentMarket[0] = null;
            channelKey = new ChannelKey(ChannelType.CB_CURRENT_MARKET_BY_PRODUCT, productKey);
            event = internalEventChannel.getChannelEvent(this, channelKey, currentMarket);
            currentMarketSupplier.dispatch(event);

            channelKey = new ChannelKey(ChannelType.CB_CURRENT_MARKET_BY_PRODUCT_V2, productKey);
            event = internalEventChannel.getChannelEvent(this, channelKey, currentMarket);
            currentMarketV2Supplier.dispatch(event);

            CurrentMarketContainerImpl productMarketWithPublic = new CurrentMarketContainerImpl(currentMarket);
            if ( bestPublicMarketlength > 0)
            {
                int publicIndexAtBest = MarketDataHelper.findPublicIndexAtBest(bestMarket[i].productKeys.productKey, bestPublicMarket);
                if (publicIndexAtBest != -1)
                {
                    bestPublicCurrentMarket[0] = bestPublicMarket[publicIndexAtBest];
                    productMarketWithPublic.setBestPublicMarketsAtTop(bestPublicCurrentMarket);
                }
            }
            channelKey = new ChannelKey(ChannelType.CB_CURRENT_MARKET_BY_PRODUCT_V3, productKey);
            event = internalEventChannel.getChannelEvent(this, channelKey, productMarketWithPublic);
            currentMarketV3Supplier.dispatch(event);
        }
    }

    private boolean publishMarketDataByClass(CurrentMarketStruct[] bestMarket, CurrentMarketContainer marketBestWithPublic)
    {
        SessionKeyContainer classKey = new SessionKeyContainer(bestMarket[0].sessionName, bestMarket[0].productKeys.classKey);
        ChannelKey channelKey;
        ChannelEvent event;
        boolean publishByClass = false;
        Integer classId = bestMarket[0].productKeys.classKey;

        boolean publishByClassV1 = currentMarketClassInterests.get(classId)!=null;
        boolean publishByClassV2 = currentMarketClassInterestsV2.get(classId)!=null;
        boolean publishByClassV3 = currentMarketClassInterestsV3.get(classId)!=null;

        // if there is class interest, we push the data through class channel,
        // otherwise, the data will flow through product channel.
        if (publishByClassV1)
        {
            channelKey = new ChannelKey(ChannelType.CB_CURRENT_MARKET_BY_CLASS, classKey);
            event = internalEventChannel.getChannelEvent(this, channelKey, bestMarket);
            currentMarketSupplier.dispatch(event);
            publishByClass = true;
        }

        if (publishByClassV2)
        {
            channelKey = new ChannelKey(ChannelType.CB_CURRENT_MARKET_BY_CLASS_V2, classKey);
            event = internalEventChannel.getChannelEvent(this, channelKey, bestMarket);
            currentMarketV2Supplier.dispatch(event);
            publishByClass = true;
        }

        if (publishByClassV3)
        {
            channelKey = new ChannelKey(ChannelType.CB_CURRENT_MARKET_BY_CLASS_V3, classKey);
            event = internalEventChannel.getChannelEvent(this, channelKey, marketBestWithPublic);
            currentMarketV3Supplier.dispatch(event);
            publishByClass = true;
        }

        return publishByClass;
    }


    public void acceptNBBOsForClass(NBBOStruct[] nbbos)
    {
        int length = nbbos.length;
        if (length > 0)
        {
            SessionKeyContainer classKey = new SessionKeyContainer(nbbos[0].sessionName, nbbos[0].productKeys.classKey);
            ChannelKey channelKey;
            ChannelEvent event;
            Integer classId = nbbos[0].productKeys.classKey;
            boolean publishByClassV1 = NBBOClassInterests.get(classId)!=null;
            boolean publishByClassV2 = NBBOClassInterestsV2.get(classId)!=null;

            if (publishByClassV1)
            {
                channelKey = new ChannelKey(ChannelType.CB_NBBO_BY_CLASS, classKey);
                event = internalEventChannel.getChannelEvent(this, channelKey, nbbos);
                theNBBOSupplier.dispatch(event);
            }

            if (publishByClassV2)
            {
                channelKey = new ChannelKey(ChannelType.CB_NBBO_BY_CLASS_V2, classKey);
                event = internalEventChannel.getChannelEvent(this, channelKey, nbbos);
                theNBBOV2Supplier.dispatch(event);
            }

            if (!publishByClassV1 && !publishByClassV2)
            {
                SessionKeyContainer productKey = null;
                NBBOStruct[] nbbo = new NBBOStruct[1];
                for(int i=0; i<length; i++)
                {
                    productKey = new SessionKeyContainer(nbbos[i].sessionName, nbbos[i].productKeys.productKey);
                    nbbo[0] = nbbos[i];
                    channelKey = new ChannelKey(ChannelType.CB_NBBO_BY_PRODUCT, productKey);
                    event = internalEventChannel.getChannelEvent(this, channelKey, nbbo);
                    theNBBOSupplier.dispatch(event);

                    channelKey = new ChannelKey(ChannelType.CB_NBBO_BY_PRODUCT_V2, productKey);
                    event = internalEventChannel.getChannelEvent(this, channelKey, nbbo);
                    theNBBOV2Supplier.dispatch(event);
                }
            }
        }
    }

    public void acceptBookDepthsForClass(BookDepthStruct[] bookDepths)
    {

        int length = bookDepths.length;
        if (length > 0)
        {
            SessionKeyContainer classKey = new SessionKeyContainer(bookDepths[0].sessionName, bookDepths[0].productKeys.classKey);
            ChannelKey channelKey;
            ChannelEvent event;
            Integer classId = bookDepths[0].productKeys.classKey;
            boolean publishByClassV2 = bookDepthClassInterestsV2.get(classId)!=null;

            // if there is class interest, we push the data through class channel, otherwise, the data will flow through product channel.
            if (publishByClassV2)
            {
                channelKey = new ChannelKey(ChannelType.CB_BOOK_DEPTH_BY_CLASS_V2, classKey);
                event = internalEventChannel.getChannelEvent(this, channelKey, bookDepths);
                bookDepthV2Supplier.dispatch(event);
            }
            else
            {
                SessionKeyContainer productKey = null;
                BookDepthStruct [] bookDepth = new BookDepthStruct[1];

                for(int i=0; i<length; i++)
                {
                    productKey = new SessionKeyContainer(bookDepths[i].sessionName, bookDepths[i].productKeys.productKey);
                    channelKey = new ChannelKey(ChannelType.CB_BOOK_DEPTH_BY_PRODUCT, productKey);
                    event = internalEventChannel.getChannelEvent(this, channelKey, bookDepths[i]);
                    bookDepthSupplier.dispatch(event);

                    if (!publishByClassV2)
                    {
                        bookDepth[0] = bookDepths[i];
                        channelKey = new ChannelKey(ChannelType.CB_BOOK_DEPTH_BY_PRODUCT_V2, productKey);
                        event = internalEventChannel.getChannelEvent(this, channelKey, bookDepth);
                        bookDepthV2Supplier.dispatch(event);
                    }
                }
            }
        }
    }

    public void acceptTickersForClass(TickerStruct[] tickers)
    {
        int length = tickers.length;
        if (length > 0)
        {
            ChannelKey channelKey;
            ChannelEvent event;
            Integer classId = tickers[0].productKeys.classKey;
            boolean publishByClassV2 = tickerClassInterestsV2.get(classId)!=null;

            if (publishByClassV2)
            {
                SessionKeyContainer classKey = new SessionKeyContainer(tickers[0].sessionName, tickers[0].productKeys.classKey);
                channelKey = new ChannelKey(ChannelType.CB_TICKER_BY_CLASS_V2, classKey);
                event = internalEventChannel.getChannelEvent(this, channelKey, tickers);
                tickerV2Supplier.dispatch(event);
            }

            SessionKeyContainer productKey = null;
            TickerStruct[] ticker = new TickerStruct[1];

            for (int i=0; i<length; i++)
            {
                // create channel key for ticker by product and dispatch new ticker data
                productKey = new SessionKeyContainer(tickers[i].sessionName, tickers[i].productKeys.productKey);
                ticker[0] = tickers[i];

                channelKey = new ChannelKey(ChannelType.CB_TICKER, productKey);
                event = internalEventChannel.getChannelEvent(this, channelKey, ticker);
                tickerSupplier.dispatch(event);

                if (!publishByClassV2)
                {
                    channelKey = new ChannelKey(ChannelType.CB_TICKER_BY_PRODUCT_V2, productKey);
                    event = internalEventChannel.getChannelEvent(this, channelKey, ticker);
                    tickerV2Supplier.dispatch(event);
                }
            }
        }
    }

    public void acceptExpectedOpeningPricesForClass(ExpectedOpeningPriceStruct[] expectedOpeningPrice)
    {
        int length = expectedOpeningPrice.length;
        if (length > 0)
        {
            SessionKeyContainer classKey = new SessionKeyContainer(expectedOpeningPrice[0].sessionName, expectedOpeningPrice[0].productKeys.classKey);
            ChannelKey channelKey;
            ChannelEvent event;
            Integer classId = expectedOpeningPrice[0].productKeys.classKey;
            boolean publishByClassV2 = eopClassInterestsV2.get(classId)!=null;

            if (publishByClassV2)
            {
                channelKey = new ChannelKey(ChannelType.CB_OPENING_PRICE_BY_CLASS_V2, classKey);
                event = internalEventChannel.getChannelEvent(this, channelKey, expectedOpeningPrice);
                openingPriceV2Supplier.dispatch(event);
            }

            // create channel key for expected opening price by class and
            // dispatch new price data
            ExpectedOpeningPriceStruct[] eop = new ExpectedOpeningPriceStruct[1];

            for (int i = 0; i<expectedOpeningPrice.length; i++)
            {
                SessionKeyContainer productKey=new SessionKeyContainer(expectedOpeningPrice[i].sessionName,  expectedOpeningPrice[i].productKeys.productKey);

                channelKey = new ChannelKey(ChannelType.CB_EXPECTED_OPENING_PRICE, classKey);
                event = internalEventChannel.getChannelEvent(this, channelKey, expectedOpeningPrice[i]);
                openingPriceSupplier.dispatch(event);

                if (!publishByClassV2)
                {
                    eop[0] = expectedOpeningPrice[i];
                    channelKey = new ChannelKey(ChannelType.CB_OPENING_PRICE_BY_PRODUCT_V2, productKey);
                    event = internalEventChannel.getChannelEvent(this, channelKey, eop);
                    openingPriceV2Supplier.dispatch(event);
                }
            }
        }
    }

    protected void subscribeMarketDataForClassByType(String sessionName, int classKey, int channelType, ChannelListener proxyListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        SessionKeyContainer sessionClassKey = new SessionKeyContainer(sessionName, classKey);
        ChannelListenerProxy channelListenerProxy = null;
        ChannelKey channelKey = new ChannelKey(channelType, sessionClassKey);
        Integer classKeyObj = classKey;
        switch (channelType)
        {
            case ChannelType.CB_RECAP_BY_CLASS:
                channelListenerProxy = recapSupplier.addChannelListener(this, proxyListener, channelKey, sessionClassKey);
                channelKey = new ChannelKey(ChannelType.RECAP_BY_CLASS, sessionClassKey);
                internalEventChannel.addChannelListener(this, recapProcessor, channelKey);
                subscriptionService.addRecapClassInterest(proxyListener, sessionName, classKey);
                recapClassInterests.put(classKeyObj, classKeyObj);
                break;
            case ChannelType.CB_RECAP_BY_CLASS_V2:
                channelListenerProxy = recapV2Supplier.addChannelListener(this, proxyListener, channelKey, sessionClassKey);
                channelKey = new ChannelKey(ChannelType.RECAP_BY_CLASS, sessionClassKey);
                internalEventChannel.addChannelListener(this, recapProcessor, channelKey);
                subscriptionService.addRecapClassInterest(proxyListener, sessionName, classKey);
                recapClassInterestsV2.put(classKeyObj, classKeyObj);
                break;
            case ChannelType.CB_CURRENT_MARKET_BY_CLASS:
                channelListenerProxy = currentMarketSupplier.addChannelListener(this, proxyListener, channelKey, sessionClassKey);
                channelKey = new ChannelKey(ChannelType.CURRENT_MARKET_BY_CLASS, sessionClassKey);
                internalEventChannel.addChannelListener(this, currentMarketProcessor, channelKey);
                subscriptionService.addCurrentMarketClassInterest(proxyListener, sessionName, classKey);
                currentMarketClassInterests.put(classKeyObj, classKeyObj);
                break;
            case ChannelType.CB_CURRENT_MARKET_BY_CLASS_V2:
                channelListenerProxy = currentMarketV2Supplier.addChannelListener(this, proxyListener, channelKey, sessionClassKey);
                channelKey = new ChannelKey(ChannelType.CURRENT_MARKET_BY_CLASS, sessionClassKey);
                internalEventChannel.addChannelListener(this, currentMarketProcessor, channelKey);
                subscriptionService.addCurrentMarketClassInterest(proxyListener, sessionName, classKey);
                currentMarketClassInterestsV2.put(classKeyObj, classKeyObj);
                break;
            case ChannelType.CB_CURRENT_MARKET_BY_CLASS_V3:
                channelListenerProxy = currentMarketV3Supplier.addChannelListener(this, proxyListener, channelKey, sessionClassKey);
                channelKey = new ChannelKey(ChannelType.CURRENT_MARKET_BY_CLASS, sessionClassKey);
                internalEventChannel.addChannelListener(this, currentMarketProcessor, channelKey);
                subscriptionService.addCurrentMarketClassInterest(proxyListener, sessionName, classKey);
                currentMarketClassInterestsV3.put(classKeyObj, classKeyObj);
                break;
            case ChannelType.CB_NBBO_BY_CLASS:
                channelListenerProxy = theNBBOSupplier.addChannelListener(this, proxyListener, channelKey, sessionClassKey);
                channelKey = new ChannelKey(ChannelType.NBBO_BY_CLASS, sessionClassKey);
                internalEventChannel.addChannelListener(this, currentMarketProcessor, channelKey);
                subscriptionService.addNBBOClassInterest(proxyListener, sessionName, classKey);
                NBBOClassInterests.put(classKeyObj, classKeyObj);
                break;
            case ChannelType.CB_NBBO_BY_CLASS_V2:
                channelListenerProxy = theNBBOV2Supplier.addChannelListener(this, proxyListener, channelKey, sessionClassKey);
                channelKey = new ChannelKey(ChannelType.NBBO_BY_CLASS, sessionClassKey);
                internalEventChannel.addChannelListener(this, currentMarketProcessor, channelKey);
                subscriptionService.addNBBOClassInterest(proxyListener, sessionName, classKey);
                NBBOClassInterestsV2.put(classKeyObj, classKeyObj);
                break;
            case ChannelType.CB_EXPECTED_OPENING_PRICE:
                channelListenerProxy = openingPriceSupplier.addChannelListener(this, proxyListener, channelKey, sessionClassKey);
                channelKey = new ChannelKey(ChannelType.OPENING_PRICE_BY_CLASS, sessionClassKey);
                internalEventChannel.addChannelListener(this, expectedOpeningPriceProcessor, channelKey);
                subscriptionService.addOpeningPriceClassInterest(proxyListener, sessionName, classKey);
                // No need to track this listener by class, since the EOP callback
                // can only receive EOP market data one at a time anyway.
                break;
            case ChannelType.CB_OPENING_PRICE_BY_CLASS_V2:
                channelListenerProxy = openingPriceV2Supplier.addChannelListener(this, proxyListener, channelKey, sessionClassKey);
                channelKey = new ChannelKey(ChannelType.OPENING_PRICE_BY_CLASS, sessionClassKey);
                internalEventChannel.addChannelListener(this, expectedOpeningPriceProcessor, channelKey);
                subscriptionService.addOpeningPriceClassInterest(proxyListener, sessionName, classKey);
                eopClassInterestsV2.put(classKeyObj, classKeyObj);
                break;
            case ChannelType.CB_TICKER_BY_CLASS_V2:
                channelListenerProxy = tickerV2Supplier.addChannelListener(this, proxyListener, channelKey, sessionClassKey);
                channelKey = new ChannelKey(ChannelType.TICKER_BY_CLASS, sessionClassKey);
                internalEventChannel.addChannelListener(this, tickerProcessor, channelKey);
                subscriptionService.addTickerClassInterest(proxyListener, sessionName, classKey);
                tickerClassInterestsV2.put(classKeyObj, classKeyObj);
                break;
            case ChannelType.CB_BOOK_DEPTH_BY_CLASS_V2:
                channelListenerProxy = bookDepthV2Supplier.addChannelListener(this, proxyListener, channelKey, sessionClassKey);
                channelKey = new ChannelKey(ChannelType.BOOK_DEPTH_BY_CLASS, sessionClassKey);
                internalEventChannel.addChannelListener(this, bookDepthProcessor, channelKey);
                subscriptionService.addBookDepthClassInterest(proxyListener, sessionName, classKey);
                bookDepthClassInterestsV2.put(classKeyObj, classKeyObj);
                break;
            case ChannelType.CB_LARGE_TRADE_LAST_SALE_BY_CLASS:	
            	channelListenerProxy = largeTradeLastSaleSupplier.addChannelListener(this, proxyListener, channelKey, sessionClassKey);
                if (Log.isDebugOn())
                {
            	Log.debug(this + " add proxyListener to its supplier with channelKey: " + channelKey + " sessionClassKey: " + sessionClassKey);
                }
                channelKey = new ChannelKey(ChannelType.LARGE_TRADE_LAST_SALE_BY_CLASS, sessionClassKey);
                internalEventChannel.addChannelListener(this, tickerProcessor, channelKey);
                if (Log.isDebugOn())
                {
                Log.debug(this + " add tickerProcessor for LargeTrade to IEC with channelKey: " +  channelKey);
                }
                subscriptionService.addLargeTradeLastSaleClassInterest(proxyListener, sessionName, classKey);
                largeTradeLastSaleClassInterests.put(classKeyObj, classKeyObj);
                break;
            default:
                break;
        }
        try
        {
            String sessionClassKeyString = SessionKeyUserDataHelper.encode(sessionClassKey);
            InstrumentedChannelListenerProxy instrumentedCLProxy = (InstrumentedChannelListenerProxy) channelListenerProxy;
            instrumentedCLProxy.addUserData(UserDataTypes.SESSION_CLASS, sessionClassKeyString);
        }
        catch(ClassCastException e)
        {
            Log.exception(this, "ClassCastException during addition of user data.", e);
        }

        String smgr = baseSession.toString();
        String listenerName = proxyListener.toString();
        StringBuilder suboid = new StringBuilder(smgr.length()+listenerName.length()+sessionName.length()+50);
        suboid.append("Sub:oid for ").append(smgr).append(' ').append(listenerName)
              .append(" sessionName:").append(sessionName)
              .append(" classKey:").append(classKey);
        Log.information(this, suboid.toString());
    }

    protected void subscribeMarketDataForProductByType(String sessionName, int classKey, int productKey, int channelType, ChannelListener proxyListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        SessionKeyContainer sessionProductKey = new SessionKeyContainer(sessionName, productKey);
        SessionKeyContainer classKeyContainer = new SessionKeyContainer(sessionName, classKey);
        ChannelListenerProxy channelListenerProxy = null;
        ChannelKey channelKey = new ChannelKey(channelType, sessionProductKey);
        switch (channelType)
        {
            case ChannelType.CB_RECAP_BY_PRODUCT:
                channelListenerProxy = recapSupplier.addChannelListener(this, proxyListener, channelKey, sessionProductKey);
                channelKey = new ChannelKey(ChannelType.RECAP_BY_CLASS, classKeyContainer);
                internalEventChannel.addChannelListener(this, recapProcessor, channelKey);
                subscriptionService.addRecapProductInterest(proxyListener, sessionName, classKey, productKey);
                break;
            case ChannelType.CB_RECAP_BY_PRODUCT_V2:
                channelListenerProxy = recapV2Supplier.addChannelListener(this, proxyListener, channelKey, sessionProductKey);
                channelKey = new ChannelKey(ChannelType.RECAP_BY_CLASS, classKeyContainer);
                internalEventChannel.addChannelListener(this, recapProcessor, channelKey);
                subscriptionService.addRecapProductInterest(proxyListener, sessionName, classKey, productKey);
                break;
            case ChannelType.CB_CURRENT_MARKET_BY_PRODUCT:
                channelListenerProxy = currentMarketSupplier.addChannelListener(this, proxyListener, channelKey, sessionProductKey);
                channelKey = new ChannelKey(ChannelType.CURRENT_MARKET_BY_CLASS, classKeyContainer);
                internalEventChannel.addChannelListener(this, currentMarketProcessor, channelKey);
                subscriptionService.addCurrentMarketProductInterest(proxyListener, sessionName, classKey, productKey);
                break;
            case ChannelType.CB_CURRENT_MARKET_BY_PRODUCT_V2:
                channelListenerProxy = currentMarketV2Supplier.addChannelListener(this, proxyListener, channelKey, sessionProductKey);
                channelKey = new ChannelKey(ChannelType.CURRENT_MARKET_BY_CLASS, classKeyContainer);
                internalEventChannel.addChannelListener(this, currentMarketProcessor, channelKey);
                subscriptionService.addCurrentMarketProductInterest(proxyListener, sessionName, classKey, productKey);
                break;
            case ChannelType.CB_CURRENT_MARKET_BY_PRODUCT_V3:
                channelListenerProxy = currentMarketV3Supplier.addChannelListener(this, proxyListener, channelKey, sessionProductKey);
                channelKey = new ChannelKey(ChannelType.CURRENT_MARKET_BY_CLASS, classKeyContainer);
                internalEventChannel.addChannelListener(this, currentMarketProcessor, channelKey);
                subscriptionService.addCurrentMarketProductInterest(proxyListener, sessionName, classKey, productKey);
                break;
            case ChannelType.CB_NBBO_BY_PRODUCT:
                channelListenerProxy = theNBBOSupplier.addChannelListener(this, proxyListener, channelKey, sessionProductKey);
                channelKey = new ChannelKey(ChannelType.NBBO_BY_CLASS, classKeyContainer);
                internalEventChannel.addChannelListener(this, currentMarketProcessor, channelKey);
                subscriptionService.addNBBOProductInterest(proxyListener, sessionName, classKey, productKey);
                break;
            case ChannelType.CB_NBBO_BY_PRODUCT_V2:
                channelListenerProxy = theNBBOV2Supplier.addChannelListener(this, proxyListener, channelKey, sessionProductKey);
                channelKey = new ChannelKey(ChannelType.NBBO_BY_CLASS, classKeyContainer);
                internalEventChannel.addChannelListener(this, currentMarketProcessor, channelKey);
                subscriptionService.addNBBOProductInterest(proxyListener, sessionName, classKey, productKey);
                break;
            case ChannelType.CB_OPENING_PRICE_BY_PRODUCT_V2:
                channelListenerProxy = openingPriceV2Supplier.addChannelListener(this, proxyListener, channelKey, sessionProductKey);
                channelKey = new ChannelKey(ChannelType.OPENING_PRICE_BY_CLASS, classKeyContainer);
                internalEventChannel.addChannelListener(this, expectedOpeningPriceProcessor, channelKey);
                subscriptionService.addOpeningPriceProductInterest(proxyListener, sessionName, classKey, productKey);
                break;
            case ChannelType.CB_TICKER:
                channelListenerProxy = tickerSupplier.addChannelListener(this, proxyListener, channelKey, sessionProductKey);
                channelKey = new ChannelKey(ChannelType.TICKER_BY_CLASS, classKeyContainer);
                internalEventChannel.addChannelListener(this, tickerProcessor, channelKey);
                subscriptionService.addTickerProductInterest(proxyListener, sessionName, classKey, productKey);
                break;
            case ChannelType.CB_TICKER_BY_PRODUCT_V2:
                channelListenerProxy = tickerV2Supplier.addChannelListener(this, proxyListener, channelKey, sessionProductKey);
                channelKey = new ChannelKey(ChannelType.TICKER_BY_CLASS, classKeyContainer);
                internalEventChannel.addChannelListener(this, tickerProcessor, channelKey);
                subscriptionService.addTickerProductInterest(proxyListener, sessionName, classKey, productKey);
                break;
            case ChannelType.CB_BOOK_DEPTH_BY_PRODUCT_V2:
                channelListenerProxy = bookDepthV2Supplier.addChannelListener(this, proxyListener, channelKey, sessionProductKey);
                channelKey = new ChannelKey(ChannelType.BOOK_DEPTH_BY_CLASS, classKeyContainer);
                internalEventChannel.addChannelListener(this, bookDepthProcessor, channelKey);
                subscriptionService.addBookDepthProductInterest(proxyListener, sessionName, classKey, productKey);
                break;
            case ChannelType.CB_BOOK_DEPTH_BY_PRODUCT:
                channelListenerProxy = bookDepthSupplier.addChannelListener(this, proxyListener, channelKey, sessionProductKey);
                channelKey = new ChannelKey(ChannelType.BOOK_DEPTH_BY_CLASS, classKeyContainer);
                internalEventChannel.addChannelListener(this, bookDepthProcessor, channelKey);
                subscriptionService.addBookDepthProductInterest(proxyListener, sessionName, classKey, productKey);
                break;
            default:
                break;
        }
        try
        {
            String sessionClassKeyString = SessionKeyUserDataHelper.encode(classKeyContainer);
            InstrumentedChannelListenerProxy instrumentedCLProxy = (InstrumentedChannelListenerProxy) channelListenerProxy;
            instrumentedCLProxy.addUserData(UserDataTypes.SESSION_CLASS, sessionClassKeyString);
        }
        catch(ClassCastException e)
        {
            Log.exception("ClassCastException during addition of user data.", e);
        }

        String smgr = baseSession.toString();
        String listenerName = proxyListener.toString();
        StringBuilder suboid = new StringBuilder(smgr.length()+listenerName.length()+sessionName.length()+70);
        suboid.append("Sub:oid for ").append(smgr).append(' ').append(listenerName)
              .append(" sessionName:").append(sessionName)
              .append(" productKey:").append(productKey)
              .append(" classKey:").append(classKey);
        Log.information(this, suboid.toString());
    }

    protected void unsubscribeMarketDataForProductByType(String sessionName, int classKey, int productKey, int channelType, ChannelListener proxyListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        SessionKeyContainer sessionProductKey = new SessionKeyContainer(sessionName, productKey);
        SessionKeyContainer sessionClassKey = new SessionKeyContainer(sessionName, classKey);
        ChannelKey channelKey = new ChannelKey(channelType, sessionProductKey);
        ChannelListenerProxy removedCLProxy = null;
        switch (channelType)
        {
            case ChannelType.CB_CURRENT_MARKET_BY_PRODUCT:
                subscriptionService.removeCurrentMarketProductInterest(proxyListener, sessionName, classKey, productKey);
                removedCLProxy = currentMarketSupplier.removeChannelListener(this, proxyListener, channelKey, sessionProductKey);
                channelKey = new ChannelKey(ChannelType.CURRENT_MARKET_BY_CLASS, sessionClassKey);
                internalEventChannel.removeChannelListener(this, currentMarketProcessor, channelKey);
                break;
            case ChannelType.CB_CURRENT_MARKET_BY_PRODUCT_V2:
                subscriptionService.removeCurrentMarketProductInterest(proxyListener, sessionName, classKey, productKey);
                removedCLProxy = currentMarketV2Supplier.removeChannelListener(this, proxyListener, channelKey, sessionProductKey);
                channelKey = new ChannelKey(ChannelType.CURRENT_MARKET_BY_CLASS, sessionClassKey);
                internalEventChannel.removeChannelListener(this, currentMarketProcessor, channelKey);
                break;
            case ChannelType.CB_CURRENT_MARKET_BY_PRODUCT_V3:
                subscriptionService.removeCurrentMarketProductInterest(proxyListener, sessionName, classKey, productKey);
                removedCLProxy = currentMarketV3Supplier.removeChannelListener(this, proxyListener, channelKey, sessionProductKey);
                channelKey = new ChannelKey(ChannelType.CURRENT_MARKET_BY_CLASS, sessionClassKey);
                internalEventChannel.removeChannelListener(this, currentMarketProcessor, channelKey);
                break;
            case ChannelType.CB_NBBO_BY_PRODUCT:
                subscriptionService.removeNBBOProductInterest(proxyListener, sessionName, classKey, productKey);
                removedCLProxy = theNBBOSupplier.removeChannelListener(this, proxyListener, channelKey, sessionProductKey);
                channelKey = new ChannelKey(ChannelType.NBBO_BY_CLASS, sessionClassKey);
                internalEventChannel.removeChannelListener(this, currentMarketProcessor, channelKey);
                break;
            case ChannelType.CB_NBBO_BY_PRODUCT_V2:
                subscriptionService.removeNBBOProductInterest(proxyListener, sessionName, classKey, productKey);
                removedCLProxy = theNBBOV2Supplier.removeChannelListener(this, proxyListener, channelKey, sessionProductKey);
                channelKey = new ChannelKey(ChannelType.NBBO_BY_CLASS, sessionClassKey);
                internalEventChannel.removeChannelListener(this, currentMarketProcessor, channelKey);
                break;
            case ChannelType.CB_RECAP_BY_PRODUCT:
                subscriptionService.removeRecapProductInterest(proxyListener, sessionName, classKey, productKey);
                removedCLProxy = recapSupplier.removeChannelListener(this, proxyListener, channelKey, sessionProductKey);
                channelKey = new ChannelKey(ChannelType.RECAP_BY_CLASS, sessionClassKey);
                internalEventChannel.removeChannelListener(this, recapProcessor, channelKey);
                break;
            case ChannelType.CB_RECAP_BY_PRODUCT_V2:
                subscriptionService.removeRecapProductInterest(proxyListener, sessionName, classKey, productKey);
                removedCLProxy = recapV2Supplier.removeChannelListener(this, proxyListener, channelKey, sessionProductKey);
                channelKey = new ChannelKey(ChannelType.RECAP_BY_CLASS, sessionClassKey);
                internalEventChannel.removeChannelListener(this, recapProcessor, channelKey);
                break;
            case ChannelType.CB_OPENING_PRICE_BY_PRODUCT_V2:
                subscriptionService.removeOpeningPriceProductInterest(proxyListener, sessionName, classKey, productKey);
                removedCLProxy = openingPriceV2Supplier.removeChannelListener(this, proxyListener, channelKey, sessionProductKey);
                channelKey = new ChannelKey(ChannelType.OPENING_PRICE_BY_CLASS, sessionClassKey);
                internalEventChannel.removeChannelListener(this, expectedOpeningPriceProcessor, channelKey);
                break;
            case ChannelType.CB_TICKER:
                subscriptionService.removeTickerProductInterest(proxyListener, sessionName, classKey, productKey);
                removedCLProxy = tickerSupplier.removeChannelListener(this, proxyListener, channelKey, sessionProductKey);
                channelKey = new ChannelKey(ChannelType.TICKER_BY_CLASS, sessionClassKey);
                internalEventChannel.removeChannelListener(this, tickerProcessor, channelKey);
                break;
            case ChannelType.CB_TICKER_BY_PRODUCT_V2:
                subscriptionService.removeTickerProductInterest(proxyListener, sessionName, classKey, productKey);
                removedCLProxy = tickerV2Supplier.removeChannelListener(this, proxyListener, channelKey, sessionProductKey);
                channelKey = new ChannelKey(ChannelType.TICKER_BY_CLASS, sessionClassKey);
                internalEventChannel.removeChannelListener(this, tickerProcessor, channelKey);
                break;
           case ChannelType.CB_BOOK_DEPTH_BY_PRODUCT:
                subscriptionService.removeBookDepthProductInterest(proxyListener, sessionName, classKey, productKey);
                removedCLProxy = bookDepthSupplier.removeChannelListener(this, proxyListener, channelKey, sessionProductKey);
                channelKey = new ChannelKey(ChannelType.BOOK_DEPTH_BY_CLASS, new SessionKeyContainer(sessionName, productKey));
                internalEventChannel.removeChannelListener(this, bookDepthProcessor, channelKey);
                break;
            case ChannelType.CB_BOOK_DEPTH_BY_PRODUCT_V2:
                subscriptionService.removeBookDepthProductInterest(proxyListener, sessionName, classKey, productKey);
                removedCLProxy = bookDepthV2Supplier.removeChannelListener(this, proxyListener, channelKey, sessionProductKey);
                channelKey = new ChannelKey(ChannelType.BOOK_DEPTH_BY_CLASS, new SessionKeyContainer(sessionName, productKey));
                internalEventChannel.removeChannelListener(this, bookDepthProcessor, channelKey);
                break;
            default:
                break;
        }

        try
        {
            InstrumentedChannelListenerProxy instrumentedCLProxyListener = (InstrumentedChannelListenerProxy) removedCLProxy;
            String sessionClassKeyString = SessionKeyUserDataHelper.encode(sessionClassKey);
            // todo: in the future, we will need to throw DVE if user pass the invalid callback for the unsubscription.
            // Since we ignored bad callback before, we will log alarm for now until we have warned all the firms whose software
            // exhibits this bad behavior
            if(instrumentedCLProxyListener != null)
            {
                instrumentedCLProxyListener.removeUserData(UserDataTypes.SESSION_CLASS, sessionClassKeyString);
            }
            else
            {
                Log.alarm(this, "Found invalid callback for market data unsubscription. Session:"+sessionName+" classKey:"+classKey+" productKey:"+productKey);
            }
        }
        catch(ClassCastException e)
        {
            Log.exception(this, "ClassCastException when removing sessionKey=" + sessionClassKey + " from user data", e);
        }

        String smgr = baseSession.toString();
        String listenerName = proxyListener.toString();
        StringBuilder unsuboid = new StringBuilder(smgr.length()+listenerName.length()+sessionName.length()+75);
        unsuboid.append("UnSub:oid for ").append(smgr).append(' ').append(listenerName)
                .append(" sessionName:").append(sessionName)
                .append(" classKey:").append(classKey)
                .append(" productKey:").append(productKey);
        Log.information(this, unsuboid.toString());
    }

    protected void unsubscribeMarketDataForClassByType(String sessionName, int classKey, int channelType, ChannelListener proxyListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        SessionKeyContainer sessionClassKey = new SessionKeyContainer(sessionName, classKey);
        ChannelKey channelKey = new ChannelKey(channelType, sessionClassKey);
        ChannelListenerProxy removedCLProxy = null;
        switch (channelType)
        {
            case ChannelType.CB_CURRENT_MARKET_BY_CLASS:
                subscriptionService.removeCurrentMarketClassInterest(proxyListener, sessionName, classKey);
                removedCLProxy = currentMarketSupplier.removeChannelListener(this, proxyListener, channelKey, sessionClassKey);
                channelKey = new ChannelKey(ChannelType.CURRENT_MARKET_BY_CLASS, sessionClassKey);
                internalEventChannel.removeChannelListener(this, currentMarketProcessor, channelKey);
                currentMarketClassInterests.remove(Integer.valueOf(classKey));
                break;
            case ChannelType.CB_CURRENT_MARKET_BY_CLASS_V2:
                subscriptionService.removeCurrentMarketClassInterest(proxyListener, sessionName, classKey);
                removedCLProxy = currentMarketV2Supplier.removeChannelListener(this, proxyListener, channelKey, sessionClassKey);
                channelKey = new ChannelKey(ChannelType.CURRENT_MARKET_BY_CLASS, sessionClassKey);
                internalEventChannel.removeChannelListener(this, currentMarketProcessor, channelKey);
                currentMarketClassInterestsV2.remove(Integer.valueOf(classKey));
                break;
            case ChannelType.CB_CURRENT_MARKET_BY_CLASS_V3:
                subscriptionService.removeCurrentMarketClassInterest(proxyListener, sessionName, classKey);
                removedCLProxy = currentMarketV3Supplier.removeChannelListener(this, proxyListener, channelKey, sessionClassKey);
                channelKey = new ChannelKey(ChannelType.CURRENT_MARKET_BY_CLASS, sessionClassKey);
                internalEventChannel.removeChannelListener(this, currentMarketProcessor, channelKey);
                currentMarketClassInterestsV3.remove(Integer.valueOf(classKey));
                break;
            case ChannelType.CB_NBBO_BY_CLASS:
                subscriptionService.removeNBBOClassInterest(proxyListener, sessionName, classKey);
                removedCLProxy = theNBBOSupplier.removeChannelListener(this, proxyListener, channelKey, sessionClassKey);
                channelKey = new ChannelKey(ChannelType.NBBO_BY_CLASS, sessionClassKey);
                internalEventChannel.removeChannelListener(this, currentMarketProcessor, channelKey);
                NBBOClassInterests.remove(Integer.valueOf(classKey));
                break;
            case ChannelType.CB_NBBO_BY_CLASS_V2:
                subscriptionService.removeNBBOClassInterest(proxyListener, sessionName, classKey);
                removedCLProxy = theNBBOV2Supplier.removeChannelListener(this, proxyListener, channelKey, sessionClassKey);
                channelKey = new ChannelKey(ChannelType.NBBO_BY_CLASS, sessionClassKey);
                internalEventChannel.removeChannelListener(this, currentMarketProcessor, channelKey);
                NBBOClassInterestsV2.remove(Integer.valueOf(classKey));
                break;
            case ChannelType.CB_RECAP_BY_CLASS:
                subscriptionService.removeRecapClassInterest(proxyListener, sessionName, classKey);
                removedCLProxy = recapSupplier.removeChannelListener(this, proxyListener, channelKey, sessionClassKey);
                channelKey = new ChannelKey(ChannelType.RECAP_BY_CLASS, sessionClassKey);
                internalEventChannel.removeChannelListener(this, recapProcessor, channelKey);
                recapClassInterests.remove(Integer.valueOf(classKey));
                break;
            case ChannelType.CB_RECAP_BY_CLASS_V2:
                subscriptionService.removeRecapClassInterest(proxyListener, sessionName, classKey);
                removedCLProxy = recapV2Supplier.removeChannelListener(this, proxyListener, channelKey, sessionClassKey);
                channelKey = new ChannelKey(ChannelType.RECAP_BY_CLASS, sessionClassKey);
                internalEventChannel.removeChannelListener(this, recapProcessor, channelKey);
                recapClassInterestsV2.remove(Integer.valueOf(classKey));
                break;
            case ChannelType.CB_OPENING_PRICE_BY_CLASS_V2:
                subscriptionService.removeOpeningPriceClassInterest(proxyListener, sessionName, classKey);
                removedCLProxy = openingPriceV2Supplier.removeChannelListener(this, proxyListener, channelKey, sessionClassKey);
                channelKey = new ChannelKey(ChannelType.OPENING_PRICE_BY_CLASS, sessionClassKey);
                internalEventChannel.removeChannelListener(this, expectedOpeningPriceProcessor, channelKey);
                eopClassInterestsV2.remove(Integer.valueOf(classKey));
                break;
            case ChannelType.CB_EXPECTED_OPENING_PRICE:
                subscriptionService.removeOpeningPriceClassInterest(proxyListener, sessionName, classKey);
                removedCLProxy = openingPriceSupplier.removeChannelListener(this, proxyListener, channelKey, sessionClassKey);
                channelKey = new ChannelKey(ChannelType.OPENING_PRICE_BY_CLASS, sessionClassKey);
                internalEventChannel.removeChannelListener(this, expectedOpeningPriceProcessor, channelKey);
                // No need to track this listener by class, since the EOP callback
                // can only receive EOP market data one at a time anyway.
                break;
            case ChannelType.CB_TICKER_BY_CLASS_V2:
                subscriptionService.removeTickerClassInterest(proxyListener, sessionName, classKey);
                removedCLProxy = tickerV2Supplier.removeChannelListener(this, proxyListener, channelKey, sessionClassKey);
                channelKey = new ChannelKey(ChannelType.TICKER_BY_CLASS, sessionClassKey);
                internalEventChannel.removeChannelListener(this, tickerProcessor, channelKey);
                tickerClassInterestsV2.remove(Integer.valueOf(classKey));
                break;
            case ChannelType.CB_BOOK_DEPTH_BY_CLASS_V2:
                subscriptionService.removeBookDepthClassInterest(proxyListener, sessionName, classKey);
                removedCLProxy = bookDepthV2Supplier.removeChannelListener(this, proxyListener, channelKey, sessionClassKey);
                channelKey = new ChannelKey(ChannelType.BOOK_DEPTH_BY_CLASS, sessionClassKey);
                internalEventChannel.removeChannelListener(this, tickerProcessor, channelKey);
                bookDepthClassInterestsV2.remove(Integer.valueOf(classKey));
                break;
            case ChannelType.CB_LARGE_TRADE_LAST_SALE_BY_CLASS:
            	subscriptionService.removeLargeTradeLastSaleClassInterest(proxyListener, sessionName, classKey);
                removedCLProxy = largeTradeLastSaleSupplier.removeChannelListener(this, proxyListener, channelKey, sessionClassKey);
                channelKey = new ChannelKey(ChannelType.LARGE_TRADE_LAST_SALE_BY_CLASS, sessionClassKey);
                internalEventChannel.removeChannelListener(this, tickerProcessor, channelKey);
                largeTradeLastSaleClassInterests.remove(Integer.valueOf(classKey));
                break;
            default:
                break;
        }

        try
        {
            String sessionClassKeyString = SessionKeyUserDataHelper.encode(sessionClassKey);
            InstrumentedChannelListenerProxy instrumentedCLProxyListener = (InstrumentedChannelListenerProxy) removedCLProxy;
            // todo: in the future, we will need to throw DVE if user pass the invalid callback for the unsubscription.
            // Since we ignored bad callback before, we will log alarm for now until we have warned all the firms whose software
            // exhibits this bad behavior
            if(instrumentedCLProxyListener != null)
            {
                instrumentedCLProxyListener.removeUserData(UserDataTypes.SESSION_CLASS, sessionClassKeyString);
            }
            else
            {
                Log.alarm(this, "Found invalid callback for market data unsubscription. Session:"+sessionName+" classKey:"+classKey);
            }
        }
        catch(ClassCastException e)
        {
            Log.exception(this, "ClassCastException when removing sessionKey=" + sessionClassKey + " from user data", e);
        }

        String smgr = baseSession.toString();
        String listenerName = proxyListener.toString();
        StringBuilder unsuboid = new StringBuilder(smgr.length()+listenerName.length()+sessionName.length()+50);
        unsuboid.append("UnSub:oid for ").append(smgr).append(' ').append(listenerName)
                .append(" sessionName:").append(sessionName)
                .append(" classKey:").append(classKey);
        Log.information(this, unsuboid.toString());
    }

    public void channelUpdate(ChannelEvent event)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling channelUpdate for session: "+baseSession);
        }
        Object[] publishInfo = (Object[])event.getEventData();
        CallbackDeregistrationInfoStruct callbackDeregistrationInfo = null;
        try
        {
            int channelType = ((Integer) publishInfo[CHANNEL_FIELD]).intValue();
            String sessionName = (String) publishInfo[SESSION_FIELD];
            Integer key = (Integer) publishInfo[KEY_FIELD];
            Short action = (Short) publishInfo[QUEUE_ACTION_FIELD];
            BaseSupplierProxy baseSupplierProxy = null;
            ChannelKey channelKey = null;
            switch (channelType)
            {
                case ChannelType.CB_RECAP_BY_CLASS:
                    com.cboe.idl.cmiCallback.CMIRecapConsumer recapListener = (com.cboe.idl.cmiCallback.CMIRecapConsumer) publishInfo[LISTENER_FIELD];
                    RecapStruct[] recaps = ServicesHelper.getMarketDataService().getRecapForClass(sessionName, key.intValue()).productRecaps;
                    try
                    {
                        recapListener.acceptRecap(recaps);
                    }
                    catch(Exception e)
                    {
                        Log.exception(this, e);
                        baseSupplierProxy = (BaseSupplierProxy)ServicesHelper.getRecapConsumerProxy(recapListener, baseSession);
                    }
                    break;
                case ChannelType.CB_RECAP_BY_CLASS_V2:
                    com.cboe.idl.cmiCallbackV2.CMIRecapConsumer recapV2Listener = (com.cboe.idl.cmiCallbackV2.CMIRecapConsumer) publishInfo[LISTENER_FIELD];
                    recaps = ServicesHelper.getMarketDataService().getRecapForClass(sessionName, key.intValue()).productRecaps;
                    try
                    {
                        recapV2Listener.acceptRecap(recaps,0,action.shortValue());
                    }
                    catch(Exception e)
                    {
                        Log.exception(this, e);
                        baseSupplierProxy = (BaseSupplierProxy)ServicesHelper.getRecapV2ConsumerProxy(recapV2Listener, baseSession,action.shortValue());
                    }
                    break;
                case ChannelType.CB_RECAP_BY_PRODUCT:
                    recapListener = (com.cboe.idl.cmiCallback.CMIRecapConsumer) publishInfo[LISTENER_FIELD];
                    RecapStruct recap = ServicesHelper.getMarketDataService().getRecapForProduct(sessionName, key.intValue());
                    RecapStruct[] aRecap = {recap};
                    try
                    {
                        recapListener.acceptRecap(aRecap);
                    }
                    catch(Exception e)
                    {
                        Log.exception(this, e);
                        baseSupplierProxy = (BaseSupplierProxy)ServicesHelper.getRecapConsumerProxy(recapListener, baseSession);
                    }
                    break;
                case ChannelType.CB_RECAP_BY_PRODUCT_V2:
                    recapV2Listener = (com.cboe.idl.cmiCallbackV2.CMIRecapConsumer) publishInfo[LISTENER_FIELD];
                    recap = ServicesHelper.getMarketDataService().getRecapForProduct(sessionName, key.intValue());
                    RecapStruct[] aV2Recap = {recap};
                    try
                    {
                        recapV2Listener.acceptRecap(aV2Recap, 0, action.shortValue());
                    }
                    catch(Exception e)
                    {
                        Log.exception(this, e);
                        baseSupplierProxy = (BaseSupplierProxy)ServicesHelper.getRecapV2ConsumerProxy(recapV2Listener, baseSession,action.shortValue());
                    }
                    break;
                case ChannelType.CB_NBBO_BY_CLASS:
                    com.cboe.idl.cmiCallback.CMINBBOConsumer nbboListener = (com.cboe.idl.cmiCallback.CMINBBOConsumer) publishInfo[LISTENER_FIELD];
                    NBBOStruct[] nbbos = ServicesHelper.getMarketDataService().getNBBOForClass(sessionName, key.intValue());
                    try
                    {
                        nbboListener.acceptNBBO(nbbos);
                    }
                    catch(Exception e)
                    {
                        Log.exception(this, e);
                        baseSupplierProxy = (BaseSupplierProxy)ServicesHelper.getNBBOConsumerProxy(nbboListener, baseSession);
                    }
                    break;
                case ChannelType.CB_NBBO_BY_CLASS_V2:
                    com.cboe.idl.cmiCallbackV2.CMINBBOConsumer nbboV2Listener = (com.cboe.idl.cmiCallbackV2.CMINBBOConsumer) publishInfo[LISTENER_FIELD];
                    nbbos = ServicesHelper.getMarketDataService().getNBBOForClass(sessionName, key.intValue());
                    try
                    {
                        nbboV2Listener.acceptNBBO(nbbos,0,action.shortValue());
                    }
                    catch(Exception e)
                    {
                        Log.exception(this, e);
                        baseSupplierProxy = (BaseSupplierProxy)ServicesHelper.getNBBOV2ConsumerProxy(nbboV2Listener, baseSession, action.shortValue());
                    }
                    break;
                case ChannelType.CB_NBBO_BY_PRODUCT:
                    nbboListener = (com.cboe.idl.cmiCallback.CMINBBOConsumer) publishInfo[LISTENER_FIELD];
                    NBBOStruct nbbo = ServicesHelper.getMarketDataService().getNBBOForProduct(sessionName, key.intValue());
                    NBBOStruct[] aNbbo = {nbbo};
                    try
                    {
                        nbboListener.acceptNBBO(aNbbo);
                    }
                    catch(Exception e)
                    {
                        Log.exception(this, e);
                        baseSupplierProxy = (BaseSupplierProxy)ServicesHelper.getNBBOConsumerProxy(nbboListener, baseSession);
                    }
                    break;
                case ChannelType.CB_NBBO_BY_PRODUCT_V2:
                    nbboV2Listener = (com.cboe.idl.cmiCallbackV2.CMINBBOConsumer) publishInfo[LISTENER_FIELD];
                    nbbo = ServicesHelper.getMarketDataService().getNBBOForProduct(sessionName, key.intValue());
                    NBBOStruct[] aV2Nbbo = {nbbo};
                    try
                    {
                        nbboV2Listener.acceptNBBO(aV2Nbbo,0,action.shortValue());
                    }
                    catch(Exception e)
                    {
                        Log.exception(this, e);
                        baseSupplierProxy = (BaseSupplierProxy)ServicesHelper.getNBBOV2ConsumerProxy(nbboV2Listener, baseSession, action.shortValue());
                    }
                    break;
                case ChannelType.CB_CURRENT_MARKET_BY_CLASS:
                    com.cboe.idl.cmiCallback.CMICurrentMarketConsumer currentMarketListener = (com.cboe.idl.cmiCallback.CMICurrentMarketConsumer) publishInfo[LISTENER_FIELD];
                    CurrentMarketStruct[] currentMarkets = ServicesHelper.getMarketDataService().getCurrentMarketForClass(sessionName, key.intValue());
                    try
                    {
                        currentMarketListener.acceptCurrentMarket(currentMarkets);
                    }
                    catch(Exception e)
                    {
                        Log.exception(this, e);
                        baseSupplierProxy = (BaseSupplierProxy)ServicesHelper.getCurrentMarketConsumerProxy(currentMarketListener, baseSession);
                    }
                    break;
                case ChannelType.CB_CURRENT_MARKET_BY_CLASS_V2:
                    com.cboe.idl.cmiCallbackV2.CMICurrentMarketConsumer currentMarketV2Listener = (com.cboe.idl.cmiCallbackV2.CMICurrentMarketConsumer) publishInfo[LISTENER_FIELD];
                    currentMarkets = ServicesHelper.getMarketDataService().getCurrentMarketForClass(sessionName, key.intValue());
                    try
                    {
                        currentMarketV2Listener.acceptCurrentMarket(currentMarkets,0,action.shortValue());
                    }
                    catch(Exception e)
                    {
                        Log.exception(this, e);
                        baseSupplierProxy = (BaseSupplierProxy)ServicesHelper.getCurrentMarketV2ConsumerProxy(currentMarketV2Listener, baseSession, action.shortValue());
                    }
                    break;

                case ChannelType.CB_CURRENT_MARKET_BY_CLASS_V3:
                    CurrentMarketStruct[] bestMarkets = null;
                    CurrentMarketStruct[] bestPublicMarkets = null;

                    com.cboe.idl.cmiCallbackV3.CMICurrentMarketConsumer currentMarketV3Listener = (com.cboe.idl.cmiCallbackV3.CMICurrentMarketConsumer) publishInfo[LISTENER_FIELD];
                    //CurrentMarketStructV2[] currentMarketsV2 = ServicesHelper.getMarketDataService().getCurrentMarketV2ForClass(sessionName, key.intValue());

                    InternalCurrentMarketStruct[] currentMarketsV3 = ServicesHelper.getMarketDataService().getCurrentMarketForClassV3(sessionName, key.intValue());
                    bestMarkets = new CurrentMarketStruct[currentMarketsV3.length];
                    bestPublicMarkets = new CurrentMarketStruct[currentMarketsV3.length];

                    for (int i = 0; i < currentMarketsV3.length; i++)
                    {
                        bestMarkets[i] = currentMarketsV3[i].bestMarket;
                        bestPublicMarkets[i] = currentMarketsV3[i].bestPublicMarketAtTop;
                        if (bestPublicMarkets[i] == null)
                        {
                            bestPublicMarkets[i] = MarketDataStructBuilder.buildCurrentMarketStruct(bestMarkets[i].productKeys);
                        }
                    }
                    try
                    {
                        currentMarketV3Listener.acceptCurrentMarket(bestMarkets,bestPublicMarkets,0,action.shortValue());
                    }
                    catch(Exception e)
                    {
                        Log.exception(this, e);
                        baseSupplierProxy = (BaseSupplierProxy)ServicesHelper.getCurrentMarketV3ConsumerProxy(currentMarketV3Listener, baseSession, action.shortValue());
                    }

                    break;
                case ChannelType.CB_CURRENT_MARKET_BY_PRODUCT:
                    currentMarketListener = (com.cboe.idl.cmiCallback.CMICurrentMarketConsumer) publishInfo[LISTENER_FIELD];
                    CurrentMarketStruct currentMarket = ServicesHelper.getMarketDataService().getCurrentMarketForProduct(sessionName, key.intValue());
                    CurrentMarketStruct[] aCurrentMarket = {currentMarket};
                    try
                    {
                        currentMarketListener.acceptCurrentMarket(aCurrentMarket);
                    }
                    catch(Exception e)
                    {
                        Log.exception(this, e);
                        baseSupplierProxy = (BaseSupplierProxy)ServicesHelper.getCurrentMarketConsumerProxy(currentMarketListener, baseSession);
                    }
                    break;
                case ChannelType.CB_CURRENT_MARKET_BY_PRODUCT_V2:
                    currentMarketV2Listener = (com.cboe.idl.cmiCallbackV2.CMICurrentMarketConsumer) publishInfo[LISTENER_FIELD];
                    currentMarket = ServicesHelper.getMarketDataService().getCurrentMarketForProduct(sessionName, key.intValue());
                    CurrentMarketStruct[] aV2CurrentMarket = {currentMarket};
                    try
                    {
                        currentMarketV2Listener.acceptCurrentMarket(aV2CurrentMarket,0,action.shortValue());
                    }
                    catch(Exception e)
                    {
                        Log.exception(this, e);
                        baseSupplierProxy = (BaseSupplierProxy)ServicesHelper.getCurrentMarketV2ConsumerProxy(currentMarketV2Listener, baseSession, action.shortValue());
                    }
                    break;
                case ChannelType.CB_CURRENT_MARKET_BY_PRODUCT_V3:
                    currentMarketV3Listener = (com.cboe.idl.cmiCallbackV3.CMICurrentMarketConsumer) publishInfo[LISTENER_FIELD];
                    InternalCurrentMarketStruct currentMarketForProduct = ServicesHelper.getMarketDataService().getCurrentMarketForProductV3(sessionName, key.intValue());
                    CurrentMarketStruct bestProductMarket = currentMarketForProduct.bestMarket;
                    CurrentMarketStruct bestProductMarketPublic = currentMarketForProduct.bestPublicMarketAtTop;
                    CurrentMarketStruct[] aV3bestProductMarket = {bestProductMarket};
                    CurrentMarketStruct[] aV3bestPublicProductMarket = {bestProductMarketPublic};
                    try
                    {
                        currentMarketV3Listener.acceptCurrentMarket(aV3bestProductMarket,aV3bestPublicProductMarket,0,action.shortValue());
                    }
                    catch(Exception e)
                    {
                        Log.exception(this, e);
                        baseSupplierProxy = (BaseSupplierProxy)ServicesHelper.getCurrentMarketV3ConsumerProxy(currentMarketV3Listener, baseSession, action.shortValue());
                    }

                    break;

                case ChannelType.CB_BOOK_DEPTH_BY_PRODUCT:
                    com.cboe.idl.cmiCallback.CMIOrderBookConsumer bookDepthListener = (com.cboe.idl.cmiCallback.CMIOrderBookConsumer) publishInfo[LISTENER_FIELD];
                    BookDepthStruct bookDepth = ServicesHelper.getOrderBookService().getBookDepth(sessionName, key.intValue(), true);
                    try
                    {
                        bookDepthListener.acceptBookDepth(bookDepth);
                    }
                    catch(Exception e)
                    {
                        Log.exception(this, e);
                        baseSupplierProxy = (BaseSupplierProxy)ServicesHelper.getBookDepthConsumerProxy(bookDepthListener, baseSession);
                    }
                    break;
                case ChannelType.CB_BOOK_DEPTH_BY_PRODUCT_V2:
                    com.cboe.idl.cmiCallbackV2.CMIOrderBookConsumer bookDepthV2Listener = (com.cboe.idl.cmiCallbackV2.CMIOrderBookConsumer) publishInfo[LISTENER_FIELD];
                    bookDepth = ServicesHelper.getOrderBookService().getBookDepth(sessionName, key.intValue(), true);
                    BookDepthStruct[] aBookDepth = {bookDepth};
                    try
                    {
                        bookDepthV2Listener.acceptBookDepth(aBookDepth,0,action.shortValue());
                    }
                    catch(Exception e)
                    {
                        Log.exception(this, e);
                        baseSupplierProxy = (BaseSupplierProxy)ServicesHelper.getBookDepthV2ConsumerProxy(bookDepthV2Listener, baseSession,action.shortValue());
                    }
                    break;
                case ChannelType.CB_BOOK_DEPTH_BY_CLASS_V2:
                    bookDepthV2Listener = (com.cboe.idl.cmiCallbackV2.CMIOrderBookConsumer) publishInfo[LISTENER_FIELD];
                    BookDepthStruct[] bookDepths = ServicesHelper.getOrderBookService().getBookDepthByClass(sessionName, key.intValue(), true);
                    try
                    {
                        bookDepthV2Listener.acceptBookDepth(bookDepths,0,action.shortValue());
                    }
                    catch(Exception e)
                    {
                        Log.exception(this, e);
                        baseSupplierProxy = (BaseSupplierProxy)ServicesHelper.getBookDepthV2ConsumerProxy(bookDepthV2Listener, baseSession,action.shortValue());
                    }
                    break;
                default:
                    if (Log.isDebugOn())
                    {
                        Log.debug(this, "session : " + baseSession + " : Illegal internal publish channel " + publishInfo[0]);
                    }
                    break;
            }
            if(baseSupplierProxy != null)
            {
                callbackDeregistrationInfo = baseSupplierProxy.getCallbackDeregistrationInfoStruct(event);
                baseSupplierProxy.getChannelAdapter().removeChannelListener(baseSupplierProxy);
                if (callbackDeregistrationInfo != null)
                {
                    baseSession.unregisterNotification(callbackDeregistrationInfo);
                }
            }
        }
        catch (Exception e)
        {
            Log.exception(this, "session : " + baseSession, e);
        }

    }

    public void acceptUserSessionLogout()
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling acceptUserSessionLogout for " + baseSession);
        }
        if( localMarketDataService)
        {
            ServicesHelper.getMarketDataRequestPublisher().removeMarketDataRequestSource(this);
        }

        // Do any individual service clean up needed for logout
        internalEventChannel.removeListenerGroup(this);
        EventChannelAdapterFactory.find().removeListenerGroup(this);
        internalEventChannel.removeChannel(this);
        EventChannelAdapterFactory.find().removeChannel(this);
        LogoutServiceFactory.find().logoutComplete(baseSession, this);


        logoutProcessor.setParent(null);
        logoutProcessor = null;

        currentMarketProcessor.setParent(null);
        currentMarketProcessor = null;
        recapProcessor.setParent(null);
        recapProcessor = null;
        tickerProcessor.setParent(null);
        tickerProcessor = null;
        bookDepthProcessor.setParent(null);
        bookDepthProcessor = null;
        expectedOpeningPriceProcessor.setParent(null);
        expectedOpeningPriceProcessor = null;
        baseSession = null;
        userId = null;

        currentMarketSupplier.removeListenerGroup(this);
        currentMarketSupplier = null;
        bookDepthSupplier.removeListenerGroup(this);
        bookDepthSupplier = null;
        theNBBOSupplier.removeListenerGroup(this);
        theNBBOSupplier = null;
        recapSupplier.removeListenerGroup(this);
        recapSupplier = null;
        tickerSupplier.removeListenerGroup(this);
        tickerSupplier = null;
        openingPriceSupplier.removeListenerGroup(this);
        openingPriceSupplier = null;

        currentMarketV2Supplier.removeListenerGroup(this);
        currentMarketV2Supplier = null;

        currentMarketV3Supplier.removeListenerGroup(this);
        currentMarketV3Supplier = null;

        bookDepthV2Supplier.removeListenerGroup(this);
        bookDepthV2Supplier = null;
        theNBBOV2Supplier.removeListenerGroup(this);
        theNBBOV2Supplier = null;
        recapV2Supplier.removeListenerGroup(this);
        recapV2Supplier = null;
        tickerV2Supplier.removeListenerGroup(this);
        tickerV2Supplier = null;
        // VTATS
        largeTradeLastSaleSupplier.removeListenerGroup(this);
        largeTradeLastSaleSupplier = null;
        openingPriceV2Supplier.removeListenerGroup(this);
        openingPriceV2Supplier = null;

        recapClassInterests = null;
        recapClassInterestsV2 = null;
        currentMarketClassInterests = null;
        currentMarketClassInterestsV2 = null;
        currentMarketClassInterestsV3 = null;
        NBBOClassInterests = null;
        NBBOClassInterestsV2 = null;
        eopClassInterestsV2 = null;
        tickerClassInterestsV2 = null;
        largeTradeLastSaleClassInterests = null;
        bookDepthClassInterestsV2 = null;
    }

    public BaseSessionManager getSessionManager()
    {
        return baseSession;
    }

	public void acceptLargeTradeLastSaleForClass(InternalTickerDetailStruct[] lastSales) {
		int length = lastSales.length;
        if (length > 0)
        {
            ChannelKey channelKey;
            ChannelEvent event;
            Integer classId = lastSales[0].lastSaleTicker.ticker.productKeys.classKey;
            boolean publishByClass = largeTradeLastSaleClassInterests.get(classId)!=null;
            if (publishByClass)
            {
            	SessionKeyContainer classKey = null;
            	
            	classKey = new SessionKeyContainer(lastSales[0].lastSaleTicker.ticker.sessionName,
                					lastSales[0].lastSaleTicker.ticker.productKeys.classKey);
            	
            	
                channelKey = new ChannelKey(ChannelType.CB_LARGE_TRADE_LAST_SALE_BY_CLASS, classKey);
                if (Log.isDebugOn())
                {
                    Log.debug(this, "session : " + baseSession +
                            " dispatch LargeTrade[" + lastSales.length +
                            "] to IEC with channelKey: " + channelKey);
                }
                event = internalEventChannel.getChannelEvent(this, channelKey, lastSales);
                largeTradeLastSaleSupplier.dispatch(event);
            }
        }	
	}

   
}
