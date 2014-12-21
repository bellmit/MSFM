package com.cboe.domain;

import com.cboe.domain.util.PriceFactory;
import com.cboe.domain.util.ValuedPrice;
import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.domain.TickScale;

/**
 * Implementation of TickScale
 */
public class TickScaleImpl implements TickScale{

    public static short REGULAR_TS = 0;
    public static short AUCTION_UNSPECIFIED_TS = 1;
    public static short AUCTION_INTERNALIZATION_TS = 2;
    public static short AUCTION_STRATEGY_TS = 3;
    public static short AUCTION_REGULAR_SINGLE_TS = 4;
    public static short AUCTION_SAL = 5;
    public static short SUB_PENNY = 6;

    public static Price SUB_PENNY_TICK_SIZE = PriceFactory.create(0.0001);
    public static Price BREAK_POINT = PriceFactory.create(3.0);

    private final short type;
    private final Price tickBelow;
    private final Price breakPoint;
    private final Price tickAbove;

    private final long tickBelowLong;
    private final long breakPointLong;
    private final long tickAboveLong;

    public TickScaleImpl(Price aTickBelow, Price aBreakPoint, Price aTickAbove){
        this(REGULAR_TS, aTickBelow, aBreakPoint, aTickAbove);
    }

    public TickScaleImpl(short aType, Price aTickBelow, Price aBreakPoint, Price aTickAbove){
        type = aType;
        tickBelow = aTickBelow;
        breakPoint = aBreakPoint;
        tickAbove = aTickAbove;
        tickBelowLong = tickBelow.toLong();
        breakPointLong = breakPoint.toLong();
        tickAboveLong = tickAbove.toLong();
    }

    public boolean isType(short aType)
    {
        return type == aType;
    }
    /**
     * Note: If the basePrice is negative, we will first make it positive and reverse the direction.
     */
    public final Price getTick(Price basePrice, int direction)
    {
        Price result;
        long basePriceLong = basePrice.toLong();
        int newDirection = direction;
        if (basePriceLong < 0) {
            basePriceLong = 0 - basePriceLong;
            newDirection = 0 - direction;
        }
        if (basePriceLong > breakPointLong) {
            result = tickAbove;
        }
        else if (basePriceLong < breakPointLong) {
            result = tickBelow;
        }
        else {
            if (newDirection > 0)
                result = tickAbove;
            else
                result = tickBelow;
        }
        return result;
    }
    
    public final long getTick(long basePriceLong, int direction)
    {
        long result;
        
        int newDirection = direction;
        if (basePriceLong < 0) {
            basePriceLong = 0 - basePriceLong;
            newDirection = 0 - direction;
        }
        if (basePriceLong > breakPointLong) {
            result = tickAboveLong;
        }
        else if (basePriceLong < breakPointLong) {
            result = tickBelowLong;
        }
        else {
            if (newDirection > 0)
                result = tickAboveLong;
            else
                result = tickBelowLong;
        }
        return result;
    }

    /**
     * add number of ticks to the price
     */
    public Price addTicks(Price basePrice, int ticks)
    {
        return basePrice.addTicks(ticks, breakPoint, tickBelow, tickAbove);
    }

    /**
     * Return the number of tick difference of price from basePrice
     */
    public int getTickDifference(Price basePrice, Price price)
    {
        return basePrice.getTickDifference(price, breakPoint, tickBelow, tickAbove);
    }

    /**
     * return a price which is the closest price on tick
     */
    public Price getNearestPriceOnTick(double value)
    {
        return PriceFactory.createNearest(value, breakPoint, tickBelow, tickAbove);
    }
    
    public final long getNearestPriceOnTickAsLong(long value)
    {
        long resultValue = PriceFactory.getNearest(value, breakPointLong, tickBelowLong, tickAboveLong);
        return resultValue;
    }

    /**
     * @Override
     * override the toString() method
     */
    public String toString()
    {
        return "type/tickBelow/breakPoint/tickAbove=" + type + "/" + tickBelow + "/" + breakPoint + "/" + tickAbove;
    }

	/**
	 * determine if a price is on tick
	 */
	public boolean isOnTick(Price price)
	{
		long priceLong = price.toLong();
		return isOnTick(priceLong);
    }

	public final boolean isOnTick(long priceLong)
	{
		boolean result;
		if (priceLong  < 0) {
			priceLong = 0 - priceLong;
		}
		if (priceLong >= breakPointLong) {
			result = priceLong % tickAboveLong == 0;
		}
		else {
			result = priceLong % tickBelowLong == 0;
		}
        return result;
	}

}
