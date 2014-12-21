package com.cboe.domain.util;

import com.cboe.idl.cmiUtil.*;
import com.cboe.idl.cmiConstants.*;
import com.cboe.interfaces.domain.Price;

/**
 * A market price is used on orders when the buyer or seller is willing to take
 * the current best price.
 * 
 * @author John Wickberg
 */
public class MarketPrice extends PriceBaseImpl
{
/**
 * Compares an object to this price.
 *
 * @return true if object is semantically equivalent to this object.
 *
 * @param anObject - object to be tested
 */
public boolean equals(Object anObject)
{
	boolean result = false;
	// handles case where object may be a PriceSqlType wrapping a Price
	if (anObject instanceof Price)
	{
		result = ((Price) anObject).isMarketPrice();
	}
	return result;
}
/**
 * Calculates hash value for this price.
 *
 * @return hash value of market prices
 */
public int hashCode()
{
	return 0;
}
/**
 * Checks to see if this price is a market price.
 * 
 * @return true - all instances are market prices
 */
public boolean isMarketPrice()
{
	return true;
}
/**
 * Converts a price to a printable string
 *
 * @return formatted string
 */
public String toString()
{
	return MARKET_STRING;
}
/**
 * Converts this price to a price structure.
 *
 * @return price structure for this price.
 */
public PriceStruct toStruct()
{
	PriceStruct aStruct = new PriceStruct();
	aStruct.type = PriceTypes.MARKET;
	aStruct.whole = 0;
	aStruct.fraction = 0;
	return aStruct;
}

public boolean lessThan(Price anotherPrice)
{
    if ( anotherPrice.isValuedPrice() )
    {
        return false;
    }
    throw new IllegalArgumentException( "Comparisons can only be done between valued price and MKT price");
}
}
