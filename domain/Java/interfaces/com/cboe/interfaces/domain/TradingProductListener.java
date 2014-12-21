package com.cboe.interfaces.domain;

import java.util.EventListener;

/**
 * A listener for state and book changes to a product.  The intended use
 * is for strategies to listen to changes to their legs, but chose a general
 * approach so that it could be used for other purposes if needed.
 *
 * @author John Wickberg
 */

public interface TradingProductListener extends EventListener {

	/**
	 * Notifies listener that the best book of a monitored product has changed.
	 *
	 * @param product trading product firing event
	 * @param book order book of trading product
	 * @param marketStatus code indicating new status of market for product
	 */
	void bestBookUpdateEvent(TradingProduct product, OrderBook book, int marketStatus);

	/**
	 * Notifies listener that trade has happened and gives the last sale price.
	 *
	 * @param product trading product firing event
	 * @param lastSalePrice price of most recent trade
	 */
	void lastSaleEvent(TradingProduct product, Price lastSalePrice);

	/**
	 * Notifies listener that the state of a monitored product has changed.
	 *
	 * @param product trading product firing event
	 * @param newState new state of product
	 */
	void stateChangeEvent(TradingProduct product, short oldState, short newState);
}
