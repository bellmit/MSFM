//
// -----------------------------------------------------------------------------------
// Source file: TradingPropertyFormatStrategy.java
//
// PACKAGE: com.cboe.interfaces.presentation.common.formatters
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.common.formatters;

import java.beans.PropertyDescriptor;

import com.cboe.interfaces.domain.tradingProperty.TradingProperty;
import com.cboe.interfaces.domain.tradingProperty.TradingPropertyGroup;

public interface TradingPropertyFormatStrategy extends FormatStrategy
{
    String PROPERTY_PROPER_STYLE_NAME = "Property Name Proper English";
    String PROPERTY_PROPER_STYLE_DESCRIPTION = "Property Name formatted with proper spacing and case.";

    String NAME_VALUE_PAIR_STYLE_NAME = "Name Value Pair";
    String NAME_VALUE_PAIR_STYLE_DESCRIPTION = "Name Value Pair. Name is NOT Proper style.";

    String PROPER_NAME_VALUE_PAIR_STYLE_NAME = "Name Value Pair. Proper Name.";
    String PROPER_NAME_VALUE_PAIR_STYLE_DESCRIPTION = "Name Value Pair. Name IS Proper style.";

    String PROPERTY_DEFINITION_NAME_VALUE_STYLE_NAME = "Property Definition Name Value Pair.";
    String PROPERTY_DEFINITION_NAME_VALUE_STYLE_DESCRIPTION = "Name Value Pair. Name and Value are from Property Definition.";

    String PROPERTY_DEFINITION_VALUE_STYLE_NAME = "Property Definition Value.";
    String PROPERTY_DEFINITION_VALUE_STYLE_DESCRIPTION = "Value using Property Definition.";

    String PROPERTY_DEFINITION_PROPER_NAME_STYLE_NAME = "Property Definition Name Proper English.";
    String PROPERTY_DEFINITION_PROPER_NAME_STYLE_DESCRIPTION = "Property Name from Property Definition, formatted with proper spacing and case.";

    String INVALID_STYLE_FORMAT = "Invalid Style Requested";

    String NO_VALUE = "No Value";

    String UNKNOWN_VALUE = "Unknown Value";

    /**
     * Attempts to format the passed TradingPropertyGroup using the default style returned by
     * getDefaultStyleForTradingPropertyGroup().
     * @param tradingPropertyGroup to format
     * @return String representation of the TradingPropertyGroup
     */
    String format(TradingPropertyGroup tradingPropertyGroup);

    /**
     * Attempts to format the passed TradingPropertyGroup using the passed style.
     * @param tradingPropertyGroup to format
     * @param styleName style to use for formatting
     * @return Object representation of a TradingPropertyGroup. See the implementation for details about what this
     * Object is.
     */
    String format(TradingPropertyGroup tradingPropertyGroup, String styleName);

    /**
     * Attempts to format the passed TradingProperty using the default style returned by
     * getDefaultStyleForTradingProperty().
     * @param tradingProperty to format
     * @return String representation of the TradingProperty
     */
    String format(TradingProperty tradingProperty);

    /**
     * Attempts to format the passed TradingProperty using the passed style.
     * @param tradingProperty to format
     * @param styleName style to use for formatting
     * @return Object representation of a TradingProperty. See the implementation for details about what this
     *         Object is.
     */
    String format(TradingProperty tradingProperty, String styleName);

    /**
     * Attempts to format the passed PropertyDescriptor using the default style returned by
     * getDefaultStyleForPropertyDescriptor().
     * @param propertyDescriptor to format
     * @param tradingProperty that may be needed to resolve certain style formats. May be null if the style does not
     * require it.
     * @return String representation of the PropertyDescriptor
     */
    String format(PropertyDescriptor propertyDescriptor, TradingProperty tradingProperty);

    /**
     * Attempts to format the passed PropertyDescriptor using the passed style.
     * @param propertyDescriptor to format
     * @param tradingProperty that may be needed to resolve certain style formats. May be null if the style does not
     * require it.
     * @param styleName style to use for formatting
     * @return Object representation of a PropertyDescriptor. See the implementation for details about what this Object
     *         is.
     */
    String format(PropertyDescriptor propertyDescriptor, TradingProperty tradingProperty, String styleName);

    /**
     * Determines the default style when calling format for TradingPropertyGroup's
     * @return default style for TradingPropertyGroup's
     */
    String getDefaultStyleForTradingPropertyGroup();

    /**
     * Determines the default style when calling format for TradingProperty's
     * @return default style for TradingProperty's
     */
    String getDefaultStyleForTradingProperty();

    /**
     * Determines the default style when calling format for PropertyDescriptor's
     * @return default style for PropertyDescriptor's
     */
    String getDefaultStyleForPropertyDescriptor();
}
