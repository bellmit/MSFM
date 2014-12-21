//
// -----------------------------------------------------------------------------------
// Source file: APIFactory.java
//
// PACKAGE: com.cboe.interfaces.presentation.api;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2001 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.api;

import com.cboe.interfaces.presentation.productConfiguration.ProductConfigurationQueryAPI;

/**
 * Defines a common definition for an APIFactory.
 */
public interface APIFactory
{
    public AdministratorAPI findAdministratorAPI();
    public CommonAPI findCommonAPI();

    /**
     * Create a MarketQueryV3API.
     * @return a marketQueryV3API object.
     *
     * @see MarketQueryV3API
     */
    public MarketQueryV3API findMarketQueryAPI();
    public OrderQueryV3API findOrderQueryAPI();

    /**
     * Create an OrderManagementTerminalAPI.
     * @return the orderManagementTerminalAPI object.
     *
     * @see OrderManagementTerminalAPI
     */
    public OrderManagementTerminalAPI findOrderManagementTerminalAPI();
    public ProductConfigurationQueryAPI findProductConfigurationQueryAPI();
    public ProductDefinitionAPI findProductDefinitionAPI();
    public ProductQueryAPI findProductQueryAPI();
    public QuoteV7API findQuoteAPI();
    public TradingSessionAPI findTradingSessionAPI();
    public UserHistoryAPI findUserHistoryAPI();
    public UserPreferenceQueryAPI findUserPreferenceQueryAPI();
    public UserTradingParametersAPI findUserTradingParametersAPI();
    public FloorTradeAPI findFloorTradeAPI();

    /**
     * Create a MarketQueryV4API.
     * @return the marketQueryV4API object.
     *
     * @see MarketQueryV4API
     */
    public MarketQueryV4API findMarketQueryV4API();
    public ManualReportingAPI findManualReportingAPI();
    
    /**
     * Create a MarketQueryV5API.
     * 
     * @return the MarketQueryV5API object.
     * 
     * @see MarketQueryV5API
     */
    public MarketQueryV5API findMarketQueryV5API();

    /**
     * Return the API that supports querying for daily order counts (e.g., cumulative quantities and prices);
     * @return
     */
    public OrderFillCountAPI findOrderFillCountAPI();

    /**
     * This method suppose to contain all the clean up logic for the API, i.e. clearing caches,
     * unsubscribing from event channels, setting variables to null, ...
     */
    public void cleanUp();
}
