package com.cboe.domain.util;


import java.text.*;
import com.cboe.idl.cmiUtil.*;
import com.objectwave.persist.*;
import com.cboe.interfaces.domain.Price;


/**
 * A wrapper for Price that can be used as an attribute of a persistent
 * object.
 *
 * @author John Wickberg
 */
public class PriceSqlType implements Price, SqlScalarType
{
	/**
	 * Value used to represent no price in the database.  A null value or empty
	 * string (which is equivalent to a null value in Oracle) cannot be used due to
	 * the way nulls are processed in JavaGrinder.  JavaGrinder will not call the
	 * SqlTypeFactory for nulls.
	 *
	 */
	public static final String NO_PRICE_DB_VALUE = "NONE";
	/**
	 * Wrapped price instance.
	 */
	private final Price delegate;

	//
	// We need to register a generator for this class with the factory for sqlScalarTypes.
	//
	static
	{
		SqlScalarTypeFactory.registerGenerator(new SqlScalarTypeGeneratorIF()
			{
				public Class typeGenerated() { return PriceSqlType.class; }
				public SqlScalarType createInstance(String dbString)
				{
					return new PriceSqlType(dbString);
				}
			} );
	}
/**
 * Creates wrapper for existing price.
 */
public PriceSqlType(Price aPrice) {
	delegate = aPrice;
}
/**
 * This method creates a price equal to specified price
 */
public PriceSqlType(double aPrice)
{
	delegate = PriceFactory.create(aPrice);
}
/**
 * This method creates a price equal to specified price
 */
public PriceSqlType(long aPrice)
{
	delegate = PriceFactory.create(aPrice);
}
/**
 * This method was created by a SmartGuide.
 * @param initialValue com.cboe.corba.utils.PriceStruct
 */
public PriceSqlType(PriceStruct initialValue)
{
	delegate = PriceFactory.create(initialValue);
}
/**
 * This method creates a price from a database string.
 *
 * @param aPrice a string formatted by toDatabaseString
 */
public PriceSqlType(String aPrice)
{
	if (aPrice.equals(NO_PRICE_DB_VALUE))
	{
		delegate = new NoPrice();
	}
	else
	{
		delegate = PriceFactory.create(aPrice);
	}
}
/**
 * Forwards call to delegate price.
 *
 * @deprecated Use {@link #addTicks(int, Price, Price, Price)} which allows break point and tick sizes to be specified.
 */
public Price addTicks(int ticks)
{
	return new PriceSqlType(delegate.addTicks(ticks));
}
/**
 * Forwards call to delegate price.
 */
public Price addTicks(int ticks, Price breakPoint, Price tickSizeBelow, Price tickSizeAbove)
{
	return delegate.addTicks(ticks, breakPoint, tickSizeBelow, tickSizeAbove);
    //delegate=delegate.addTicks(ticks, breakPoint, tickSizeBelow, tickSizeAbove);
    //return this;
}

public Price addPrice(Price priceToAdd)
{
    return new PriceSqlType(delegate.addPrice(priceToAdd));
}

public Price subtractPrice(Price priceToSubtract)
{
    return new PriceSqlType(delegate.subtractPrice(priceToSubtract));
}

public Price subtractAndKeepSign(Price priceToSubtract)
{
    return new PriceSqlType(delegate.subtractAndKeepSign(priceToSubtract));
}

/**
 * Return the number of tick difference of price from the original price.
 */
public int getTickDifference(Price price, Price breakPoint, Price tickSizeBelow, Price tickSizeAbove)
{
    return delegate.getTickDifference(price, breakPoint, tickSizeBelow, tickSizeAbove);
}
/**
 * Forwards call to delegate price.
 */
public boolean equals(Object anObject)
{
	if (anObject instanceof PriceSqlType)
	{
		return delegate.equals(((PriceSqlType) anObject).delegate);
	}
	else
	{
		// allow Price to be compared to PriceSqlType
		return delegate.equals(anObject);
	}
}
/**
 * Forwards call to delegate price.
 */
public int getFraction()
{
	return delegate.getFraction();
}
/**
 * Forwards call to delegate price.
 */
public int getFraction(int denominator)
{
	return delegate.getFraction(denominator);
}
/**
 * Forwards call to delegate price.
 */
public int getWhole()
{
	return delegate.getWhole();
}
/**
 * Forwards call to delegate price.
 */
public int getWhole(int denominator)
{
	return delegate.getWhole(denominator);
}
/**
 * Forwards call to delegate price.
 */
public boolean greaterThan(Price anotherPrice)
{
		return !lessThan(anotherPrice) && !equals(anotherPrice);
}
/**
 * Forwards call to delegate price.
 */
public boolean greaterThanOrEqual(Price anotherPrice)
{
		return !lessThan(anotherPrice);
}
/**
 * Forwards call to delegate price.
 */
public int hashCode()
{
	return delegate.hashCode();
}
/**
 * Forwards call to delegate price
 */
public boolean isCabinetPrice()
{
    return delegate.isCabinetPrice();
}
/**
 * Forwards call to delegate price.
 */
public boolean isMarketPrice()
{
	return delegate.isMarketPrice();
}
/**
 * Forwards call to delegate price.
 */
public boolean isNoPrice()
{
	return delegate.isNoPrice();
}
/**
 * Forwards call to delegate price.
 */
public boolean isValuedPrice()
{
	return delegate.isValuedPrice();
}
/**
 * Forwards call to delegate price.
 */
public boolean lessThan(Price anotherPrice)
{
	if (anotherPrice instanceof PriceSqlType)
	{
		return delegate.lessThan(((PriceSqlType) anotherPrice).delegate);
	}
	else
	{
		// all Price to be compared to PriceSqlType
		return delegate.lessThan(anotherPrice);
	}
}
/**
 * Forwards call to delegate price.
 */
public boolean lessThanOrEqual(Price anotherPrice)
{
		return lessThan(anotherPrice) || equals(anotherPrice);
}
/**
 * Creates a string representation of the price for storage in the database.
 *
 * @return decimally formatted string
 */
public String toDatabaseString()
{
	if (delegate.isNoPrice())
	{
		return NO_PRICE_DB_VALUE;
	}
	else
	{
		return delegate.toString();
	}
}
/**
 * Converts price to double value.
 *
 * @return converted value.
 */
public double toDouble()
{
	return delegate.toDouble();
}
/**
 * Converts price to long value.
 *
 * @return converted value.
 */
public long toLong()
{
	return delegate.toLong();
}
/**
 * Converts a price to a printable string
 *
 * @return formatted string
 */
public String toString()
{
	return delegate.toString();
}
/**
 * Converts this price to a price structure.
 *
 * @return price structure for this price.
 */
public PriceStruct toStruct()
{
	return delegate.toStruct();
}
}
