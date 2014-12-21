package com.cboe.interfaces.domain.marketData;




/**
 * Market data history service is used to create market data history entries.
 * History entries are created in the history server. This is done to limit 
 * the amount of garbage created due to history entry creation. This in turn
 * reduces the GC times. This should improve thruput.  
 * The interaction sequence:
 * MarketDataHistoryHomeImpl -> MarketDataHistoryServiceHomeImpl -> 
 * 1. MarketDataHistoryServiceProxyImpl (for trade servers) -> CORBA call to 
 *    history server -> MarketDataServiceStub in history server -> 
 *    MarketDataHistoryHomeImpl -> MarketDataHistoryServiceHomeImpl ->
 *    MarketDataHistoryServiceLocalImpl -> persist to database
 * 2. MarketDataHistoryServiceLocalImpl (for history server) -> persist to database
 *  
 * @author singh
 *
 */

public interface MarketDataHistoryServiceHome
{
	public static final String HOME_NAME = "MarketDataHistoryServiceHome";
	
	MarketDataHistoryService getServiceObject ();
	
	String getServiceIdentifier ();
	
	void releaseServiceObject (MarketDataHistoryService serviceObject);
	
	void notifyHomeOfServiceFailure (MarketDataHistoryService failedService);
	
	void notifyHomeOfServiceResumption (String historyServerRouteName);
}
