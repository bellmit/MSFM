package com.cboe.interfaces.domain.marketData;

import com.cboe.exceptions.NotFoundException;
import com.cboe.idl.cmiMarketData.CurrentMarketStruct;
import com.cboe.idl.cmiMarketData.ExchangeIndicatorStruct;
import com.cboe.idl.cmiMarketData.ExpectedOpeningPriceStruct;
import com.cboe.idl.cmiMarketData.NBBOStruct;
import com.cboe.idl.cmiUtil.TimeStruct;
import com.cboe.idl.internalBusinessServices.MarketDataHistoryEntriesStruct;
import com.cboe.idl.marketData.InternalTickerDetailStruct;
import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.domain.MarketUpdate;

/**
 * A home for the market data history.
 * Changed return value on createZZZ methods.
 * 
 * @author John Wickberg
 * @author Magic Magee
 */
public interface MarketDataHistoryHome
{
	/**
	 * Name of the home.
	 */
	public static final String HOME_NAME = "MarketDataHistoryHome";

	/**
	 * Creates a history entry from a market best.
	 * 
	 * @param bestMarket
	 *            current market values for product
	 * @param underlyingPrice
	 *            price of the underlying security of the product
	 */
	void createCurrentMarketEntry(CurrentMarketStruct bestMarket, CurrentMarketStruct bestLimitMarket,
			CurrentMarketStruct bestPublicMarket, NBBOStruct nbboStruct, NBBOStruct botrStruct,
			ExchangeIndicatorStruct[] exchangeIndicatorStruct, Price underlyingPrice, short productState, long entryTime,
			String location);
	
	/**
     * Creates a history entry from a market update.
     * 
     * @param update
     *            current market values for product
     * @param underlyingPrice
     *            price of the underlying security of the product
     */
    void createCurrentMarketEntry(MarketUpdate update,NBBOStruct nbboStruct, NBBOStruct botrStruct,
            ExchangeIndicatorStruct[] exchangeIndicatorStruct, Price underlyingPrice, short productState, long entryTime,
            String location);
	
	    
	/**
	 * Creates an history entry from a expected opening price.
	 * 
	 * @param expectedOpeningPrice
	 *            calculated opening price values
	 * @param underlyingPrice
	 *            price of underlying product
	 * @return created history entry
	 */
	void createExpectedOpenPriceEntry(ExpectedOpeningPriceStruct expectedOpeningPrice, Price underlyingPrice, short productState,
			long entryTime);

	/**
	 * Creates an entry from a last sale. This metohd will use market data such
	 * as nbbo, best public price and size from market data instead of that is
	 * provided in lastSale message This method should be used when TickerDetail
	 * does not have all the market information such as when received as result
	 * of trade done on SBT server as opposed to TPF
	 */

	void createLastSaleEntry(TimeStruct saleTime, InternalTickerDetailStruct lastSale, Price underlyingPrice, short productState,
			long entryTime);

	/**
	 * Creates an history entry from a product State change.
	 * 
	 * @param int
	 *            productKey product changing state
	 * @param Price
	 *            underlying price
	 * @param productState
	 *            new state of product
	 */
	void createProductStateChangeEntry(String sessionName, int productKey, Price underlyingPrice, short productState);

	/**
	 * Creates market data history entry
	 */
	void create(String blockId, MarketDataHistoryEntriesStruct[] entries);

	void create(MarketDataHistoryEntry entries);
	
	/**
	 * Searches history for entries given a product and starting time. The
	 * result will be in reverse time sequence. Search result will be limited to
	 * a maximum size determined by the implementation.
	 * 
	 * @param productKey
	 *            key of requested product
	 * @param startTime
	 *            starting time for search. Since result is in reverse time
	 *            sequence, the starting time is the time of the most recent
	 *            desired entry.
	 * @return entries found for product and time
	 */
	MarketDataHistoryEntry[] findByTime(int productKey, long startTime, short direction) throws NotFoundException;

	/**
	 * Searches history for all last sale entries for the current day given a
	 * session and product. The result will be returned in ascending time order.
	 * Will assume that business day and calendar day are equivalent.
	 * 
	 * @param sessionName
	 *            name of session
	 * @param productKey
	 *            key of requested product
	 * @return entries found for product and time
	 */
	MarketDataHistoryEntry[] findCurrentDayLastSales(String sessionName, int productKey);

	/**
	 * Purges entries that are older than desired retention time.
	 * 
	 * @param retentionCutoff
	 *            oldest time that will be retained
	 */
	void purge(long retentionCutoff);

	void createShortSaleTriggeredModeEntry(String sessionName, int productKey, boolean shortSaleTriggeredMode);
}
