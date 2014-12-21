//
// -----------------------------------------------------------------------------------
// Source file: SimpleBooleanTradingProperty.java
//
// PACKAGE: com.cboe.interfaces.domain.tradingProperty
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.domain.tradingProperty;

/**
 * Defines a contract for a TradingProperty that represents a single simple boolean
 */
public interface SimpleBooleanTradingProperty extends TradingProperty
{
    /**
     * Gets the boolean for the trading property
     */
    boolean getBooleanValue();

    /**
     * Sets the boolean for the trading property
     */
    void setBooleanValue(boolean booleanValue);
}
