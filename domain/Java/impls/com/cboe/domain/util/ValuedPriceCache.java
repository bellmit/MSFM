package com.cboe.domain.util;

import com.cboe.domain.util.PriceSqlType;
import com.cboe.idl.cmiConstants.PriceTypes;
import com.cboe.idl.cmiUtil.PriceStruct;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

/**
 * Cache of price objects.
 * 
 * @author Daljinder Singh
 * 
 */

public class ValuedPriceCache
{
	private static class ThreadLocalPriceStruct extends ThreadLocal
	{
		protected Object initialValue()
		{
			PriceStruct priceStruct = new PriceStruct();

			priceStruct.type = PriceTypes.VALUED;

			return priceStruct;
		}

		public PriceStruct getPriceStruct()
		{
			return (PriceStruct) get();
		}
	}

	private static class CacheStruct
	{
		public ValuedPrice valuedPrice;

		public PriceStruct priceStruct;
		
		public PriceSqlType priceSqlType;
	}

	private static ThreadLocalPriceStruct threadLocalPriceStruct = new ThreadLocalPriceStruct();

	private static final int DEFAULT_MAX_DOLLAR_AMOUNT = 100;

	private static int MAX_DOLLAR_AMOUNT = DEFAULT_MAX_DOLLAR_AMOUNT;

	private static final int NUM_PENNIES = 100;

	private static final int PENNIES_MULTIPLICATION_FACTOR = 10000000;

	static CacheStruct[][] cachePositiveValues;

	static CacheStruct[][] cacheNegativeValues;

	static
	{
		getPriceCacheProperties();

		initializeCache();
	}

	private static void getPriceCacheProperties()
	{
		String maxDollarValue = System.getProperty("priceCache.maxDollarValue");

		if (maxDollarValue != null)
		{
			try
			{
				MAX_DOLLAR_AMOUNT = Integer.parseInt(maxDollarValue);

				if (MAX_DOLLAR_AMOUNT <= 0)
				{
					MAX_DOLLAR_AMOUNT = DEFAULT_MAX_DOLLAR_AMOUNT;
				}
			}
			catch (Exception e)
			{
				Log.medium("ValuedPriceCache: invalid value for property: priceCache.maxDollarValue. Value: " + maxDollarValue
						+ ". Using default value: "
						+ DEFAULT_MAX_DOLLAR_AMOUNT);

				MAX_DOLLAR_AMOUNT = DEFAULT_MAX_DOLLAR_AMOUNT;
			}
		}
	}

	public static int getMaxDollarAmount()
	{
		return MAX_DOLLAR_AMOUNT;
	}

	private static CacheStruct createCacheStruct(PriceStruct priceStruct)
	{
		CacheStruct cacheStruct = new CacheStruct();

		cacheStruct.priceStruct = new PriceStruct();
		cacheStruct.priceStruct.type = PriceTypes.VALUED;
		cacheStruct.priceStruct.whole = priceStruct.whole;
		cacheStruct.priceStruct.fraction = priceStruct.fraction;

		cacheStruct.valuedPrice = new ValuedPrice(cacheStruct.priceStruct);
		
		cacheStruct.priceSqlType = new PriceSqlType(cacheStruct.valuedPrice);
		
		return cacheStruct;
	}

	private static void initializeCache()
	{
		cachePositiveValues = new CacheStruct[MAX_DOLLAR_AMOUNT][NUM_PENNIES];
		cacheNegativeValues = new CacheStruct[MAX_DOLLAR_AMOUNT][NUM_PENNIES];

		PriceStruct priceStruct = getThreadLocalPriceStruct();

		for (int i = 0; i < MAX_DOLLAR_AMOUNT; i++)
		{
			for (int j = 0; j < NUM_PENNIES; j++)
			{
				priceStruct.whole = i;
				priceStruct.fraction = j * PENNIES_MULTIPLICATION_FACTOR;
				cachePositiveValues[i][j] = createCacheStruct(priceStruct);

				priceStruct.whole = -priceStruct.whole;
				priceStruct.fraction = -priceStruct.fraction;
				cacheNegativeValues[i][j] = createCacheStruct(priceStruct);
			}
		}

		Log.information("Created price cache. Max price: " + (MAX_DOLLAR_AMOUNT - 1)
				+ "."
				+ (NUM_PENNIES - 1));
	}

	public static ValuedPrice lookup(String aPrice)
	{
		ValuedPrice rval = lookupCache(ValuedPrice.toLongValue(aPrice));

		return rval != null ? rval : new ValuedPrice(aPrice);
	}

	public static ValuedPrice lookup(double aPrice)
	{
		ValuedPrice rval = lookupCache(ValuedPrice.toLongValue(aPrice));

		return rval != null ? rval : new ValuedPrice(aPrice);
	}

	public static ValuedPrice lookup(long aPrice)
	{
		ValuedPrice rval = lookupCache(aPrice);

		return rval != null ? rval : new ValuedPrice(aPrice);
	}

	private static ValuedPrice lookupCache(long aPrice)
	{
		return lookup(ValuedPrice.getWholeForValue(aPrice),
				ValuedPrice.getFractionForValue(aPrice));
	}

	public static ValuedPrice lookup(PriceStruct priceStruct)
	{
		ValuedPrice rval = lookup(priceStruct.whole, priceStruct.fraction);

		return rval != null ? rval : new ValuedPrice(priceStruct);
	}

	public static PriceStruct lookupPriceStruct(PriceStruct priceStruct)
	{
		CacheStruct struct = lookupEntry(priceStruct.whole,
				priceStruct.fraction);

		return struct != null ? struct.priceStruct : priceStruct;
	}

	public static PriceSqlType lookupSqlType(String aPrice)
	{
	    PriceSqlType rval = lookupPriceSqlType(ValuedPrice.toLongValue(aPrice));
	    return rval != null ? rval : new PriceSqlType(aPrice);
	}
	
    public static PriceSqlType lookupSqlType(long aPrice)
    {
        PriceSqlType rval = lookupPriceSqlType(aPrice);
        return rval != null ? rval : new PriceSqlType(aPrice);
    }	
	
    public static PriceSqlType lookupSqlType(PriceStruct aPrice)
    {
        PriceSqlType rval = lookupPriceSqlType(aPrice);
        return rval != null ? rval : new PriceSqlType(aPrice);
    }	
	
	public static PriceSqlType lookupSqlType(ValuedPrice aPrice)
	{
	    PriceSqlType rval = lookupPriceSqlType(aPrice);
	    return rval != null ? rval : new PriceSqlType(aPrice);
	}
	
	private static ValuedPrice lookup(int aWhole, int aFraction)
	{
		CacheStruct struct = lookupEntry(aWhole, aFraction);

		return struct != null ? struct.valuedPrice : null;
	}
	
	private static PriceSqlType lookupPriceSqlType(long aPrice)
	{
	    CacheStruct struct = lookupEntry(ValuedPrice.getWholeForValue(aPrice),
                                         ValuedPrice.getFractionForValue(aPrice));
	    return struct != null ? struct.priceSqlType : null;
	}
	
    private static PriceSqlType lookupPriceSqlType(PriceStruct aPrice)
    {
        CacheStruct struct = lookupEntry(aPrice.whole, aPrice.fraction);
        return struct != null ? struct.priceSqlType : null;
    }	
    
    private static PriceSqlType lookupPriceSqlType(ValuedPrice aPrice)
    {
        CacheStruct struct = lookupEntry(aPrice.getWhole(), aPrice.getFraction());
        return struct != null ? struct.priceSqlType : null;
    }       

	private static CacheStruct lookupEntry(int aWhole, int aFraction)
	{
		// if the fraction is not in pennies then return null
		//
		int fraction = aFraction / PENNIES_MULTIPLICATION_FACTOR;
		if ((aFraction - (fraction *PENNIES_MULTIPLICATION_FACTOR)) != 0)
		{
			return null;
		}

		int whole = aWhole;
		CacheStruct[][] array = cachePositiveValues;

		if ((whole <= 0) && (fraction <= 0))
		{
			whole = -whole;
			fraction = -fraction;
			array = cacheNegativeValues;
		}

		if ((whole >= MAX_DOLLAR_AMOUNT) || (whole < 0)
				|| (fraction >= NUM_PENNIES)
				|| (fraction < 0))
		{
			return null;
		}

		return array[whole][fraction];
	}

	private static PriceStruct getThreadLocalPriceStruct()
	{
		return threadLocalPriceStruct.getPriceStruct();
	}

	public static PriceStruct lookupPriceStruct(int whole, int fraction)
	{
		CacheStruct struct = lookupEntry (whole, fraction);
		
		return struct != null ? struct.priceStruct : new PriceStruct (PriceTypes.VALUED, whole, fraction);
	}
}
