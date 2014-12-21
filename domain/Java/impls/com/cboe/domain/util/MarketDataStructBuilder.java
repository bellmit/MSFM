package com.cboe.domain.util;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.cboe.idl.cmiConstants.CurrentMarketViewTypes;
import com.cboe.idl.cmiConstants.VolumeTypes;
import com.cboe.idl.cmiIntermarketMessages.ExchangeMarketStruct;
import com.cboe.idl.cmiMarketData.BookDepthStruct;
import com.cboe.idl.cmiMarketData.BookDepthUpdatePriceStruct;
import com.cboe.idl.cmiMarketData.BookDepthUpdateStruct;
import com.cboe.idl.cmiMarketData.CurrentMarketStruct;
import com.cboe.idl.cmiMarketData.CurrentMarketStructV2;
import com.cboe.idl.cmiMarketData.CurrentMarketViewStruct;
import com.cboe.idl.cmiMarketData.ExchangeIndicatorStruct;
import com.cboe.idl.cmiMarketData.ExchangeVolumeStruct;
import com.cboe.idl.cmiMarketData.LastSaleStructV4;
import com.cboe.idl.cmiMarketData.MarketDataDetailStruct;
import com.cboe.idl.cmiMarketData.MarketDataHistoryDetailEntryStruct;
import com.cboe.idl.cmiMarketData.MarketDataHistoryEntryStruct;
import com.cboe.idl.cmiMarketData.MarketVolumeStruct;
import com.cboe.idl.cmiMarketData.NBBOStruct;
import com.cboe.idl.cmiMarketData.OrderBookPriceStruct;
import com.cboe.idl.cmiMarketData.RecapStruct;
import com.cboe.idl.cmiMarketData.RecapStructV4;
import com.cboe.idl.cmiMarketData.RecapStructV5;
import com.cboe.idl.cmiMarketData.TickerStruct;
import com.cboe.idl.cmiMarketData.TickerStructV4;
import com.cboe.idl.cmiProduct.ProductKeysStruct;
import com.cboe.idl.cmiProduct.ProductNameStruct;
import com.cboe.idl.cmiUser.ExchangeAcronymStruct;
import com.cboe.idl.cmiUtil.KeyValueStruct;
import com.cboe.idl.cmiUtil.TimeStruct;
import com.cboe.idl.constants.BOBIndicatoryConstants;
import com.cboe.idl.marketData.BOStruct;
import com.cboe.idl.marketData.ClosingQuoteStruct;
import com.cboe.idl.marketData.ClosingQuoteSummaryStruct;
import com.cboe.idl.marketData.CurrentMarketStateChangeStruct;
import com.cboe.idl.marketData.ExchangeGateIndicatorStruct;
import com.cboe.idl.marketData.InternalTickerDetailStruct;
import com.cboe.idl.marketData.InternalTickerStruct;
import com.cboe.idl.marketData.QuoteQueryStruct;
import com.cboe.idl.marketData.QuoteQueryV2Struct;
import com.cboe.idl.marketData.QuoteQueryV3Struct;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.domain.tradingProperty.MarketDataAwayExchanges;

/**
 * A helper that makes it easy to create valid CORBA structs.  The structs created
 * by the methods of this class have default values for all attributes.  There are
 * also some test methods that can be used to check if a struc
 * t is a default struct.
 *
 * @author John Wickberg
 */
public class MarketDataStructBuilder
{

    static final int DEFAULT_MAX_CACHED_QUANTITY = 2000;
    
    static MarketVolumeStruct[][][] marketDataStructBank= new MarketVolumeStruct[4][DEFAULT_MAX_CACHED_QUANTITY][2];
    
    static ExchangeVolumeStruct[][] exchangeVolumeDataStructBank= new ExchangeVolumeStruct[MarketDataAwayExchanges.values().length][DEFAULT_MAX_CACHED_QUANTITY];
    static short[] volumeTypes=new short[] {VolumeTypes.LIMIT,VolumeTypes.AON,VolumeTypes.CUSTOMER_ORDER,VolumeTypes.PROFESSIONAL_ORDER};
    /*
     * If this flag is turned on in setContext we will use the pre-created structs.
     * By default this is turned off;
     */
    private static boolean useMarketVolumeStructCache; 
    private static boolean useExchangeVolumeDataStructCache; 
    static
    {
        try
        {
            String val = System.getProperty("marketVolumeStructCache.use");
            
            String useExchangeVolumeDataStruct = System.getProperty("exchangeVolumeDataStructCache.use");

            useMarketVolumeStructCache = (val != null) && (val.compareToIgnoreCase("true") == 0);
            
            useExchangeVolumeDataStructCache = (useExchangeVolumeDataStruct != null) && (useExchangeVolumeDataStruct.compareToIgnoreCase("true") == 0);
        }
        catch (Exception e)
        {
            useMarketVolumeStructCache = false;
        }
        if(useMarketVolumeStructCache)
        {
            for (short i=0;i<volumeTypes.length;i++){
                for (int j = 0; j < DEFAULT_MAX_CACHED_QUANTITY; j++){
                    for( int k=0;k<2;k++){
                        MarketVolumeStruct x= new MarketVolumeStruct();
                        x.volumeType=volumeTypes[i];
                        x.quantity=j;
                        x.multipleParties= k==1;
                        marketDataStructBank[i][j][k]=x;
                    }
                }
            }
        }
        
        if(useExchangeVolumeDataStructCache)
        {
            for (int i = 0; i < exchangeVolumeDataStructBank.length; i++){
                int exchangeIndex = MarketDataAwayExchanges.values()[i].getExchangeId();
                for (int j = 0; j < DEFAULT_MAX_CACHED_QUANTITY; j++){
                    ExchangeVolumeStruct x= new ExchangeVolumeStruct();
                    x.exchange = MarketDataAwayExchanges.findLinkageExchange(exchangeIndex).exchangeString;
                    x.volume = j;
                    exchangeVolumeDataStructBank[exchangeIndex][j] = x;
                }
            }
        }
        Log.information("MarketDataStructBuilder: Using useMarketVolumeStructCache = " + useMarketVolumeStructCache);
        Log.information("MarketDataStructBuilder: Using useExchangeVolumeDataStructCache = " + useExchangeVolumeDataStructCache);
    }
   //-DmarketVolumeStructCache.use=true
    public static MarketVolumeStruct getMarketVolumeStruct(
            short volumeType, int quantity, boolean multipleParties)
    {
        if(useMarketVolumeStructCache && quantity < DEFAULT_MAX_CACHED_QUANTITY)
        {
            switch (volumeType) 
            {
                case VolumeTypes.LIMIT:
                    return getStruct(quantity, multipleParties, 0);
                case VolumeTypes.AON:
                    return getStruct(quantity, multipleParties, 1);
                case VolumeTypes.CUSTOMER_ORDER:
                    return getStruct(quantity, multipleParties, 2);
                case VolumeTypes.PROFESSIONAL_ORDER:
                    return getStruct(quantity, multipleParties, 3);
            }
        }
        
        /*
         * create and return a new instance if either the user has passed a 
         * wrong volume type or if the useMarketVolumeStructCache flag is turned off
         */
            MarketVolumeStruct rslt = new MarketVolumeStruct();
            rslt.volumeType=volumeType;
            rslt.quantity=quantity;
            rslt.multipleParties=multipleParties;
            return rslt;
    }
    
    public static ExchangeVolumeStruct getExchangeVolumeStruct(String exchange, int volume)
    {
        ExchangeVolumeStruct returnValue = null;
        /*
         * If the flag is turned off return a new EVS
         */
        if(! useExchangeVolumeDataStructCache)
        {
            returnValue = new ExchangeVolumeStruct();
            returnValue.exchange = exchange;
            returnValue.volume   = volume;
            return returnValue;
            
        }
        
        /*
         * See if the exchange is present in the exchange array and the volume is less than the max limit
         */
        MarketDataAwayExchanges marketDataAwayExchange = MarketDataAwayExchanges.findLinkageExchange(exchange);
        
        if(marketDataAwayExchange != MarketDataAwayExchanges.UNSPECIFIED && volume < DEFAULT_MAX_CACHED_QUANTITY)
        {
            returnValue = exchangeVolumeDataStructBank[marketDataAwayExchange.getExchangeId()][volume];
        }
        
        /*
         * See if the return value is same as the one the user request to avoid returning some value
         * that got corrupted in our bank. We have to find a way of having immutable structs!
         */
        if( (returnValue != null) && (returnValue.exchange.equals(exchange)) && (returnValue.volume == volume) )
        {
            return returnValue;
        }
        
        /*
         * The value got corrupted; Create a new EVS
         * and add it back to the bank
         */
        returnValue = new ExchangeVolumeStruct();
        returnValue.exchange = exchange;
        returnValue.volume   = volume;
       
        if(marketDataAwayExchange != MarketDataAwayExchanges.UNSPECIFIED  && volume < DEFAULT_MAX_CACHED_QUANTITY)
        {
            /*
             * Add to the bank only if it is available in our configured MarketDataAwayExchanges
             */
            exchangeVolumeDataStructBank[marketDataAwayExchange.getExchangeId()][volume] = returnValue;
        }
        
        return returnValue;
    }

    private static MarketVolumeStruct getStruct(int quantity, boolean multipleParties, int i)
    {
        MarketVolumeStruct currentMktVolStruct = marketDataStructBank[i][quantity][multipleParties?1:0];
        
        if(currentMktVolStruct.quantity != quantity || 
           currentMktVolStruct.multipleParties != multipleParties ||
           currentMktVolStruct.volumeType != volumeTypes[i])
        {
            MarketVolumeStruct rslt = new MarketVolumeStruct();
            rslt.volumeType = volumeTypes[i];
            rslt.quantity = quantity;
            rslt.multipleParties = multipleParties;
            marketDataStructBank[i][quantity][multipleParties?1:0] = rslt;
            return rslt;
        }
        else
        {
            return currentMktVolStruct;
        }
    }

    /**
     * All methods are static, no instance needs to be created.
     *
     * @author John Wickberg
     */
    private MarketDataStructBuilder()
    {
        super();
    }
    /**
     * Creates a market data history entry containing default values for
     * all attributes.
     *
     * @author John Wickberg
     */
    public static MarketDataHistoryEntryStruct buildMarketDataHistoryEntryStruct()
    {
        MarketDataHistoryEntryStruct entry = new MarketDataHistoryEntryStruct();
        entry.askPrice = StructBuilder.buildPriceStruct();
        entry.askSize = 0;
        entry.bidPrice = StructBuilder.buildPriceStruct();
        entry.bidSize = 0;
        entry.buyerAcronym = "";
        entry.entryType = 0;
        entry.eopType = 0;
        entry.exceptionCode="";
        entry.marketCondition = 0;
        entry.optionalData = "";
        entry.physLocation = "";
        entry.price = StructBuilder.buildPriceStruct();
        entry.quantity = 0;
        entry.reportTime = StructBuilder.buildDateTimeStruct();;
        entry.sellerAcronym = "";
        entry.source = ' ';
        entry.underlyingLastSalePrice = StructBuilder.buildPriceStruct();
        return entry;
    }

    public static MarketDataHistoryDetailEntryStruct buildMarketDataHistoryDetailEntryStruct()
    {
        MarketDataHistoryEntryStruct entry = new MarketDataHistoryEntryStruct();
        entry.askPrice = StructBuilder.buildPriceStruct();
        entry.askSize = 0;
        entry.bidPrice = StructBuilder.buildPriceStruct();
        entry.bidSize = 0;
        entry.buyerAcronym = "";
        entry.entryType = 0;
        entry.eopType = 0;
        entry.exceptionCode="";
        entry.marketCondition = 0;
        entry.optionalData = "";
        entry.physLocation = "";
        entry.prefix = "";
        entry.price = StructBuilder.buildPriceStruct();
        entry.quantity = 0;
        entry.reportTime = StructBuilder.buildDateTimeStruct();;
        entry.sellerAcronym = "";
        entry.source = ' ';
        entry.underlyingLastSalePrice = StructBuilder.buildPriceStruct();

        MarketDataDetailStruct detailEntry = new MarketDataDetailStruct();
        detailEntry.overrideIndicator = '0';
        detailEntry.nbboAskPrice = StructBuilder.buildPriceStruct();
        detailEntry.nbboBidPrice = StructBuilder.buildPriceStruct();
        detailEntry.nbboAskExchanges = new ExchangeVolumeStruct[0];
        detailEntry.nbboBidExchanges = new ExchangeVolumeStruct[0];
        detailEntry.tradeThroughIndicator = false;
        detailEntry.exchangeIndicators = new ExchangeIndicatorStruct[0];
        detailEntry.brokers=new ExchangeAcronymStruct[0];
        detailEntry.contras = new ExchangeAcronymStruct[0] ;
        detailEntry.bestPublishedBidPrice = StructBuilder.buildPriceStruct();
        detailEntry.bestPublishedAskPrice = StructBuilder.buildPriceStruct();
        detailEntry.bestPublishedBidVolume = 0;
        detailEntry.bestPublishedAskVolume = 0;
        MarketDataHistoryDetailEntryStruct  detailEntryStruct = new MarketDataHistoryDetailEntryStruct();
        detailEntryStruct.historyEntry = entry;
        detailEntryStruct.detailData = detailEntry;
        detailEntryStruct.detailData.extensions = new KeyValueStruct[0];

        return detailEntryStruct;

    }

    public static CurrentMarketStruct getNonContingencyBBO(CurrentMarketStructV2 market)
    {
        CurrentMarketViewStruct[] marketViews = market.currentMarketViews;
        CurrentMarketStruct nonContingencyMarket = null;
        for(int i = 0; i < marketViews.length; i++)
        {
            if(marketViews[i].currentMarketViewType == CurrentMarketViewTypes.BEST_LIMIT_PRICE)
            {
                nonContingencyMarket = MarketDataStructBuilder.buildCurrentMarketStruct(market.sessionName, market.productKeys, market.exchange, market.bidIsMarketBest, market.askIsMarketBest, market.sentTime, market.legalMarket, marketViews[i]);
            }
        }
        return nonContingencyMarket;
    }

    public static CurrentMarketStruct getContingencyBBO(CurrentMarketStructV2 market)
    {
        CurrentMarketViewStruct[] marketViews = market.currentMarketViews;
        CurrentMarketStruct contingencyMarket = null;
        for(int i = 0; i < marketViews.length; i++)
        {
            if(marketViews[i].currentMarketViewType == CurrentMarketViewTypes.BEST_PRICE)
            {
                contingencyMarket = MarketDataStructBuilder.buildCurrentMarketStruct(market.sessionName, market.productKeys, market.exchange, market.bidIsMarketBest, market.askIsMarketBest, market.sentTime, market.legalMarket, marketViews[i]);
                break;
            }
        }
        return contingencyMarket;
    }

    /**
     * Creates a unique CurrentMarketV2 struct that has only the BEST_PRICE view and the
     * PUBLIC_OFFER VIEW (if and only if the PUBLIC_OFFER is at the Best Price).
     * This is the CurrentMarketStructV2 that the Client side currently needs to see
     * @param CurrentMarketStuctV2 (as received on the CurrentMarket Event Channel.
     * @return CurrentMarketStructV2 that contains only client required information.
     */
    public static CurrentMarketStructV2 buildBestCustomerProfV2(CurrentMarketStructV2 market)
    {
        // todo -- maybe write a PriceStructComparator with static compare method, so we won't have to
        //         always create ValuedPrice wrappers to compare
        ValuedPriceComparator priceComparator = new ValuedPriceComparator();
        CurrentMarketStructV2 bestCurrentV2 = new CurrentMarketStructV2();
        CurrentMarketViewStruct[] marketViews = market.currentMarketViews;
        bestCurrentV2.askIsMarketBest = market.askIsMarketBest;
        bestCurrentV2.bidIsMarketBest = market.bidIsMarketBest;
        bestCurrentV2.exchange = market.exchange;
        bestCurrentV2.legalMarket = market.legalMarket;
        bestCurrentV2.productKeys = market.productKeys;
        bestCurrentV2.sentTime = market.sentTime;
        bestCurrentV2.sessionName = market.sessionName;
        bestCurrentV2.currentMarketViews = new CurrentMarketViewStruct[2];

        CurrentMarketViewStruct bestMarketView = null;
        CurrentMarketViewStruct publicMarketView = null;
        CurrentMarketViewStruct bestPublicMarketView = null;

        for(int i = 0; i < marketViews.length; i++)
        {
            if(marketViews[i].currentMarketViewType == CurrentMarketViewTypes.BEST_PRICE)
            {
                bestMarketView = marketViews[i];
                bestCurrentV2.currentMarketViews[0] = bestMarketView;
            }
            else if (marketViews[i].currentMarketViewType == CurrentMarketViewTypes.BEST_PUBLIC_ORDER_PRICE)
            {
                publicMarketView = marketViews[i];
            }
        }

        if (publicMarketView == null)
        {
            bestPublicMarketView = buildCurrentMarketViewStruct();
            bestPublicMarketView.currentMarketViewType = CurrentMarketViewTypes.BEST_PUBLIC_ORDER_PRICE;
        }
        else
        {
            bestPublicMarketView = new CurrentMarketViewStruct();
            bestPublicMarketView.currentMarketViewType = CurrentMarketViewTypes.BEST_PUBLIC_ORDER_PRICE;
            if (priceComparator.compare(PriceFactory.createValuedPrice (publicMarketView.bidPrice),
                    PriceFactory.createValuedPrice (bestMarketView.bidPrice)) == 0)
            {   //Public bid is at the CBOE best
                bestPublicMarketView.bidPrice = bestMarketView.bidPrice;

                bestPublicMarketView.bidSizeSequence = new MarketVolumeStruct[2];

                //get the sequence size for BidSequence
                for (int i = 0; i < publicMarketView.bidSizeSequence.length; i ++)
                {
                    if (publicMarketView.bidSizeSequence[i].volumeType == VolumeTypes.CUSTOMER_ORDER)
                    {
                        bestPublicMarketView.bidSizeSequence[0] = publicMarketView.bidSizeSequence[i];
                    }
                    else if (publicMarketView.bidSizeSequence[i].volumeType == VolumeTypes.PROFESSIONAL_ORDER)
                    {
                        bestPublicMarketView.bidSizeSequence[1] = publicMarketView.bidSizeSequence[i];
                    }

                }
                if (bestPublicMarketView.bidSizeSequence[0] == null)
                {
                    bestPublicMarketView.bidSizeSequence[0] = new MarketVolumeStruct();
                    bestPublicMarketView.bidSizeSequence[0].volumeType = VolumeTypes.CUSTOMER_ORDER;
                    bestPublicMarketView.bidSizeSequence[0].multipleParties = false;
                    bestPublicMarketView.bidSizeSequence[0].quantity = 0;
                }
                if (bestPublicMarketView.bidSizeSequence[1] == null)
                {
                    bestPublicMarketView.bidSizeSequence[1] = new MarketVolumeStruct();
                    bestPublicMarketView.bidSizeSequence[1].volumeType = VolumeTypes.PROFESSIONAL_ORDER;
                    bestPublicMarketView.bidSizeSequence[1].multipleParties = false;
                    bestPublicMarketView.bidSizeSequence[1].quantity = 0;
                }

            }

            if (priceComparator.compare(PriceFactory.createValuedPrice (publicMarketView.askPrice),
                    PriceFactory.createValuedPrice (bestMarketView.askPrice)) == 0)
            {   //Public bid is at the CBOE best
                bestPublicMarketView.askPrice = bestMarketView.askPrice;
                bestPublicMarketView.askSizeSequence = new MarketVolumeStruct[2];
                //get the sequence size for BidSequence
                for (int i = 0; i < publicMarketView.askSizeSequence.length; i ++)
                {
                    if (publicMarketView.askSizeSequence[i].volumeType == VolumeTypes.CUSTOMER_ORDER)
                    {
                        bestPublicMarketView.askSizeSequence[0] = publicMarketView.askSizeSequence[i];
                    }
                    else if (publicMarketView.askSizeSequence[i].volumeType == VolumeTypes.PROFESSIONAL_ORDER)
                    {
                        bestPublicMarketView.askSizeSequence[1] = publicMarketView.askSizeSequence[i];
                    }

                }
                if (bestPublicMarketView.askSizeSequence[0] == null)
                {
                    bestPublicMarketView.askSizeSequence[0] = new MarketVolumeStruct();
                    bestPublicMarketView.askSizeSequence[0].volumeType = VolumeTypes.CUSTOMER_ORDER;
                    bestPublicMarketView.askSizeSequence[0].multipleParties = false;
                    bestPublicMarketView.askSizeSequence[0].quantity = 0;
                }
                if (bestPublicMarketView.askSizeSequence[1] == null)
                {
                    bestPublicMarketView.askSizeSequence[1] = new MarketVolumeStruct();
                    bestPublicMarketView.askSizeSequence[1].volumeType = VolumeTypes.PROFESSIONAL_ORDER;
                    bestPublicMarketView.askSizeSequence[1].multipleParties = false;
                    bestPublicMarketView.askSizeSequence[1].quantity = 0;
                }

            }

            // moved this outside of else, so bestCurrentV2.currentMarketViews[1] would also be set when publicMarketView == null
//            bestCurrentV2.currentMarketViews[1] = bestPublicMarketView;
            priceComparator = null; //for fast GC

        }

        bestCurrentV2.currentMarketViews[1] = bestPublicMarketView;
        return bestCurrentV2;
    }



    /**
      * gets an Empty recap struct
      * @return RecapStruct
      */
    public static RecapStruct buildRecapStruct(ProductKeysStruct keys) {
        ProductNameStruct name = ClientProductStructBuilder.buildProductNameStruct();

        RecapStruct recap = new RecapStruct();
        recap.productInformation = name;
        recap.productKeys = keys;

        recap.askPrice = StructBuilder.buildPriceStruct();
        recap.askSize = 0;
        recap.askTime = StructBuilder.buildTimeStruct();
        recap.bidDirection = ' ';
        recap.bidPrice = StructBuilder.buildPriceStruct();
        recap.bidSize = 0;
        recap.bidTime = StructBuilder.buildTimeStruct();
        recap.closePrice = StructBuilder.buildPriceStruct();
        recap.highPrice = StructBuilder.buildPriceStruct();
        recap.lastSalePrice = StructBuilder.buildPriceStruct();
        recap.lastSaleVolume = 0;
        recap.lowPrice = StructBuilder.buildPriceStruct();
        recap.netChange = StructBuilder.buildPriceStruct();
        recap.netChangeDirection = ' ';
        recap.openInterest = 0;
        recap.openPrice = StructBuilder.buildPriceStruct();
        recap.isOTC = false;
        recap.previousClosePrice = StructBuilder.buildPriceStruct();
        recap.recapPrefix = "";
        recap.tick = StructBuilder.buildPriceStruct();
        recap.tickDirection = ' ';
        recap.totalVolume = 0;
        recap.tradeTime = StructBuilder.buildTimeStruct();
        recap.sessionName = "";

        return recap;
    }

    /**
      * gets an Empty current market struct
      * @return CurrentMarketStruct
      */
    public static RecapStruct buildRecapStruct(String sessionName, ProductKeysStruct keys) {
        RecapStruct recap = buildRecapStruct(keys);
        recap.sessionName = sessionName;
        return recap;
    }

    /**
     * Returns a new RecapStructV4 with its fields set to the values of the supplied struct.
     * @param struct
     * @return
     */
    public static RecapStructV4 cloneRecapStructV4(RecapStructV4 struct)
    {
        RecapStructV4 newStruct = new RecapStructV4();

        newStruct.classKey = struct.classKey;
        newStruct.productKey = struct.productKey;
        newStruct.productType = struct.productType;
        newStruct.exchange = struct.exchange;
        newStruct.priceScale = struct.priceScale;
        newStruct.lowPrice = struct.lowPrice;
        newStruct.highPrice = struct.highPrice;
        newStruct.openPrice = struct.openPrice;
        newStruct.previousClosePrice = struct.previousClosePrice;
        newStruct.sentTime = struct.sentTime;
        newStruct.statusCodes = struct.statusCodes;

        return newStruct;
    }

    /**
      * gets an Empty ticker struct
      * @return TickerStruct
      */
    public static TickerStruct buildTickerStruct(ProductKeysStruct keys) {
        TickerStruct ticker = new TickerStruct();
        ticker.productKeys = keys;
        ticker.exchangeSymbol = "";
        ticker.lastSalePrice = StructBuilder.buildPriceStruct();
        ticker.lastSaleVolume = 0;
        ticker.salePostfix = "";
        ticker.salePrefix = "";
        ticker.sessionName = "";


        return ticker;
    }

    public static InternalTickerDetailStruct buildInternalTickerDetailStruct(ProductKeysStruct keys) {
        InternalTickerDetailStruct tickerDetail = new InternalTickerDetailStruct();
        tickerDetail.lastSaleTicker = new InternalTickerStruct();
        tickerDetail.lastSaleTicker.ticker = buildTickerStruct(keys);
        tickerDetail.lastSaleTicker.tradeTime = StructBuilder.buildTimeStruct();
        tickerDetail.source = ' ';
        tickerDetail.isDisseminated = false;
        tickerDetail.tradeId = StructBuilder.buildCboeIdStruct();
        tickerDetail.detailData = new MarketDataDetailStruct();
        tickerDetail.detailData.bestPublishedAskPrice = StructBuilder.buildPriceStruct();
        tickerDetail.detailData.bestPublishedBidPrice = StructBuilder.buildPriceStruct();
        tickerDetail.detailData.bestPublishedAskVolume = 0;
        tickerDetail.detailData.bestPublishedBidVolume = 0;
        tickerDetail.detailData.brokers = new ExchangeAcronymStruct[0];
        tickerDetail.detailData.contras = new ExchangeAcronymStruct[0];
        tickerDetail.detailData.exchangeIndicators = new ExchangeIndicatorStruct[0];
        tickerDetail.detailData.extensions = new KeyValueStruct[0];
        tickerDetail.detailData.nbboAskExchanges = new ExchangeVolumeStruct[0];
        tickerDetail.detailData.nbboBidExchanges = new ExchangeVolumeStruct[0];
        tickerDetail.detailData.nbboAskPrice = StructBuilder.buildPriceStruct();
        tickerDetail.detailData.nbboBidPrice = StructBuilder.buildPriceStruct();
        tickerDetail.detailData.overrideIndicator = ' ';
        tickerDetail.detailData.tradeThroughIndicator = false;
        tickerDetail.botrStruct = buildNBBOStruct(keys);
        return tickerDetail;
    }

    /**
      * gets an Empty current market struct
      * @return CurrentMarketStruct
      */
    public static TickerStruct buildTickerStruct(String sessionName, ProductKeysStruct keys) {
        TickerStruct ticker = buildTickerStruct(keys);
        ticker.sessionName = sessionName;
        return ticker;
    }

    /**
      * gets an Empty NBBO struct
      * @return NBBOStruct
      */
    public static NBBOStruct buildNBBOStruct(ProductKeysStruct keys) {
        NBBOStruct NBBO = new NBBOStruct();

        NBBO.productKeys = keys;
        NBBO.bidPrice = StructBuilder.buildPriceStruct();
        NBBO.bidExchangeVolume = new ExchangeVolumeStruct[0];
        NBBO.askPrice = StructBuilder.buildPriceStruct();
        NBBO.askExchangeVolume = new ExchangeVolumeStruct[0];
        NBBO.sentTime = StructBuilder.buildTimeStruct();
        NBBO.sessionName = "";

        return NBBO;
    }

    /**
     * gets an Empty BO struct
     * @return NBBOStruct
     */
   public static BOStruct buildBOStruct() {

       BOStruct BO = new BOStruct();

       BO.askPrice = StructBuilder.buildPriceStruct();
       BO.bidPrice = StructBuilder.buildPriceStruct();
       BO.askVolume = 0;
       BO.bidVolume = 0;

       return BO;
   }

    public static ExchangeMarketStruct buildExchangeMarketStruct()
    {
        ExchangeMarketStruct struct = new ExchangeMarketStruct();
        struct.askExchangeVolumes = new ExchangeVolumeStruct[0];
        struct.bestAskPrice =  StructBuilder.buildPriceStruct();
        struct.bestBidPrice =  StructBuilder.buildPriceStruct();
        struct.bidExchangeVolumes = new ExchangeVolumeStruct[0];
        struct.marketInfoType = 0;

        return struct;
    }


    /**
      * gets an Empty current market struct
      * @return CurrentMarketStruct
      */
    public static NBBOStruct buildNBBOStruct(String sessionName, ProductKeysStruct keys) {
        NBBOStruct NBBO = buildNBBOStruct(keys);
        NBBO.sessionName = sessionName;
        return NBBO;
    }

    /**
      * gets an Empty current market struct
      * @return CurrentMarketStruct
      */
    public static CurrentMarketStruct buildCurrentMarketStruct(ProductKeysStruct keys) {
        CurrentMarketStruct currentMarket = new CurrentMarketStruct();
        currentMarket.productKeys = keys;
        currentMarket.exchange = "";
        currentMarket.askIsMarketBest = false;
        currentMarket.askPrice = StructBuilder.buildPriceStruct();
        currentMarket.askSizeSequence = new MarketVolumeStruct[0];
        currentMarket.bidIsMarketBest = false;
        currentMarket.bidPrice = StructBuilder.buildPriceStruct();
        currentMarket.bidSizeSequence = new MarketVolumeStruct[0];
        currentMarket.sentTime = StructBuilder.buildTimeStruct();
        currentMarket.sessionName = "";
        return currentMarket;
    }

    //creates only a base CurrenvMarketV2 struct
    public static CurrentMarketStructV2 buildCurrentMarketStructV2(ProductKeysStruct keys)
    {
            CurrentMarketStructV2 currentMarketV2 = new CurrentMarketStructV2();
            currentMarketV2.productKeys = keys;
            currentMarketV2.exchange = "";
            currentMarketV2.askIsMarketBest = false;
            currentMarketV2.currentMarketViews = new CurrentMarketViewStruct[0];
            currentMarketV2.sentTime = StructBuilder.buildTimeStruct();
            currentMarketV2.sessionName = "";
            return currentMarketV2;
    }


    /**
      * gets an Empty book depth struct
      * @parm keys - ProductKeysStruct
      * @return BookDepthStruct
      */
    public static BookDepthStruct buildBookDepthStruct(ProductKeysStruct keys) {
        BookDepthStruct bookDepth = new BookDepthStruct();
        bookDepth.productKeys = keys;
        bookDepth.sessionName = "";
    bookDepth.buySideSequence = new OrderBookPriceStruct[0];
    bookDepth.sellSideSequence = new OrderBookPriceStruct[0];
        bookDepth.allPricesIncluded = true;
        bookDepth.transactionSequenceNumber = 0;
        return bookDepth;
    }

    /**
      * gets an Empty book depth struct
      * @parm sessionName - String
      * @parm keys - ProductKeysStruct
      * @return BookDepthStruct
      */
    public static BookDepthStruct buildBookDepthStruct(String sessionName, ProductKeysStruct keys)    {
        BookDepthStruct bookDepth = new BookDepthStruct();
        bookDepth.sessionName = sessionName;
        return bookDepth;
    }

    /**
      * gets an Empty book depth update struct
      * @parm sessionName - String
      * @parm keys - ProductKeysStruct
      * @return BookDepthUpdate Struct
      */
    public static BookDepthUpdateStruct buildBookDepthUpdateStruct(ProductKeysStruct keys) {
        BookDepthUpdateStruct bookDepthUpdate = new BookDepthUpdateStruct();
        bookDepthUpdate.productKeys = keys;
        bookDepthUpdate.sessionName = "";
    bookDepthUpdate.buySideChanges = new BookDepthUpdatePriceStruct[0];
    bookDepthUpdate.sellSideChanges = new BookDepthUpdatePriceStruct[0];
        bookDepthUpdate.sequenceNumber = 0;
        return bookDepthUpdate;
    }

    /**
      * gets an Empty book depth update struct
      * @parm sessionName - String
      * @parm keys - ProductKeysStruct
      * @return BookDepthUpdateStruct
      */
    public static BookDepthUpdateStruct buildBookDepthUpdateStruct(String sessionName, ProductKeysStruct keys)    {
        BookDepthUpdateStruct bookDepthUpdate = new BookDepthUpdateStruct();
        bookDepthUpdate.sessionName = sessionName;
        return bookDepthUpdate;
    }

    /**
      * gets a current market struct from current market view struct
      * @return CurrentMarketStruct
      */
    public static CurrentMarketStruct buildCurrentMarketStruct(String sessionName, ProductKeysStruct keys, String exchange, boolean bidIsMarketBest, boolean askIsMarketBest, TimeStruct sentTime, boolean legalMarket, CurrentMarketViewStruct view) {
        CurrentMarketStruct currentMarket = buildCurrentMarketStruct(keys);
        currentMarket.sessionName = sessionName;
        currentMarket.exchange = exchange;
        currentMarket.askPrice = view.askPrice;
        currentMarket.askIsMarketBest = askIsMarketBest;
        currentMarket.askSizeSequence = view.askSizeSequence;
        currentMarket.bidIsMarketBest = bidIsMarketBest;
        currentMarket.bidPrice = view.bidPrice;
        currentMarket.bidSizeSequence = view.bidSizeSequence;
        currentMarket.legalMarket = legalMarket;
        currentMarket.sentTime = sentTime;

        return currentMarket;
    }




    /**
     * Builds a MarketVolumeStruct
     * @return new MarketVolumeStruct
     */
    public static MarketVolumeStruct buildMarketVolumeStruct()
    {
        MarketVolumeStruct struct = new MarketVolumeStruct();
        struct.quantity = 0;
        struct.volumeType = VolumeTypes.NO_CONTINGENCY;
        struct.multipleParties = false;
        return struct;
    }


    /**
     * Clones a MarketVolumeStruct
     * @return cloned MarketVolumeStruct
     */
    public static MarketVolumeStruct cloneMarketVolumeStruct(MarketVolumeStruct struct)
    {
        MarketVolumeStruct clonedStruct = new MarketVolumeStruct();
        clonedStruct.quantity = struct.quantity;
        clonedStruct.volumeType = struct.volumeType;
        clonedStruct.multipleParties = struct.multipleParties;
        return clonedStruct;
    }

    /**
     * Builds a MarketViewStruct
     * @return new MarketViewStruct
     */
    public static CurrentMarketViewStruct buildCurrentMarketViewStruct()
    {
        CurrentMarketViewStruct struct = new CurrentMarketViewStruct();
        struct.askPrice = StructBuilder.buildPriceStruct();
        struct.bidPrice = StructBuilder.buildPriceStruct();
        struct.askSizeSequence = new MarketVolumeStruct[0];
        struct.bidSizeSequence = new MarketVolumeStruct[0];
        struct.currentMarketViewType = CurrentMarketViewTypes.BEST_PUBLIC_ORDER_PRICE;
        return struct;
    }



    /**
      * gets an Empty current market struct
      * @return CurrentMarketStruct
      */
    public static CurrentMarketStruct buildCurrentMarketStruct(String sessionName, ProductKeysStruct keys) {
        CurrentMarketStruct currentMarket = buildCurrentMarketStruct(keys);
        currentMarket.sessionName = sessionName;
        return currentMarket;
    }

    /**
     * gets an empty express current market struct
     * @return CurrentMarketStruct
     */
    public static com.cboe.idl.cmiMarketData.CurrentMarketStructV4 buildCurrentMarketStructV4(int classKey, short productType, String exchange, int productKey)
    {
        com.cboe.idl.cmiMarketData.CurrentMarketStructV4 currentMarket = new com.cboe.idl.cmiMarketData.CurrentMarketStructV4();

        currentMarket.classKey = classKey;
        currentMarket.productKey = productKey;
        currentMarket.productType = productType;
        currentMarket.exchange = exchange;
        currentMarket.currentMarketType = 0;
        currentMarket.bidPrice = 0;
        currentMarket.bidTickDirection = ' ';
        currentMarket.bidSizeSequence = new com.cboe.idl.cmiMarketData.MarketVolumeStructV4[0];
        currentMarket.askPrice = 0;
        currentMarket.askSizeSequence = new com.cboe.idl.cmiMarketData.MarketVolumeStructV4[0];
        currentMarket.sentTime = 0;
        currentMarket.productState = 0;
        currentMarket.priceScale = 0;

        return currentMarket;
    }

    public static LastSaleStructV4 cloneLastSaleStructV4(LastSaleStructV4 struct)
    {
        LastSaleStructV4 newStruct = new LastSaleStructV4();

        newStruct.classKey = struct.classKey;
        newStruct.productKey = struct.productKey;
        newStruct.productType = struct.productType;
        newStruct.exchange = struct.exchange;
        newStruct.priceScale = struct.priceScale;
        newStruct.lastSaleTime = struct.lastSaleTime;
        newStruct.lastSalePrice = struct.lastSalePrice;
        newStruct.lastSaleVolume = struct.lastSaleVolume;
        newStruct.totalVolume = struct.totalVolume;
        newStruct.tickDirection = struct.tickDirection;
        newStruct.netPriceChange = struct.netPriceChange;
        newStruct.sentTime = struct.sentTime;

        return newStruct;
    }

    public static TickerStructV4 cloneTickerStructV4(TickerStructV4 struct)
    {
        TickerStructV4 newStruct = new TickerStructV4();
        newStruct.classKey = struct.classKey;
        newStruct.productKey = struct.productKey;
        newStruct.productType = struct.productType;
        newStruct.exchange = struct.exchange;
        newStruct.priceScale = struct.priceScale;
        newStruct.sentTime = struct.sentTime;
        newStruct.tradeTime = struct.tradeTime;
        newStruct.tradePrice = struct.tradePrice;
        newStruct.tradeVolume = struct.tradeVolume;
        newStruct.salePrefix = struct.salePrefix;
        newStruct.salePostfix = struct.salePostfix;

        return newStruct;
    }

    public static synchronized boolean isEqual(MarketVolumeStruct[] sequenceA, MarketVolumeStruct[] sequenceB)
    {
        HashMap hashA = getConsolidatedMap(sequenceA);
        HashMap hashB = getConsolidatedMap(sequenceB);
        boolean isEqual = true;

        if (hashA.size() != hashB.size())
        {
            isEqual = false;
        }
        else
        {
            Set mapEntries = hashA.entrySet();
            Iterator iterator = mapEntries.iterator();
            while (iterator.hasNext())
            {
                Map.Entry entry = (Map.Entry) iterator.next();
                String key = entry.getKey().toString();
                if (hashB.containsKey(key))
                {
                    int quantityA = ((Integer)entry.getValue()).intValue();
                    int quantityB = ((Integer)hashB.get(key)).intValue();
                    if (quantityA != quantityB)
                    {
                        isEqual = false;
                        break;
                    }
                }
                else
                {
                    isEqual = false;
                    break;
                }
            } //end of while loop
        }
        return isEqual;
    }

    private static HashMap getConsolidatedMap(MarketVolumeStruct[] sequence)
    {
        HashMap result = new HashMap();
        if (sequence != null)
        {
            for (int i = 0; i < sequence.length; i++)
            {
                if (sequence[i] != null)
                {
                    Short contingencyType = new Short(sequence[i].volumeType);
                    int quantity = sequence[i].quantity;

                    if (result.containsKey(contingencyType))
                    {
                        int existingQuantity = ((Integer)result.get(contingencyType)).intValue();
                        quantity = sequence[i].quantity + existingQuantity;
                    }
                    result.put(contingencyType, new Integer(quantity));
                }
            }
        }
        return result;
    }

    /**
     * Creates a Quotequery struct data containing default values for
     * all attributes.
     *
     * @author Nikhil Patel
     */
    public static QuoteQueryStruct buildQuoteQueryStruct()
    {
        QuoteQueryStruct entry = new QuoteQueryStruct();
        short product = 0;
        entry.productKeys = new ProductKeysStruct(0,0,product,0);
        entry.errorCode = 0;
    entry.bestmarketCurrentMarketStruct = buildCurrentMarketStruct(new ProductKeysStruct(0,0,product,0));
        entry.bestmarketPublicCurrentMarketStruct = buildCurrentMarketStruct(new ProductKeysStruct(0,0,product,0));
        ProductKeysStruct productKeysStruct = new ProductKeysStruct(0,0,product,0);
        entry.botrStruct = buildNBBOStruct(productKeysStruct);
        entry.exchangeIndicators = new ExchangeIndicatorStruct[0];
        entry.localProductState = 0;
        entry.linkageClassGateIndicators = new ExchangeGateIndicatorStruct[0];
        return entry;
    }

    /**
     * Creates a QuoteQueryV3 struct data containing default values for
     * all attributes.
     *
     * @return
     */
    public static QuoteQueryV3Struct buildQuoteQueryV3Struct()
    {
        QuoteQueryStruct entry = buildQuoteQueryStruct();
        ProductKeysStruct productKeys =  entry.productKeys;

        QuoteQueryV2Struct qqV2 = new QuoteQueryV2Struct();
        qqV2.nbboStruct = buildNBBOStruct(productKeys);
        qqV2.quoteQueryStruct = entry;

        QuoteQueryV3Struct qqV3 = new QuoteQueryV3Struct();
        qqV3.cboeMarket = buildBOStruct();
        qqV3.derivedQuote = buildBOStruct();
        qqV3.topOfBook = buildBOStruct();
        qqV3.quoteQueryV2Struct = qqV2;
        qqV3.BOBIndicator = BOBIndicatoryConstants.HYBRID_NOT_HAL;

        return qqV3;
    }

    /**
     * Creates a ClosingQuote  struct data containing default values for
     * all attributes.
     *
     * @author Nikhil Patel
     */
    public static ClosingQuoteStruct buildClosingQuoteStruct()
    {
        ClosingQuoteStruct entry = new ClosingQuoteStruct();
        short product = 0;
        entry.productKey = 0;
        entry.closingBidPrice = StructBuilder.buildPriceStruct();
        entry.closingAskPrice = StructBuilder.buildPriceStruct();
        return entry;
    }

    /**
     * Creates a ClosingQuote  struct data containing default values for
     * all attributes.
     *
     * @author Nikhil Patel
     */
    //cmiProduct::ReportingClassKey reportingClassKey;
    //  long errorCode;
      //    marketData::ClosingQuoteStructSequence closingQuoteStructs;
    public static ClosingQuoteSummaryStruct buildClosingQuoteSummaryStruct()
    {
        ClosingQuoteSummaryStruct entry = new ClosingQuoteSummaryStruct();
        short product = 0;
        entry.errorCode = 0;
        entry.closingQuoteStructs = new ClosingQuoteStruct[0] ;
        return entry;
    }

    /**
     * Create RecapStructV5 with default values.
     * @return Recapstructv5
     */
    public static RecapStructV5 buildRecapStructV5()
    {
        RecapStructV5 recapStructV5 = new RecapStructV5();
        ProductKeysStruct productKeysStruct = new ProductKeysStruct();
        recapStructV5.aRecapStruct = MarketDataStructBuilder.buildRecapStruct(productKeysStruct);
        recapStructV5.highPriceTime = StructBuilder.buildDateTimeStruct();
        recapStructV5.lowPriceTime = StructBuilder.buildDateTimeStruct();
        recapStructV5.openingPriceTime = StructBuilder.buildDateTimeStruct();
        recapStructV5.highPriceVolume = 0;
        recapStructV5.lastSalePriceVolume = 0;
        recapStructV5.lowPriceVolume = 0;
        recapStructV5.openPriceVolume = 0;
        recapStructV5.numberOfTrades = 0;
        recapStructV5.aRecapSuffix = "";

        return recapStructV5;
    }

    public static CurrentMarketStateChangeStruct buildCurrentMarketStateChangeStruct()
    {
        CurrentMarketStateChangeStruct result = new CurrentMarketStateChangeStruct();
        ProductKeysStruct productKeysStruct = new ProductKeysStruct();
        result.bestMarket = buildCurrentMarketStruct(productKeysStruct);
        result.bestPublicMarket = buildCurrentMarketStruct(productKeysStruct);
        result.newState = 0;
        result.oldState = 0;
        result.productStateTransactionSequenceNumber = 0;
        return result;
    }

}
