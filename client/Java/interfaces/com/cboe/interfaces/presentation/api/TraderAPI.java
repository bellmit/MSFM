package com.cboe.interfaces.presentation.api;



/**
 * This interface represents the Trader application API to the CAS.
 *
 * @author Derek T. Chambers-Boucher
 * @version 10/12/1999
 */
public interface TraderAPI extends  CommonAPI,
                                    MarketQueryAPI,
                                    ProductQueryAPI,
                                    OrderEntryAPI,
                                    OrderQueryAPI,
                                    ProductDefinitionAPI,
                                    TradingSessionAPI,
                                    UserPreferenceQueryAPI,
                                    UserHistoryAPI,
                                    MarketQueryV2API,
                                    MarketQueryV3API,
                                    OrderQueryV2API,
                                    OrderQueryV3API,
                                    OrderEntryV3API,
                                    OrderEntryV5API,
                                    OrderEntryV7API,
                                    FloorTradeAPI

{
    static public final String TRANSLATOR_NAME = "TRANSLATOR";

    OrderEntryFacade getOrderEntryAPI();

}
