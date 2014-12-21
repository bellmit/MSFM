package com.cboe.interfaces.domain;

/**
 * this interface is defined to define the tick scale related to a trading class and certain
 * operations related to tick
 */
public interface TickScale {

    /**
     * Given a base price, and the direction of price change, this
     * method will return the correct tick.
     */
    public Price getTick(Price basePrice, int direction);

    /**
     * determine if a price is on tick
     */
    public boolean isOnTick(Price price);

    /**
     * add number of ticks to the price
     */
    public Price addTicks(Price basePrice, int ticks);

    /**
     * return a price which is the closest price on tick
     */
    public Price getNearestPriceOnTick(double value);

    /**
     * Return the number of tick difference of price from basePrice
     */
    public int getTickDifference(Price basePrice, Price price);
}
