/*
 * Created on Apr 25, 2005
 */
package com.cboe.interfaces.domain.tradingProperty;

/**
 * @author saborm
 *
 * TODO What is this type for?
 */
public interface OpeningPriceValidation extends TradingProperty
{
    public static final short WORST_MM_QUOTE_MULTIPLIER = 0;
    public static final short DPM_QUOTE_MULTIPLIER = 1;
    public static final short BEST_MM_QUOTE_EPW = 2;
    public static final short NO_QUOTE = 3;
    
    public void setValidationType(short type);
    public short getValidationType();
}
