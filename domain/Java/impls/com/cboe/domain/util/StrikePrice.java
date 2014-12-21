package com.cboe.domain.util;

import com.cboe.idl.cmiProduct.ProductDescriptionStruct;
import com.cboe.idl.cmiConstants.*;
import com.cboe.interfaces.domain.Price;

public class StrikePrice
{
    private static final Price zeroPrice = PriceFactory.create(0.0);
    private Price     maxStrikePrice; 
    private Price     minStrikePriceFraction;
    
    
    /**
     * Creates a StrikePrice object initialized with specific product 
     * description information.
     *
     * @param   productDescription
     */
    public StrikePrice(ProductDescriptionStruct productDescription)
    {
        this.maxStrikePrice = PriceFactory.create(productDescription.maxStrikePrice);
        this.minStrikePriceFraction = PriceFactory.create(productDescription.minimumStrikePriceFraction);
    }
    
    /**
     * Adds or subtracts ticks to a price.
     *
     * @param   value   original price value
     * @param   ticks	number of ticks to add to this price.  If ticks is negative,
     *					the value is subtracted from the price
     * @param   productDescription
     * @return 			new <code>Price</code> object offset by number of ticks
     *
     */
    public static Price addTicks(double value, int nbrTicks, ProductDescriptionStruct productDescription)
    {
        return addTicks(PriceFactory.create(value), nbrTicks, productDescription);
    }
    
    /**
     * Adds or subtracts ticks to a price.
     *
     * @param   aPrice  original price object
     * @param   ticks	number of ticks to add to this price.  If ticks is negative,
     *					the value is subtracted from the price
     * @param   productDescription
     * @return 			new <code>Price</code> object offset by number of ticks
     *
     */
    public static Price addTicks(Price curPrice, int nbrTicks, ProductDescriptionStruct productDescription)
    {
        return addTicks(curPrice,
                        nbrTicks,
                        PriceFactory.create(productDescription.maxStrikePrice), 
                        PriceFactory.create(productDescription.minimumStrikePriceFraction));
    }
    
    /**
     * Adds or subtracts ticks to a price.
     *
     * @param   value   original price value
     * @param   ticks	number of ticks to add to this price.  If ticks is negative,
     *					the value is subtracted from the price
     * @param   maxStrikePrice   max strike price
     * @param   minStrikePriceFraction  minimum tick value for strike price
     * @return 			new <code>Price</code> object offset by number of ticks
     *
     */
    public static Price addTicks(Price price, int nbrTicks, Price maxStrikePrice, Price minStrikePriceFraction)
    {
        Price newPrice;
        Price curPrice = toStrikePriceRange(price, maxStrikePrice, minStrikePriceFraction);
        if ( price == curPrice )
        {
            long tempPrice = price.toLong() + minStrikePriceFraction.toLong()*nbrTicks;
            newPrice = toStrikePriceRange(PriceFactory.create(tempPrice), maxStrikePrice, minStrikePriceFraction);
        }
        else
        {
            newPrice = curPrice;
        }
        return newPrice;
    }
    
    
    /**
     * Creates a price nearest to requested price that is on a valid tick regardless
     * of direction.
     * 
     * @param   value   price value to quantize
     * @param   productDescription
     * @return 			new <code>Price</code> object that is at the closest valid tick
     *
     */
    public static Price nearestPrice(double value, ProductDescriptionStruct productDescription)
    {
        return nearestPrice(PriceFactory.create(value), productDescription);
    }
    
    /**
     * Creates a price nearest to requested price that is on a valid tick regardless
     * of direction.
     * 
     * @param   aPrice  price to quantize
     * @param   productDescription
     * @return 			new <code>Price</code> object that is at the closest valid tick
     *
     */
    public static Price nearestPrice(Price aPrice, ProductDescriptionStruct productDescription)
    {
        Price curPrice = toStrikePriceRange(aPrice,
        		PriceFactory.create(productDescription.maxStrikePrice),
        		PriceFactory.create(productDescription.minimumStrikePriceFraction) );
        if ( aPrice == curPrice )
        {                                        
            return PriceFactory.createNearest(aPrice.toDouble(),
                                              zeroPrice,
                                              zeroPrice,
                                              PriceFactory.create(productDescription.minimumStrikePriceFraction));
        }
        else
        {
            return curPrice;
        }
    }
    
    /**
     * Checks if price is a valid strike price for a given product.
     * 
     * @param   value   price value to validate
     * @param   productDescription
     * @return  boolean true if price is valid
     */
    public static boolean isValidStrikePrice(double value, ProductDescriptionStruct productDescription)
    {
        return isValidStrikePrice(PriceFactory.create(value), productDescription);
    }
    
    /**
     * Checks if price is a valid strike price for a given product.
     * 
     * @param   value   price value to validate
     * @return  boolean true if price is valid
     */
    public boolean isValidStrikePrice(double value)
    {
        return isValidStrikePrice(PriceFactory.create(value));
    }
     
    /**
     * Checks if price is a valid strike price for a given product.
     * 
     * @param   aPrice   price to validate
     * @param   productDescription
     * @return  boolean true if price is valid
     */
    public static boolean isValidStrikePrice(Price aPrice, ProductDescriptionStruct productDescription)
    {
        return isValidStrikePrice(aPrice,
        		PriceFactory.create(productDescription.maxStrikePrice),
        		PriceFactory.create(productDescription.minimumStrikePriceFraction));
    }
    
    /**
     * Checks if price is a valid strike price for a given product.
     * 
     * @param   aPrice   price to validate
     * @return  boolean true if price is valid
     */
    public boolean isValidStrikePrice(Price aPrice)
    {
        return isValidStrikePrice(aPrice, this.maxStrikePrice, this.minStrikePriceFraction);
    }
    
    /**
     * Checks if price is a valid strike price for a given product.
     * Price has to be non negative, less than or equal to maxStrikePrice, and be
     * on a valid tick to be considered valid.
     *    NoPrice is invalid. MarketPrice is invalid.
     * 
     * @param   aPrice  price to validate
     * @param   maxStrikePrice   max price
     * @param   minStrikePriceFraction  tick value for strike price
     * @return  boolean true if price is valid
     */
    public static boolean isValidStrikePrice(Price aPrice, Price maxStrikePrice, Price minStrikePriceFraction)
    {
        boolean isValid = false;
        if ( aPrice.lessThanOrEqual(maxStrikePrice) && aPrice.greaterThan(zeroPrice) )
        {
            if ( aPrice.isValuedPrice() )
            {
                long targetPrice = aPrice.toLong();
                long minTick = minStrikePriceFraction.toLong();
                isValid = (targetPrice % minTick) == 0;
            }
        }
        return isValid;
    }
    
    
    /**
     * Returns the smallest (closest to zero) Price that is
     * not less than the argument and is on a valid tick for the given product.
     * 
     * @param   aPrice  price to quantize
     * @param   productDescription
     * @return  Price   quantized price 
     */
    public static Price ceilToTick(Price aPrice, ProductDescriptionStruct productDescription)
    {
        return ceilToTick(aPrice,
        		PriceFactory.create(productDescription.maxStrikePrice),
        		PriceFactory.create(productDescription.minimumStrikePriceFraction));
    }
    /**
     * Returns the smallest (closest to zero) Price that is
     * not less than the argument and is on a valid tick for the given product.
     * If price passed in is negative or equal 0 the return value is equal to minStrikePriceFraction,
     * if price passed in is greater than maxStrikePrice the return value is equal to maxStrikePrice. 
     * 
     * @param   aPrice  price to quantize
     * @param   maxStrikePrice   max strike price
     * @param   minStrikePriceFraction  minimum tick value for strike price
     * @return  Price   quantized price 
     */
    public static Price ceilToTick(Price aPrice, Price maxStrikePrice, Price minStrikePriceFraction)
    {
        Price newPrice = toStrikePriceRange(aPrice, maxStrikePrice, minStrikePriceFraction);
        
        if ( newPrice == aPrice )
        {
            long minTick = minStrikePriceFraction.toLong();
            long targetPrice = aPrice.toLong();
            if ( targetPrice % minTick != 0)
            {
                long tempPrice = (targetPrice / minTick)*minTick + minTick;
                newPrice = PriceFactory.create(tempPrice);
            }
        }
        return newPrice;
    }
    
    /**
     * Returns the largest (closest to max Strike price) Price that is
     * not greater than the argument and is on a valid tick for the given product.
     * 
     * @param   aPrice  price to quantize
     * @param   productDescription
     * @return  Price   quantized price
     */
    public static Price floorToTick(Price aPrice, ProductDescriptionStruct productDescription)
    {
        return floorToTick(aPrice,
        		PriceFactory.create(productDescription.maxStrikePrice),
        		PriceFactory.create(productDescription.minimumStrikePriceFraction));
    }
    /**
     * Returns the largest (closest to max Strike price) Price that is
     * not greater than the argument and is on a valid tick for the given product.
     * If price passed in is negative or equal 0 the return value is equal to minStrikePriceFraction,
     * if price passed in is greater than maxStrikePrice the return value is equal to maxStrikePrice. 
     * 
     * @param   aPrice  price to quantize
     * @param   maxStrikePrice   maximum strike price
     * @param   minStrikePriceFraction    min tick value for a strike price
     * @return  Price   quantized price 
     */
    public static Price floorToTick(Price aPrice, Price maxStrikePrice, Price minStrikePriceFraction)
    {
        Price newPrice = toStrikePriceRange(aPrice, maxStrikePrice, minStrikePriceFraction);
        
        if ( newPrice == aPrice )    
        {
            long minTick = minStrikePriceFraction.toLong();
            long targetPrice = aPrice.toLong();
            if ( targetPrice % minTick != 0)
            {
                long tempPrice = (targetPrice / minTick)*minTick;
                newPrice = PriceFactory.create(tempPrice);
            }
        }
        return newPrice;
    }
    
    /**
     * Returns the Price by adding ticks to the floor of a given price.
     * If input price is already a valid tick, net result is adding tick(s).
     * 
     * @param   aPrice  starting price
     * @param   ticks	number of ticks to tick up from quantized price.  
     * @param   productDescription
     * @return  Price   quantized price plus tick(s)
     */
    public static Price tickUp(Price aPrice, int nbrTicks, ProductDescriptionStruct productDescription)
    {
        return addTicks(floorToTick(aPrice, productDescription), nbrTicks, productDescription);
    }
    
    /**
     * Returns the Price by adding ticks to the floor of a given price.
     * If input price is already a valid tick, net result is adding tick(s).
     * 
     * @param   aPrice  starting price
     * @param   ticks	number of ticks to tick up from quantized price.  
     * @return  Price   quantized price plus tick(s)
     */
    public Price tickUp(Price aPrice, int nbrTicks)
    {
        Price newPrice;
        Price tempPrice = floorToTick(aPrice, maxStrikePrice, minStrikePriceFraction);
        if (tempPrice.greaterThan(aPrice))
        {
            newPrice = tempPrice;
        }
        else
        {
            newPrice = addTicks(tempPrice, nbrTicks, maxStrikePrice, minStrikePriceFraction);
        }
        return newPrice;
    }
    
    /**
     * Returns the Price by adding ticks to the floor of a given price.
     * If input price is already a valid tick, net result is adding tick(s).
     * 
     * @param   value   starting price value
     * @param   ticks	number of ticks to tick up from quantized price.  
     * @return  Price   quantized price plus tick(s)
     */
    public Price tickUp(double value, int nbrTicks)
    {
        return tickUp(PriceFactory.create(value), nbrTicks);
    }
    
    /**
     * Returns the Price by subtracting ticks to the ceil of a given price.
     * If input price is already a valid tick, net result is subtracting tick(s).
     * 
     * @param   aPrice  starting price
     * @param   ticks	number of ticks to tick down from quantized price.  
     * @param   productDescription
     * @return  Price   quantized price minus tick(s)
     */
    public static Price tickDown(Price aPrice, int nbrTicks, ProductDescriptionStruct productDescription)
    {
        return addTicks(ceilToTick(aPrice, productDescription), -nbrTicks, productDescription);
    }
    
    /**
     * Returns the Price by subtracting ticks to the ceil of a given price.
     * If input price is already a valid tick, net result is subtracting tick(s).
     * 
     * @param   aPrice  starting price
     * @param   ticks	number of ticks to tick down from quantized price.  
     * @return  Price   quantized price minus tick(s)
     */
    public Price tickDown(Price aPrice, int nbrTicks)
    {
        Price newPrice;
        Price tempPrice = ceilToTick(aPrice, maxStrikePrice, minStrikePriceFraction);
        if ( tempPrice.lessThan(aPrice) )
        {
            newPrice = tempPrice;
        }
        else
        {
            newPrice = addTicks(tempPrice,-nbrTicks, maxStrikePrice, minStrikePriceFraction);
        }
        return newPrice;
    }
    
    /**
     * Returns the Price by subtracting ticks to the ceil of a given price.
     * If input price is already a valid tick, net result is subtracting tick(s).
     * 
     * @param   value   starting price value
     * @param   ticks	number of ticks to tick down from quantized price.  
     * @return  Price   quantized price minus tick(s)
     */
    public Price tickDown(double value, int nbrTicks)
    {
        return tickDown(PriceFactory.create(value), nbrTicks);
    }
    
    public static Price toStrikePriceRange(Price aPrice, Price maxStrikePrice, Price minStrikePriceFraction )
    {
        long tempPrice = 0;
        if ( aPrice.lessThanOrEqual(zeroPrice) )
        {
            tempPrice = minStrikePriceFraction.toLong();
            return PriceFactory.create(tempPrice);
        }
        else if ( aPrice.greaterThan(maxStrikePrice) )
        {
            tempPrice = maxStrikePrice.toLong();
            return PriceFactory.create(tempPrice);
        }
        else
        {
            return aPrice;
        }
    }
}