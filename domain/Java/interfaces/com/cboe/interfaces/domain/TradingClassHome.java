package com.cboe.interfaces.domain;

import java.util.Map;
import java.util.Timer;

import com.cboe.exceptions.NotFoundException;
import com.cboe.idl.cmiStrategy.StrategyStruct;
import com.cboe.idl.product.ProductClassStruct;

/**
 * A home for TradingClass instances.
 * Added three methods for manipulating trading property cache for reducing failover time.
 *
 * @author John Wickberg
 * @author singh Daljinder
 */

public interface TradingClassHome {

    /**
     * Name used to get home from HomeFactory.  Must also be name used for
     * home in configuration.
     */
    public static final String HOME_NAME = "TradingClassHome";    

    /**
     * Create a new trading class.  It is not considered an error to try to
     * create a class that already exists, the existing class will be returned.
     *
     * @param classDefinition struct containing class definition
     * @return created or existing trading class
     */
    void createProxyObjects();
       
    TradingClass create(ProductClassStruct classDefinition);
    
    TradingClass createProxy(ProductClassStruct classDefinition);

    /**
     * Create a new trading class.  It is not considered an error to try to
     * create a class that already exists, the existing class will be returned.
     *
     * @param classDefinition struct containing class definition
     * @return created or existing trading class
     */
    TradingClass create(ProductClassStruct classDefinition, StrategyStruct[] strategies);
    TradingClass createProxy(ProductClassStruct classDefinition, StrategyStruct[] strategies);

    /**
     * Get trading parameters from global and store them in the local cache to reduce go master time
     * of the trade server. This method also registers a consumer for trading property event channel
     */
    void fetchTradingProperty (String sessionName, int classKey);

    /**
     * Sets the trading property cache size. Trading property cache is used for storing trading parameters
     * to reduce fail over time of trade server
     */
    void initTradingPropertyCache (int size);

    /**
     * Gets the trading parameter from the trading property cache.
     */
    Object findCachedTradingProperty (String sessionName, int classKey);

    /**
     * Finds a trading class given its key.
     *
     * @param classKey key of desired class
     * @return trading class with the given key
     * @exception NotFoundException if class is not found
     */
    TradingClass findByKey(int classKey) throws NotFoundException;

    /**
     * Finds all trading classes with a given symbol.
     *
     * @param classSymbol symbol of desired classes
     * @return trading class having given symbol
     */
    TradingClass[] findBySymbol(String classSymbol);

    /**
     * Finds all configured trading classes.
     *
     * @return all configured trading classes
     */
    TradingClass[] findAllConfigured();
    
    TradingClass[] findAllConfiguredIncludingBuddyClasses();

    /**
     * Finds all configured trading classes for a given trading session.
     *
     * @param sessionName name of the trading session
     * @return all configured trading classes for the session
     */
    TradingClass[] findAllConfiguredForSession(String sessionName);
    
    TradingClass[] findAllConfiguredForSession(String sessionName, boolean includeProxyClasses);

    /**
     * Return if should use property service
     */
    boolean getUsePropertyService();

    /**
     * Any interested service/home that wants to be notified when product state is changed should
     * register itself with TradingClassHome via this method.
     * @param listner
     */
    void registerForProductStateChange(TradingProductStateListener listner);

    /**
     * Returns the listners registered with this home for product state change notification.
     * @return
     */
    public TradingProductStateListener[] getListnersForProductStateChange();

    /**
     * Threshold time for product state change timing, which will be logged.
     * returned value of 0 indicats logging is disabled.
    **/
    public long getProdStateChangeTimingLogThreshold();

    /** Get a reference to the timer used for quote locks and triggers
     *
     * @return
     */
    public Timer getTimer();

    /*
     * The maximum number of nested QRM levels that will be processed
     */
    public int getNumberOfNestedQRMLevels();

    public Map getMOCStaggeredTimerMap();

//    public void setElementInfoMap(TradingSessionElementInfoStruct[] info); //future usage
//    public HashMap getElementInfoMap();
    
    /*
     * Boolean for blanking-out acronyms on notifications
     */
    public boolean getPublishBlankAcrs();
    
    public int getCrossProductAuctionTradeAttemptMax();
    
    public boolean getUseProctectionPrice();
    
    /**
     * A convenience method to turn on/off quote lock notification
     * @return boolean
     */
    public boolean isQuoteLockNotificationNeeded();
    
    /**
     * Inform the home that all trading class building has been completed. So it can
     * do some neccessary task if it needs
     */
    public void completedBuildTradingClasses();

}


