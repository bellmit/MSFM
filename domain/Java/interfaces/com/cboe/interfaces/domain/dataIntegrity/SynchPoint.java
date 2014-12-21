package com.cboe.interfaces.domain.dataIntegrity;

import java.util.Map;

import com.cboe.exceptions.DataValidationException;

public interface SynchPoint
{
    // TODO Enumerate all the different type values
    // The following constants are used to construct fully qualified names for counts instrumented by a server
    // Server types
    public static final String SERVER_TYPE_OHS = "ohs";
    public static final String SERVER_TYPE_TE = "te";
    // Data types
    public static final String DATA_TYPE_TRADING_PRODUCT = "tradingproduct";
    public static final String DATA_TYPE_PRODUCT = "product";
    public static final String DATA_TYPE_ORDER = "order";
    public static final String DATA_TYPE_USER = "user";
    public static final String DATA_TYPE_TRADINGSESSION = "tradingsession";
    // Count types
    public static final String COUNT_TYPE_OBJECT = "Object";
    public static final String COUNT_TYPE_EVENT = "Event";
    public static final String COUNT_OPEN_STATE = "Open";
    // allow plus or minus 5 percent on object counts by default
    public static final Double MAX_PERCENTAGE_THRESHOLD_OBJECT_COUNT = .05;
    // allow plus or minus 10 percent on object counts by default
    public static final Double MAX_PERCENTAGE_THRESHOLD_UPDATE_COUNT = .1;
    
    public static final String ORDER_PROPERTYKEY = DATA_TYPE_ORDER;
    public static final String ORDERUPDATE_PROPERTYKEY = DATA_TYPE_ORDER + COUNT_TYPE_EVENT;
    public static final String ORSID_PROPERTYKEY = "orsid";
    public static final String ORSIDINDEX_PROPERTYKEY = "orsidIndex";
    public static final String TRADINGPRODUCT_PROPERTYKEY = DATA_TYPE_TRADING_PRODUCT;
    public static final String TRADINGPRODUCTUPDATE_PROPERTYKEY = DATA_TYPE_TRADING_PRODUCT + COUNT_TYPE_EVENT;
    public static final String TRADINGPRODUCTOPEN_PROPERTYKEY = DATA_TYPE_TRADING_PRODUCT + COUNT_OPEN_STATE;
    public static final String USER_PROPERTYKEY = DATA_TYPE_USER;
    public static final String USERUPDATE_PROPERTYKEY = DATA_TYPE_USER + COUNT_TYPE_EVENT;
    public static final String TRADINGSESSIONUPDATE_PROPERTYKEY = DATA_TYPE_TRADINGSESSION + COUNT_TYPE_EVENT;
    public static final String PRODUCT_PROPERTYKEY = DATA_TYPE_PRODUCT;
    public static final String PRODUCTUPDATE_PROPERTYKEY = DATA_TYPE_PRODUCT + COUNT_TYPE_EVENT;
    public static final String TIMEUNIT_PROPERTYKEYSUFFIX = "TimeUnit";
    public static final String TIMEUNITDELAY_PROPERTYKEYSUFFIX = "MaxNumTimeUnitDelay";
    public static final String MAXTHRESHOLD_PROPERTY_SUFFIX = "MaxThreshold";
    public static final String PERIOD_PROPERTYKEY = "dataintegrityCheckPeriodMaster";
    public static final String ORDER_BOOK_COUNT_PROPERTYKEY = "orderBook";
    public static final String ORDER_TOTAL_QUANTITY_PROPERTYKEY = "OrderTotalQuantity";
    
    public static final String TIMEUNIT_MILLISECONDS = "milliseconds";
    public static final String TIMEUNIT_SECONDS = "seconds";
    public String toString();

    public long getLastCountAddedTime();

    public void setLastCountAddedTime(long lastCountAddedTime);

    /**
     * Provides comparison of one <code>SynchPoint</code> to another <code>SynchPoint</code>.  The comparison must use the 
     * provided server counts to calculate the maximum threshold and return whether the <code>synchPoint</code> invoking the
     * comparison is one of the following:
     * 
     * <li>below minimum threshold</li>
     * <li>between the minimum and maximum threshold</li>
     * <li>above the maximum threshold</li>
     * 
     * The result of the comparison will 
     *  
     * @param secondaryCounts
     * @return
     * @throws DataValidationException
     */
    public Map<String, String> compareWith(SynchPoint serverCounts) throws DataValidationException;
    
    /**
     * In subtypes designed for specific servers, create one count object for each instrumented object or event
     * 
     * @throws DataValidationException
     */
    public void initializeCounts(Map<String, String> thresholds) throws DataValidationException;

    public void resetCounter();
}