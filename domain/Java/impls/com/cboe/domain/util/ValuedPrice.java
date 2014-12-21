package com.cboe.domain.util;

import com.cboe.idl.cmiConstants.PriceTypes;
import com.cboe.idl.cmiUtil.PriceStruct;
import com.cboe.interfaces.domain.Price;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

/**
 * This class is used to represent prices.
 *
 * @author Dave Wegener
 * @author John Wickberg
 * 
 */

public class ValuedPrice extends PriceBaseImpl {
	/**
	 * Default scale to be used when scale is not specified in constructor.
	 */
	public final static int DEFAULT_SCALE = 1000000000;

	/**
	 * Default premium price break point.
	 */
	public final static ValuedPrice DEFAULT_BREAK_POINT = new ValuedPrice(3.0);

	/**
	 * Default tick size below break point.
	 */
	public final static ValuedPrice DEFAULT_TICK_SIZE_BELOW = new ValuedPrice(0.0625);

	/**
	 * Default tick size above break point.
	 */
	public final static ValuedPrice DEFAULT_TICK_SIZE_ABOVE = new ValuedPrice(0.125);

	/**
	 * Largest difference that will be considered zero for price comparisons.
	 */
	private final static double EPSILON = 1.0e-12D;
	/**
	 * String of zeros for padding formatted price.
	 */
	private final static String ZERO_PADDING = "00000000000";
	/**
	 * Fraction denominator for display.
	 */
	public final static int FRACTION_DENOMINATOR = 10000;
	/**
	 * Number of digits to use for price fraction.
	 */
	private final static int FRACTION_LENGTH = 4;
	/**
	 * Price value in standard form.
	 */
	private long value;
	/**
	 * Formatted representation of price.  No public method can change a price value once it has
	 * been created, so caching the formatted price is OK.
	 */
	private transient String formattedPrice = null;
/**
 * Creates a default price.
 */
private ValuedPrice()
{
	setValue(0L);
	createFormattedPrice();
}
/**
 * Creates a price equal with its value set to the specified price.
 *
 * @param aPrice - the price value to be used.
 */
public ValuedPrice(long aPrice)
{
	setValue(aPrice);
	createFormattedPrice();
}
/**
 * Creates a price equal with its value set to the specified price.
 *
 * @param aPrice - the price value to be used.
 */
public ValuedPrice(double aPrice)
{
	this();
	setPrice(aPrice);
	createFormattedPrice();
}
/**
 * Creates a price using the passed price struct as its value.
 *
 * @param initialValue - the initial price value to be copied and saved
 */
public ValuedPrice(PriceStruct initialValue)
{
	setValue((long) initialValue.whole * DEFAULT_SCALE + (long) initialValue.fraction);
	createFormattedPrice();
}
/**
 * Creates a price from a string.
 *
 * @param aPrice a valid price.
 */
public ValuedPrice(String aPrice)
{
	this();
	setPrice(aPrice);
	createFormattedPrice();
}

/**
 * Creates a printable string from the price
 *
 */
private void createFormattedPrice()
{
    if(formattedPrice == null)
    {
        String fraction = "" + Math.abs(getFraction(FRACTION_DENOMINATOR));
        String sign = getValue() >= 0 ? "" : "-";
        formattedPrice = sign + Math.abs(getWhole(FRACTION_DENOMINATOR)) + "." + ZERO_PADDING.substring(0, FRACTION_LENGTH - fraction.length()) + fraction;
    }
}

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
 * @deprecated Use {@link #addTicks(int, Price, Price, Price)} where break point and tick sizes are specified.
 */
public Price addTicks(int ticks) {
	return addTicks(ticks, DEFAULT_BREAK_POINT, DEFAULT_TICK_SIZE_BELOW, DEFAULT_TICK_SIZE_ABOVE);
}
/**
 * Produces a new <code>Price</code> offset by the number of ticks.
 *
 * @see Price#addTicks(int, Price, Price, Price)
 * @since Increment 5
 */
public Price addTicks(int ticks, Price breakPoint, Price tickSizeBelow, Price tickSizeAbove) {
	long breakPointValue = breakPoint.toLong();
	long belowValue = tickSizeBelow.toLong();
	long aboveValue = tickSizeAbove.toLong();
	long myValue = getValue();
	long multiplier = 1;
	long newValue;
	if (myValue < 0) {
		myValue = -myValue;
		ticks = -ticks;
		multiplier = -1;
	}
	if (myValue >= breakPointValue) {
		newValue = myValue + ticks * aboveValue;
		// need to check if below tick must be used
		if (newValue < breakPointValue) {
			// note that ticks must be negative so adding will decrease magnitude
			long belowTicks = ticks + (myValue - breakPointValue) / aboveValue;
			newValue = breakPointValue + belowTicks * belowValue;
		}
	}
	else {
		newValue = myValue + ticks * belowValue;
		// need to check if above tick must be used
		if (newValue > breakPointValue) {
			long aboveTicks = ticks - (breakPointValue - myValue) / belowValue;
			newValue = breakPointValue + aboveTicks * aboveValue;
		}
	}
	ValuedPrice newPrice = PriceFactory.createValuedPrice(multiplier * newValue);
	return newPrice;
}

public Price addPrice(Price priceToAdd)
{
    long myValue = getValue();
    long priceToAddValue = priceToAdd.toLong();
    
    ValuedPrice newPrice = PriceFactory.createValuedPrice(myValue + priceToAddValue);
    return newPrice;
}

public Price subtractPrice(Price priceToSubtract)
{
    long myValue = getValue();
    long priceToAddValue = priceToSubtract.toLong();
    
    ValuedPrice newPrice = PriceFactory.createValuedPrice(Math.max(0, myValue - priceToAddValue));
    return newPrice;
}

public Price subtractAndKeepSign(Price priceToSubtract)
{
    long myValue = getValue();
    long priceToAddValue = priceToSubtract.toLong();
    
    ValuedPrice newPrice = PriceFactory.createValuedPrice(myValue - priceToAddValue);
    return newPrice;
}

/**
 * Return the number of tick difference of price from the original price.
  */
public int getTickDifference(Price price, Price breakPoint, Price tickSizeBelow, Price tickSizeAbove)
{
    if (!price.isValuedPrice())
    {
        return -1;
    }

    int difference = getTicksDifferenceFromPremiumBreakPoint(price, breakPoint, tickSizeBelow, tickSizeAbove) -
                     getTicksDifferenceFromPremiumBreakPoint(this, breakPoint, tickSizeBelow, tickSizeAbove);

    return difference;
}

private int getTicksDifferenceFromPremiumBreakPoint(Price price, Price breakPoint, Price tickSizeBelow, Price tickSizeAbove)
{
    long priceDiff = price.toLong() - breakPoint.toLong();
    if (priceDiff < 0)
    {
        return (int)(priceDiff/tickSizeBelow.toLong());
    }
    else
    {
        return (int)(priceDiff/tickSizeAbove.toLong());
    }
}

/**
 * For prices under 3 dollars, a tick is equal to 1/16.  For prices
 * of 3 dollars or higher, a tick is equal to 1/8.
 *
 * @return 			new Price object tick.
 */
public PriceStruct getTickValue() {
        ValuedPrice newPrice;
	if (getWhole() < 3) {
                newPrice = (ValuedPrice)PriceFactory.create((double)(1d/16));
	}
	else {
                newPrice = (ValuedPrice)PriceFactory.create((double)(1d/8));
	}
	return newPrice.toStruct();
}

/**
 * Converts a string with format xxx ss+ to a double with the ss+ value converted as sixteenths.
 *
 * @return converted value
 *
 * @param aPrice - a string in valid sixteenths format(xxx ss+).
 */
private static double convertSixteenth(String aPrice) throws NumberFormatException
{
	int sixteenthBegin = aPrice.lastIndexOf(' ');
	double convertedValue = 0.0;
	int sixteenths;
	if (sixteenthBegin < 0)
	{
		// optional whole value of price is missing
		sixteenths = Integer.parseInt(aPrice.substring(0, aPrice.length() - 1));
	}
	else
	{
		sixteenths = Integer.parseInt(aPrice.substring(sixteenthBegin + 1, aPrice.length() - 1));
		try
		{
		    convertedValue = Double.parseDouble(aPrice.substring(0, sixteenthBegin));
		} 
		catch(NumberFormatException nfe)
		{
		    throw new NumberFormatException("Price string does not contain a parsable double: " + aPrice);
		}
	}

	if (sixteenths > 15)
	{
		throw new NumberFormatException("Invalid number of sixteenths in price: " + aPrice);
	}

	return convertedValue += (0.0625 * sixteenths);
}
/**
 * Converts lowercase alpha characters
 * in the range of a - g to the appropriate fraction in eighths.
 *
 * @return converted value in eighths
 *
 * @param anEighth - a char between 'a' and 'g', must be lower case.
 */
private static double eighthValue(char anEighth) throws NumberFormatException
{
	if (anEighth < 'a' || anEighth > 'g')
	{
		throw new NumberFormatException("Invalid eighth " + anEighth);
	}
	return ((((int) anEighth) - ((int) ('a'))) + 1) * 0.125;
}
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
	// checking for Price allows comparisons between PriceSqlTypes and
	// ValuedPrices
	if (anObject instanceof Price)
	{
		Price anotherPrice = (Price) anObject;
		if (anotherPrice.isValuedPrice())
		{
		    result = getValue() == anotherPrice.toLong();
		}
	}
	return result;
}

public static int getFractionForValue (long aValue)
{
	return (int) (aValue % DEFAULT_SCALE);
}

/**
 * Gets the fractional value of the price.
 *
 * @return int
 */
public int getFraction()
{
	return (int) (getValue() % DEFAULT_SCALE);
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
	int tempFraction = getRawFraction(denominator) % denominator;
    // change sign if required.  Want whole + fraction to be correct.
    if (getValue() < 0) {
        tempFraction = -tempFraction;
    }
	return tempFraction;
}
/**
 * Gets the raw fractional value of the price with a specified denominator.
 * @param denominator value that fraction will be expressed in
 * @return fraction in requested denominator.  Note result is always positive and
 *         may equal denominator due to rounding.
 */
private int getRawFraction(int denominator)
{
    // use positive number to make sure rounding is done the correctly
	long tempFraction = Math.abs((long) getFraction() * 10L);
	tempFraction *= denominator;
	tempFraction /= DEFAULT_SCALE;
	tempFraction += 5;
	tempFraction /= 10;
	return (int) tempFraction;
}
/**
 * Gets value of price.  The value is a combination of the whole and fractional amounts.
 *
 * @return price value
 */
protected long getValue()
{
	return value;
}

public static int getWholeForValue (long aValue)
{
	return (int) (aValue / DEFAULT_SCALE);
}


/**
 * Gets the whole dollar amount of this price.
 *
 * @return whole dollar amount
 */
public int getWhole()
{
	return (int) (getValue() / DEFAULT_SCALE);
}
/**
 * Gets the whole dollar amount of this price.
 *
 * @return whole dollar amount
 */
public int getWhole(int denominator)
{
	int tempWhole = (int) Math.abs(getValue() / DEFAULT_SCALE);
    if (getRawFraction(denominator) == denominator) {
        tempWhole += 1;
    }
    if (getValue() < 0) {
        tempWhole = -tempWhole;
    }
    return tempWhole;
}
/**
 * Calculates hash value for this price.
 *
 * @return hash value of this price
 */
public int hashCode() {
	return (int) getValue();
}
/**
 * Checks to see if this price is a valued price.
 *
 * @return true - all instances are valued prices
 */
public boolean isValuedPrice()
{
	return true;
}

/**
 * Performs less than comparison between this price and another price.
 *
 * @return true if this price is greater than the other price
 *
 * @param anotherPrice - a price to be tested
 */
public boolean greaterThan(Price anotherPrice)
{    
    if ( anotherPrice.isMarketPrice() )
    {
        return true;
    }
    if ( !anotherPrice.isValuedPrice() )
    {
        throw new IllegalArgumentException( "Comparisons can only be done between valued prices or with mkt price");
    }
    
    return (getValue() > anotherPrice.toLong());
}

/**
 * Performs greater than or equal comparison between this price and another price.
 *
 * @return true if this price is greater than or equal to the other price
 *
 * @param anotherPrice - a price to be tested
 */
public boolean greaterThanOrEqual(Price anotherPrice)
{
    if ( anotherPrice.isMarketPrice() )
    {
        return true;
    }
    if ( !anotherPrice.isValuedPrice() )
    {
        throw new IllegalArgumentException( "Comparisons can only be done between valued prices or with mkt price");
    }
    
    return (getValue() >= anotherPrice.toLong());
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
    if ( anotherPrice.isMarketPrice() )
    {
        return true;
    }
	if ( !anotherPrice.isValuedPrice() )
	{
		throw new IllegalArgumentException( "Comparisons can only be done between valued prices or with mkt price");
	}
	
    return (getValue() < anotherPrice.toLong());
}

/**
 * Performs less than or equal comparison between this price and another price.
 *
 * @return true if this price is less than or equal to the other price
 *
 * @param anotherPrice - a price to be tested
 */
public boolean lessThanOrEqual(Price anotherPrice)
{
    if ( anotherPrice.isMarketPrice() )
    {
        return true;
    }
    if ( !anotherPrice.isValuedPrice() )
    {
        throw new IllegalArgumentException( "Comparisons can only be done between valued prices or with mkt price");
    }
    
    return (getValue() <= anotherPrice.toLong());
}

public static long toLongValue (double aPrice)
{
    double epsilon = 0.5D;
    if ( aPrice < 0 )
    {
        epsilon = -0.5D;
    }
	
    return (long) (aPrice * (double) DEFAULT_SCALE + epsilon);  // add or subtract 1/2 to round
}


/**
 * Sets the price to the specified value after adjusting by the scale.
 *
 * @param aPrice - unadjusted value for price
 */
private void setPrice( double aPrice )
{
	setValue(toLongValue (aPrice));
}


public static long toLongValue (String aPrice)
{
    char anEighth = aPrice.charAt(aPrice.length() - 1);
    double tmpPrice = 0.0d;
    try
    {
        if (Character.isLetter(anEighth))
        {
            tmpPrice = Double.parseDouble(aPrice.substring(0, aPrice.length() - 1));
            tmpPrice += eighthValue(anEighth);
        }
        else
        {
            if (anEighth == '+')
            {
                tmpPrice = convertSixteenth(aPrice);
            }
            else
            {
                tmpPrice = Double.parseDouble(aPrice);
            }
        }
        
    } 
    catch(NumberFormatException nfe)
    {
        Log.exception("In ValuedPrice.toLongValue(String) argument '" + aPrice + "' does not contain a parsable double. ", nfe);
    }
    return toLongValue (tmpPrice);
}

/**
 * Sets the value of this price to value of converted string.
 *
 * @param aPrice - a valid price string
 */
private void setPrice(String aPrice)
{
	setValue(toLongValue (aPrice));
}
/**
 * Sets value of this price.
 *
 * @param newValue combined value containing whole dollar amount and fraction amount
 */
protected void setValue(long newValue)
{
	formattedPrice = null;
	value = newValue;
}
/**
 * Converts price to double value.
 *
 * @return converted value.
 */
public double toDouble()
{
	return (double) getValue() / (double) DEFAULT_SCALE;
}
/**
 * Converts price to long value.
 *
 * @return converted value.
 */
public long toLong()
{
	return getValue();
}
/**
 * Converts a price to a printable string
 *
 * @return formatted string
 */
public String toString()
{
	return formattedPrice;
}

/**
 * Converts this price to a price structure.
 *
 * @return price structure for this price.
 */
public PriceStruct toStruct()
{
    return PriceFactory.createPriceStruct(PriceTypes.VALUED, getWhole(), getFraction());

    // Have considered creating a cached price struct, but since
    // its values are public, an external user could change the
    // struct that has been returned.
    
//	PriceStruct aStruct = new PriceStruct();
//	aStruct.type = PriceTypes.VALUED;
//	aStruct.whole = getWhole();
//	aStruct.fraction = getFraction();
//	return aStruct;
}

    public static boolean isValuedPrice(PriceStruct priceStruct)
    {
        if(priceStruct.type == PriceTypes.VALUED)
            return true;
        else
            return false;
    }

    public static long getValuedPriceInCents(PriceStruct priceStruct) throws IllegalArgumentException
    {
        if(isValuedPrice(priceStruct)) {
            double bPrice = Math.round((((double) ((long) priceStruct.whole * DEFAULT_SCALE + (long) priceStruct.fraction)) / (double) DEFAULT_SCALE) * 100.00);
            return (long) bPrice;
        }
        else
            throw new IllegalArgumentException("Failed to convert Price into cents - Price is not a Valued Price.");
    }

}
