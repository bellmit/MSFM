//
// -----------------------------------------------------------------------------------
// Source file: APIFactoryImpl.java
//
// PACKAGE: com.cboe.presentation.api;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2001 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.api;

import com.cboe.interfaces.presentation.api.*;
import com.cboe.interfaces.presentation.productConfiguration.ProductConfigurationQueryAPI;

/**
 * Implements the finders for an APIFactory for the client GUI.
 * @author Nick DePasquale
 * @author Troy Wehrle
 */
public class APIFactoryImpl implements APIFactory
{
    private OrderFillCountAPI orderCountAPI;

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
  return getMarketMakerAPI();
}
/**
 * findCommonAPI method comment.
 */
public CommonAPI findCommonAPI()
{
  return getMarketMakerAPI();
}

public MarketQueryV4API findMarketQueryV4API()
{
    return getMarketMakerAPI();
}


public MarketQueryV5API findMarketQueryV5API()
{
	return getMarketMakerAPI();
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
  return getMarketMakerAPI();
}
/**
 * findOrderQueryAPI method comment.
 */
public OrderQueryV3API findOrderQueryAPI()
{
  return getMarketMakerAPI();
}

public OrderManagementTerminalAPI findOrderManagementTerminalAPI()
{
    return OrderManagementTerminalAPIFactory.find();
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
  return getMarketMakerAPI();
}
/**
 * findProductQueryAPI method comment.
 */
public ProductQueryAPI findProductQueryAPI()
{
  return getMarketMakerAPI();
}
/**
 * findQuoteAPI method comment.
 */
public QuoteV7API findQuoteAPI()
{
  return getMarketMakerAPI();
}
/**
 * findTradingSessionAPI method comment.
 */
public TradingSessionAPI findTradingSessionAPI()
{
  return getMarketMakerAPI();
}
/**
 * findUserPreferenceQueryAPI method comment.
 */
public UserPreferenceQueryAPI findUserPreferenceQueryAPI()
{
  return getMarketMakerAPI();
}
/**
 * findUserTradingParametersAPI method comment.
 */
public UserTradingParametersAPI findUserTradingParametersAPI()
{
  return getMarketMakerAPI();
}

public FloorTradeAPI findFloorTradeAPI()
{
  return getMarketMakerAPI();
}

/**
 * This method suppose to contain all the clean up logic for the API, i.e. clearing caches,
 * unsubscribing from event channels, setting variables to null, ...
 */
public void cleanUp()
{
    //Do nothing here
}

/**
 * 
 */
private MarketMakerAPI getMarketMakerAPI()
{
  return MarketMakerAPIFactory.find();
}
/**
 * findUserHistoryAPI method comment.
 */
public UserHistoryAPI findUserHistoryAPI()
{
  return getMarketMakerAPI();
}

/**
 * Return the API that supports querying for daily order counts (e.g., cumulative quantities and prices);
 *
 * @return
 */
public synchronized OrderFillCountAPI findOrderFillCountAPI()
{
    if (orderCountAPI == null)
    {
        orderCountAPI = new OrderFillCountAPIImpl();
    }
    return orderCountAPI;
}
}
