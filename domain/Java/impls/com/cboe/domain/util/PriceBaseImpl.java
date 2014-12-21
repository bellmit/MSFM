package com.cboe.domain.util;

import java.text.*;
import com.cboe.idl.cmiUtil.*;
import com.cboe.idl.cmiConstants.*;
import com.cboe.interfaces.domain.Price;

/**
 * This class is used to represent prices.
 *
 * @author John Wickberg
 */

public abstract class PriceBaseImpl implements Price
{
/**
 * Produces a new <code>Price</code> offset by the number of ticks.
 * The actual price difference between the original price and the
 * price with the ticks added will depend upon the original price.
 * For prices under 3 dollars, a tick is equal to 1/16.  For prices
 * of 3 dollars or higher, a tick is equal to 1/8.
 *
 * @param ticks		number of ticks to add to this price.  If ticks is negative,
 *					the value is subtracted from the price.
 * @return 			new <code>Price</code> object offset by number of ticks.
 * @since Increment 2
 * @deprecated Use {@link #addTicks(int, Price, Price, Price)} where break point and tick size are parameters
 */
public Price addTicks(int ticks)
{
	throw new IllegalArgumentException("Method addTicks only implemented for ValuedPrice");
}
/**
 * Produces a new <code>Price</code> offset by the number of ticks.
 *
 * @param ticks		number of ticks to add to this price.  If ticks is negative,
 *					the value is subtracted from the price.
 * @param breakPoint price at which tick size changes
 * @param tickSizeBelow tick size for prices below break point
 * @param tickSizeAbove tick size for prices above break point
 * @return 			new <code>Price</code> object offset by number of ticks.
 * @since Increment 5
 */
public Price addTicks(int ticks, Price breakPoint, Price tickSizeBelow, Price tickSizeAbove)
{
	throw new IllegalArgumentException("Method addTicks only implemented for ValuedPrice");
}

public Price addPrice(Price priceToAdd)
{
    throw new IllegalArgumentException("Method addPrice only implemented for ValuedPrice");
}

public Price subtractPrice(Price priceToSubtract)
{
    throw new IllegalArgumentException("Method subtractPrice only implemented for ValuedPrice");
}

public Price subtractAndKeepSign(Price priceToSubtract)
{
    throw new IllegalArgumentException("Method subtractPrice only implemented for ValuedPrice");
}

/**
 * Return the number of tick difference of price from the original price.
 */
public int getTickDifference(Price price, Price breakPoint, Price tickSizeBelow, Price tickSizeAbove)
{
	throw new IllegalArgumentException("Method getTickDifference only implemented for ValuedPrice");
}
   /**
 * Gets the fractional value of the price.
 *
 * @return int
 */
public int getFraction()
{
	throw new IllegalArgumentException("Only implemented by ValuedPrice");
}
/**
 * Gets the fractional value of the price with a specified denominator.  The frational value
 * will be converted from its default denominator to the requested denominator.
 *
 * @param denominator value that fraction will be expressed in.
 * @return fraction in requested denominator
 */
public int getFraction(int denominator)
{
	throw new IllegalArgumentException("Only implemented by ValuedPrice");
}
/**
 * Gets the whole dollar amount of this price.
 *
 * @return whole dollar amount
 */
public int getWhole()
{
	throw new IllegalArgumentException("Only implemented by ValuedPrice");
}
/**
 * Gets the whole value of the price with a specified denominator.  The whole
 * value will be rounded according to the given denominator.
 *
 * @param denominator used for rounding whole value
 * @return whole value rounded according to denominator
 */
public int getWhole(int denominator)
{
	throw new IllegalArgumentException("Only implemented by ValuedPrice");
}
/**
 * Performs greater than comparison between this price and another price.
 *
 * @return true if this price is greater than the other price
 *
 * @param anotherPrice - a price to be tested
 */
public boolean greaterThan(Price anotherPrice)
{
	return !lessThan( anotherPrice ) && !equals( anotherPrice ) ;
}
/**
 * Performs greater than or equal to comparison between this price and another price.
 *
 * @return true if this price is greater than or equal to the other price
 *
 * @param anotherPrice - a price to be tested
 */
public boolean greaterThanOrEqual(Price anotherPrice)
{
	return !lessThan( anotherPrice );
}
/**
 * Checks to see if this price is a cabinet price
 *
 * @return true if this price is a cabinet price
 */
public boolean isCabinetPrice()
{
    return false;
}
/**
 * Checks to see if this price is a market price.
 *
 * @return true if this price is a market price
 */
public boolean isMarketPrice()
{
	return false;
}
/**
 * Checks to see if this price is a no price.
 *
 * @return true if this price is a no price
 */
public boolean isNoPrice()
{
	return false;
}
/**
 * Checks to see if this price is a valued price.
 *
 * @return true if this price is a valued price
 */
public boolean isValuedPrice()
{
	return false;
}
/**
 * Performs less than comparison between this price and another price.
 *
 * @return true if this price is less than the other price
 *
 * @param anotherPrice - a price to be tested
 */
public boolean lessThan(Price anotherPrice)
{
	throw new IllegalArgumentException("Only implemented by ValuedPrice");
}
/**
 * Performs less than or equal to comparison between this price and another price.
 *
 * @return true if this price is less than or equal to the other price
 *
 * @param anotherPrice - a price to be tested
 */
public boolean lessThanOrEqual(Price anotherPrice)
{
	return lessThan( anotherPrice ) || equals( anotherPrice ) ;
}
/**
 * Converts price to double value.
 *
 * @return converted value.
 */
public double toDouble()
{
	throw new IllegalArgumentException("Only implemented by ValuedPrice");
}
/**
 * Converts price to long value.  The value returned by this method will have an
 * implied decimal point, it is not just the whole dollar amount.
 *
 * @return converted value.
 */
public long toLong()
{
	throw new IllegalArgumentException("Only implemented by ValuedPrice");
}
/**
 * Converts this price to a price structure.
 *
 * @return price structure for this price.
 */
public abstract PriceStruct toStruct();
}
