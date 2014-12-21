package com.cboe.interfaces.application;


/**
 * @author Arun Ramachandran Nov 12, 2009
 *
 */
public interface TradingClassStatusQueryServiceHome
{
    /**
     * Name that will be used for this home.
     */
    public final static String HOME_NAME = "TradingClassStatusQueryServiceHome";

    /**
     * Creates an instance of the TradingClassStatusQueryService.
     */
    public TradingClassStatusQueryService create(SessionManager sessionManager);
    
    /**
     * Finds an instance or creates an instance of the TradingClassStatusQueryService.
     */
    
    public TradingClassStatusQueryService find(SessionManager session);
    
    /**
     * CleansUp an instance of the TradingClassStatusQueryService.
     */
    
    public void remove(SessionManager session);
}