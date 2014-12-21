package com.cboe.presentation.common.formatters;

import com.cboe.domain.util.*;
import com.cboe.idl.cmiConstants.PriceTypes;
import com.cboe.idl.cmiConstants.PriceConstants;
import com.cboe.idl.cmiMarketData.ExpectedOpeningPriceStruct;
import com.cboe.idl.cmiUtil.PriceStruct;
import com.cboe.interfaces.domain.Price;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.Collections;

/**
 * Format factory for creating Price formatters.
 * @author Nick DePasquale
 * @author Troy Wehrle
 */
public abstract class DisplayPriceFactory extends PriceFactory
{
    private static Price noPrice = null;
    private static Price marketPrice = null;
    private static Price cabinetPrice = null;

    private static Map<Price, Price> priceMap = Collections.synchronizedMap(new WeakHashMap<Price, Price>());

    /**
     * Creates a price using the passed price value.
     * @param aPrice - NoPrice
     */
    public static Price create(NoPrice aPrice)
    {
        return new DisplayPrice(aPrice);
    }

    /**
     * Creates a price using the passed price value.
     * @param aPrice - CabinetPrice
     */
    public static Price create(CabinetPrice aPrice)
    {
        return new DisplayPrice(aPrice);
    }

    /**
     * Creates a price using the passed price value.
     * @param aPrice - MarketPrice
     */
    public static Price create(MarketPrice aPrice)
    {
        return new DisplayPrice(aPrice);
    }

    /**
     * Creates a price using the passed price value.
     * @param aPrice - ValuedPrice
     */
    public static Price create(ValuedPrice aPrice)
    {
        return new DisplayPrice(aPrice);
    }

    /**
     * Creates a price using the passed price value.
     * @param aPrice - the initial price value
     */
    public static Price create(double aPrice)
    {
        return new DisplayPrice(aPrice);
    }

    public static Price create(int aPrice, byte priceScale)
    {
        if(aPrice == PriceConstants.NO_PRICE)
        {
            return createNoPrice();
        }
        else
        {
            return create((double) aPrice / Math.pow(10, priceScale));
        }
    }

    /**
     * Creates a price using the passed price value.
     * @param aPrice - the initial price value
     */
    public static Price create(long aPrice)
    {
        return new DisplayPrice(aPrice);
    }

    /**
     * Creates a price using the passed price struct as its value.
     * @param initialValue - the initial price value to be copied and saved
     */
    public static Price create(PriceStruct initialValue)
    {
        Price result;
        switch (initialValue.type)
        {
            case PriceTypes.NO_PRICE:
                result = createNoPrice();
                break;
            case PriceTypes.CABINET:
                result = createCabinetPrice();
                break;
            case PriceTypes.MARKET:
                result = createMarketPrice();
                break;
            case PriceTypes.VALUED:
                result = getValuedPrice(initialValue);
                break;
            default:
                throw new IllegalArgumentException("Invalid price type: " + initialValue.type);
        }
        return result;
    }

    /**
     * Creates a price using the passed price value.
     * @param aPrice - the initial price value
     */
    public static Price create(String aPrice)
    {
        return new DisplayPrice(aPrice);
    }

    /**
     * Returns NoPrice object.
     */
    public static Price createNoPrice()
    {
        if (noPrice == null)
        {
            PriceStruct struct = StructBuilder.buildPriceStruct();
            struct.type = PriceTypes.NO_PRICE;
            noPrice = new DisplayPrice(struct);
        }
        return noPrice;
    }

    /**
     * Returns CabinetPrice object.
     */
    public static Price createCabinetPrice()
    {
        if (cabinetPrice == null)
        {
            PriceStruct struct = StructBuilder.buildPriceStruct();
            struct.type = PriceTypes.CABINET;
            cabinetPrice = new DisplayPrice(struct);
        }
        return cabinetPrice;
    }

    /**
     * Returns MarketPrice object.
     */
    public static Price createMarketPrice()
    {
        if (marketPrice == null)
        {
            PriceStruct struct = StructBuilder.buildPriceStruct();
            struct.type = PriceTypes.MARKET;
            marketPrice = new DisplayPrice(struct);
        }
        return marketPrice;
    }

    /**
     * Creates a price for an ExpectedOpeningPriceStruct
     * @param eopStruct - The ExpectedOpeningPriceStruct
     * @return a Price
     */
    public static Price create(ExpectedOpeningPriceStruct eopStruct)
    {
        Price value;
        if (eopStruct != null)
        {
            value = new DisplayExpectedOpeningPrice(eopStruct);
        }
        else
        {
            value = new DisplayExpectedOpeningPrice(0.00);
        }
        return value;
    }

/*
    private static Map<Integer, Map<Integer, Price>> priceMap = new HashMap<Integer, Map<Integer, Price>>();

    private synchronized static Price getValuedPrice(PriceStruct struct)
    {
        if (struct.type != PriceTypes.VALUED)
        {
            throw new IllegalArgumentException("PriceStruct.type != PriceTypes.VALUED");
        }
        Map<Integer, Price> wholeMap = getWholeMap(struct.whole);
        Price retVal = wholeMap.get(struct.fraction);
        if (retVal == null)
        {
            retVal = new DisplayPrice(struct);
            wholeMap.put(struct.fraction, retVal);
        }
        return retVal;
    }

    private static Map<Integer, Price> getWholeMap(int whole)
    {
        Map<Integer, Price> retVal = priceMap.get(whole);
        if (retVal == null)
        {
            retVal = new HashMap<Integer, Price>();
            priceMap.put(whole, retVal);
        }
        return retVal;
    }
*/

    private static Price getValuedPrice(PriceStruct struct)
    {
        if (struct.type != PriceTypes.VALUED)
        {
            throw new IllegalArgumentException("PriceStruct.type != PriceTypes.VALUED");
        }

        //todo: override hashCode() in DisplayPrice
        Price key = new DisplayPrice(struct)
        {
            public int hashCode()
            {
                return getWhole();
            }
        };
        // for performance, not adding extra synchronization around the priceMap
        // SynchronizedMap -- would rather leave the possibility of the cached
        // price being replaced
        Price cachedPrice = priceMap.get(key);
        if (cachedPrice == null)
        {
            priceMap.put(key, key);
        }
        else
        {
            key = cachedPrice;
        }
        return key;
    }
}
