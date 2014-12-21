package com.cboe.domain.marketDataReportService;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import com.cboe.domain.product.OptionTypeImpl;
import com.cboe.domain.util.DateWrapper;
import com.cboe.domain.util.NoPrice;
import com.cboe.domain.util.PriceFactory;
import com.cboe.domain.util.PriceSqlType;
import com.cboe.idl.cmiMarketData.TickerStruct;
import com.cboe.idl.cmiUtil.TimeStruct;
import com.cboe.infrastructureServices.foundationFramework.HomeFactory;
import com.cboe.infrastructureServices.foundationFramework.PersistentBObject;
import com.cboe.infrastructureServices.foundationFramework.exceptionHandling.CBOELoggableException;
import com.cboe.infrastructureServices.foundationFramework.exceptionHandling.FatalFoundationFrameworkException;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.persistenceService.AttributeDefinition;
import com.cboe.infrastructureServices.persistenceService.DBAdapter;
import com.cboe.infrastructureServices.persistenceService.ObjectChangesIF;
import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.domain.TradeReport;
import com.cboe.interfaces.domain.marketDataReportService.MarketDataHistoryForReports;
import com.cboe.interfaces.domain.marketDataReportService.MarketDataHistoryForReportsHome;
import com.cboe.interfaces.domain.marketDataReportService.RecapForReport;
import com.cboe.interfaces.domain.product.Option;
import com.cboe.interfaces.domain.product.OptionType;
import com.cboe.idl.cmiMarketData.RecapStructV5;
import com.cboe.idl.constants.SalePrefixes;
import com.cboe.interfaces.businessServices.MarketDataReportProcessorHome;

/**
 * A persistent implementation of <code>RecapForReport</code>.
 * 
 * 
 * @author Cognizant Technology Solutions.
 */
public class RecapForReportImpl extends PersistentBObject implements RecapForReport
{
    public static final String TABLE_NAME = "recap_for_reports";
    /**
     * product key.
     */
    private int productKey;
    /**
     * name of the session.
     */
    private String sessionName;
    /**
     * class key.
     */
    private int classKey;
    /**
     * open interest.
     */
    private int openInterest;
    /**
     * Underlying Price.
     */
    private PriceSqlType underlyingPrice;
    /**
     * no. of trades.
     */
    private int numberOfTrades;
    /**
     * Price of the last trade.
     */
    private PriceSqlType lastSalePrice;
    /**
     * Time of last trade.
     */
    private long tradeTime;
    /**
     * Volume of last trade.
     */
    private int lastSaleVolume;
    /**
     * Total volume traded at last sale price since last time it changed.
     */
    private int lastSalePriceVolume;
    /**
     * Total volume traded during current trading day.
     */
    private int totalVolume;
    /**
     * Amount price changed between most recent trade and previous trade.
     */
    private PriceSqlType tickAmount;
    /**
     * Tick direction of last trade.
     */
    private char tickDirection;
    /**
     * Bid direction of last bid.
     */
    private char bidDirection;
    /**
     * Net change direction.
     */
    private char netChangeDirection;
    /**
     * Net change in price for current trading day.
     */
    private PriceSqlType netChange;
    /**
     * Most recent bid price.
     */
    private PriceSqlType bidPrice;
    /**
     * Size of most recent bid.
     */
    private int bidSize;
    /**
     * Time of most recent bid.
     */
    private long bidTime;
    /**
     * Most recent ask price.
     */
    private PriceSqlType askPrice;
    /**
     * Size of most recent ask.
     */
    private int askSize;
    /**
     * Time of most recent ask.
     */
    private long askTime;
    /**
     * OTC indicator
     */
    private boolean OTCInd;
    /**
     * Prefix string for recap. This needs to be broken into separate news and status indicators.
     */
    private String recapPrefix;
    /**
     * Highest price product traded at during current trading day.
     */
    private PriceSqlType highPrice;
    /**
     * Volume traded at the highest price.
     */
    private int highPriceVolume;
    /**
     * Lowest price product traded at during current trading day.
     */
    private PriceSqlType lowPrice;
    /**
     * Volume traded at the lowest price.
     */
    private int lowPriceVolume;
    /**
     * Price at which product opened.
     */
    private PriceSqlType openPrice;
    /**
     * Volume traded at the opening price. Volume will include all trades done while open price, low
     * price, and high price are equal.
     */
    private int openPriceVolume;
    /**
     * Price at which product closed. During a trading day, value will be closing price of previous
     * trading day.
     */
    private PriceSqlType closePrice;
    /**
     * Indicator set to true if trades have been received during the current day.
     */
    private boolean hasBeenTraded;
    /**
     * Zero as default value to avoid null pointer exceptions on conversions
     */
    private static final PriceSqlType zero = new PriceSqlType(new NoPrice());
    /**
     * Product type code. This value is needed so that persistent instances can be read.
     */
    protected short productType;
    /**
     * The type of this option, call or put.
     */
    protected OptionTypeImpl optionType;
    /**
     * Underlying type of the product.
     */
    private int underlyingCategory;
    /**
     * low price time.
     */
    private long lowPriceTime;
    /**
     * high price time.
     */
    private long highPriceTime;
    /**
     * opening price time.
     */
    private long openingPriceTime;
    /**
     * Cached reference to the history home.
     */
    private static MarketDataHistoryForReportsHome marketDataHistoryForReportsHome;
    /**
     * Cached reference to the processor home.
     */
    private static MarketDataReportProcessorHome marketDataReportProcessorHome;
    
    /**
     * Flag for calculation of recap using MDRS history .
     */
    private static boolean recalcRecapFromMDRSHistory;   
    
    /**
     * Name of property that holds flag for calculation of recap using MDRS history .
     */
    private static final String RECALC_RECAP_FROM_MDRS_HISTORY = "recalcRecapFromMDRSHistory";
    
    /**
     * Flag for calculation of recap.
     */
    private static boolean mustRecalcValues;
    
    /**
     * Name of property that holds flag for calculation of recap.
     */
    private static final String MUST_RECALC_VALUES = "mustRecalcValues";
    
    /**
     * represent null suffix value configured in xml
     */
    public static final String NULL_SUFFIX_VALUE = "NULL";
    
    /**
     * represent empty suffix value configured in xml
     */
    public static final String EMPTY_SUFFIX_VALUE = "EMPTY";
    
    /**
     * Gets read recalcRecapFromMDRSHistory property 
     */
    static
    {
        String recalFromHistoryProperty = System.getProperty(RECALC_RECAP_FROM_MDRS_HISTORY); 
        
        if( recalFromHistoryProperty != null && recalFromHistoryProperty.equals("false"))
        {
            recalcRecapFromMDRSHistory = false;
        }
        else
        {    
            recalcRecapFromMDRSHistory = true;
        }
        
        Log.information("RecapForReportImpl::recalcRecapFromMDRSHistory: " + recalcRecapFromMDRSHistory);
        
        String mustRecalcValuesProperty =  System.getProperty(MUST_RECALC_VALUES, "true");

		if(mustRecalcValuesProperty != null && mustRecalcValuesProperty.equals("false"))
		{
             mustRecalcValues = false;
		}
		else
		{
             mustRecalcValues = true;
		}
        Log.information("RecapForReportImpl::mustRecalcValues: " + mustRecalcValues);
    }
    
    /*
     * JavaGrinder variables
     */
    private static Field _productKey;
    private static Field _sessionName;
    private static Field _classKey;
    private static Field _openInterest;
    private static Field _underlyingPrice;
    private static Field _numberOfTrades;
    private static Field _lastSalePrice;
    private static Field _lastSaleVolume;
    private static Field _lastSalePriceVolume;
    private static Field _tradeTime;
    private static Field _totalVolume;
    private static Field _recapPrefix;
    private static Field _askTime;
    private static Field _askSize;
    private static Field _askPrice;
    private static Field _bidTime;
    private static Field _bidSize;
    private static Field _bidPrice;
    private static Field _netChange;
    private static Field _tickDirection;
    private static Field _bidDirection;
    private static Field _netChangeDirection;
    private static Field _tickAmount;
    private static Field _lowPrice;
    private static Field _lowPriceVolume;
    private static Field _highPrice;
    private static Field _highPriceVolume;
    private static Field _openPrice;
    private static Field _openPriceVolume;
    private static Field _closePrice;
    private static Field _hasBeenTraded;
    private static Field _OTCInd;
    private static Vector classDescriptor;
    private static Field _productType;
    private static Field _optionType;
    private static Field _underlyingCategory;
    private static Field _lowPriceTime;
    private static Field _highPriceTime;
    private static Field _openingPriceTime;
    /**
     * This static block will be regenerated if persistence is regenerated.
     */
    static
    { /* NAME:fieldDefinition: */
        try
        {
            _productKey = RecapForReportImpl.class.getDeclaredField("productKey");
            _productKey.setAccessible(true);
            _sessionName = RecapForReportImpl.class.getDeclaredField("sessionName");
            _sessionName.setAccessible(true);
            _classKey = RecapForReportImpl.class.getDeclaredField("classKey");
            _classKey.setAccessible(true);
            _openInterest = RecapForReportImpl.class.getDeclaredField("openInterest");
            _openInterest.setAccessible(true);
            _underlyingPrice = RecapForReportImpl.class.getDeclaredField("underlyingPrice");
            _underlyingPrice.setAccessible(true);
            _numberOfTrades = RecapForReportImpl.class.getDeclaredField("numberOfTrades");
            _numberOfTrades.setAccessible(true);
            _lastSalePrice = RecapForReportImpl.class.getDeclaredField("lastSalePrice");
            _lastSalePrice.setAccessible(true);
            _tradeTime = RecapForReportImpl.class.getDeclaredField("tradeTime");
            _tradeTime.setAccessible(true);
            _lastSaleVolume = RecapForReportImpl.class.getDeclaredField("lastSaleVolume");
            _lastSaleVolume.setAccessible(true);
            _lastSalePriceVolume = RecapForReportImpl.class.getDeclaredField("lastSalePriceVolume");
            _lastSalePriceVolume.setAccessible(true);
            _totalVolume = RecapForReportImpl.class.getDeclaredField("totalVolume");
            _totalVolume.setAccessible(true);
            _tickDirection = RecapForReportImpl.class.getDeclaredField("tickDirection");
            _tickDirection.setAccessible(true);
            _bidDirection = RecapForReportImpl.class.getDeclaredField("bidDirection");
            _bidDirection.setAccessible(true);
            _netChangeDirection = RecapForReportImpl.class.getDeclaredField("netChangeDirection");
            _netChangeDirection.setAccessible(true);
            _netChange = RecapForReportImpl.class.getDeclaredField("netChange");
            _netChange.setAccessible(true);
            _bidPrice = RecapForReportImpl.class.getDeclaredField("bidPrice");
            _bidPrice.setAccessible(true);
            _bidSize = RecapForReportImpl.class.getDeclaredField("bidSize");
            _bidSize.setAccessible(true);
            _bidTime = RecapForReportImpl.class.getDeclaredField("bidTime");
            _bidTime.setAccessible(true);
            _askPrice = RecapForReportImpl.class.getDeclaredField("askPrice");
            _askPrice.setAccessible(true);
            _askSize = RecapForReportImpl.class.getDeclaredField("askSize");
            _askSize.setAccessible(true);
            _askTime = RecapForReportImpl.class.getDeclaredField("askTime");
            _askTime.setAccessible(true);
            _OTCInd = RecapForReportImpl.class.getDeclaredField("OTCInd");
            _OTCInd.setAccessible(true);
            _recapPrefix = RecapForReportImpl.class.getDeclaredField("recapPrefix");
            _recapPrefix.setAccessible(true);
            _tickAmount = RecapForReportImpl.class.getDeclaredField("tickAmount");
            _tickAmount.setAccessible(true);
            _highPrice = RecapForReportImpl.class.getDeclaredField("highPrice");
            _highPrice.setAccessible(true);
            _highPriceVolume = RecapForReportImpl.class.getDeclaredField("highPriceVolume");
            _highPriceVolume.setAccessible(true);
            _lowPrice = RecapForReportImpl.class.getDeclaredField("lowPrice");
            _lowPrice.setAccessible(true);
            _lowPriceVolume = RecapForReportImpl.class.getDeclaredField("lowPriceVolume");
            _lowPriceVolume.setAccessible(true);
            _openPrice = RecapForReportImpl.class.getDeclaredField("openPrice");
            _openPrice.setAccessible(true);
            _openPriceVolume = RecapForReportImpl.class.getDeclaredField("openPriceVolume");
            _openPriceVolume.setAccessible(true);
            _closePrice = RecapForReportImpl.class.getDeclaredField("closePrice");
            _closePrice.setAccessible(true);
            _hasBeenTraded = RecapForReportImpl.class.getDeclaredField("hasBeenTraded");
            _hasBeenTraded.setAccessible(true);
            _productType = RecapForReportImpl.class.getDeclaredField("productType");
            _productType.setAccessible(true);
            _optionType = RecapForReportImpl.class.getDeclaredField("optionType");
            _optionType.setAccessible(true);
            _underlyingCategory = RecapForReportImpl.class.getDeclaredField("underlyingCategory");
            _underlyingCategory.setAccessible(true);
            _lowPriceTime = RecapForReportImpl.class.getDeclaredField("lowPriceTime");
            _lowPriceTime.setAccessible(true);
            _highPriceTime = RecapForReportImpl.class.getDeclaredField("highPriceTime");
            _highPriceTime.setAccessible(true);
            _openingPriceTime = RecapForReportImpl.class.getDeclaredField("openingPriceTime");
            _openingPriceTime.setAccessible(true);
        }
        catch (NoSuchFieldException ex)
        {
            Log.exception(ex);
        }
    }

    /**
     * Class used to summarize cancel quantities when rebuilding recap
     */
    private static class PriceSummary
    {
        public Price summaryPrice;
        public int quantity;

        public PriceSummary(Price aPrice, int aQuantity)
        {
            summaryPrice = aPrice;
            quantity = aQuantity;
        }

        /**
         * Find index summary item in list by Price.
         */
        public static int findByPrice(List summaryList, Price wantedPrice)
        {
            int foundItem = -1;
            for (int i = 0; i < summaryList.size(); i++)
            {
                PriceSummary item = (PriceSummary) summaryList.get(i);
                if (item.summaryPrice.equals(wantedPrice))
                {
                    foundItem = i;
                    break;
                }
            }
            return foundItem;
        }
    }

    /**
     * Creates an uninitialized instance.
     */
    public RecapForReportImpl()
    {
        super();
    }

    /**
     * Calculates the quantity that should be used for this last sale based on summarized cancels.
     */
    private int adjustLastSaleQuantity(List cancelSummary, MarketDataHistoryForReports lastSale)
    {
        int adjustedQuantity;
        int index = PriceSummary.findByPrice(cancelSummary, lastSale.getLastSalePrice());
        if (index >= 0)
        {
            PriceSummary summary = (PriceSummary) cancelSummary.get(index);
            if (summary.quantity > lastSale.getLastSaleVolume())
            {
                adjustedQuantity = 0;
                summary.quantity -= lastSale.getLastSaleVolume();
            }
            else
            {
                adjustedQuantity = lastSale.getLastSaleVolume() - summary.quantity;
                // summary complete - remove it so shorten future searches
                cancelSummary.remove(index);
            }
        }
        else
        {
            adjustedQuantity = lastSale.getLastSaleVolume();
        }
        return adjustedQuantity;
    }

    /**
     * Performs instance initialization.
     */
    public void create(String sessionName, int classKey, int categoryType, int productKey,
            short productType, char optionType, Price closePrice,
            int openInterest)
    {
        setSessionName(sessionName);
        setProductKey(productKey);
        setClassKey(classKey);
        setUnderlyingPrice(zero);
        setClosePrice(new PriceSqlType(closePrice));
        if (Character.isLetter(optionType))
        {
            setOptionType(OptionTypeImpl.getOptionType(optionType));
        }
        setOpenInterest(openInterest);
        setProductType(productType);
        setUnderlyingCategoryType(categoryType);
        setAskPrice(zero);
        setBidPrice(zero);
        setRecapPrefix("");
        setBidDirection(' ');
        setTickDirection(' ');
        setOTC(false);
        resetLastSaleValues();
    }

    /**
     * Getter for product key.
     */
    public int getProductKey()
    {
        return (int) editor.get(_productKey, productKey);
    }

    /**
     * Getter for session name.
     */
    public String getSessionName()
    {
        return (String) editor.get(_sessionName, sessionName);
    }

    /**
     * Getter for class key.
     */
    public int getClassKey()
    {
        return (int) editor.get(_classKey, classKey);
    }

    /**
     * Getter for open interest.
     */
    public int getOpenInterest()
    {
        return (int) editor.get(_openInterest, openInterest);
    }

    /**
     * Getter for underlying price.
     */
    public PriceSqlType getUnderlyingPrice()
    {
        return (PriceSqlType) editor.get(_underlyingPrice, underlyingPrice);
    }

    /**
     * Getter for no. of trades.
     */
    public int getNumberOfTrades()
    {
        return (int) editor.get(_numberOfTrades, numberOfTrades);
    }

    /**
     * Getter for ask price.
     */
    public PriceSqlType getAskPrice()
    {
        return (PriceSqlType) editor.get(_askPrice, askPrice);
    }

    /**
     * Getter for ask size.
     */
    public int getAskSize()
    {
        return (int) editor.get(_askSize, askSize);
    }

    /**
     * Getter for ask time.
     */
    public long getAskTime()
    {
        return (long) editor.get(_askTime, askTime);
    }

    /**
     * Getter for bid price.
     */
    public PriceSqlType getBidPrice()
    {
        return (PriceSqlType) editor.get(_bidPrice, bidPrice);
    }

    /**
     * Getter for bid size.
     */
    public int getBidSize()
    {
        return (int) editor.get(_bidSize, bidSize);
    }

    /**
     * Getter for bid time.
     */
    public long getBidTime()
    {
        return (long) editor.get(_bidTime, bidTime);
    }

    /**
     * Getter for close price.
     */
    public PriceSqlType getClosePrice()
    {
        return (PriceSqlType) editor.get(_closePrice, closePrice);
    }

    /**
     * Getter for has been traded indicator.
     */
    public boolean getHasBeenTraded()
    {
        return (boolean) editor.get(_hasBeenTraded, hasBeenTraded);
    }

    /**
     * Determines if last sale prefix is a cancel.
     */
    public boolean isCancelPrefix(String lastSalePrefix)
    {
        if (lastSalePrefix == null)
        {
            return false;
        }
        return lastSalePrefix.equals(TradeReport.CANCELOPEN_PREFIX)
                || lastSalePrefix.equals(TradeReport.CANCEL_PREFIX)
                || lastSalePrefix.equals(TradeReport.CANCELLAST_PREFIX)
                || lastSalePrefix.equals(TradeReport.CANCELONLY_PREFIX);
    }

    /**
     * Determines if last sale prefix is a block trade or EFP or GWAP.
     */
    public boolean externalTradePrefix(String prefix)
    {
        if (prefix == null)
        {
            return false;
        }
        return prefix.equals(TradeReport.BLOCK_TRADE_PREFIX)
                || prefix.equals(TradeReport.EXCHANGE_FOR_PHYSICAL_PREFIX)
                || prefix.equals(TradeReport.GWAP_TRADE_PREFIX);
    }

    /**
     * Getter for OTC indicator
     */
    public boolean isOTC()
    {
        return editor.get(_OTCInd, OTCInd);
    }

    /**
     * Getter for high price.
     */
    public PriceSqlType getHighPrice()
    {
        return (PriceSqlType) editor.get(_highPrice, highPrice);
    }

    /**
     * Getter for high price volume.
     */
    public int getHighPriceVolume()
    {
        return (int) editor.get(_highPriceVolume, highPriceVolume);
    }

    /**
     * Getter for last sale price.
     */
    public PriceSqlType getLastSalePrice()
    {
        return (PriceSqlType) editor.get(_lastSalePrice, lastSalePrice);
    }

    /**
     * Getter for last sale volume.
     */
    public int getLastSaleVolume()
    {
        return (int) editor.get(_lastSaleVolume, lastSaleVolume);
    }

    /**
     * Getter for last sale price volume.
     */
    public int getLastSalePriceVolume()
    {
        return (int) editor.get(_lastSalePriceVolume, lastSalePriceVolume);
    }

    /**
     * Getter for low price
     */
    public PriceSqlType getLowPrice()
    {
        return (PriceSqlType) editor.get(_lowPrice, lowPrice);
    }

    /**
     * Getter for low price volume.
     */
    public int getLowPriceVolume()
    {
        return (int) editor.get(_lowPriceVolume, lowPriceVolume);
    }

    /**
     * Gets the market data report history home.
     */
    public MarketDataHistoryForReportsHome getMarketDataHistoryForReportsHome()
    {
        if (marketDataHistoryForReportsHome == null)
        {
            try
            {
                marketDataHistoryForReportsHome = (MarketDataHistoryForReportsHome) HomeFactory.getInstance().findHome(MarketDataHistoryForReportsHome.HOME_NAME);
            }
            catch (Exception e)
            {
                Log.exception(this, "Unable to get market data report history home", e);
            }
        }
        return marketDataHistoryForReportsHome;
    }

    /**
     * Getter for net change.
     */
    public PriceSqlType getNetChange()
    {
        return (PriceSqlType) editor.get(_netChange, netChange);
    }

    /**
     * Getter for open price.
     */
    public PriceSqlType getOpenPrice()
    {
        return (PriceSqlType) editor.get(_openPrice, openPrice);
    }

    /**
     * Getter for open price volume.
     */
    public int getOpenPriceVolume()
    {
        return editor.get(_openPriceVolume, openPriceVolume);
    }

    /**
     * Getter for recap prefix.
     */
    public String getRecapPrefix()
    {
        return (String) editor.get(_recapPrefix, recapPrefix);
    }

    /**
     * Getter for tick amount.
     */
    public PriceSqlType getTickAmount()
    {
        return (PriceSqlType) editor.get(_tickAmount, tickAmount);
    }

    /**
     * Getter for tick direction.
     */
    public char getTickDirection()
    {
        return (char) editor.get(_tickDirection, tickDirection);
    }

    /**
     * Getter for bid direction.
     */
    public char getBidDirection()
    {
        return (char) editor.get(_bidDirection, bidDirection);
    }

    /**
     * Getter for net change direction.
     */
    public char getNetChangeDirection()
    {
        return (char) editor.get(_netChangeDirection, netChangeDirection);
    }

    /**
     * Getter for total volume.
     */
    public int getTotalVolume()
    {
        return (int) editor.get(_totalVolume, totalVolume);
    }

    /**
     * Getter for trade time..
     */
    public long getTradeTime()
    {
        return (long) editor.get(_tradeTime, tradeTime);
    }

    /**
     * Getter for low price time.
     */
    public long getLowPriceTime()
    {
        return (long) editor.get(_lowPriceTime, lowPriceTime);
    }

    /**
     * Getter for high price time.
     */
    public long getHighPriceTime()
    {
        return (long) editor.get(_highPriceTime, highPriceTime);
    }

    /**
     * Getter for opening price time.
     */
    public long getOpeningPriceTime()
    {
        return (long) editor.get(_openingPriceTime, openingPriceTime);
    }

    /**
     * Describe how this class relates to the relational database.
     */
    private void initDescriptor()
    {
        synchronized (RecapForReportImpl.class)
        {
            if (classDescriptor != null)
                return;
            Vector tempDescriptor = getSuperDescriptor();
            tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("product_Key", _productKey));
            tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("session_Name", _sessionName));
            tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("class_key", _classKey));
            tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("open_Interest", _openInterest));
            tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("underlying_Price", _underlyingPrice));
            tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("number_Of_Trades", _numberOfTrades));
            tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("last_sale_price", _lastSalePrice));
            tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("trade_time", _tradeTime));
            tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("last_sale_vol", _lastSaleVolume));
            tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("last_sale_price_vol", _lastSalePriceVolume));
            tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("total_vol", _totalVolume));
            tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("tick_dir", _tickDirection));
            tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("bid_dir", _bidDirection));
            tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("net_chg_dir", _netChangeDirection));
            tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("tick_amt", _tickAmount));
            tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("net_chg", _netChange));
            tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("bid_price", _bidPrice));
            tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("bid_size", _bidSize));
            tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("bid_time", _bidTime));
            tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("ask_price", _askPrice));
            tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("ask_size", _askSize));
            tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("ask_time", _askTime));
            tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("otc_ind", _OTCInd));
            tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("recap_prefix", _recapPrefix));
            tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("high_price", _highPrice));
            tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("high_price_vol", _highPriceVolume));
            tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("low_price", _lowPrice));
            tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("low_price_vol", _lowPriceVolume));
            tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("open_price", _openPrice));
            tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("open_price_vol", _openPriceVolume));
            tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("close_price", _closePrice));
            tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("has_been_traded_ind", _hasBeenTraded));
            tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("prod_type_code", _productType));
            tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("opt_type_code", _optionType));
            tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("underlying_category", _underlyingCategory));
            tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("lowPriceTime", _lowPriceTime));
            tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("highPriceTime", _highPriceTime));
            tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("openingPriceTime", _openingPriceTime));
            classDescriptor = tempDescriptor;
        }
    }

    /**
     * Needed to define table name and the description of this class.
     */
    public ObjectChangesIF initializeObjectEditor()
    {
        final DBAdapter result = (DBAdapter) super.initializeObjectEditor();
        if (classDescriptor == null)
        {
            initDescriptor();
        }
        result.setTableName(TABLE_NAME);
        result.setClassDescription(classDescriptor);
        return result;
    }

    /**
     * Setter for product key.
     */
    public void setProductKey(int aValue)
    {
        editor.set(_productKey, aValue, productKey);
    }

    /**
     * Setter for session name.
     */
    public void setSessionName(String aValue)
    {
        editor.set(_sessionName, aValue, sessionName);
    }

    /**
     * Setter for class key.
     */
    public void setClassKey(int aValue)
    {
        editor.set(_classKey, aValue, classKey);
    }

    /**
     * Setter for open interest.
     */
    public void setOpenInterest(int aValue)
    {
        editor.set(_openInterest, aValue, openInterest);
    }

    /**
     * Setter for underlying price.
     */
    public void setUnderlyingPrice(PriceSqlType aValue)
    {
        editor.set(_underlyingPrice, aValue, underlyingPrice);
    }

    /**
     * Setter for product key.
     */
    public void setNumberOfTrades(int aValue)
    {
        editor.set(_numberOfTrades, aValue, numberOfTrades);
    }

    /**
     * Setter for ask price.
     */
    public void setAskPrice(PriceSqlType aValue)
    {
        editor.set(_askPrice, aValue, askPrice);
    }

    /**
     * Setter for ask size.
     */
    public void setAskSize(int aValue)
    {
        editor.set(_askSize, aValue, askSize);
    }

    /**
     * Setter for ask time.
     */
    public void setAskTime(long aValue)
    {
        editor.set(_askTime, aValue, askTime);
    }

    /**
     * Setter for bid price.
     */
    public void setBidPrice(PriceSqlType aValue)
    {
        editor.set(_bidPrice, aValue, bidPrice);
    }

    /**
     * Setter for bid size.
     */
    public void setBidSize(int aValue)
    {
        editor.set(_bidSize, aValue, bidSize);
    }

    /**
     * Setter for bid time.
     */
    public void setBidTime(long aValue)
    {
        editor.set(_bidTime, aValue, bidTime);
    }

    /**
     * Setter for close price.
     */
    public void setClosePrice(PriceSqlType aValue)
    {
        editor.set(_closePrice, aValue, closePrice);
    }

    /**
     * Setter for has been traded indicator.
     */
    public void setHasBeenTraded(boolean aValue)
    {
        editor.set(_hasBeenTraded, aValue, hasBeenTraded);
    }

    /**
     * Setter for has OTC indicator
     */
    public void setOTC(boolean aValue)
    {
        editor.set(_OTCInd, aValue, OTCInd);
    }

    /**
     * Setter for high price.
     */
    public void setHighPrice(PriceSqlType aValue)
    {
        editor.set(_highPrice, aValue, highPrice);
    }

    /**
     * Setter for high price volume.
     */
    public void setHighPriceVolume(int aValue)
    {
        editor.set(_highPriceVolume, aValue, highPriceVolume);
    }

    /**
     * Setter for last sale price.
     */
    public void setLastSalePrice(PriceSqlType aValue)
    {
        editor.set(_lastSalePrice, aValue, lastSalePrice);
    }

    /**
     * Setter for last sale volume.
     */
    public void setLastSaleVolume(int aValue)
    {
        editor.set(_lastSaleVolume, aValue, lastSaleVolume);
    }

    /**
     * Setter for last sale volume.
     */
    public void setLastSalePriceVolume(int aValue)
    {
        editor.set(_lastSalePriceVolume, aValue, lastSalePriceVolume);
    }

    /**
     * Setter for low price
     */
    public void setLowPrice(PriceSqlType aValue)
    {
        editor.set(_lowPrice, aValue, lowPrice);
    }

    /**
     * Setter for low price volume
     */
    public void setLowPriceVolume(int aValue)
    {
        editor.set(_lowPriceVolume, aValue, lowPriceVolume);
    }

    /**
     * Setter for net change.
     */
    public void setNetChange(PriceSqlType aValue)
    {
        editor.set(_netChange, aValue, netChange);
    }

    /**
     * Setter for open price.
     */
    public void setOpenPrice(PriceSqlType aValue)
    {
        editor.set(_openPrice, aValue, openPrice);
    }

    /**
     * Setter for open price volume.
     */
    public void setOpenPriceVolume(int aValue)
    {
        editor.set(_openPriceVolume, aValue, openPriceVolume);
    }

    /**
     * Setter for recap prefix.
     */
    public void setRecapPrefix(String aValue)
    {
        editor.set(_recapPrefix, aValue, recapPrefix);
    }

    /**
     * Setter for tick amount.
     */
    public void setTickAmount(PriceSqlType aValue)
    {
        editor.set(_tickAmount, aValue, tickAmount);
    }

    /**
     * Setter for tick direction.
     */
    public void setTickDirection(char aValue)
    {
        editor.set(_tickDirection, aValue, tickDirection);
    }

    /**
     * Setter for bid direction.
     */
    public void setBidDirection(char aValue)
    {
        editor.set(_bidDirection, aValue, bidDirection);
    }

    /**
     * Setter for net change direction.
     */
    public void setNetChangeDirection(char aValue)
    {
        editor.set(_netChangeDirection, aValue, netChangeDirection);
    }

    /**
     * Setter for total volume.
     */
    public void setTotalVolume(int aValue)
    {
        editor.set(_totalVolume, aValue, totalVolume);
    }

    /**
     * Setter for trade time.
     */
    public void setTradeTime(long aValue)
    {
        editor.set(_tradeTime, aValue, tradeTime);
    }

    /**
     * Sets low price time.
     * 
     * @param aValue
     */
    protected void setLowPriceTime(long aValue)
    {
        editor.set(_lowPriceTime, aValue, lowPriceTime);
    }

    /**
     * Sets high price time.
     * 
     * @param aValue
     */
    protected void setHighPriceTime(long aValue)
    {
        editor.set(_highPriceTime, aValue, highPriceTime);
    }

    /**
     * Sets opening price time.
     * 
     * @param aValue
     */
    protected void setOpeningPriceTime(long aValue)
    {
        editor.set(_openingPriceTime, aValue, openingPriceTime);
    }

    /**
     * Summaries price/quantity for all cancel entries.
     */
    public List summarizeLastSaleCancels(TickerStruct currentCancel,
            MarketDataHistoryForReports[] lastSales)
    {
    	Log.information(this, ">> Entering into method summarizeLastSaleCancels");
        // not expecting more than a couple prices at the most.
        LinkedList summaries = new LinkedList();
        PriceSummary currentSummary = new PriceSummary(PriceFactory.create(currentCancel.lastSalePrice), currentCancel.lastSaleVolume);
        summaries.add(currentSummary);
        for (int i = 0; i < lastSales.length; i++)
        {
            if (isCancelPrefix(lastSales[i].getTickerPrefix()))
            {
                if (!currentSummary.summaryPrice.equals(lastSales[i].getLastSalePrice()))
                {
                    int index = PriceSummary.findByPrice(summaries, lastSales[i].getLastSalePrice());
                    if (index >= 0)
                    {
                        currentSummary = (PriceSummary) summaries.get(index);
                    }
                    else
                    {
                        currentSummary = new PriceSummary(lastSales[i].getLastSalePrice(), 0);
                        summaries.add(currentSummary);
                    }
                }
                currentSummary.quantity += lastSales[i].getLastSaleVolume();
            }
        }
        return summaries;
    }

    /**
     * Determines if ticker is for cancel or regular.
     */
    public synchronized void updateTickerEntry(TimeStruct tradeTime, TickerStruct updatedTicker)
    {
        if (isCancelPrefix(updatedTicker.salePrefix))
        {
            updateCancelTicker(tradeTime, updatedTicker);
        }
        else
        {
            updateLastSaleTicker(tradeTime, updatedTicker);
        }
        // set recap prefix so that end users can know why recap changed
        setRecapPrefix(updatedTicker.salePrefix);
    }

    /**
     * Updates this summary with values from a cancel ticker (the result of a trade bust).
     */
    private void updateCancelTicker(TimeStruct tradeTime, TickerStruct cancel)
    {
        //boolean mustRecalcValues = false;
        PriceSqlType cancelPrice = new PriceSqlType(cancel.lastSalePrice);
        setTotalVolume(getTotalVolume() - cancel.lastSaleVolume);
        setNumberOfTrades(getNumberOfTrades() - 1);
        if (cancelPrice.equals(getLastSalePrice()))
        {
            setLastSalePriceVolume(getLastSalePriceVolume() - cancel.lastSaleVolume);
            if (getLastSaleVolume() > cancel.lastSaleVolume)
            {
                setLastSaleVolume(getLastSaleVolume() - cancel.lastSaleVolume);
            }
            else
            {
                // won't do re-calc just to get last sale quantity when price doesn't change.
                setLastSaleVolume(0);
                mustRecalcValues = true; // TPF is currently re-calculating this.
            }
        }
        if (cancelPrice.equals(getLowPrice()))
        {
            if (getLowPriceVolume() > cancel.lastSaleVolume)
            {
                setLowPriceVolume(getLowPriceVolume() - cancel.lastSaleVolume);
            }
            else
            {
                mustRecalcValues = true;
            }
        }
        if (cancelPrice.equals(getHighPrice()))
        {
            if (getHighPriceVolume() > cancel.lastSaleVolume)
            {
                setHighPriceVolume(getHighPriceVolume() - cancel.lastSaleVolume);
            }
            else
            {
                mustRecalcValues = true;
            }
        }
        if (cancel.salePrefix.equals(TradeReport.CANCELOPEN_PREFIX))
        {
            if (getOpenPriceVolume() > cancel.lastSaleVolume)
            {
                setOpenPriceVolume(getOpenPriceVolume() - cancel.lastSaleVolume);
            }
            else
            {
                mustRecalcValues = true;
            }
        }
        if (mustRecalcValues && recalcRecapFromMDRSHistory )
        {
            recalcRecapFromHistory(tradeTime, cancel);
        }
    }

    private void recalcRecapFromHistory(TimeStruct tradeTime, TickerStruct currentCancel)
    {
        MarketDataHistoryForReports[] lastSales = getMarketDataHistoryForReportsHome().findCurrentDayLastSales(currentCancel.sessionName, currentCancel.productKeys.productKey);
        if (lastSales.length > 0)
        {
            resetLastSaleValues();
            List cancelSummary = summarizeLastSaleCancels(currentCancel, lastSales);
            for (int i = 0; i < lastSales.length; i++)
            {
                if (!isCancelPrefix(lastSales[i].getTickerPrefix()))
                {
                    int adjustedQuantity = adjustLastSaleQuantity(cancelSummary, lastSales[i]);
                    if (adjustedQuantity > 0)
                    {
                        PriceSqlType salePrice = new PriceSqlType(lastSales[i].getLastSalePrice());
                        updateLastSaleValues(tradeTime, lastSales[i].getTickerPrefix(), salePrice, adjustedQuantity);
                    }
                }
            }
        }
        else
        {
            Log.alarm(this, "Unable to recalculate recap for product("
                    + currentCancel.productKeys.productKey + "), no history entries found");
        }
    }

    /**
     * Resets all values used in last sale processing of tickers.
     */
    private void resetLastSaleValues()
    {
        setTotalVolume(0);
        setNumberOfTrades(0);
        setLastSalePrice(zero);
        setLastSaleVolume(0);
        setLastSalePriceVolume(0);
        setNetChange(zero);
        setNetChangeDirection(' ');
        setTickAmount(zero);
        setLowPrice(zero);
        setLowPriceVolume(0);
        setHighPrice(zero);
        setHighPriceVolume(0);
        setOpenPrice(zero);
        setOpenPriceVolume(0);
        setHasBeenTraded(false);
        setLowPriceTime(0);
        setHighPriceTime(0);
        setOpeningPriceTime(0);
    }

    /**
     * Updates this summary with values from last sale ticker.
     */
    private void updateLastSaleTicker(TimeStruct tradeTime, TickerStruct lastSale)
    {
        PriceSqlType price = new PriceSqlType(lastSale.lastSalePrice);
        updateLastSaleValues(tradeTime, lastSale.salePrefix, price, lastSale.lastSaleVolume);
        setTradeTime(System.currentTimeMillis());
    }

    /**
     * Updates the last sale values.
     */
    private void updateLastSaleValues(TimeStruct tradeTime, String prefix, PriceSqlType salePrice,
            int saleVolume)
    {
        if (getHasBeenTraded())
        {
            setTickAmount(new PriceSqlType(salePrice.toDouble() - getLastSalePrice().toDouble()));
            setTickDirection(getDirection(getTickAmount()));
            
        }
        else
        {
            setTickAmount(new PriceSqlType(0.0));
            setTickDirection(getDirection(getTickAmount()));
        }
        boolean externalTrade = externalTradePrefix(prefix);
        if (!externalTrade || (externalTrade && !getHasBeenTraded()))
        {
            if (getLastSalePrice().equals(salePrice))
            {
                setLastSalePriceVolume(getLastSalePriceVolume() + saleVolume);
            }
            else
            {
                setLastSalePriceVolume(saleVolume);
            }
            if(isEligibleForLSPUpdate(prefix))
            {
            	setLastSalePrice(salePrice);
                setLastSaleVolume(saleVolume);
            }
        }
        setTotalVolume(saleVolume + getTotalVolume());
        setNumberOfTrades(getNumberOfTrades() + 1);
        if (getHasBeenTraded() && !externalTrade)
        {
            if (getLowPrice() != null && salePrice.lessThan(getLowPrice()))
            {
                setLowPrice(salePrice);
                setLowPriceTime(DateWrapper.convertToMillis(tradeTime));
                setLowPriceVolume(saleVolume);
            }
            else if (salePrice.equals(getLowPrice()))
            {
                setLowPriceVolume(getLowPriceVolume() + saleVolume);
            }
            if (salePrice.greaterThan(getHighPrice()))
            {
                setHighPrice(salePrice);
                setHighPriceTime(DateWrapper.convertToMillis(tradeTime));
                setHighPriceVolume(saleVolume);
            }
            else if (salePrice.equals(getHighPrice()))
            {
                setHighPriceVolume(getHighPriceVolume() + saleVolume);
            }
            // update open price volume with all trades until a trade has been done at a different
            // price (low or high
            // will no longer equal the open)
            if (salePrice.equals(getOpenPrice()) && getOpenPrice().equals(getLowPrice())
                    && getOpenPrice().equals(getHighPrice()))
            {
                setOpenPriceVolume(getOpenPriceVolume() + saleVolume);
            }
        }
        else if (!getHasBeenTraded())
        {
            setLowPrice(salePrice);
            setLowPriceTime(DateWrapper.convertToMillis(tradeTime));
            setLowPriceVolume(saleVolume);
            setHighPrice(salePrice);
            setHighPriceTime(DateWrapper.convertToMillis(tradeTime));
            setHighPriceVolume(saleVolume);
            setOpenPrice(salePrice);
            setOpeningPriceTime(DateWrapper.convertToMillis(tradeTime));
            setOpenPriceVolume(saleVolume);
             if (getClosePrice().isNoPrice()|| isZeroPrice(getClosePrice()))
            {
                setClosePrice(salePrice);
            }
        }
        setNetChange(new PriceSqlType(salePrice.toDouble() - getClosePrice().toDouble()));
        setNetChangeDirection(getDirection(getNetChange()));
        setHasBeenTraded(true);
        
        if(isOpenTrade(prefix))
        {
            setOpenPrice(salePrice);
            setOpeningPriceTime(DateWrapper.convertToMillis(tradeTime));
            setOpenPriceVolume(saleVolume);
        }
    }

    /**
     * Determines if trade sale prefix is OPEN or OPNL.
     * @param  prefixVal prefix value to compare
     * @return boolean if trade sale prefix is OPEN or OPNL
     */
    public boolean isOpenTrade(final String prefixVal)
    {
        return SalePrefixes.OPENING_TRADE.equals(prefixVal) || SalePrefixes.OPENING_ONLY_TRADE.equals(prefixVal);
    }
    
    /**
     * Determines if price in zero.
     * @param  Price
     * @return boolean
     */
    public boolean isZeroPrice(Price price)
    {
        Price zeroPrice = new PriceSqlType(0);
        return zeroPrice.equals(price);
    }


    public String toString()
    {
        String recapString = "Recap : [" + "classKey : " + this.getClassKey() + "\n ProductKey : "
                + this.getProductKey() + "\n SessionName : " + this.getSessionName()
                + "\n LowPrice : " + this.getLowPrice() + "\n LowPriceVolume : "
                + this.getLowPriceVolume() + "\n HighPrice : " + this.getHighPrice()
                + "\n HighPriceVolume : " + this.getHighPriceVolume() + "\n LastSaleVolume : "
                + this.getLastSaleVolume() + "\n LastSalePriceVolume : "
                + this.getLastSalePriceVolume() + "\n NumberOfTrades : " + this.getNumberOfTrades()
                + "\n OpenInterest : " + this.getOpenInterest() + "\n OpenPriceVolume : "
                + this.getOpenPriceVolume() + "\n RecapPrefix : " + this.getRecapPrefix()
                + "\n TotalVolume : " + this.getTotalVolume() + "\n ClosePrice : "
                + this.getClosePrice() + "\n UnderlyingPrice : " + this.getUnderlyingPrice()
                + "\n TradeTime : " + this.getTradeTime() + "\n HasBeenTraded : "
                + this.getHasBeenTraded() + "\n LastSalePrice : " + this.getLastSalePrice()
                + "\n AskPrice : " + this.getAskPrice() + "\n AskTime : " + this.getAskTime()
                + "\n AskSize : " + this.getAskSize() + "\n BidPrice : " + this.getBidPrice()
                + "\n BidTime : " + this.getBidTime() + "\n BidSize : " + this.getBidSize()
                + "\n NetChange : " + this.getNetChange() + "\n NetChangeDirection : "
                + this.getNetChangeDirection() + "\n TickAmount : " + this.getTickAmount()
                + "\n TickDirection : " + this.getTickDirection() + "\n BidDirection : "
                + this.getBidDirection() + "\n UnderlyingCategoryType : "
                + this.getUnderlyingCategoryType() + "\n ProductType : " + this.getProductType()
                + "\n OptionType : " + this.getOptionType() + "\n OpenInterest : "
                + this.getOpenInterest() + "\n LowPriceTime : " + this.getLowPriceTime()
                + "\n HighPriceTime : " + this.getHighPriceTime() + "\n OpeningPriceTime : "
                + this.getOpeningPriceTime() 
                + "]";
        return recapString;
    }

    /**
     * Updates this summary with values from open interest.
     */
    public void updateOpenInterest(int openInterest)
    {
        setOpenInterest(openInterest);
    }

    /**
     * Gets product type value for this product.
     * 
     * @return product type value
     */
    public short getProductType()
    {
        return editor.get(_productType, productType);
    }

    /**
     * Sets product type value.
     * 
     * @param newType product type value
     */
    protected void setProductType(short newType)
    {
        editor.set(_productType, newType, productType);
    }

    /**
     * Gets option type of this product.
     * 
     * @see Option#getOptionType
     */
    public OptionType getOptionType()
    {
        return (OptionType) editor.get(_optionType, optionType);
    }

    /**
     * Sets option type code.
     * 
     * @param newType new type code
     */
    protected void setOptionType(OptionType newType)
    {
        editor.set(_optionType, newType, optionType);
    }

    /**
     * Gets product type value for this product.
     * 
     * @return product type value
     */
    public int getUnderlyingCategoryType()
    {
        return editor.get(_underlyingCategory, underlyingCategory);
    }

    /**
     * Sets product type value.
     * 
     * @param newType product type value
     */
    protected void setUnderlyingCategoryType(int categoryType)
    {
        editor.set(_underlyingCategory, categoryType, underlyingCategory);
    }
    
    /** Returns change direction based on the passed value.
     * @param amount
     * @return char change direction
     */
    private char getDirection(PriceSqlType amount)
    {
        PriceSqlType zeroPrice= new PriceSqlType(0.0);
 
        if((!amount.isNoPrice() && amount.isValuedPrice()) && amount.greaterThan(zeroPrice))
        {
            return '+';
        }
        else if((!amount.isNoPrice() && amount.isValuedPrice()) && amount.lessThan(zeroPrice))
        {
            return '-';
        }
        else if((!amount.isNoPrice() && amount.isValuedPrice()) && amount.equals(zeroPrice))
        {
            return '0';
        }
        return ' ';
    }
    
    /**
     * Create a recap struct.
     */
    public RecapStructV5 updateRecapStructWithLastSale(RecapStructV5 recapStructV5)
    {
    	recapStructV5.aRecapStruct.lastSalePrice = getLastSalePrice().toStruct();
    	recapStructV5.aRecapStruct.lastSaleVolume = getLastSaleVolume();
    	recapStructV5.aRecapStruct.totalVolume = getTotalVolume();
    	recapStructV5.aRecapStruct.tradeTime = DateWrapper.convertToTime(getTradeTime());
    	recapStructV5.aRecapStruct.tickDirection = getTickDirection();
    	recapStructV5.aRecapStruct.tick = getTickAmount().toStruct();
    	recapStructV5.aRecapStruct.netChange = getNetChange().toStruct();
    	recapStructV5.aRecapStruct.netChangeDirection = getNetChangeDirection();
    	recapStructV5.aRecapStruct.highPrice = getHighPrice().toStruct();
    	recapStructV5.aRecapStruct.lowPrice = getLowPrice().toStruct();
    	recapStructV5.aRecapStruct.openPrice = getOpenPrice().toStruct();
        return	recapStructV5;
    }
    
    /**
     * return MarketDataReportProcessorHome instance
     * @return instance of MarketDataReportProcessorHome 
     */
    public MarketDataReportProcessorHome getMarketDataReportProcessorHome()
    {
        if (marketDataReportProcessorHome == null)
        {
            try
            {
                marketDataReportProcessorHome = (MarketDataReportProcessorHome) HomeFactory.getInstance().findHome(MarketDataReportProcessorHome.HOME_NAME);
            }
            catch (CBOELoggableException ex)
            {
                throw new FatalFoundationFrameworkException(ex, "Unable to get market data report processor home");
            }
        }
        return marketDataReportProcessorHome;
    }
    
    /**
     * Confirms whether the LSP related details need to be updated or not 
     * @param prefix prefix which needs to be compare
     * @return true if suffix is not configured in xml otherwise false 
     */
    public boolean isEligibleForLSPUpdate(final String prefix)
    {
    	boolean isConfigured = false;
    	String suffix = prefix;
    	if(prefix == null)
    	{
    		suffix = NULL_SUFFIX_VALUE;
    	}
    	else if(prefix.trim().length() == 0)
    	{
    		suffix = EMPTY_SUFFIX_VALUE;
    	}
    	isConfigured = isConfiguredSuffixes(suffix);
    	// If Opening trade is the only trade of the day, in this specific case we do want to update the LSP.
    	if(isConfigured && isOpenTrade(prefix) && !getHasBeenTraded())
    	{
    		return true;
    	}
    	return !isConfigured;
    }
    
    /**
     * confirm is the suffix configured in XML for excluding 
     * @param suffix which needs to be check
     * @return true if suffix is configured in xml otherwise false 
     */
    private boolean isConfiguredSuffixes(final String suffix)
    {
    	boolean result = false;
    	final String[] excludedTickerSuffixArray = getMarketDataReportProcessorHome().getExcludedTickerSuffix(sessionName);
    	if(excludedTickerSuffixArray != null)
    	{
    		for(String excludedTickerSuffix : excludedTickerSuffixArray)
        	{
        		if(suffix.equalsIgnoreCase(excludedTickerSuffix))
        		{
        			result = true;
        		}
        	}
    	}
    	return result;
    }
}
