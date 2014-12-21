//
// -----------------------------------------------------------------------------------
// Source file: SimpleIntegerTradingProperty.java
//
// PACKAGE: com.cboe.interfaces.domain.tradingProperty
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.domain.tradingProperty;

/**
 * Defines a contract for a TradingProperty that represents a single simple integer
 */
public interface SimpleIntegerTradingProperty extends TradingProperty
{
    /**
     * Gets the integer for the trading property
     */
    int getIntegerValue();

    /**
     * Sets the integer for the trading property
     */
    void setIntegerValue(int integerValue);
}
