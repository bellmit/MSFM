package com.cboe.interfaces.domain;

/**
 * This interface is designed to provide a place to hold a range of price
 */
public interface PriceRange {

    public Price getStartingPrice();
    public Price getEndingPrice();
    public boolean isPriceWithinRange(Price midPrice);
    public boolean pricesAreCrossed();
}
