package com.cboe.domain.util;

import com.cboe.exceptions.DataValidationException;
import com.cboe.idl.cmiConstants.PriceTypes;
import com.cboe.idl.cmiErrorCodes.DataValidationCodes;
import com.cboe.idl.cmiUtil.PriceStruct;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.domain.Price;
import com.cboe.util.ExceptionBuilder;

/**
 * This class is used to represent prices. 
 * Modified to use price cache - Dalji 8/5/06
 *
 * @author John Wickberg
 * @author singh
 */

public abstract class PriceFactory
{

    private static Price No_Price;
    private static Price Market_Price;
    private static Price Cabinet_Price;
    private static PriceStruct No_PriceStruct;
    private static PriceStruct Market_PriceStruct;
    private static PriceStruct Cabinet_PriceStruct;
    private static PriceSqlType No_PriceSqlType;
    private static PriceSqlType Market_PriceSqlType;
    private static PriceSqlType Cabinet_PriceSqlType;

    private static boolean usePriceCache; // don't use price cache by default

    static
    {
        No_Price = new NoPrice();
        Market_Price = new MarketPrice();
        Cabinet_Price = new CabinetPrice();

        No_PriceStruct = No_Price.toStruct();
        Market_PriceStruct = Market_Price.toStruct();
        Cabinet_PriceStruct = Cabinet_Price.toStruct();

        No_PriceSqlType = new PriceSqlType(PriceSqlType.NO_PRICE_STRING);
        Market_PriceSqlType = new PriceSqlType(PriceSqlType.MARKET_STRING); 
        Cabinet_PriceSqlType = new PriceSqlType(PriceSqlType.CABINET_STRING);
        
        try
        {
            String val = System.getProperty("priceCache.use");

            usePriceCache = (val != null) && (val.compareToIgnoreCase("true") == 0);
        }
        catch (Exception e)
        {
            usePriceCache = false;
        }

        Log.information("PriceFactory: Using priceCache = " + usePriceCache);
    }

    /**
     * Creates a price equal with its value set to the specified price.
     *
     * @param aPrice - the price value to be used.
     */
    public static Price create(double aPrice)
    {
        return usePriceCache ? ValuedPriceCache.lookup(aPrice) : new ValuedPrice (aPrice);
    }

    /**
     * Creates a price equal with its value set to the specified price.
     *
     * @param aPrice - the price value to be used.
     */
    public static Price create(long aPrice)
    {
        return usePriceCache ? ValuedPriceCache.lookup(aPrice) : new ValuedPrice (aPrice);
    }

    public static Price getNoPrice()
    {
        return No_Price;
    }

    public static Price getMarketPrice()
    {
        return Market_Price;
    }

    public static PriceStruct createPriceStruct (short type, int whole, int fraction)
    {
        PriceStruct result = null;

        switch (type)
        {
            case PriceTypes.NO_PRICE:
                result = PriceFactory.No_PriceStruct;
                break;
            case PriceTypes.CABINET:
                result = PriceFactory.Cabinet_PriceStruct;
                break;
            case PriceTypes.MARKET:
                result = PriceFactory.Market_PriceStruct;
                break;

            case PriceTypes.VALUED:
                result = ValuedPriceCache.lookupPriceStruct(whole, fraction);

                if ((result.whole != whole) || (result.fraction != fraction))
                {
                    //			Log.alarm("Cached price struct found modified. Found whole/fraction" + 
                    //					result.whole + "/" + result.fraction + ", expected: " + whole + "/" + fraction + 
                    //					". Creating new PriceStruct object for the whole/fraction pair.");

                    result = new PriceStruct (PriceTypes.VALUED, whole, fraction);
                }
                break;
            default:
                result = new PriceStruct (type, whole, fraction);
            break;
        }
		// Safety condition to prevent PriceStruct fields to be unexpectedly changed by validation
		// classes (e.g. OrderValidationOpenOutcryStrategy.validateContingencyPrice). Aimed primarily to PriceTypes.VALUED
		// but generalized for all price types to prevent similar issues if their creation change in the future
		if (result.type != type)
		{
			result = new PriceStruct (type, whole, fraction);
		}
        return result;
    }

    /**
     * Creates a price using the passed price struct as its value.
     *
     * @param initialValue - the initial price value to be copied and saved
     */
    public static Price create(PriceStruct initialValue)
    {
        Price result = null;
        switch (initialValue.type)
        {
            case PriceTypes.NO_PRICE:
                result = No_Price;
                break;
            case PriceTypes.CABINET:
                result = Cabinet_Price;
                break;
            case PriceTypes.MARKET:
                result = Market_Price;
                break;
            case PriceTypes.VALUED:
                result = usePriceCache ? ValuedPriceCache.lookup(initialValue) : new ValuedPrice (initialValue);
                break;
            default:
                throw new IllegalArgumentException("Invalid price type: " + initialValue.type);
        }
        return result;
    }

    /**
     * Creates a price using the passed price types as its value.
     *
     * @param p_priceType - one of PriceTypes constants
     * @param p_value - value for VALUED prices (should be DEFAULT_SCALE (billionths) otherwise, ignored)
     */
    public static Price create(final short p_priceType, final long p_value)
    {
        Price result;
        switch (p_priceType)
        {
            case PriceTypes.NO_PRICE:
                result = No_Price;
                break;
            case PriceTypes.CABINET:
                result = Cabinet_Price;
                break;
            case PriceTypes.MARKET:
                result = Market_Price;
                break;
            case PriceTypes.VALUED:
                result = usePriceCache ? ValuedPriceCache.lookup(p_value) : new ValuedPrice (p_value);
                break;
            default:
                throw new IllegalArgumentException("Invalid price type: " + p_priceType);
        }
        return result;
    }

    /**
     * use createValuedPrice when you ALWAYS want a ValuedPrice object irrespective of the price type
     * @param struct
     * @return
     */
    public static ValuedPrice createValuedPrice (PriceStruct struct)
    {
        return usePriceCache ? ValuedPriceCache.lookup (struct) : new ValuedPrice (struct); 
    }

    public static ValuedPrice createValuedPrice (String aPrice)
    {
        return usePriceCache ? ValuedPriceCache.lookup(aPrice) : new ValuedPrice (aPrice);
    }    
    
    public static ValuedPrice createValuedPrice (double aPrice)
    {
        return usePriceCache ? ValuedPriceCache.lookup(aPrice) : new ValuedPrice (aPrice);
    }

    public static ValuedPrice createValuedPrice (long aPrice)
    {
        return usePriceCache ? ValuedPriceCache.lookup(aPrice) : new ValuedPrice (aPrice);
    }

    /**
     * Creates a price from a string.
     *
     * @param  aPrice: it could be the followings:
     *      1. Price.MARKET_STRING
     *      2. Price.NO_PRICE_STRING
     *      3. a valid price.
     */
    public static Price create(String aPrice)
    {
        if (aPrice.equalsIgnoreCase(Price.MARKET_STRING)){
            return Market_Price;
        }
        if(aPrice.equalsIgnoreCase(Price.CABINET_STRING))
        {
            return Cabinet_Price;
        }
        if (aPrice.equalsIgnoreCase(Price.NO_PRICE_STRING)){
            return No_Price;
        }

        return usePriceCache ? ValuedPriceCache.lookup(aPrice) : new ValuedPrice (aPrice);
    }

    /**
     * Creates a "whole tick" price that is nearest to the requested value.
     *
     * @param value starting price value
     * @param breakPoint break point price
     * @param tickSizeBelow tick size below break point
     * @param tickSizeAbove tick size above break point
     * @return nearest price
     */
    public static Price createNearest(double value, Price breakPoint, Price tickSizeBelow, Price tickSizeAbove) {
    long resultValue = getNearest(ValuedPrice.toLongValue(value), breakPoint.toLong(), tickSizeBelow.toLong(), tickSizeAbove.toLong());
	ValuedPrice result = new ValuedPrice(resultValue);
	return result;
}

public static long getNearest(long resultValue, long breakPoint, long tickSizeBelow, long tickSizeAbove)
{
        long tick;
        long multiplier = 1;
        if (resultValue < 0) {
            resultValue = -resultValue;
            multiplier = -1;
        }
    if (resultValue >= breakPoint) {
        tick = tickSizeAbove;
        }
        else {
        tick = tickSizeBelow;
        }
        long newValue = resultValue / tick;
        if (resultValue % tick >= tick >> 1) {
            newValue++;
        }
    resultValue = multiplier * newValue * tick;
        if (Log.isDebugOn())
        {
            Log.debug("Inside the method PriceFactory::createNearest newValue :" + newValue + ":");            	
        Log.debug("Inside the method PriceFactory::createNearest result :" + resultValue + ":");             
        }
    return resultValue;
}

    /**
     * Validates a PriceStruct.
     *
     * @param aPriceStruct - the PriceStruct to validate.
     */
    public static void validatePriceStruct(PriceStruct aPriceStruct) throws DataValidationException
    {
        if ((aPriceStruct.fraction > 0 && aPriceStruct.whole < 0) ||
                (aPriceStruct.fraction < 0 && aPriceStruct.whole > 0)) {
            throw ExceptionBuilder.dataValidationException("Invalid PriceStruct, whole and fraction have different sign.", DataValidationCodes.INVALID_PRICE);
        }
    }

    /**
     * return a new price object with opposite sign than the price passed in
     * Note: this only works with valued price. Otherwise return the price passed in.
     */
    public static Price flipSignOnPrice(Price price)
    {
        if (price.isValuedPrice()) {
            double value = 0.0D - price.toDouble();
            return PriceFactory.create(value);
        }
        return price;
    }

    public static PriceStruct lookupPriceStruct(PriceStruct priceStruct)
    {
        PriceStruct result = null;
        switch (priceStruct.type)
        {
            case PriceTypes.NO_PRICE:
                result = No_PriceStruct;
                break;
            case PriceTypes.CABINET:
                result = Cabinet_PriceStruct;
                break;
            case PriceTypes.MARKET:
                result = Market_PriceStruct;
                break;
            case PriceTypes.VALUED:
                result = usePriceCache ? ValuedPriceCache.lookupPriceStruct(priceStruct) : priceStruct;
                break;
            default:
                throw new IllegalArgumentException("Invalid price type: " + priceStruct.type);
        }
        return result;
    }

    public static PriceSqlType createPriceSqlType(String priceString)
    {
        if (priceString.equalsIgnoreCase(PriceSqlType.NO_PRICE_DB_VALUE)
            || priceString.equalsIgnoreCase(Price.NO_PRICE_STRING))
        {
            return No_PriceSqlType;
        }
        
        if (priceString.equalsIgnoreCase(Price.CABINET_STRING))
        {
            return Cabinet_PriceSqlType;
        }     
        
        if (priceString.equalsIgnoreCase(Price.MARKET_STRING))
        {
            return Market_PriceSqlType;
        }     
        return usePriceCache ? ValuedPriceCache.lookupSqlType(priceString) : new PriceSqlType(priceString);
    }        
    
    public static PriceSqlType createPriceSqlType(PriceStruct priceStruct)
    {
        PriceSqlType result = null;
        switch (priceStruct.type)
        {
            case PriceTypes.NO_PRICE:
                result = No_PriceSqlType;
                break;
            case PriceTypes.CABINET:
                result = Cabinet_PriceSqlType;
                break;
            case PriceTypes.MARKET:
                result = Market_PriceSqlType;
                break;
            case PriceTypes.VALUED:
                result = usePriceCache ? ValuedPriceCache.lookupSqlType(priceStruct) : new PriceSqlType(priceStruct);
                break;
            default:
                throw new IllegalArgumentException("Invalid price type: " + priceStruct.type);
        }
        return result;
    }    
    
    public static PriceSqlType createPriceSqlType(Price price)
    {
        if (price.isNoPrice())
        {
            return No_PriceSqlType;
        }
        
        if (price.isCabinetPrice())
        {
            return Cabinet_PriceSqlType;
        }     
        
        if (price.isMarketPrice())
        {
            return Market_PriceSqlType;
        }     
        
        if(price.isValuedPrice())
        {
            return usePriceCache ? ValuedPriceCache.lookupSqlType(price.toStruct()) : new PriceSqlType(price);
        }
        
        throw new IllegalArgumentException("Invalid price argument," +
                                               " price must be one of: NoPrice, CabinetPrice, MarketPrice or ValuedPrice.");

    }    
}
