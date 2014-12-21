//
// -----------------------------------------------------------------------------------
// Source file: SimpleDoubleTradingProperty.java
//
// PACKAGE: com.cboe.interfaces.domain.tradingProperty
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.domain.tradingProperty;

/**
 * Defines a contract for a TradingProperty that represents a single simple double
 */
public interface SimpleDoubleTradingProperty extends TradingProperty
{
    /**
     * Gets the double for the trading property
     */
    double getDoubleValue();

    /**
     * Sets the double for the trading property
     */
    void setDoubleValue(double doubleValue);
}
