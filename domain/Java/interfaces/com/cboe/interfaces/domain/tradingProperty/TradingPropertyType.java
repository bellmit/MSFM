//
// -----------------------------------------------------------------------------------
// Source file: TradingPropertyType.java
//
// PACKAGE: com.cboe.interfaces.domain.tradingProperty;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.domain.tradingProperty;

/**
 * Provides the contract for references and conversion between a Trading Property integer constant and the name.
 */
public interface TradingPropertyType
{
    String PROPERTY_TYPE_NOT_DEFINED = "PROPERTY_TYPE_NOT_DEFINED";

    /**
     * Returns the defined integer constant
     */
    int getType();

    /**
     * Returns the programmatic name
     */
    String getName();

    /**
     * Gets the property category that will contain this type.
     */
    String getPropertyCategory();

    /**
     * Returns the English formatted name
     */
    String getFullName();

    /**
     * Returns whether the type is product class specific or not.
     * @return True if applicable to specific product classes, false if only applicable to trading sessions.
     */
    boolean isProductClassSpecific();

    /**
     * Returns the English formatted all classes name for getAllClasses() lookup
     */
    String getAllClassesName();
}

