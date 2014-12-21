package com.cboe.domain.util;
import com.cboe.domain.TickScaleImpl;
import com.cboe.domain.util.PriceFactory;

import com.cboe.idl.cmiProduct.ProductDescriptionStruct;
import com.cboe.idl.cmiConstants.*;
import com.cboe.idl.cmiUtil.PriceStruct;
import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.domain.TradingProduct;

public class PremiumPrice /*extends PriceValidator*/ {
    public static final Price ZERO_PRICE = PriceFactory.create(0.0);
    private static final long ZERO_PRICE_LONG = ZERO_PRICE.toLong();
    public static final Price ABOVE_MAX_PREMIUM_PRICE = PriceFactory.create(10000.0);
    public static final long ABOVE_MAX_PREMIUM_PRICE_LONG = ABOVE_MAX_PREMIUM_PRICE.toLong(); 
    public static final Price BELOW_MAX_NEG_PREMIUM_PRICE = PriceFactory.create(-10000.0);
    
    private Price     myBreakPoint;
    private Price     myBelowTick;
    private Price     myAboveTick;

    public final static int DEFAULT_SCALE_TO_PENNY = 10000000;

/**
 * Creates a PremiumPrice object initialized with premium specific product
 * description information.
 *
 * @param   productDescription
 */
public PremiumPrice(ProductDescriptionStruct productDescription)
{
    this.myBreakPoint = PriceFactory.create(productDescription.premiumBreakPoint);
    this.myBelowTick = PriceFactory.create(productDescription.minimumBelowPremiumFraction);
    this.myAboveTick = PriceFactory.create(productDescription.minimumAbovePremiumFraction);
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
public static Price addTicks(double value, int nbrTicks, ProductDescriptionStruct productDescription) {
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
public static Price addTicks(Price curPrice, int nbrTicks, ProductDescriptionStruct productDescription) {
	return curPrice.addTicks(nbrTicks,
                             PriceFactory.createValuedPrice (productDescription.premiumBreakPoint),
                             PriceFactory.createValuedPrice (productDescription.minimumBelowPremiumFraction),
                             PriceFactory.createValuedPrice (productDescription.minimumAbovePremiumFraction));
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
public static Price nearestPrice(double value, ProductDescriptionStruct productDescription) {
	return PriceFactory.createNearest(value,
                               PriceFactory.createValuedPrice (productDescription.premiumBreakPoint),
                               PriceFactory.createValuedPrice (productDescription.minimumBelowPremiumFraction),
                               PriceFactory.createValuedPrice (productDescription.minimumAbovePremiumFraction));
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
public static Price nearestPrice(Price aPrice, ProductDescriptionStruct productDescription) {
	return nearestPrice(aPrice.toDouble(), productDescription);
}

/**
 * Checks if price falls on valid tick for a given product.
 *
 * @param   value   price value to validate
 * @param   productDescription
 * @return  boolean true if price is on a valid tick
 */
public static boolean isValidTick(double value, ProductDescriptionStruct productDescription) {
	return isValidTick(PriceFactory.create(value), productDescription);
}

/**
 * Checks if price falls on valid tick for a given product.
 *
 * @param   value   price value to validate
 * @return  boolean true if price is on a valid tick
 */
public boolean isValidTick(double value) {
	return isValidTick(PriceFactory.create(value));
}

/**
 * Checks if price falls on valid tick for a given product.
 *
 * @param   aPrice   price to validate
 * @param   productDescription
 * @return  boolean true if price is on a valid tick
 */
public static boolean isValidTick(Price aPrice, ProductDescriptionStruct productDescription) {
	return isValidTick(aPrice,
                       PriceFactory.createValuedPrice (productDescription.premiumBreakPoint),
                       PriceFactory.createValuedPrice (productDescription.minimumBelowPremiumFraction),
                       PriceFactory.createValuedPrice (productDescription.minimumAbovePremiumFraction));
}

/**
 * Checks if price falls on valid tick for a given product.
 *
 * @param   aPrice   price to validate
 * @return  boolean true if price is on a valid tick
 */
public boolean isValidTick(Price aPrice) {
	return isValidTick(aPrice, myBreakPoint, myBelowTick, myAboveTick);
}

/**
 * Checks if price falls on valid tick for a given product.  MarketPrice is
 * considered valid.  NoPrice is not.
 *
 * @param   aPrice  price to validate
 * @param   premiumBreakPoint   price break point where the tick value changes
 * @param   premiumBelowTick    tick value below the break point
 * @param   premiumAboveTick    tick value above the break point
 * @return  boolean true if price is on a valid tick
 */
public static boolean isValidTick(Price aPrice,
                                  Price premiumBreakPoint,
                                  Price premiumBelowTick,
                                  Price premiumAboveTick) {
    boolean priceOnValidTick = false;
    if ( aPrice.isMarketPrice() ) {
        priceOnValidTick = true;
    } else if ( aPrice.isValuedPrice() ) {
        priceOnValidTick = true;
        long minimumTick;
        long breakPoint = premiumBreakPoint.toLong();
        // Take absolute value of price argument to account for possibility of
        // negative premiums
        long targetPrice = Math.abs(aPrice.toLong());

        if ( targetPrice > breakPoint ) {
            minimumTick = premiumAboveTick.toLong();
            priceOnValidTick = ( ( targetPrice - breakPoint ) % minimumTick ) == 0;
        } else if ( targetPrice < breakPoint ) {
            minimumTick = premiumBelowTick.toLong();
            priceOnValidTick = ( ( breakPoint - targetPrice ) % minimumTick ) == 0;
        }
    }
	return priceOnValidTick;
}

public static boolean hasMoreDecimalsThanSpecified(Price aPrice,
                                                   Price premiumTick) 
{
    return aPrice.isValuedPrice() && ((aPrice.toLong() % premiumTick.toLong()) > 0);
}

/**
 * Checks if price falls on valid tick for a given product.  MarketPrice is
 * considered valid.  NoPrice is not.
 *
 * @param   aPriceLong              price to validate
 * @param   premiumBreakPointLong   price break point where the tick value changes
 * @param   premiumBelowTickLong    tick value below the break point
 * @param   premiumAboveTickLong    tick value above the break point
 * @return  boolean true if price is on a valid tick
 */
public static boolean isValidTickValuedPrice(long aPriceLong,
                                             long premiumBreakPointLong,
                                             long premiumBelowTickLong,
                                             long premiumAboveTickLong) {
    
        boolean priceOnValidTick = true;
        
        // Take absolute value of price argument to account for possibility of
        // negative premiums
        long targetPrice = Math.abs(aPriceLong);

        if ( targetPrice > premiumBreakPointLong) {
            priceOnValidTick = ( ( targetPrice - premiumBreakPointLong ) % premiumAboveTickLong ) == 0;
        } else if ( targetPrice < premiumBreakPointLong ) {
            priceOnValidTick = ( ( premiumBreakPointLong - targetPrice ) % premiumBelowTickLong ) == 0;
        }
    return priceOnValidTick;
}

/**
 * Checks if price is a valid premium for orders of specified product type.
 *
 * @param   value   price value to validate
 * @param   productDescription
 * @param   productType
 * @return  boolean true if price is valid premium
 */
public static boolean isValidForOrder(double value, ProductDescriptionStruct productDescription, short productType) {
	return isValidForOrder(PriceFactory.create(value), productDescription, productType);
}


/**
 * Checks if price is a valid premium for orders of specified product type.
 *
 * @param   aPrice   price to validate
 * @param   productDescription
 * @param   productType
 * @return  boolean true if price is valid premium
 */
public static boolean isValidForOrder(Price aPrice, ProductDescriptionStruct productDescription, short productType) {
	return isValidForOrder(aPrice,
                           PriceFactory.createValuedPrice (productDescription.premiumBreakPoint),
                           PriceFactory.createValuedPrice (productDescription.minimumBelowPremiumFraction),
                           PriceFactory.createValuedPrice (productDescription.minimumAbovePremiumFraction),
                           productType);
}

/**
 * Checks if price is a valid premium for orders of specified product type.
 *
 * @param   aPrice  price to validate
 * @param   premiumBreakPoint   price break point where the tick value changes
 * @param   premiumBelowTick    tick value below the break point
 * @param   premiumAboveTick    tick value above the break point
 * @param   productType
 * @return  boolean true if price is valid premium
 */
public static boolean isValidForOrder(Price aPrice,
                                      Price premiumBreakPoint,
                                      Price premiumBelowTick,
                                      Price premiumAboveTick,
                                      short productType) {
    boolean premiumIsValid = false;
    if ( isValidTick(aPrice, premiumBreakPoint, premiumBelowTick, premiumAboveTick) ) {
       premiumIsValid = true;
       if ( productType != ProductTypes.STRATEGY && aPrice.isValuedPrice() && aPrice.lessThanOrEqual(ZERO_PRICE) ) {
          premiumIsValid = false;
       }
       if ( aPrice.isValuedPrice() && aPrice.greaterThanOrEqual(ABOVE_MAX_PREMIUM_PRICE) ) {
          premiumIsValid = false;
       }
    }
	return premiumIsValid;
}

/**
 * Checks if price is a valid premium for quotes of specified product type.
 *
 * @param   value   price value to validate
 * @param   productDescription
 * @param   productType
 * @return  boolean true if price is valid premium
 */
public static boolean isValidForQuote(double value, ProductDescriptionStruct productDescription, short productType) {
	return isValidForOrder(PriceFactory.create(value), productDescription, productType);
}

/**
 * Checks if price is a valid premium for quotes of specified product type.
 *
 * @param   aPrice   price to validate
 * @param   productDescription
 * @param   productType
 * @return  boolean true if price is valid premium
 */
public static boolean isValidForQuote(Price aPrice, ProductDescriptionStruct productDescription, short productType) {
	return isValidForQuote(aPrice,
                           PriceFactory.createValuedPrice (productDescription.premiumBreakPoint),
                           PriceFactory.createValuedPrice (productDescription.minimumBelowPremiumFraction),
                           PriceFactory.createValuedPrice (productDescription.minimumAbovePremiumFraction),
                           productType);
}

/**
 * Checks if price is a valid premium for quotes of specified product type.
 *
 * @param   aPrice  price to validate
 * @param   premiumBreakPoint   price break point where the tick value changes
 * @param   premiumBelowTick    tick value below the break point
 * @param   premiumAboveTick    tick value above the break point
 * @param   productType
 * @return  boolean true if price is valid premium
 */
public static boolean isValidForQuote(Price aPrice,
                                      Price premiumBreakPoint,
                                      Price premiumBelowTick,
                                      Price premiumAboveTick,
                                      short productType) {
    boolean premiumIsValid = false;
    
    if(aPrice.isMarketPrice())
        return true;
    
    if(aPrice.isValuedPrice()){
        long aPriceLong = aPrice.toLong();
        premiumIsValid = isValidTickValuedPrice(aPriceLong, 
                                                premiumBreakPoint.toLong(), 
                                                premiumBelowTick.toLong(), 
                                                premiumAboveTick.toLong());
        if ((aPriceLong >= ABOVE_MAX_PREMIUM_PRICE_LONG)
            || (productType != ProductTypes.STRATEGY && aPriceLong <= ZERO_PRICE_LONG)){
            premiumIsValid = false;
         }
    }
	return premiumIsValid;
}

/**
 * Returns the smallest (closest to negative infinity) Price that is
 * not less than the argument and is on a valid tick for the given product.
 *
 * @param   aPrice  price to quantize
 * @param   productDescription
 * @return  Price   quantized price
 */
public static Price ceilToTick(Price aPrice, ProductDescriptionStruct productDescription) {
    return ceilToTick(aPrice,
                       PriceFactory.createValuedPrice (productDescription.premiumBreakPoint),
                       PriceFactory.createValuedPrice (productDescription.minimumBelowPremiumFraction),
                       PriceFactory.createValuedPrice (productDescription.minimumAbovePremiumFraction));
}
/**
 * Returns the smallest (closest to negative infinity) Price that is
 * not less than the argument and is on a valid tick for the given product.
 *
 * @param   aPrice  price to quantize
 * @param   premiumBreakPoint   price break point where the tick value changes
 * @param   premiumBelowTick    tick value below the break point
 * @param   premiumAboveTick    tick value above the break point
 * @return  Price   quantized price
 */
public static Price ceilToTick(Price aPrice,
                               Price premiumBreakPoint,
                               Price premiumBelowTick,
                               Price premiumAboveTick) {
    long tempPrice = 0;
    long minimumTick;
    long breakPoint = premiumBreakPoint.toLong();
    int direction = 1;

    long targetPrice = aPrice.toLong();
    long targetPriceMagnitude = Math.abs(targetPrice);

    if ( targetPrice < 0 ) {
        direction = -1;
    }

    if ( targetPriceMagnitude > breakPoint ) {
        minimumTick = premiumAboveTick.toLong();
        if ( (targetPriceMagnitude - breakPoint) % minimumTick != 0 && targetPrice > 0 ) {
            tempPrice = minimumTick;
        }
        tempPrice = direction * (breakPoint + ((targetPriceMagnitude - breakPoint)/minimumTick)*minimumTick) + tempPrice;
    } else if ( targetPriceMagnitude < breakPoint ) {
        minimumTick = premiumBelowTick.toLong();
        if ( (breakPoint - targetPriceMagnitude) % minimumTick != 0 && targetPrice < 0 ) {
            tempPrice = minimumTick;
        }
        tempPrice = direction * (breakPoint - ((breakPoint - targetPriceMagnitude)/minimumTick)*minimumTick) + tempPrice;
    } else {
        tempPrice = targetPrice;
    }
	return PriceFactory.create(tempPrice);
}

/**
 * Returns the largest (closest to positive infinity) Price that is
 * not greater than the argument and is on a valid tick for the given product.
 *
 * @param   aPrice  price to quantize
 * @param   productDescription
 * @return  Price   quantized price
 */
public static Price floorToTick(Price aPrice, ProductDescriptionStruct productDescription) {
    return floorToTick(aPrice,
                       PriceFactory.createValuedPrice (productDescription.premiumBreakPoint),
                       PriceFactory.createValuedPrice (productDescription.minimumBelowPremiumFraction),
                       PriceFactory.createValuedPrice (productDescription.minimumAbovePremiumFraction));
}
/**
 * Returns the largest (closest to positive infinity) Price that is
 * not greater than the argument and is on a valid tick for the given product.
 *
 * @param   aPrice  price to quantize
 * @param   premiumBreakPoint   price break point where the tick value changes
 * @param   premiumBelowTick    tick value below the break point
 * @param   premiumAboveTick    tick value above the break point
 * @return  Price   quantized price
 */
public static Price floorToTick(Price aPrice,
                                Price premiumBreakPoint,
                                Price premiumBelowTick,
                                Price premiumAboveTick) {
    long tempPrice = 0;
    long minimumTick;
    long breakPoint = premiumBreakPoint.toLong();
    int direction = 1;

    long targetPrice = aPrice.toLong();
    long targetPriceMagnitude = Math.abs(targetPrice);

    if ( targetPrice < 0 ) {
        direction = -1;
    }

    if ( targetPriceMagnitude > breakPoint ) {
        minimumTick = premiumAboveTick.toLong();
        if ( (targetPriceMagnitude - breakPoint) % minimumTick != 0 && targetPrice < 0 ) {
            tempPrice = -minimumTick;
        }
        tempPrice = direction * (breakPoint + ((targetPriceMagnitude - breakPoint)/minimumTick)*minimumTick) + tempPrice;
    } else if ( targetPriceMagnitude < breakPoint ) {
        minimumTick = premiumBelowTick.toLong();
        if ( (breakPoint - targetPriceMagnitude) % minimumTick != 0 && targetPrice > 0 ) {
            tempPrice = -minimumTick;
        }
        tempPrice = direction * (breakPoint - ((breakPoint - targetPriceMagnitude)/minimumTick)*minimumTick) + tempPrice;
    }else {
        tempPrice = targetPrice;
    }
	return PriceFactory.create(tempPrice);
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
public static Price tickUp(Price aPrice, int nbrTicks, ProductDescriptionStruct productDescription) {
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
public Price tickUp(Price aPrice, int nbrTicks) {
	return floorToTick(aPrice, myBreakPoint, myBelowTick, myAboveTick).addTicks(nbrTicks, myBreakPoint, myBelowTick, myAboveTick);
}

/**
 * Returns the Price by adding ticks to the floor of a given price.
 * If input price is already a valid tick, net result is adding tick(s).
 *
 * @param   value   starting price value
 * @param   ticks	number of ticks to tick up from quantized price.
 * @return  Price   quantized price plus tick(s)
 */
public Price tickUp(double value, int nbrTicks) {
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
public static Price tickDown(Price aPrice, int nbrTicks, ProductDescriptionStruct productDescription) {
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
public Price tickDown(Price aPrice, int nbrTicks) {
	return ceilToTick(aPrice, myBreakPoint, myBelowTick, myAboveTick).addTicks(-nbrTicks, myBreakPoint, myBelowTick, myAboveTick);
}

/**
 * Returns the Price by subtracting ticks to the ceil of a given price.
 * If input price is already a valid tick, net result is subtracting tick(s).
 *
 * @param   value   starting price value
 * @param   ticks	number of ticks to tick down from quantized price.
 * @return  Price   quantized price minus tick(s)
 */
public Price tickDown(double value, int nbrTicks) {
	return tickDown(PriceFactory.create(value), nbrTicks);
}


/**
 *
 * @param responsePrice
 * @param auctionPrice
 * @param auctionTickInPenny
 * @return true if the response price is on tick for auction, false otherwise
 */
public static boolean isValidTickForAuction(Price responsePrice,
                                            Price auctionPrice,
                                            int auctionTickInPenny) {
    Price incrementPrice = PriceFactory.create(DEFAULT_SCALE_TO_PENNY * auctionTickInPenny);
    boolean priceOnValidTick = false;
    if ( responsePrice.isValuedPrice() ) {
        long minimumTick = incrementPrice.toLong();
        if (minimumTick > 0) {
            priceOnValidTick = ( ( responsePrice.toLong() - auctionPrice.toLong() ) % minimumTick ) == 0;
        }
    }

    return priceOnValidTick;
}
    /**
     * Check price for firm match orders.
     * @param price
     * @return true if market price or in penny
     */
    public static boolean isValidFirmMatchOrderPrice(PriceStruct price) {
        boolean isInPenny = false;

        if (price.type == PriceTypes.MARKET) {
            isInPenny = true;
        }
        else if (price.type == PriceTypes.LIMIT || price.type == PriceTypes.VALUED) {
            isInPenny = ((price.fraction % DEFAULT_SCALE_TO_PENNY ) == 0);
        }

        return isInPenny;
    }

}
