package com.cboe.interfaces.domain;

/**
 * A definition of a leg of a strategy.
 *
 * @author John Wickberg
 */
public interface TradingStrategyLeg {

	/**
	 * Gets the ratio quantity.
	 */
	public int getRatioQuantity();

	/**
	 * Gets the side.
	 */
	public Side getSide();

	/**
	 * Gets the trading product of this leg.
	 */
	public TradingProduct getTradingProduct();

	/**
	 * Gets the productKey of this leg.
	 */
	public int getProductKey();
}
