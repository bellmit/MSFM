//
// -----------------------------------------------------------------------------------
// Source file: APIFactoryImpl.java
//
// PACKAGE: com.cboe.presentation.api;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.api;

import com.cboe.interfaces.presentation.api.*;
import com.cboe.interfaces.presentation.productConfiguration.ProductConfigurationQueryAPI;
import com.cboe.interfaces.instrumentation.api.InstrumentationMonitorAPI;

public class APIFactoryImpl implements APIFactory
{
    public APIFactoryImpl()
    {
      super();
    }

    public AdministratorAPI findAdministratorAPI()
    {
        throw getUnsupportedException("AdministratorAPI");
    }

    public CommonAPI findCommonAPI()
    {
        throw getUnsupportedException("CommonAPI");
    }

    public MarketQueryV4API findMarketQueryV4API()
    {
        throw getUnsupportedException("MarketQueryV4API");
    }
    @Override
    public MarketQueryV5API findMarketQueryV5API()
    {
	throw getUnsupportedException("MarketQueryV5API");
    }
    public ManualReportingAPI findManualReportingAPI()
    {
        throw getUnsupportedException("ManualReportingAPI");
    }

    public MarketQueryV3API findMarketQueryAPI()
    {
        throw getUnsupportedException("MarketQueryV3API");
    }

    public OrderQueryV3API findOrderQueryAPI()
    {
        throw getUnsupportedException("OrderQueryV2API");
    }

    public ProductConfigurationQueryAPI findProductConfigurationQueryAPI()
    {
        throw getUnsupportedException("ProductConfigurationQueryAPI");
    }

    public ProductDefinitionAPI findProductDefinitionAPI()
    {
        throw getUnsupportedException("ProductDefinitionAPI");
    }

    public ProductQueryAPI findProductQueryAPI()
    {
        return getInstrumentationMonitorAPI();
    }

    public QuoteV7API findQuoteAPI()
    {
        throw getUnsupportedException("QuoteV2API");
    }

    public TradingSessionAPI findTradingSessionAPI()
    {
        throw getUnsupportedException("TradingSessionAPI");
    }

    public UserPreferenceQueryAPI findUserPreferenceQueryAPI()
    {
        throw getUnsupportedException("UserPreferenceQueryAPI");
    }

    public UserTradingParametersAPI findUserTradingParametersAPI()
    {
        throw getUnsupportedException("UserTradingParametersAPI");
    }

    public UserHistoryAPI findUserHistoryAPI()
    {
        throw getUnsupportedException("UserHistoryAPI");
    }

    public OrderManagementTerminalAPI findOrderManagementTerminalAPI()
    {
        throw getUnsupportedException("OrderManagementTerminalAPI");
    }

	public FloorTradeAPI findFloorTradeAPI()
	{
        throw getUnsupportedException("FloorTradeAPI");
	}

    public OrderFillCountAPI findOrderFillCountAPI()
    {
        throw getUnsupportedException("OrderFillCountAPI");
    }

    /**
     * This method suppose to contain all the clean up logic for the API, i.e. clearing caches,
     * unsubscribing from event channels, setting variables to null, ...
     */
    public void cleanUp()
    {
        getInstrumentationMonitorAPI().cleanUp();
    }

    private InstrumentationMonitorAPI getInstrumentationMonitorAPI()
    {
        return InstrumentationTranslatorFactory.find();
    }

    private UnsupportedOperationException getUnsupportedException(String interfaceNotSupported)
    {
        return new UnsupportedOperationException("The interface " + interfaceNotSupported + " is not supported by this API.");
    }
}
