package com.cboe.interfaces.domain;

/**
 * Represents buy/sell, and As_defined/opposite sides.
 *
 * @author Mark Novak
 * @author Tom Lynch
 * @author John Wickberg
 */
public interface Side {

	public final static String BUY_STRING = "BUY";
	public final static String SELL_STRING = "SELL";
    public final static String AS_DEFINED_STRING = "DFND";
    public final static String OPPOSITE_STRING = "OPST";
    public final static String SELL_SHORT_STRING = "SSHT";
    public final static String SELL_SHORT_EXEMPT_STRING = "SSX";

/**
 * @return com.cboe.utils.Side
 */
public Side getOtherSide();
/**
 * This method will return whether the first price passed is better than the second price.
 * @return boolean
 * @param firstPrice com.cboe.utils.Price
 * @param secondPrice com.cboe.utils.Price
 */
public boolean isFirstBetter(Price firstPrice, Price secondPrice );
/**
 * This method will return whether the first price passed is better than or equal to the second price.
 * This is a convenience method to make code more readable.
 * @return boolean
 * @param firstPrice com.cboe.utils.Price
 * @param secondPrice com.cboe.utils.Price
 */
public boolean isFirstBetterOrEqual(Price firstPrice, Price secondPrice );

/**
 * return a boolean to indicate if this side is at the same side as aSide.
 */
public boolean isSameSide(Side aSide);
/**
 * @return boolean
 */
public boolean isBuySide( );

/**
 * @return boolean
 */
public boolean isSellSide();

/**
 * @return boolean
 */
public boolean isAsDefinedSide( );

/**
 * @return boolean
 */
public boolean isOppositeSide( );

/**
 * One tick ahead depends on the side. For the sell side, one tick ahead is to
 * subtract one tick from the price, while for the buy side, one tick ahead is to
 * add one tick to the price.
 * @ return int
 */
public int getMinTickAhead();

/**
 * Compare two prices(one is for its own side,  and the other is for the other side.
 * return true if this two prices are tradable
 */
public boolean areTwoPricesTradable(Price ownSidePrice, Price otherSidePrice);

/**
 * return the value representing this side. The values are defined by interface Sides
 */
public char toSideValue();

/**
 * return the Price that is equal to the "orginal price" improved by some "amount"
 * for example a BuySide price will be increased by "amount"
 * for example a SellSide price will be decreased by "amount"
 */
public Price calculateMoreCompetitivePrice( Price origPrice, Price amount );

/**
 * return the Price that is equal to the "orginal price" worsened by some "amount"
 * for example a BuySide price will be decreased by "amount"
 * for example a SellSide price will be increased by "amount"
 */
public Price calculateLessCompetitivePrice( Price origPrice, Price amount );

/**
 * If the 2 tradables are exact match on price
 * It is the extreme case of areTwoPricesTradable()
 * @param ownSidePrice
 * @param otherSidePrice
 * @return
 */
public boolean areTwoPricesLocked(Price ownSidePrice, Price otherSidePrice);

/**
 * Return best of both prices
 * @param firstPrice
 * @param secondPrice
 * @return
 */
public Price getBestPrice(Price firstPrice, Price secondPrice);

}
