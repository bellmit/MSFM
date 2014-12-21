package com.cboe.domain.dataIntegrity;

import java.util.HashMap;
import java.util.Map;

import com.cboe.exceptions.DataValidationException;
import com.cboe.infrastructureServices.foundationFramework.HomeFactory;
import com.cboe.infrastructureServices.foundationFramework.exceptionHandling.CBOELoggableException;
import com.cboe.interfaces.domain.OrderBookHome;
import com.cboe.interfaces.domain.dataIntegrity.SynchPoint;
import com.cboe.util.ExceptionBuilder;

/**
 * @author dowat
 *
 */
public class TeSynchPoint extends BaseSynchPoint implements SynchPoint
{
    private static final long serialVersionUID = 2575542017603711525L;
    private CountAt tradingproductCount;
    private CountAt tradingproductEventCount;
    private CountAt tradingsessionEventCount;
    private CountAt tradingproductOpenCount;
    private CountAt orderBookCount;
    private static int orderBookMismatchCounter;
    public static int ALARM_TIRGGER_COUNT = 5;
    private static int ORDER_BOOK_REFRESH_TRIGER_COUNT = ALARM_TIRGGER_COUNT-1;

    public TeSynchPoint()
    {
        super(SynchPoint.SERVER_TYPE_TE);
        this.numCounts = 7;
    }
    public void addTradingproductCount(long count, long timestamp) throws DataValidationException
    {
        this.getTradingproductCount().addCount(count, timestamp);
        this.lastCountAddedTime = timestamp;
    }
    public void addTradingproductOpenCount(long count, long timestamp) throws DataValidationException
    {
      this.getTradingproductOpenCount().addCount(count, timestamp);   
      this.lastCountAddedTime = timestamp;
    }
    public void addTradingproductUpdateCount(long count, long timestamp) throws DataValidationException
    {
        this.getTradingproductEventCount().addCount(count, timestamp);
        this.lastCountAddedTime = timestamp;
    }
    public void addTradingsessionUpdateCount(long count, long timestamp) throws DataValidationException
    {
        this.getTradingsessionEventCount().addCount(count, timestamp);
        this.lastCountAddedTime = timestamp;
    }
    public void addOrderBookCount(long count, long timestamp) throws DataValidationException
    {
        this.getOrderBookCount().addCount(count, timestamp);
        this.lastCountAddedTime = timestamp;
    }

    /**
     * Initialize counts for a trade engine synch point
     * Currently we instrument 8 counts:
     * <li>Local cache order count, coming from OrderQueryHome</li>
     * <li>Distributed cache order update count coming from DataintegrityHolder(DistributedCacheHome)</li>
     * <li>Local cache product data update count coming from DataIntegirtyHolder(ProductUpdateConsumer)</li>
     * <li>Local cache trading product object count coming from TradingProductHome</li>
     * <li>Local cache open trading product object count coming from TradingProductHome</li>
     * <li>Local cache trading product update count coming from DataIntregrityHolder(TradingSessionHome)</li>
     * <li>Distributed cache trading product update count coming from DataIntegrityHolder(DistributedCacheHome)</li>
     * <li>Local cache user update count</li>
     * @throws DataValidationException
     */
    public void initializeCounts(Map<String, String> thresholds) throws DataValidationException
    {
        this.setThresholdParams(thresholds);
        // Initialize period in base
        super.setConfiguredPeriod();
        // Order object count against local cache
        String orderTimeUnitMultiplier = thresholdParams.get(SynchPoint.ORDER_PROPERTYKEY + SynchPoint.TIMEUNIT_PROPERTYKEYSUFFIX);
        String orderTimeUnitDelay = thresholdParams.get(SynchPoint.ORDER_PROPERTYKEY + SynchPoint.TIMEUNITDELAY_PROPERTYKEYSUFFIX);
        this.setOrderCount(CountAtDynamicThreshold.getInstance(SynchPoint.ORDER_PROPERTYKEY, BaseSynchPoint.SERVER_TYPE_TE, orderTimeUnitMultiplier, orderTimeUnitDelay));

        // Order book object count 
        String orderBookTimeUnitMultiplier = thresholdParams.get(SynchPoint.ORDER_BOOK_COUNT_PROPERTYKEY + SynchPoint.TIMEUNIT_PROPERTYKEYSUFFIX);
        String orderBookTimeUnitDelay = thresholdParams.get(SynchPoint.ORDER_BOOK_COUNT_PROPERTYKEY + SynchPoint.TIMEUNITDELAY_PROPERTYKEYSUFFIX);
        this.setOrderBookCount(CountAtDynamicThreshold.getInstance(SynchPoint.ORDER_BOOK_COUNT_PROPERTYKEY, BaseSynchPoint.SERVER_TYPE_TE, orderBookTimeUnitMultiplier, orderBookTimeUnitDelay));
        
        // Order update event count against distributed cache
        String orderUpdateTimeUnitMultiplier = thresholdParams.get(SynchPoint.ORDERUPDATE_PROPERTYKEY + SynchPoint.TIMEUNIT_PROPERTYKEYSUFFIX);
        String orderUpdateTimeUnitDelay = thresholdParams.get(SynchPoint.ORDERUPDATE_PROPERTYKEY + SynchPoint.TIMEUNITDELAY_PROPERTYKEYSUFFIX);
        this.setOrderEventCount(CountAtDynamicThreshold.getInstance(SynchPoint.ORDERUPDATE_PROPERTYKEY, BaseSynchPoint.SERVER_TYPE_TE, orderUpdateTimeUnitMultiplier, orderUpdateTimeUnitDelay));
        // Trading product object count against local cache 
        String tradingproductMaxThreshold = thresholdParams.get(SynchPoint.TRADINGPRODUCT_PROPERTYKEY + SynchPoint.MAXTHRESHOLD_PROPERTY_SUFFIX);
        this.setTradingproductCount(CountAt.getAbsoluteCount(SynchPoint.TRADINGPRODUCT_PROPERTYKEY, BaseSynchPoint.SERVER_TYPE_TE, tradingproductMaxThreshold));
        // Open trading product count against local cache 
        String tradingproductOpenMaxThreshold = thresholdParams.get(SynchPoint.TRADINGPRODUCTOPEN_PROPERTYKEY + SynchPoint.MAXTHRESHOLD_PROPERTY_SUFFIX);
        this.setTradingproductOpenCount(CountAt.getAbsoluteCount(SynchPoint.TRADINGPRODUCTOPEN_PROPERTYKEY, BaseSynchPoint.SERVER_TYPE_TE, tradingproductOpenMaxThreshold));
        // Trading product update count against distributed cache 
        String tradingproductUpdateMaxThreshold = thresholdParams.get(SynchPoint.TRADINGPRODUCTUPDATE_PROPERTYKEY + SynchPoint.MAXTHRESHOLD_PROPERTY_SUFFIX);
        this.setTradingproductEventCount(CountAt.getAbsoluteCount(SynchPoint.TRADINGPRODUCTUPDATE_PROPERTYKEY, BaseSynchPoint.SERVER_TYPE_TE, tradingproductUpdateMaxThreshold));
        // User object count against local cache
        String userMaxThreshold = thresholdParams.get(SynchPoint.USER_PROPERTYKEY + SynchPoint.MAXTHRESHOLD_PROPERTY_SUFFIX);
        this.setUserCount(CountAt.getAbsoluteCount(SynchPoint.USER_PROPERTYKEY, BaseSynchPoint.SERVER_TYPE_TE, userMaxThreshold));
        // User update count against local cache 
        String userUpdateMaxThreshold = thresholdParams.get(SynchPoint.USERUPDATE_PROPERTYKEY + SynchPoint.MAXTHRESHOLD_PROPERTY_SUFFIX);
        this.setUserEventCount(CountAt.getAbsoluteCount(SynchPoint.USERUPDATE_PROPERTYKEY, BaseSynchPoint.SERVER_TYPE_TE, userUpdateMaxThreshold));
        // Trading session update count on event channel
        String tradingsessionUpdateMaxThreshold = thresholdParams.get(SynchPoint.TRADINGSESSIONUPDATE_PROPERTYKEY + SynchPoint.MAXTHRESHOLD_PROPERTY_SUFFIX);
        this.setTradingsessionEventCount(CountAt.getAbsoluteCount(SynchPoint.TRADINGSESSIONUPDATE_PROPERTYKEY, BaseSynchPoint.SERVER_TYPE_TE, tradingsessionUpdateMaxThreshold));


    }
    @Override
    public Map<String, String> compareWith(SynchPoint secondaryCounts) throws DataValidationException
    { 
        int discrepancyCount = 0;
        TeSynchPoint slaveSp = (TeSynchPoint) secondaryCounts;
        TeSynchPoint masterSp = this;
        Map<String, String> comparisonMessages = new HashMap<String, String>();
        int result = BaseSynchPoint.COUNT_NOT_COMPARED;
        // compare object count for local Order cache counts
        result = masterSp.getOrderCount().isInRange(slaveSp.getOrderCount());
        slaveSp.setHistoricalComparisonResult(masterSp.getOrderCount(),slaveSp.getOrderCount(),result);
        /*
        if(result != CountAt.INRANGE)
        {
            comparisonMessages.put(slaveSp.getOrderCount().getName(), this.getCountComparisonMsg(this.getOrderCount(), slaveSp.getOrderCount(), result)); 
            discrepancyCount++;
        }
        */
        // Order update event count against distributed cache
        result = masterSp.getOrderEventCount().isInRange(slaveSp.getOrderEventCount());
        slaveSp.setHistoricalComparisonResult(masterSp.getOrderEventCount(),slaveSp.getOrderEventCount(),result);
        if(result != CountAt.INRANGE)
        {
            comparisonMessages.put(slaveSp.getOrderEventCount().getName(), masterSp.getCountComparisonMsg(masterSp.getOrderEventCount(), slaveSp.getOrderEventCount(), result));
            discrepancyCount++;
       }
        // Trading product object count against local cache 
        result = masterSp.getTradingproductCount().isInRange(slaveSp.getTradingproductCount());
        slaveSp.setHistoricalComparisonResult(masterSp.getTradingproductCount(),slaveSp.getTradingproductCount(),result);
        if(result != CountAt.INRANGE)
        {
            comparisonMessages.put(slaveSp.getTradingproductCount().getName(), masterSp.getCountComparisonMsg(masterSp.getTradingproductCount(), slaveSp.getTradingproductCount(), result));
            discrepancyCount++;
        }
        // Open trading product object count against local cache 
        result = masterSp.getTradingproductOpenCount().isInRange(slaveSp.getTradingproductOpenCount());
        slaveSp.setHistoricalComparisonResult(masterSp.getTradingproductOpenCount(),slaveSp.getTradingproductOpenCount(),result);
        if(result != CountAt.INRANGE)
        {
            comparisonMessages.put(slaveSp.getTradingproductOpenCount().getName(), masterSp.getCountComparisonMsg(masterSp.getTradingproductOpenCount(), slaveSp.getTradingproductOpenCount(), result));
            discrepancyCount++;
        }
        // Trading product update count against distributed cache 
        result = masterSp.getTradingproductEventCount().isInRange(slaveSp.getTradingproductEventCount());
        slaveSp.setHistoricalComparisonResult(masterSp.getTradingproductEventCount(),slaveSp.getTradingproductEventCount(),result);
        if(result != CountAt.INRANGE)
        {
            comparisonMessages.put(slaveSp.getTradingproductEventCount().getName(), masterSp.getCountComparisonMsg(masterSp.getTradingproductEventCount(), slaveSp.getTradingproductEventCount(), result));
            discrepancyCount++;
        }
        // User object count against local cache 
        result = masterSp.getUserCount().isInRange(slaveSp.getUserCount());
        slaveSp.setHistoricalComparisonResult(masterSp.getUserCount(),slaveSp.getUserCount(),result);
        //if(result != CountAt.INRANGE)
        //{
        //    comparisonMessages.put(slaveSp.getUserCount().getName(), masterSp.getCountComparisonMsg(masterSp.getUserCount(), slaveSp.getUserCount(), result));
        //    discrepancyCount++;
        //}
        // User update count against local cache 
        result = masterSp.getUserEventCount().isInRange(slaveSp.getUserEventCount());
        slaveSp.setHistoricalComparisonResult(masterSp.getUserEventCount(),slaveSp.getUserEventCount(),result);
        if(result != CountAt.INRANGE)
        {
            comparisonMessages.put(slaveSp.getUserEventCount().getName(), masterSp.getCountComparisonMsg(masterSp.getUserEventCount(), slaveSp.getUserEventCount(), result));
            discrepancyCount++;
        }
        // Trading session update count on event channel
        result = masterSp.getTradingsessionEventCount().isInRange(slaveSp.getTradingsessionEventCount());
        slaveSp.setHistoricalComparisonResult(masterSp.getTradingsessionEventCount(),slaveSp.getTradingsessionEventCount(),result);
        if(result != CountAt.INRANGE)
        {
            comparisonMessages.put(slaveSp.getTradingsessionEventCount().getName(), masterSp.getCountComparisonMsg(masterSp.getTradingsessionEventCount(), slaveSp.getTradingsessionEventCount(), result));
            discrepancyCount++;
        }

        // Order book count on event channel
        result = masterSp.getOrderBookCount().isInRange(slaveSp.getOrderBookCount());
        slaveSp.setHistoricalComparisonResult(masterSp.getOrderBookCount(),slaveSp.getOrderBookCount(),result);
        if(result != CountAt.INRANGE)
        {
            comparisonMessages.put(slaveSp.getOrderBookCount().getName(), masterSp.getCountComparisonMsg(masterSp.getOrderBookCount(), slaveSp.getOrderBookCount(), result));
            discrepancyCount++;
    		orderBookMismatchCounter++;
            try {
            	if (orderBookMismatchCounter ==ORDER_BOOK_REFRESH_TRIGER_COUNT)
            	{
            		OrderBookHome orderBookHome = (OrderBookHome) HomeFactory.getInstance().findHome(OrderBookHome.HOME_NAME);
            		orderBookHome.refreshOrderCount();
            		orderBookMismatchCounter=0;
            	}
			} catch (CBOELoggableException e) {}
        }
        else
        {
    		orderBookMismatchCounter=0;
        }
        
        // set last comparison history for whole synch point
        slaveSp.setSynchPointStatus(this.lastCountAddedTime, discrepancyCount, comparisonMessages);
        return comparisonMessages;
    }
    public String getSynchPointComparisonHistory(String countName)
    {
        StringBuffer spHistory = new StringBuffer();
        if(SynchPoint.ORDER_PROPERTYKEY.equals(countName) || "all".equalsIgnoreCase(countName))
        {
            spHistory.append(getOrderCount().getHistory());
        }
        
        if(SynchPoint.ORDERUPDATE_PROPERTYKEY.equals(countName) || "all".equalsIgnoreCase(countName))
        {
            spHistory.append(this.getOrderEventCount().getHistory());
        }
        
        if(SynchPoint.TRADINGPRODUCT_PROPERTYKEY.equals(countName) || "all".equalsIgnoreCase(countName))
        {
            spHistory.append(this.getTradingproductCount().getHistory());
        }
        
        if(SynchPoint.TRADINGPRODUCTOPEN_PROPERTYKEY.equals(countName) || "all".equalsIgnoreCase(countName))
        {
            spHistory.append(this.getTradingproductOpenCount().getHistory());
        }
        
        if(SynchPoint.TRADINGPRODUCTUPDATE_PROPERTYKEY.equals(countName) || "all".equalsIgnoreCase(countName))
        {
            spHistory.append(this.getTradingproductEventCount().getHistory());
        }
        if(SynchPoint.USER_PROPERTYKEY.equals(countName) || "all".equalsIgnoreCase(countName))
        {
            spHistory.append(this.getUserCount().getHistory());
        }
        if(SynchPoint.USERUPDATE_PROPERTYKEY.equals(countName) || "all".equalsIgnoreCase(countName))
        {
            spHistory.append(this.getUserEventCount().getHistory());
        }
        if(SynchPoint.TRADINGSESSIONUPDATE_PROPERTYKEY.equals(countName) || "all".equalsIgnoreCase(countName))
        {
            spHistory.append(this.getTradingsessionEventCount().getHistory());
        }
        if(SynchPoint.ORDER_BOOK_COUNT_PROPERTYKEY.equals(countName) || "all".equalsIgnoreCase(countName))
        {
            spHistory.append(this.getOrderBookCount().getHistory());
        }
        return spHistory.toString();
    }
    @Override
    public String toString()
    {
        StringBuffer teSynchPointSb = new StringBuffer();
        teSynchPointSb.append("$$$$$$$$$$$$$$$$$$$$$$$ TE SynchPoint $$$$$$$$$$$$$$$$$$$$$$$").append("\n");
        teSynchPointSb.append(this.getOrderCount().toString()).append("\n");
        teSynchPointSb.append(this.getOrderEventCount().toString()).append("\n");
        teSynchPointSb.append(this.getTradingproductCount().toString()).append("\n");
        teSynchPointSb.append(this.getTradingproductOpenCount().toString()).append("\n");
        teSynchPointSb.append(this.getTradingproductEventCount().toString()).append("\n");
        teSynchPointSb.append(this.getUserCount().toString()).append("\n");
        teSynchPointSb.append(this.getUserEventCount().toString()).append("\n");
        teSynchPointSb.append(this.getTradingsessionEventCount().toString()).append("\n");
        teSynchPointSb.append(this.getOrderBookCount().toString()).append("\n");
        teSynchPointSb.append("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$").append("\n");
        return teSynchPointSb.toString();
    }
    
    public void setMaxTresholdForCount(String countName, Double maxPercentThreshold) throws DataValidationException
    {
        validateMaxThreshold(maxPercentThreshold);

        if(SynchPoint.ORDER_PROPERTYKEY.equals(countName))
        {
            this.getOrderCount().setPercentMaxThreshold(maxPercentThreshold);
        }
        else if(SynchPoint.ORDERUPDATE_PROPERTYKEY.equals(countName))
        {
            this.getOrderEventCount().setPercentMaxThreshold(maxPercentThreshold);
        }
        else if(SynchPoint.TRADINGPRODUCT_PROPERTYKEY.equals(countName))
        {
            this.getTradingproductCount().setPercentMaxThreshold(maxPercentThreshold);
        }
        else if(SynchPoint.TRADINGPRODUCTOPEN_PROPERTYKEY.equals(countName))
        {
            this.getTradingproductOpenCount().setPercentMaxThreshold(maxPercentThreshold);
        }
        else if(SynchPoint.TRADINGPRODUCTUPDATE_PROPERTYKEY.equals(countName))
        {
            this.getTradingproductEventCount().setPercentMaxThreshold(maxPercentThreshold);
        }
        else if(SynchPoint.USER_PROPERTYKEY.equals(countName))
        {
            this.getUserCount().setPercentMaxThreshold(maxPercentThreshold);
        }
        else if(SynchPoint.USERUPDATE_PROPERTYKEY.equals(countName))
        {
            this.getUserEventCount().setPercentMaxThreshold(maxPercentThreshold);
        }
        else if(SynchPoint.TRADINGSESSIONUPDATE_PROPERTYKEY.equals(countName))
        {
            this.getTradingsessionEventCount().setPercentMaxThreshold(maxPercentThreshold);
        }
        else if(SynchPoint.ORDER_BOOK_COUNT_PROPERTYKEY.equals(countName))
        {
            this.getOrderBookCount().setPercentMaxThreshold(maxPercentThreshold);
        }
        else 
        {
            throw ExceptionBuilder.dataValidationException("Unable to find a count provider for provider name[" + countName + "], unable to set injectable count provider", -2);
        }
    }
    private void validateMaxThreshold(Double maxPercentThreshold) throws DataValidationException
    {
        if( maxPercentThreshold < 0 )
        {
            throw ExceptionBuilder.dataValidationException("Invalid threhsold[" + maxPercentThreshold + "], must be >= 0", COUNT_NOT_COMPARED);
        }
    }
    public void setEventCountOffsets(TeSynchPoint dcSynchPoint)
    {
        super.setEventCountOffsets(dcSynchPoint);
        this.getTradingproductEventCount().setOffset(dcSynchPoint.getTradingproductEventCount().getCurrentCount() - this.getTradingproductEventCount().getCurrentCount());
        this.getTradingsessionEventCount().setOffset(dcSynchPoint.getTradingsessionEventCount().getCurrentCount() - this.getTradingsessionEventCount().getCurrentCount());
    }
    public void resetEventCountOffsets()
    {
        super.resetEventCountOffsets();
        this.getTradingproductEventCount().setOffset(0);
        this.getTradingsessionEventCount().setOffset(0);
    }
    public void setTradingproductCount(CountAt tradingproductCount)
    {
        this.tradingproductCount = tradingproductCount;
    }
    public void setTradingproductEventCount(CountAt tradingproductEventCount)
    {
        this.tradingproductEventCount = tradingproductEventCount;
    }
    public void setTradingproductOpenCount(CountAt tradingproductEventCount)
    {
        this.tradingproductOpenCount = tradingproductEventCount;
    }
    public void setTradingsessionEventCount(CountAt tradingsessionEventCount)
    {
        this.tradingsessionEventCount = tradingsessionEventCount;
    }
    public void setOrderBookCount(CountAt orderBookCount)
    {
        this.orderBookCount = orderBookCount;
    }

    public CountAt getTradingproductEventCount()
    {
        return tradingproductEventCount;
    }
    public CountAt getTradingsessionEventCount()
    {
        return tradingsessionEventCount;
    }
    public CountAt getTradingproductCount()
    {
        return tradingproductCount;
    }
    public CountAt getTradingproductOpenCount()
    {
        return tradingproductOpenCount;
    }

    public CountAt getOrderBookCount()
    {
        return orderBookCount;
    }

}
