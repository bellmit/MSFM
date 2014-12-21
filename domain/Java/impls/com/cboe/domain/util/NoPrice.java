package com.cboe.domain.util;

import com.cboe.idl.cmiUtil.*;
import com.cboe.idl.cmiConstants.*;
import com.cboe.interfaces.domain.Price;

/**
 * A no price object is a use of the Null Object pattern to represent a null price.
 * A null price struct cannot be sent in a CORBA call, so instances of this class
 * will be used instead.
 *
 * @author John Wickberg
 */
public class NoPrice extends PriceBaseImpl
{
    private static PriceStruct noPriceStruct = new PriceStruct(PriceTypes.NO_PRICE, 0, 0);
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
		result = ((Price) anObject).isNoPrice();
	}
	return result;
}
/**
 * Calculates hash value for this price.
 *
 * @return hash value of no prices
 */
public int hashCode()
{
	return 0;
}
/**
 * Checks to see if this price is a no price.
 *
 * @return true - all instances are no prices
 */
public boolean isNoPrice()
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
	return NO_PRICE_STRING;
}
/**
 * Converts this price to a price structure.
 *
 * @return price structure for this price.
 */
public PriceStruct toStruct()
{
    PriceStruct aStruct = noPriceStruct;
    
    if ((noPriceStruct.type != PriceTypes.NO_PRICE) || (aStruct.whole != 0) || (aStruct.fraction != 0))
    {
        aStruct = new PriceStruct();
        aStruct.type = PriceTypes.NO_PRICE;
        aStruct.whole = 0;
        aStruct.fraction = 0;
    }
    
	return aStruct;
}
}
