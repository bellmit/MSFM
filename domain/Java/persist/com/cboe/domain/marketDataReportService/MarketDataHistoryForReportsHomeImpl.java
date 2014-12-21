package com.cboe.domain.marketDataReportService;

import java.util.Calendar;
import java.util.Vector;

import com.cboe.domain.util.DateWrapper;
import com.cboe.exceptions.AlreadyExistsException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.idl.cmiConstants.MarketDataHistoryEntryTypes;
import com.cboe.idl.cmiMarketData.TickerStruct;
import com.cboe.infrastructureServices.foundationFramework.BOHome;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.persistenceService.ObjectQuery;
import com.cboe.infrastructureServices.persistenceService.PersistenceException;
import com.cboe.interfaces.domain.HistoryServiceIdGenerator;
import com.cboe.interfaces.domain.marketDataReportService.MarketDataHistoryForReports;
import com.cboe.interfaces.domain.marketDataReportService.MarketDataHistoryForReportsHome;
import com.objectwave.persist.constraints.ConstraintCompare;

/**
 * A home for persisting each and every ticker coming to market data report server.
 * 
 * @author Cognizant Technology Solution
 */
public class MarketDataHistoryForReportsHomeImpl extends BOHome implements
        MarketDataHistoryForReportsHome
{
    /**
     * Reference to History ID generator
     */
    private HistoryServiceIdGenerator idGenerator = null;

    /**
     * MarketDataReportHistoryHomeImpl constructor.
     */
    public MarketDataHistoryForReportsHomeImpl()
    {
        super();
    }

    /**
     * create Last sale entry to Market Data Report History
     */
    public MarketDataHistoryForReports createLastSaleEntry(TickerStruct ticker,
            long entryTime) throws AlreadyExistsException, DataValidationException, SystemException
    {
        MarketDataHistoryForReportsImpl newInstance = null;
        newInstance = new MarketDataHistoryForReportsImpl(true, getIdGenerator());
        newInstance.setAsTransient(true);
        addToContainer(newInstance);
        newInstance.createLastSaleEntry(ticker,entryTime);
        newInstance.setAsTransient(false);
        try
        {
            newInstance.insert();
        }
        catch (PersistenceException e)
        {
            Log.exception(this, "Unable to persist market data report history entry ", e);
            throw new RuntimeException(e);
        }
        return newInstance;
    }

    /**
     * Gets History id generator to be used
     */
    public HistoryServiceIdGenerator getIdGenerator()
    {
        return idGenerator;
    }

    /**
     * Sets History id generator to be used
     */
    public void setIdGenerator(HistoryServiceIdGenerator idGenerator)
    {
        this.idGenerator = idGenerator;
    }

    /**
     * Searches history for all last sale entries for the current day given a session and product.
     * The result will be returned in ascending time order. Will assume that business day and
     * calendar day are equivalent.
     * 
     * @param sessionName name of session
     * @param productKey key of requested product
     * @return entries found for product and time
     */
    public MarketDataHistoryForReports[] findCurrentDayLastSales(String sessionName, int productKey)
    {
        MarketDataHistoryForReportsImpl example = new MarketDataHistoryForReportsImpl();
        addToContainer(example);
        ObjectQuery query = new ObjectQuery(example);
        example.setProductKey(productKey);
        example.setSessionName(sessionName);
        example.setEntryType(MarketDataHistoryEntryTypes.PRICE_REPORT_ENTRY);
        long currentTime = System.currentTimeMillis();
        long startTime = DateWrapper.convertToMillis(DateWrapper.convertToDate(currentTime));
        long endTime = currentTime;
        example.setDayOfWeek((byte) getDayOfWeek(startTime));
        ConstraintCompare timeConstraint = new ConstraintCompare();
        timeConstraint.setPersistence(example);
        timeConstraint.setField("entryTime");
        timeConstraint.setCompValue(String.valueOf(startTime));
        timeConstraint.setComparison(">=");
        query.addConstraint(timeConstraint);
        ConstraintCompare timeEndConstraint = new ConstraintCompare();
        timeEndConstraint.setPersistence(example);
        timeEndConstraint.setField("entryTime");
        timeEndConstraint.setCompValue(String.valueOf(endTime));
        timeEndConstraint.setComparison("<");
        query.addConstraint(timeEndConstraint);
        query.addOrderByField("entryTime");
        try
        {
            Vector queryResult = query.find();
            MarketDataHistoryForReports[] result = new MarketDataHistoryForReports[queryResult.size()];
            queryResult.copyInto(result);
            return result;
        }
        catch (PersistenceException e)
        {
            Log.exception(this, "Market Data Report History query failed for product = "
                    + productKey, e);
            return new MarketDataHistoryForReports[0];
        }
    }

    /**
     * This returns the day of the week based on the time in milliSeconds
     */
    int getDayOfWeek(long timeInMillis)
    {
        DateWrapper dateWrapper = new DateWrapper(timeInMillis);
        Calendar calendar = dateWrapper.getCalendar();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        // for Calendar, 1 - Sunday 2 - Monday...
        return (dayOfWeek);
    }
}
