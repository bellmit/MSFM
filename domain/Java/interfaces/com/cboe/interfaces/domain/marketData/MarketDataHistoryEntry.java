package com.cboe.interfaces.domain.marketData;

import com.cboe.idl.cmiMarketData.*;
import com.cboe.idl.internalBusinessServices.MarketDataHistoryEntriesStruct;
import com.cboe.idl.internalBusinessServices.MarketDataHistoryEntryStructV1;
import com.cboe.idl.marketData.InternalTickerStruct;
import com.cboe.idl.marketData.InternalTickerDetailStruct;
import com.cboe.idl.cmiUtil.*;
import com.cboe.idl.trade.*;
import com.cboe.util.*;
import com.cboe.interfaces.domain.Price;


/**
 * A recording of a last sale or quote.  An object containing the
 * union of data from these sources is needed so that a combined
 * report can be created.
 *
 * @author John Wickberg
 * @author Magic Magee
 */
public interface MarketDataHistoryEntry
{
/**
 * Creates an entry from a market best.
 *
 * @param bestMarket current market values for entry includes best price even if only contains contingent orders
 * @param bestLimitMarket current market values for entry best prices must have non-contingent orders
 * @param bestPublicMarket
 * @param underlyingPrice price of underlying security
 * @param location TODO
 */
void createCurrentMarketEntry(CurrentMarketStruct bestMarket,
                              CurrentMarketStruct bestLimitMarket,
                              CurrentMarketStruct bestPublicMarket,
                              NBBOStruct nbboStruct,
                              NBBOStruct botrStruct,
                              ExchangeIndicatorStruct[] exchangeIndicatorStruct,
                              Price underlyingPrice,
                              short productState,
                              long entryTime, String location);

/**
 * Creates an entry for an expected opening price
 *
 * @param expectedOpeningPrice ExpectedOpeningPriceStruct
 */
void createExpectedOpenPriceEntry(ExpectedOpeningPriceStruct expectedOpeningPrice,
                                  Price underlyingPrice,
                                  short productState,
                                  long entryTime) ;


/**
 * Creates an entry from a last sale.
 * This metohd will use market data such as nbbo, best public price and size from market data
 * instead of that is provided in lastSale message
 * This method should be used when TickerDetail does not have all the market information such as
 * when received as result of trade done on SBT server as opposed to TPF
 */

void createLastSaleEntry(TimeStruct saleTime,
                         InternalTickerDetailStruct lastSale,
                         Price underlyingPrice,
                         short productState,
                         long entryTime);

/**
 * Creates an history entry from a product State change.
 *
 * @param productKey product changing state
 * @param price an underlying price
 * @param productState new state of product
 */
void createProductStateChangeEntry(String sessionName,
                                   int productKey,
                                   Price underlyingPrice,
                                   short productState);
/**
 * Gets the type code of this entry.
 */
short getEntryType();
/**
 * Gets the entry time in milliseconds.
 */
long getEntryTime();
/**
 * Gets last sale price of this market history entry.
 *
 * @return last sale price or null if entry is not a price report.
 */
Price getLastSalePrice();
/**
 * Gets last sale quantity of this market history entry.
 *
 * @return last sale quantity or 0 if entry is not a price report.
 */
int getLastSaleVolume();
/**
 * Gets last sale prefix of this market history entry.
 *
 * @return last sale prefix or null if entry is not a price report.
 */
String getTickerPrefix();

int getProductKey();

/**
 * Converts this entry to a CORBA struct.
 *
 * @return market data history struct
 */
MarketDataHistoryEntryStruct toStruct();

/**
 * Converts this entry to a CORBA struct.
 *
 * @return market data history detail entry struct
 */
public MarketDataHistoryDetailEntryStruct toDetailStruct();

/**
 * Converts this entry to a CORBA struct
 */
public MarketDataHistoryEntryStructV1 toMarketDataHistoryEntryStructV1Struct ();

/**
 * Creates MarketDataHistoryEntry from CORBA struct
 * @param struct
 * @return
 */
public MarketDataHistoryEntry fromCORBAStruct (MarketDataHistoryEntryStructV1 struct);

public MarketDataHistoryEntriesStruct toMarketDataHistoryEntriesStruct();

public MarketDataHistoryEntry fromCORBAStruct (MarketDataHistoryEntriesStruct struct);

}
