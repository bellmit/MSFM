package com.cboe.interfaces.domain;




/**
 * Quote history service is used to create quote history entries.
 * History entries are created in the history server. This is done to limit 
 * the amount of garbage created due to history entry creation. This in turn
 * reduces the GC times. This should improve thruput.  
 * The interaction sequence:
 * QuoteHistoryHomeImpl -> QuoteHistoryServiceHomeImpl -> 
 * 1. QuoteHistoryServiceProxyImpl (for trade servers) -> CORBA call to 
 *    history server -> QuoteServiceStub in history server -> 
 *    QuoteHistoryHomeImpl -> QuoteHistoryServiceHomeImpl ->
 *    QuoteHistoryServiceLocalImpl -> persist to database
 * 2. QuoteHistoryServiceLocalImpl (for history server) -> persist to database
 *  
 * @author singh
 *
 */
public interface QuoteHistoryServiceHome
{
	public static final String HOME_NAME = "QuoteHistoryServiceHome";
	
	QuoteHistoryService getServiceObject ();
	
	void releaseServiceObject (QuoteHistoryService serviceObject);
	
	void notifyHomeOfServiceFailure (QuoteHistoryService failedService);
	
	void notifyHomeOfServiceResumption (String historyServerRouteName);

	String getServiceIdentifier();

}
