package com.cboe.interfaces.domain;


/**
 * An iterator used to pass a block of quotes from the MarketMakerQuoteService to
 * the BrokerService.
 *
 * @author John Wickberg
 */
public interface QuoteBlockIterator {
	
	void process();
	
	void init();
	
    int getClassKey();

}
