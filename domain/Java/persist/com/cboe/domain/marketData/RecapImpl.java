package com.cboe.domain.marketData;

import java.util.LinkedList;
import java.util.List;

import com.cboe.domain.util.DateWrapper;
import com.cboe.domain.util.MarketDataStructBuilder;
import com.cboe.domain.util.NoPrice;
import com.cboe.domain.util.PriceFactory;
import com.cboe.domain.util.PriceSqlType;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;
import com.cboe.idl.cmiMarketData.RecapStruct;
import com.cboe.idl.cmiMarketData.TickerStruct;
import com.cboe.idl.cmiProduct.ProductKeysStruct;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.HomeFactory;
import com.cboe.infrastructureServices.foundationFramework.exceptionHandling.CBOELoggableException;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.businessServices.MarketDataReportService;
import com.cboe.interfaces.businessServices.MarketDataReportServiceHome;
import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.domain.TradeReport;
import com.cboe.interfaces.domain.marketData.MarketDataHistoryEntry;
import com.cboe.interfaces.domain.marketData.MarketDataHistoryHome;
import com.cboe.interfaces.domain.marketData.Recap;
import com.cboe.idl.cmiMarketData.RecapStructV5;

/**
 * A persistent implementation of <code>Recap</code>.
 *
 * @author John Wickberg
 */
public class RecapImpl extends BObject implements Recap
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
    
    private static boolean recalcRecapFromMDHistory;    
    /**
     * Flag for calculation of recap using MDRS history .
     */
    private static boolean recalcRecapFromMDRService; 

     /**
     * Name of property that holds flag for calculation of recap using MDRS history .
     */
    private static final String RECALC_RECAP_FROM_MDRS_SERVICE = "recalcRecapFromMDRService";

    /**
     * Cached reference of MarketDataReportService
     */
    private MarketDataReportService marketDataReportService;
    /**
     * variable used for manual price reporting functionality
     */
    private int numberOfTrades; 

    static
    {
        String temp = System.getProperty("recalcRecapFromMDHistory"); 
        
        if( temp != null && temp.equals("false"))
        {
            recalcRecapFromMDHistory = false;
        }
        else
        {    
            recalcRecapFromMDHistory = true;
        }
        
        Log.information("RecapImpl::recalcRecapFromMDHistory: " + recalcRecapFromMDHistory);
    }
    
    static
    {
     
        String recalcRecapFromMDRServiceFlag = System.getProperty(RECALC_RECAP_FROM_MDRS_SERVICE); 
        
        if( recalcRecapFromMDRServiceFlag != null && recalcRecapFromMDRServiceFlag.equals("false"))
        {
            recalcRecapFromMDRService = false;
        }
        else
        {    
            recalcRecapFromMDRService = true;
        }
        
        Log.information("RecapImpl::recalcRecapFromMDRService: " + recalcRecapFromMDRService);
 
 
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
public RecapImpl()
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
	return askPrice;
}
/**
 * Getter for ask size.
 */
private int getAskSize()
{
	return askSize;
}
/**
 * Getter for ask time.
 */
private long getAskTime()
{
	return askTime;
}
/**
 * Getter for bid price.
 */
private PriceSqlType getBidPrice()
{
	return bidPrice;
}
/**
 * Getter for bid size.
 */
private int getBidSize()
{
	return bidSize;
}
/**
 * Getter for bid time.
 */
private long getBidTime()
{
	return bidTime;
}
/**
 * Getter for close price.
 */
protected PriceSqlType getClosePrice()
{
	return closePrice;
}
/**
 * Getter for has been traded indicator.
 */
private boolean getHasBeenTraded()
{
	return hasBeenTraded;
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
    return lastSalePrefix.equals(TradeReport.CANCELOPEN_PREFIX) || lastSalePrefix.equals(TradeReport.CANCEL_PREFIX)
    || lastSalePrefix.equals(TradeReport.CANCELLAST_PREFIX) || lastSalePrefix.equals(TradeReport.CANCELONLY_PREFIX);
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
	return OTCInd;
}
/**
 * Getter for high price.
 */
protected PriceSqlType getHighPrice()
{
	return highPrice;
}
/**
 * Getter for high price volume.
 */
public synchronized int getHighPriceVolume()
{
	return highPriceVolume;
}
/**
 * Getter for last sale price.
 */
public synchronized Price getLastSalePrice()
{
	return lastSalePrice;
}
/**
 * Getter for last sale volume.
 */
public synchronized int getLastSaleVolume()
{
	return lastSaleVolume;
}
/**
 * Getter for last sale price volume.
 */
protected int getLastSalePriceVolume()
{
	return lastSalePriceVolume;
}
/**
 * Getter for low price
 */
protected PriceSqlType getLowPrice()
{
	return lowPrice;
}
/**
 * Getter for low price volume.
 */
protected int getLowPriceVolume()
{
	return lowPriceVolume;
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
	return netChange;
}
/**
 * Getter for open price.
 */
protected PriceSqlType getOpenPrice()
{
	return openPrice;
}
/**
 * Getter for open price volume.
 */
protected int getOpenPriceVolume()
{
	return openPriceVolume;
}
/**
 * Getter for recap prefix.
 */
private String getRecapPrefix()
{
	return recapPrefix;
}
/**
 * Getter for tick amount.
 */
protected PriceSqlType getTickAmount()
{
	return tickAmount;
}
/**
 * Getter for tick direction.
 */
public synchronized char getTickDirection()
{
	return tickDirection;
}
/**
 * Getter for bid direction.
 */
private char getBidDirection()
{
	return bidDirection;
}
/**
 * Getter for net change direction.
 */
private char getNetChangeDirection()
{
	return netChangeDirection;
}
/**
 * Getter for total volume.
 */
protected int getTotalVolume()
{
	return totalVolume;
}
/**
 * Getter for trade time..
 */
private long getTradeTime()
{
	return tradeTime;
}

/**
 * Recalculates recap values by reading history.
 */
private void recalcRecapFromHistory(TickerStruct currentCancel)
{
    Log.information(this, "Start Recalculating recap from market data history for product: " + currentCancel.productKeys.productKey);
    MarketDataHistoryEntry[] lastSales = getMarketDataHistoryHome().findCurrentDayLastSales(currentCancel.sessionName, currentCancel.productKeys.productKey);
    Log.information(this, "Done findCurrentDayLastSales for recalculating recap from market data history for product: " + currentCancel.productKeys.productKey);    
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
        Log.information(this, "Done recalculating recap from market data history for product: " + currentCancel.productKeys.productKey);        
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
    //Added for Manual Price Reporting
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
	setClosePrice(zero);
    setHasBeenTraded(false);
}
/**
 * Setter for ask price.
 */
private void setAskPrice(PriceSqlType aValue)
{
	askPrice= aValue;
}
/**
 * Setter for ask size.
 */
private void setAskSize(int aValue)
{
	askSize= aValue;
}
/**
 * Setter for ask time.
 */
private void setAskTime(long aValue)
{
	askTime= aValue;
}
/**
 * Setter for bid price.
 */
private void setBidPrice(PriceSqlType aValue)
{
	bidPrice= aValue;
}
/**
 * Setter for bid size.
 */
private void setBidSize(int aValue)
{
	bidSize= aValue;
}
/**
 * Setter for bid time.
 */
private void setBidTime(long aValue)
{
	bidTime= aValue;
}
/**
 * Setter for close price.
 */
private void setClosePrice(PriceSqlType aValue)
{
	closePrice= aValue;
}
/**
 * Setter for has been traded indicator.
 */
private void setHasBeenTraded(boolean aValue)
{
	hasBeenTraded= aValue;
}
/**
 * Setter for has OTC indicator
 */
private void setOTC(boolean aValue)
{
	OTCInd= aValue;
}
/**
 * Setter for high price.
 */
private void setHighPrice(PriceSqlType aValue)
{
	highPrice= aValue;
}
/**
 * Setter for high price volume.
 */
private void setHighPriceVolume(int aValue)
{
	highPriceVolume= aValue;
}
/**
 * Setter for last sale price.
 */
private void setLastSalePrice(PriceSqlType aValue)
{
	lastSalePrice= aValue;
}
/**
 * Setter for last sale volume.
 */
private void setLastSaleVolume(int aValue)
{
	lastSaleVolume= aValue;
}
/**
 * Setter for last sale volume.
 */
private void setLastSalePriceVolume(int aValue)
{
	lastSalePriceVolume= aValue;
}
/**
 * Setter for low price
 */
private void setLowPrice(PriceSqlType aValue)
{
	lowPrice= aValue;
}
/**
 * Setter for low price volume
 */
private void setLowPriceVolume(int aValue)
{
	lowPriceVolume= aValue;
}
/**
 * Setter for net change.
 */
private void setNetChange(PriceSqlType aValue)
{
	netChange= aValue;
}
/**
 * Setter for open price.
 */
private void setOpenPrice(PriceSqlType aValue)
{
	openPrice= aValue;
}
/**
 * Setter for open price volume.
 */
private void setOpenPriceVolume(int aValue)
{
	openPriceVolume= aValue;
}
/**
 * Setter for recap prefix.
 */
private void setRecapPrefix(String aValue)
{
	recapPrefix= aValue;
}
/**
 * Setter for tick amount.
 */
private void setTickAmount(PriceSqlType aValue)
{
	tickAmount= aValue;
}
/**
 * Setter for tick direction.
 */
private void setTickDirection(char aValue)
{
	tickDirection= aValue;
}
/**
 * Setter for bid direction.
 */
private void setBidDirection(char aValue)
{
	bidDirection= aValue;
}
/**
 * Setter for net change direction.
 */
private void setNetChangeDirection(char aValue)
{
	netChangeDirection= aValue;
}
/**
 * Setter for total volume.
 */
private void setTotalVolume(int aValue)
{
	totalVolume= aValue;
}
/**
 * Setter for trade time.
 */
private void setTradeTime(long aValue)
{
	tradeTime= aValue;
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
public synchronized RecapStruct toStruct()
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
public synchronized void update(RecapStruct recap)
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
public synchronized void updateForMarketDataSummary(RecapStruct recap) {
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
 * Determines if ticker is for cancel or regular.
 */
public synchronized void update(TickerStruct ticker) {
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
    //Added for Manual Price Reporting.
    setNumberOfTrades(getNumberOfTrades()-1);
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

    if (mustRecalcValues && recalcRecapFromMDHistory)
    {
        recalcRecapFromHistory(cancel);
    }
    Log.information(this, "Before updateCancelTicker calling MDRS. mustRecalcValues: "+mustRecalcValues +" recalcRecapFromMDRService: "+recalcRecapFromMDRService+" recalcRecapFromMDHistory: "+recalcRecapFromMDHistory +" getLowPrice(): "+getLowPrice() +" getHighPrice(): "+getHighPrice()+" getLastSaleVolume(): "+getLastSaleVolume() );
    if (mustRecalcValues && recalcRecapFromMDRService)
    {
        recalcRecapFromMDRService(cancel);
        Log.information(this, "After updateCancelTicker called MDRS. mustRecalcValues: "+mustRecalcValues +" recalcRecapFromMDRService: "+recalcRecapFromMDRService+" recalcRecapFromMDHistory: "+recalcRecapFromMDHistory +" getLowPrice(): "+getLowPrice() +" getHighPrice(): "+getHighPrice()+" getLastSaleVolume(): "+getLastSaleVolume() );
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
    //Added for Manual Price Reporting.
    setNumberOfTrades(getNumberOfTrades()+1);
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
 * This method is used to get Number of Trades.
 * @return Number Of Trades.
 * @author Cognizant Technology Solutions.
 */
public int getNumberOfTrades() 
{
    return numberOfTrades;
}

/**
 * This method is used to set Number of Trades.
 * @param int
 * @author Cognizant Technology Solutions.
 */
public void setNumberOfTrades(int trades) 
{
    this.numberOfTrades = trades;
}


/**
 * Recalculates recap values by reading MDRS history.
 */
    private void recalcRecapFromMDRService(TickerStruct currentCancel) {
        RecapStructV5 recapStructV5;
        try {
            recapStructV5 = getMarketDataReportService().getLatestRecapStruct(currentCancel);
            updateLastSaleValues(recapStructV5);
        } catch (Exception e) {
            Log.exception(e);
            Log.alarm("Unable to Process recalculation of recap from MarketDataReportService " );
        }
    }

    /**
     * Gets reference to market data home.
     * 
     * @return MarketDataServiceHomeImpl
     * @author Cognizant Technology Solutions
     */
    protected MarketDataReportService getMarketDataReportService() {
        if (marketDataReportService == null) {
            try
            {
                MarketDataReportServiceHome marketDataReportServiceHome = (MarketDataReportServiceHome) HomeFactory.getInstance().findHome(MarketDataReportServiceHome.HOME_NAME);
                marketDataReportService = marketDataReportServiceHome.find();
                if (Log.isDebugOn()) {
                    Log.debug("MarketDataReportService Created Successfully."
                            + marketDataReportService);
                }
            } catch (CBOELoggableException e) {
                Log.exception(this, e);
            }
        }
        return marketDataReportService;
    }

    /**
     * Updates the last sale values.
     */
    public void updateLastSaleValues(RecapStructV5 recapStructV5)

    {
        PriceSqlType tickAmount = new PriceSqlType(recapStructV5.aRecapStruct.tick);
        if(tickAmount.isNoPrice()){
            tickAmount = new PriceSqlType(0.0);
        }
        setTickAmount(tickAmount);

        setLastSalePriceVolume(recapStructV5.lastSalePriceVolume);

        PriceSqlType lastSalePrice = new PriceSqlType(recapStructV5.aRecapStruct.lastSalePrice);
        if(lastSalePrice.isNoPrice()){
            lastSalePrice = new PriceSqlType(0.0);
        }
        setLastSalePrice(lastSalePrice);

        setLastSaleVolume(recapStructV5.aRecapStruct.lastSaleVolume);

        setTotalVolume(recapStructV5.aRecapStruct.totalVolume);

        PriceSqlType lowPrice = new PriceSqlType(recapStructV5.aRecapStruct.lowPrice);
        if(lowPrice.isNoPrice()){
            lowPrice = new PriceSqlType(0.0);
        }
        setLowPrice(lowPrice);

        setLowPriceVolume(recapStructV5.lowPriceVolume);

        PriceSqlType highPrice = new PriceSqlType(recapStructV5.aRecapStruct.highPrice);
        if(highPrice.isNoPrice()){
            highPrice = new PriceSqlType(0.0);
        }
        setHighPrice(highPrice);

        setHighPriceVolume(recapStructV5.highPriceVolume);

        PriceSqlType openPrice = new PriceSqlType(recapStructV5.aRecapStruct.openPrice);
        if(openPrice.isNoPrice()){
            openPrice = new PriceSqlType(0.0);
        }
        setOpenPrice(openPrice);

        setOpenPriceVolume(recapStructV5.openPriceVolume);

        PriceSqlType closePrice = new PriceSqlType(recapStructV5.aRecapStruct.closePrice);
        if(closePrice.isNoPrice()){
            closePrice = new PriceSqlType(0.0);
        }
        setClosePrice(closePrice);


        PriceSqlType netChange = new PriceSqlType(recapStructV5.aRecapStruct.netChange);
        if(netChange.isNoPrice()){
            netChange = new PriceSqlType(0.0);
        }
        setNetChange(netChange);

        setNumberOfTrades(recapStructV5.numberOfTrades);

    }    
  
}
