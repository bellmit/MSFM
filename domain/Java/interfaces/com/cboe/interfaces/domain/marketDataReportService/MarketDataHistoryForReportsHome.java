package com.cboe.interfaces.domain.marketDataReportService;

import com.cboe.exceptions.AlreadyExistsException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.idl.cmiMarketData.TickerStruct;
import com.cboe.interfaces.domain.HistoryServiceIdGenerator;

/**
 * A home class used to create or find each and every ticker entry information for Market Data
 * Report Service.
 * 
 * @author Cognizant Technology Solutions.
 */
public interface MarketDataHistoryForReportsHome
{
    /**
     * Name of the home.
     */
    public final static String HOME_NAME = "MarketDataHistoryForReportsHome";

    /**
     * Creates last sale entries to history for reports
     * 
     * @param ticker
     * @param entryTime
     * @return MarketDataHistoryForReports
     * @throws AlreadyExistsException
     * @throws DataValidationException
     * @throws SystemException
     */
    public MarketDataHistoryForReports createLastSaleEntry(TickerStruct ticker,
            long entryTime) throws AlreadyExistsException, DataValidationException, SystemException;

    /**
     * Gets History id generator
     * 
     * @return HistoryServiceIdGenerator
     */
    public HistoryServiceIdGenerator getIdGenerator();

    /**
     * Sets the History id generator to be used.
     * 
     * @param idGenerator
     */
    public void setIdGenerator(HistoryServiceIdGenerator idGenerator);

    /**
     * Searches history for all last sale entries for the current day given a session and product.
     * The result will be returned in ascending time order. Will assume that business day and
     * calendar day are equivalent.
     * 
     * @param sessionName name of session
     * @param productKey key of requested product
     * @return entries found for product and time
     */
    MarketDataHistoryForReports[] findCurrentDayLastSales(String sessionName, int productKey);
}