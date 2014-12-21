package com.cboe.interfaces.domain.marketDataReportService;

import com.cboe.idl.cmiMarketData.TickerStruct;
import com.cboe.interfaces.domain.Price;

/**
 * A recording of a last sale or quote. 
 * 
 * @author Cognizant Technology Solutions.
 */
public interface MarketDataHistoryForReports
{
    /**
     * Creates an entry from a last sale.
     * 
     */
    void createLastSaleEntry(TickerStruct ticker,long entryTime);

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

    /** 
     * Gets Product Key
     * @return Product Key
     */
    int getProductKey();
}
