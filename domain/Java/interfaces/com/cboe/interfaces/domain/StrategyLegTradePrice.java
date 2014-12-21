package com.cboe.interfaces.domain;

/**
 * Holds the result of calculated prices for a leg of a strategy.
 *
 */
public interface StrategyLegTradePrice {
	
	/**
	 * The following constants defines leg trade price's relation 
	 * to the leg market.
	 * 
	 * NOT_APPLY: no relation check 
	 * MARKET_INVERTED: the bid ask price is inverted
	 * IN_BETWEEN: price above the bid, and below the ask.
	 * TOUCH_CBOE_BID: price touches CBOE bid and CBOE bid is the BB
	 * TOUCH_CBOE_ASK: price touches CBOE ask and CBOE ask is the BO
	 * TOUCH_CBOE_BOTH: price touches CBOE bid and ask, and they are the BBO
	 * OUTSIDE_BB: price is outside BBO
	 * OUTSIDE_BO: price is outside BBO
	 * 
	 */
	enum TradePriceMarketRelation
	{
		NOT_APPLY(true, false, false, 0),
		INCOMPLETE_MARKET(false, false, false, 0),
		LOCK_AT_LOCKED_MARKET(true, false, false, 0),
		MARKET_INVERTED(false, true, false, 0),
		IN_BETWEEN(true, false, false, 0),
		IN_NO_ASK(true, false, false, 0),
		TOUCH_CBOE_BID_NO_ASK(true, true, false, +1),
		TOUCH_CBOE_BID_WIDER_THAN_TICK(true, true, false, +1),
		TOUCH_CBOE_ASK_WIDER_THAN_TICK(true, true, false, -1),
		TOUCH_CBOE_BID_TICK_WIDE(true, true, true, +1),
		TOUCH_CBOE_ASK_TICK_WIDE(true, true, true, -1),
		TOUCH_CBOE_BOTH(true, true, false, 0),
		OUTSIDE_BB(false, false, false, +1),
		OUTSIDE_BO(false, false, false, -1);

		public final boolean validTradePrice;
		public final boolean touchMarket;
		public final boolean tickWideAndTouchMarket;
		public final int adjustDirection;

		TradePriceMarketRelation(
				boolean validTradePrice,
				boolean touchMarket,
				boolean tickWideAndTouchMarket,
				int adjustDirection)
		{
			this.validTradePrice = validTradePrice;
			this.touchMarket = touchMarket;
			this.tickWideAndTouchMarket = tickWideAndTouchMarket;
            this.adjustDirection = adjustDirection;
		}
	}
    
	/**
	 * Gets the ratio quantity of the leg.
	 */
	int getRatioQuantity();

	/**
	 * Gets the side of the leg.
	 */
	Side getSide();

	/**
	 * Gets the price at which the leg should be traded. Without split trade, the length is one.
         * With split trade, the length is two.
	 */
	Price[] getTradePrices();

    /**
     * get the first element of trade prices. This is basically a convenient method for non split trade
     */
    Price getTradePrice();

	/**
	 * Gets the trading product.
	 */
	TradingProduct getTradingProduct();

    /**
     * Gets the product key.
     */
    int getProductKey();

    /**
     * Return a boolean to indicate if the split trading method has been employed
     * in the process of calculating the trade prices for the leg
     */
    boolean splitTradingEmployed();

    /**Return an array with two numbers ( x + y = 1) which tells how to split the volume.
     * The first number corresponding to the first trade price and second number corresponding
     * to the second price of the results of getTradePrices().
     *
     * Note: if splitTradingEmployed() return false, the result of this method is not defined.
     */
    double[] getSplitRatios();
}
