package com.cboe.interfaces.domain.tradingProperty;

/**
 * @author Mike Hasbrouck
 *
 * This interface defines the constant values need to 
 * control any price adjustments for reporting only purposes
 * 
 */
 
 // -----------------------------------------------------------------------------------
 // Copyright (c) 2000-2006 The Chicago Board Options Exchange. All Rights Reserved.
 // -----------------------------------------------------------------------------------
 
public interface ReportingPriceAdjustment extends TradingProperty
{
    public static final short NO_PRICE_ADJUSTMENT = 1;
    public static final short SQUARE_PRICE = 2;
    
}
