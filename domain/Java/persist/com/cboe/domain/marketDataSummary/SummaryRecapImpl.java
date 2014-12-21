package com.cboe.domain.marketDataSummary;

import com.cboe.interfaces.domain.marketData.*;
import com.cboe.interfaces.domain.TradeReport;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.persistenceService.*;
import com.cboe.idl.cmiMarketData.*;
import com.cboe.idl.cmiProduct.*;
import com.cboe.idl.trade.*;
import com.cboe.domain.util.*;
import com.cboe.interfaces.domain.Price;
import com.cboe.util.*;
import java.lang.reflect.*;
import java.util.*;

/**
 * A persistent implementation of <code>Recap</code>.
 *
 * @author Ravi Rade
 */
public class SummaryRecapImpl extends PersistentBObject implements Recap
{
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
	 * Amount price changed between most recent trade and previous
	 * trade.
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
	 * Prefix string for recap.  This needs to be broken into
	 * separate news and status indicators.
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
     * Volume traded at the opening price.  Volume will include all trades done while open price, low price,
     * and high price are equal.
     */
    private int openPriceVolume;
	/**
	 * Price at which product closed.  During a trading day, value
	 * will be closing price of previous trading day.
	 */
	private PriceSqlType closePrice;
	/**
	 * Indicator set to true if trades have been received during
	 * the current day.
	 */
	private boolean hasBeenTraded;
	/**
     * Zero as default value to avoid null pointer exceptions on conversions
     */
	private static final PriceSqlType zero = new PriceSqlType(0.0);
    /**
     * Cached reference to the history home.
     */
    private static MarketDataHistoryHome marketDataHistoryHome;
    
    /**
     * prev. trading day's closing price
     */
    private PriceSqlType yesterdaysClosePrice;
    /**
     * prev. trading day's closing price suffix
     */
    private String yesterdaysClosePriceSuffix;
    /**
     * today's recap suffix
     */
    private String recapSuffix;
	

	/*
	 * JavaGrinder variables
	 */
	static Field _lastSalePrice;
	static Field _lastSaleVolume;
	static Field _lastSalePriceVolume;
	static Field _tradeTime;
	static Field _totalVolume;
	static Field _recapPrefix;
	static Field _askTime;
	static Field _askSize;
	static Field _askPrice;
	static Field _bidTime;
	static Field _bidSize;
	static Field _bidPrice;
	static Field _netChange;
	static Field _tickDirection;
	static Field _bidDirection;
	static Field _netChangeDirection;
	static Field _tickAmount;
	static Field _lowPrice;
	static Field _lowPriceVolume;
	static Field _highPrice;
	static Field _highPriceVolume;
	static Field _openPrice;
	static Field _openPriceVolume;
	static Field _closePrice;
	static Field _hasBeenTraded;
	static Field _OTCInd;
	static Vector classDescriptor;
    static Field _yesterdaysClosePrice;
    static Field _yesterdaysClosePriceSuffix;
    static Field _closePriceSuffix;
    
	/**
	* This static block will be regenerated if persistence is regenerated.
	*/
	static { /*NAME:fieldDefinition:*/
		try{
			_lastSalePrice = SummaryRecapImpl.class.getDeclaredField("lastSalePrice");
            _lastSalePrice.setAccessible(true);
			_tradeTime = SummaryRecapImpl.class.getDeclaredField("tradeTime");
            _tradeTime.setAccessible(true);
			_lastSaleVolume = SummaryRecapImpl.class.getDeclaredField("lastSaleVolume");
            _lastSaleVolume.setAccessible(true);
			_lastSalePriceVolume = SummaryRecapImpl.class.getDeclaredField("lastSalePriceVolume");
            _lastSalePriceVolume.setAccessible(true);
			_totalVolume = SummaryRecapImpl.class.getDeclaredField("totalVolume");
            _totalVolume.setAccessible(true);
			_tickDirection = SummaryRecapImpl.class.getDeclaredField("tickDirection");
            _tickDirection.setAccessible(true);
			_bidDirection = SummaryRecapImpl.class.getDeclaredField("bidDirection");
            _bidDirection.setAccessible(true);
			_netChangeDirection = SummaryRecapImpl.class.getDeclaredField("netChangeDirection");
            _netChangeDirection.setAccessible(true);
			_netChange = SummaryRecapImpl.class.getDeclaredField("netChange");
            _netChange.setAccessible(true);
			_bidPrice = SummaryRecapImpl.class.getDeclaredField("bidPrice");
            _bidPrice.setAccessible(true);
			_bidSize = SummaryRecapImpl.class.getDeclaredField("bidSize");
            _bidSize.setAccessible(true);
			_bidTime = SummaryRecapImpl.class.getDeclaredField("bidTime");
            _bidTime.setAccessible(true);
			_askPrice = SummaryRecapImpl.class.getDeclaredField("askPrice");
            _askPrice.setAccessible(true);
			_askSize = SummaryRecapImpl.class.getDeclaredField("askSize");
            _askSize.setAccessible(true);
			_askTime = SummaryRecapImpl.class.getDeclaredField("askTime");
            _askTime.setAccessible(true);
			_OTCInd = SummaryRecapImpl.class.getDeclaredField("OTCInd");
            _OTCInd.setAccessible(true);
			_recapPrefix = SummaryRecapImpl.class.getDeclaredField("recapPrefix");
            _recapPrefix.setAccessible(true);
			_tickAmount = SummaryRecapImpl.class.getDeclaredField("tickAmount");
            _tickAmount.setAccessible(true);
			_highPrice = SummaryRecapImpl.class.getDeclaredField("highPrice");
            _highPrice.setAccessible(true);
			_highPriceVolume = SummaryRecapImpl.class.getDeclaredField("highPriceVolume");
            _highPriceVolume.setAccessible(true);
			_lowPrice = SummaryRecapImpl.class.getDeclaredField("lowPrice");
            _lowPrice.setAccessible(true);
			_lowPriceVolume = SummaryRecapImpl.class.getDeclaredField("lowPriceVolume");
            _lowPriceVolume.setAccessible(true);
			_openPrice = SummaryRecapImpl.class.getDeclaredField("openPrice");
            _openPrice.setAccessible(true);
			_openPriceVolume = SummaryRecapImpl.class.getDeclaredField("openPriceVolume");
            _openPriceVolume.setAccessible(true);
			_closePrice = SummaryRecapImpl.class.getDeclaredField("closePrice");
            _closePrice.setAccessible(true);
			_hasBeenTraded = SummaryRecapImpl.class.getDeclaredField("hasBeenTraded");
            _hasBeenTraded.setAccessible(true);
            _yesterdaysClosePrice = SummaryRecapImpl.class.getDeclaredField("yesterdaysClosePrice");
            _yesterdaysClosePrice.setAccessible( true );
            _yesterdaysClosePriceSuffix = SummaryRecapImpl.class.getDeclaredField("yesterdaysClosePriceSuffix");
            _yesterdaysClosePriceSuffix.setAccessible( true );
            _closePriceSuffix = SummaryRecapImpl.class.getDeclaredField("recapSuffix");
            _closePriceSuffix.setAccessible(true);
		}
		catch (NoSuchFieldException ex) { System.out.println(ex); }
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
public SummaryRecapImpl()
{
	super();
}
/**
 * Calculates the quantity that should be used for this last sale based on summarized cancels.
 */
private int adjustLastSaleQuantity(List cancelSummary, MarketDataHistoryEntry lastSale)
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
 *
 * @author John Wickberg
 */
public void create()
{
    create(new NoPrice());
}

/**
 * Performs instance initialization.
 *
 * @author John Wickberg
 */
public void create(Price closePrice)
{
	setAskPrice(zero);
	setBidPrice(zero);
	setRecapPrefix("");
	setBidDirection(' ');
        setTickDirection(' ');
	setOTC(false);
    resetLastSaleValues();
    setClosePrice(new PriceSqlType(closePrice));
}
/**
 * Getter for ask price.
 */
private PriceSqlType getAskPrice()
{
	return (PriceSqlType) editor.get(_askPrice, askPrice);
}
/**
 * Getter for ask size.
 */
private int getAskSize()
{
	return (int) editor.get(_askSize, askSize);
}
/**
 * Getter for ask time.
 */
private long getAskTime()
{
	return (long) editor.get(_askTime, askTime);
}
/**
 * Getter for bid price.
 */
private PriceSqlType getBidPrice()
{
	return (PriceSqlType) editor.get(_bidPrice, bidPrice);
}
/**
 * Getter for bid size.
 */
private int getBidSize()
{
	return (int) editor.get(_bidSize, bidSize);
}
/**
 * Getter for bid time.
 */
private long getBidTime()
{
	return (long) editor.get(_bidTime, bidTime);
}
/**
 * Getter for close price.
 */
protected PriceSqlType getClosePrice()
{
	return (PriceSqlType) editor.get(_closePrice, closePrice);
}
/**
 * Getter for has been traded indicator.
 */
private boolean getHasBeenTraded()
{
	return (boolean) editor.get(_hasBeenTraded, hasBeenTraded);
}
/**
 * Determines if last sale prefix is a cancel.
 */
private boolean isCancelPrefix(String lastSalePrefix)
{
    if(lastSalePrefix == null)
    {
        return false;
    }
    return lastSalePrefix.equals(TradeReport.CANCELOPEN_PREFIX) || lastSalePrefix.equals(TradeReport.CANCEL_PREFIX);
}

/**
 * Determines if last sale prefix is a block trade or EFP or GWAP.
 */
private boolean externalTradePrefix(String prefix)
{
    if(prefix == null)
    {
        return false;
    }
    return prefix.equals(TradeReport.BLOCK_TRADE_PREFIX) || 
           prefix.equals(TradeReport.EXCHANGE_FOR_PHYSICAL_PREFIX) ||
           prefix.equals(TradeReport.GWAP_TRADE_PREFIX);
}

/**
 * Getter for OTC indicator
 */
private boolean isOTC()
{
	return editor.get(_OTCInd, OTCInd );
}
/**
 * Getter for high price.
 */
protected PriceSqlType getHighPrice()
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
public Price getLastSalePrice()
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
protected int getLastSalePriceVolume()
{
	return (int) editor.get(_lastSalePriceVolume, lastSalePriceVolume);
}
/**
 * Getter for low price
 */
protected PriceSqlType getLowPrice()
{
	return (PriceSqlType) editor.get(_lowPrice, lowPrice);
}
/**
 * Getter for low price volume.
 */
protected int getLowPriceVolume()
{
	return (int) editor.get(_lowPriceVolume, lowPriceVolume);
}
/**
 * Gets the market data history home.
 */
private MarketDataHistoryHome getMarketDataHistoryHome()
{
    if (marketDataHistoryHome == null)
    {
        try
        {
            marketDataHistoryHome = (MarketDataHistoryHome) HomeFactory.getInstance().findHome(MarketDataHistoryHome.HOME_NAME);
        }
        catch (Exception e)
        {
            Log.exception(this, "Unable to get market data history home", e);
        }
    }
    return marketDataHistoryHome;
}
/**
 * Getter for net change.
 */
protected PriceSqlType getNetChange()
{
	return (PriceSqlType) editor.get(_netChange, netChange);
}
/**
 * Getter for open price.
 */
protected PriceSqlType getOpenPrice()
{
	return (PriceSqlType) editor.get(_openPrice, openPrice);
}
/**
 * Getter for open price volume.
 */
protected int getOpenPriceVolume()
{
	return editor.get(_openPriceVolume, openPriceVolume);
}
/**
 * Getter for recap prefix.
 */
private String getRecapPrefix()
{
	return (String) editor.get(_recapPrefix, recapPrefix);
}
/**
 * Getter for tick amount.
 */
protected PriceSqlType getTickAmount()
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
private char getBidDirection()
{
	return (char) editor.get(_bidDirection, bidDirection);
}
/**
 * Getter for net change direction.
 */
private char getNetChangeDirection()
{
	return (char) editor.get(_netChangeDirection, netChangeDirection);
}
/**
 * Getter for total volume.
 */
protected int getTotalVolume()
{
	return (int) editor.get(_totalVolume, totalVolume);
}
/**
 * Getter for trade time..
 */
private long getTradeTime()
{
	return (long) editor.get(_tradeTime, tradeTime);
}

/**
 * Describe how this class relates to the relational database.
 */
private void initDescriptor()
{
	synchronized (SummaryRecapImpl.class)
	{
		if (classDescriptor != null)
			return;
		Vector tempDescriptor = getSuperDescriptor();
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
		tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("yesterdays_close_price", _yesterdaysClosePrice));
		tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("close_price_suffix", _closePriceSuffix));
		tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("yesterdays_close_price_suffix", _yesterdaysClosePriceSuffix));
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
		initDescriptor();
	result.setTableName("recap");
	result.setClassDescription(classDescriptor);
	return result;
}
/**
 * Recalculates recap values by reading history.
 */
private void recalcRecapFromHistory(TickerStruct currentCancel)
{
    Log.debug(this, "Recalculating recap from market data history for product: " + currentCancel.productKeys.productKey);
    MarketDataHistoryEntry[] lastSales = getMarketDataHistoryHome().findCurrentDayLastSales(currentCancel.sessionName, currentCancel.productKeys.productKey);
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
                    updateLastSaleValues(currentCancel.salePrefix, salePrice, adjustedQuantity);
                }
            }
        }
    }
    else
    {
        Log.alarm(this, "Unable to recalculate recap for product(" + currentCancel.productKeys.productKey + "), no history entries found");
    }
}
/**
 * Resets all values used in last sale processing of tickers.
 */
private void resetLastSaleValues() {
    setTotalVolume(0);
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
	setClosePrice(zero);
    setHasBeenTraded(false);
}
/**
 * Setter for ask price.
 */
private void setAskPrice(PriceSqlType aValue)
{
	editor.set(_askPrice, aValue, askPrice);
}
/**
 * Setter for ask size.
 */
private void setAskSize(int aValue)
{
	editor.set(_askSize, aValue, askSize);
}
/**
 * Setter for ask time.
 */
private void setAskTime(long aValue)
{
	editor.set(_askTime, aValue, askTime);
}
/**
 * Setter for bid price.
 */
private void setBidPrice(PriceSqlType aValue)
{
	editor.set(_bidPrice, aValue, bidPrice);
}
/**
 * Setter for bid size.
 */
private void setBidSize(int aValue)
{
	editor.set(_bidSize, aValue, bidSize);
}
/**
 * Setter for bid time.
 */
private void setBidTime(long aValue)
{
	editor.set(_bidTime, aValue, bidTime);
}
/**
 * Setter for close price.
 */
private void setClosePrice(PriceSqlType aValue)
{
	editor.set(_closePrice, aValue, closePrice);
}
/**
 * Setter for has been traded indicator.
 */
private void setHasBeenTraded(boolean aValue)
{
	editor.set(_hasBeenTraded, aValue, hasBeenTraded);
}
/**
 * Setter for has OTC indicator
 */
private void setOTC(boolean aValue)
{
	editor.set(_OTCInd, aValue, OTCInd);
}
/**
 * Setter for high price.
 */
private void setHighPrice(PriceSqlType aValue)
{
	editor.set(_highPrice, aValue, highPrice);
}
/**
 * Setter for high price volume.
 */
private void setHighPriceVolume(int aValue)
{
	editor.set(_highPriceVolume, aValue, highPriceVolume);
}
/**
 * Setter for last sale price.
 */
private void setLastSalePrice(PriceSqlType aValue)
{
	editor.set(_lastSalePrice, aValue, lastSalePrice);
}
/**
 * Setter for last sale volume.
 */
private void setLastSaleVolume(int aValue)
{
	editor.set(_lastSaleVolume, aValue, lastSaleVolume);
}
/**
 * Setter for last sale volume.
 */
private void setLastSalePriceVolume(int aValue)
{
	editor.set(_lastSalePriceVolume, aValue, lastSalePriceVolume);
}
/**
 * Setter for low price
 */
private void setLowPrice(PriceSqlType aValue)
{
	editor.set(_lowPrice, aValue, lowPrice);
}
/**
 * Setter for low price volume
 */
private void setLowPriceVolume(int aValue)
{
	editor.set(_lowPriceVolume, aValue, lowPriceVolume);
}
/**
 * Setter for net change.
 */
private void setNetChange(PriceSqlType aValue)
{
	editor.set(_netChange, aValue, netChange);
}
/**
 * Setter for open price.
 */
private void setOpenPrice(PriceSqlType aValue)
{
	editor.set(_openPrice, aValue, openPrice);
}
/**
 * Setter for open price volume.
 */
private void setOpenPriceVolume(int aValue)
{
	editor.set(_openPriceVolume, aValue, openPriceVolume);
}
/**
 * Setter for recap prefix.
 */
private void setRecapPrefix(String aValue)
{
	editor.set(_recapPrefix, aValue, recapPrefix);
}
/**
 * Setter for tick amount.
 */
private void setTickAmount(PriceSqlType aValue)
{
	editor.set(_tickAmount, aValue, tickAmount);
}
/**
 * Setter for tick direction.
 */
private void setTickDirection(char aValue)
{
	editor.set(_tickDirection, aValue, tickDirection);
}
/**
 * Setter for bid direction.
 */
private void setBidDirection(char aValue)
{
	editor.set(_bidDirection, aValue, bidDirection);
}
/**
 * Setter for net change direction.
 */
private void setNetChangeDirection(char aValue)
{
	editor.set(_netChangeDirection, aValue, netChangeDirection);
}
/**
 * Setter for total volume.
 */
private void setTotalVolume(int aValue)
{
	editor.set(_totalVolume, aValue, totalVolume);
}
/**
 * Setter for trade time.
 */
private void setTradeTime(long aValue)
{
	editor.set(_tradeTime, aValue, tradeTime);
}
/**
 * Summaries price/quantity for all cancel entries.
 */
private List summarizeLastSaleCancels(TickerStruct currentCancel, MarketDataHistoryEntry[] lastSales)
{
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
                if (index > 0)
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
 * Converts this recap to a CORBA struct.
 */
public RecapStruct toStruct()
{
	// a kludge to get around the fact that
	// MarketDataStructBuilder.buildRecapStruct() wants a productKeysStruct
	// MarkerDataImpl will override the values anyway.
	ProductKeysStruct productKeys = new ProductKeysStruct();
	RecapStruct struct = MarketDataStructBuilder.buildRecapStruct(productKeys);

	// now to override with the data we have
	struct.lastSalePrice = getLastSalePrice().toStruct();
	struct.tradeTime = DateWrapper.convertToTime(getTradeTime());
	struct.lastSaleVolume = getLastSaleVolume();
	struct.totalVolume = getTotalVolume();
	struct.tickDirection = getTickDirection();
	struct.bidDirection = getBidDirection();
	struct.netChangeDirection = getNetChangeDirection();
	struct.netChange = getNetChange().toStruct();
	struct.bidPrice = getBidPrice().toStruct();
	struct.bidSize = getBidSize();
	struct.bidTime = DateWrapper.convertToTime(getBidTime());
	struct.askPrice = getAskPrice().toStruct();
	struct.askSize = getAskSize();
	struct.askTime = DateWrapper.convertToTime(getAskTime());
	struct.recapPrefix = getRecapPrefix();
	struct.tick = getTickAmount().toStruct();
	struct.lowPrice = getLowPrice().toStruct();
	struct.highPrice = getHighPrice().toStruct();
	struct.openPrice = getOpenPrice().toStruct();
	struct.closePrice = getClosePrice().toStruct();
	struct.isOTC = isOTC();
	// previousClosePrice - whatever the structbuilder does

	return struct;
}
/**
 * Updates this recap from a CORBA struct.
 *
 * @author John Wickberg
 */
public void update(RecapStruct recap)
{
    // Update only those fields that have been specified.
    // since tips will send only those fields that have changed.
    // NOTE: Tips sends composite information i.e. if bid price is not given
    // then bid size and bid time is also not given.

    // Last sale info not given
    if (!PriceFactory.create(recap.lastSalePrice).isNoPrice())
    {
	    setTickDirection(recap.tickDirection);
	    setTradeTime(DateWrapper.convertToMillis(recap.tradeTime));
	    setLastSalePrice(new PriceSqlType(recap.lastSalePrice));
        setTotalVolume(recap.totalVolume);
	    setLastSaleVolume(recap.lastSaleVolume);
	    setNetChange(new PriceSqlType(recap.netChange));
	    setNetChangeDirection(recap.netChangeDirection);
    }

    // Bid info changed
    if (!PriceFactory.create(recap.bidPrice).isNoPrice())
    {
	    setBidPrice(new PriceSqlType(recap.bidPrice));
    	setBidSize(recap.bidSize);
	    setBidTime(DateWrapper.convertToMillis(recap.bidTime));
    	setBidDirection(recap.bidDirection);
    }

    // Ask info changed
    if (!PriceFactory.create(recap.askPrice).isNoPrice())
    {
	    setAskPrice(new PriceSqlType(recap.askPrice));
    	setAskSize(recap.askSize);
	    setAskTime(DateWrapper.convertToMillis(recap.askTime));
    }

    // Always update these
	setRecapPrefix(recap.recapPrefix);
	setOTC(recap.isOTC);
}

/**
 * update the recap for summary
 */
public void updateForMarketDataSummary(RecapStruct recap) {
    setAskPrice( new PriceSqlType(recap.askPrice));
    setAskSize(recap.askSize);
    setAskTime(DateWrapper.convertToMillis(recap.askTime));
    setBidDirection(recap.bidDirection);
    setBidPrice( new PriceSqlType(recap.bidPrice));
    setBidSize(recap.bidSize);
    setBidTime(DateWrapper.convertToMillis(recap.bidTime));
    setClosePrice( new PriceSqlType(recap.closePrice));
    setHasBeenTraded( recap.totalVolume != 0 );
    setHighPrice( new PriceSqlType(recap.highPrice));
    setLastSalePrice( new PriceSqlType(recap.lastSalePrice));
    setLastSaleVolume( recap.lastSaleVolume);
    setLowPrice( new PriceSqlType( recap.lowPrice));
    setNetChange( new PriceSqlType( recap.netChange));
    setNetChangeDirection( recap.netChangeDirection );
    setOpenPrice( new PriceSqlType( recap.openPrice));
    setOTC( recap.isOTC);
    setRecapPrefix( recap.recapPrefix);
    setTickAmount( new PriceSqlType( recap.tick));
    setTickDirection( recap.tickDirection );
    setTotalVolume( recap.totalVolume );
    setTradeTime(DateWrapper.convertToMillis(recap.tradeTime));

}

/**
 * update the recap for summary
 */
public void updateForMarketDataSummary(RecapStruct recap, String todaysClosingSuffix, String lastDaysClosingSuffix) {
    updateForMarketDataSummary(recap);
    setYesterdaysClosePrice(new PriceSqlType( recap.previousClosePrice));
    setRecapSuffix( todaysClosingSuffix);
    setYesterdaysClosePriceSuffix( lastDaysClosingSuffix );
}

/**
 * Determines if ticker is for cancel or regular.
 */
public void update(TickerStruct ticker) {
    if (isCancelPrefix(ticker.salePrefix)) {
        updateCancelTicker(ticker);
    }
    else {
        updateLastSaleTicker(ticker);
    }
    // set recap prefix so that end users can know why recap changed
    setRecapPrefix(ticker.salePrefix);
}
/**
 * Updates this summary with values from a cancel ticker (the result of a trade bust).
 */
private void updateCancelTicker(TickerStruct cancel)
{
    boolean mustRecalcValues = false;
    PriceSqlType cancelPrice = new PriceSqlType(cancel.lastSalePrice);
    setTotalVolume(getTotalVolume() - cancel.lastSaleVolume);
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

    if (cancel.salePrefix.equals(TradeReport.CANCELOPEN_PREFIX)) {
        if (getOpenPriceVolume() > cancel.lastSaleVolume)
        {
            setOpenPriceVolume(getOpenPriceVolume() - cancel.lastSaleVolume);
        }
        else
        {
            mustRecalcValues = true;
        }
    }

    if (mustRecalcValues)
    {
        recalcRecapFromHistory(cancel);
    }
}
/**
 * Updates this summary with values from last sale ticker.
 */
private void updateLastSaleTicker(TickerStruct lastSale)
{
	PriceSqlType price = new PriceSqlType(lastSale.lastSalePrice);
    updateLastSaleValues(lastSale.salePrefix, price, lastSale.lastSaleVolume);
	setTradeTime(System.currentTimeMillis());
}
/**
 * Updates the last sale values.
 */
private void updateLastSaleValues(String prefix, PriceSqlType salePrice, int saleVolume)
{
	if (getHasBeenTraded())
	{
		setTickAmount(new PriceSqlType(salePrice.toDouble() - getLastSalePrice().toDouble()));
	}
	else
	{
		setTickAmount(new PriceSqlType(0.0));
	}

    boolean externalTrade = externalTradePrefix(prefix);
    if(!externalTrade || (externalTrade && !getHasBeenTraded()))
    {
        if (getLastSalePrice().equals(salePrice))
        {
            setLastSalePriceVolume(getLastSalePriceVolume() + saleVolume);
        }
        else
        {
            setLastSalePriceVolume(saleVolume);
        }

        setLastSalePrice(salePrice);
        setLastSaleVolume(saleVolume);
    }
	setTotalVolume(saleVolume + getTotalVolume());
	if (getHasBeenTraded() && !externalTrade)
	{
		if (salePrice.lessThan(getLowPrice()))
		{
			setLowPrice(salePrice);
            setLowPriceVolume(saleVolume);
		}
        else if (salePrice.equals(getLowPrice()))
        {
            setLowPriceVolume(getLowPriceVolume() + saleVolume);
        }

        if (salePrice.greaterThan(getHighPrice()))
		{
			setHighPrice(salePrice);
            setLowPriceVolume(saleVolume);
		}
        else if (salePrice.equals(getHighPrice()))
        {
            setHighPriceVolume(getHighPriceVolume() + saleVolume);
        }

        // update open price volume with all trades until a trade has been done at a different price (low or high
        // will no longer equal the open)
        if (salePrice.equals(getOpenPrice()) && getOpenPrice().equals(getLowPrice()) && getOpenPrice().equals(getHighPrice()))
        {
            setOpenPriceVolume(getOpenPriceVolume() + saleVolume);
        }
	}
	else if(!getHasBeenTraded())
	{
		setLowPrice(salePrice);
        setLowPriceVolume(saleVolume);
		setHighPrice(salePrice);
        setHighPriceVolume(saleVolume);
		// fixme: open and close will need to be set another way.
		setOpenPrice(salePrice);
        setOpenPriceVolume(saleVolume);
        if (getClosePrice().isNoPrice()) {
    		setClosePrice(salePrice);
        }
	}
	setNetChange(new PriceSqlType(salePrice.toDouble() - getClosePrice().toDouble()));
	setHasBeenTraded(true);
}
/**
 * Added for ManualPriceReporting
 * @author Cognizant Technology Solutions.
 */
 public int getNumberOfTrades()
{
    return 0;
}

	 /**
	  * Getter for yesterday's close price.
	  */
	 protected PriceSqlType getYesterdaysClosePrice()
	 {
		 return (PriceSqlType) editor.get(_yesterdaysClosePrice, yesterdaysClosePrice);
	 }
	 
	 /**
	  * setter for close yesterday's price.
	  */
	 private void setYesterdaysClosePrice(PriceSqlType aValue)
	 {
		 editor.set(_yesterdaysClosePrice, aValue, yesterdaysClosePrice);
	 }
	 
	 /**
	  * Getter for yesterday's close price suffix.
	  */
	 protected String getYesterdaysClosePriceSuffix()
	 {
		 return (String) editor.get(_yesterdaysClosePriceSuffix, yesterdaysClosePriceSuffix);
	 }
	 
	 /**
	  * setter for yesterday's close price suffix.
	  */
	 private void setYesterdaysClosePriceSuffix(String lastDaysClosePriceSuffix)
	 {
		 editor.set(_yesterdaysClosePriceSuffix, lastDaysClosePriceSuffix, yesterdaysClosePriceSuffix);
	 }
	
	 /**
	  * Getter for recap suffix
	  */
	 protected String getRecapSuffix()
	 {
		 return (String)editor.get(_closePriceSuffix, recapSuffix);
	 }
	
	 /**
	  * Setter for today's close price suffux.
	  */
	 private void setRecapSuffix(String closingSuffix)
	 {
		 editor.set(_closePriceSuffix, closingSuffix, recapSuffix);
	 }
	 	 
}
