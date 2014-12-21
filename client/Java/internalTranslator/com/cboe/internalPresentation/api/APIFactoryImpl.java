//
// -----------------------------------------------------------------------------------
// Source file: APIFactoryImpl.java
//
// PACKAGE: com.cboe.internalPresentation.api;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2001 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.api;

import com.cboe.interfaces.internalPresentation.SystemAdminAPI;

import com.cboe.interfaces.presentation.api.*;
import com.cboe.interfaces.presentation.productConfiguration.ProductConfigurationQueryAPI;

import com.cboe.presentation.api.ProductConfigurationQueryAPIFactory;
import com.cboe.presentation.api.ManualReportingAPIFactory;

/**
 * Implements the finders for an APIFactory for the SA GUI.
 * @author Troy Wehrle
 */
public class APIFactoryImpl implements APIFactory
{
/**
 * APIFactoryImpl constructor comment.
 */
public APIFactoryImpl()
{
    super();
}
/**
 * findAdministratorAPI method comment.
 */
public AdministratorAPI findAdministratorAPI()
{
    return getSystemAdminAPI();
}
/**
 * findCommonAPI method comment.
 */
public CommonAPI findCommonAPI()
{
    return getSystemAdminAPI();
}

public MarketQueryV4API findMarketQueryV4API()
{
    return getSystemAdminAPI();
}

@Override
public MarketQueryV5API findMarketQueryV5API()
{
    throw new UnsupportedOperationException("Method not implemented.");
}


public ManualReportingAPI findManualReportingAPI()
{
    return ManualReportingAPIFactory.find();
}

    /**
 * findMarketQueryAPI method comment.
 */
public MarketQueryV3API findMarketQueryAPI()
{
    return getSystemAdminAPI();
}
/**
 * findOrderQueryAPI method comment.
 */
public OrderQueryV3API findOrderQueryAPI()
{
    return getSystemAdminAPI();
}

public ProductConfigurationQueryAPI findProductConfigurationQueryAPI()
{
    return ProductConfigurationQueryAPIFactory.find();
}

/**
 * findProductDefinitionAPI method comment.
 */
public ProductDefinitionAPI findProductDefinitionAPI()
{
    return getSystemAdminAPI();
}
/**
 * findProductQueryAPI method comment.
 */
public ProductQueryAPI findProductQueryAPI()
{
    return getSystemAdminAPI();
}
/**
 * findQuoteAPI method comment.
 */
public QuoteV7API findQuoteAPI()
{
    return getSystemAdminAPI();
}
/**
 * findTradingSessionAPI method comment.
 */
public TradingSessionAPI findTradingSessionAPI()
{
    return getSystemAdminAPI();
}
/**
 * findUserPreferenceQueryAPI method comment.
 */
public UserPreferenceQueryAPI findUserPreferenceQueryAPI()
{
    return getSystemAdminAPI();
}
/**
 * findUserTradingParametersAPI method comment.
 */
public UserTradingParametersAPI findUserTradingParametersAPI()
{
    return getSystemAdminAPI();
}
/**
 */
private SystemAdminAPI getSystemAdminAPI()
{
    return SystemAdminAPIFactory.find();
}
/**
 * findUserHistoryAPI method comment.
 */
public UserHistoryAPI findUserHistoryAPI()
{
    return getSystemAdminAPI();
}

/**
 * This method suppose to contain all the clean up logic for the API, i.e. clearing caches,
 * unsubscribing from event channels, setting variables to null, ...
 */
public void cleanUp()
{
    // Do nothing here
}

public MarketQueryV2API findMarketQueryV2API()
{
    return getSystemAdminAPI();
}

public OrderQueryV2API findOrderQueryV2API()
{
    return getSystemAdminAPI();
}

public QuoteV2API findQuoteV2API()
{
    return getSystemAdminAPI();
}

public OrderManagementTerminalAPI findOrderManagementTerminalAPI()
{
    return SAOrderManagementTerminalAPIFactory.find();
}

public FloorTradeAPI findFloorTradeAPI()
{
  	return getSystemAdminAPI();
}

/**
 * Return the API that supports querying for daily order counts (e.g., cumulative quantities and prices);
 *
 * @return
 */
public synchronized OrderFillCountAPI findOrderFillCountAPI()
{
    throw new UnsupportedOperationException("Method not implemented.");
}
}
