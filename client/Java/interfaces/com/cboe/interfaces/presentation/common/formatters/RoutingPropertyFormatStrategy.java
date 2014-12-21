//
// -----------------------------------------------------------------------------------
// Source file: RoutingPropertyFormatStrategy.java
//
// PACKAGE: com.cboe.interfaces.presentation.common.formatters
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.common.formatters;

import com.cboe.interfaces.domain.routingProperty.BaseProperty;
import com.cboe.interfaces.domain.routingProperty.BasePropertyGroup;

/**
 * @author Thomas Morrow
 * @since Jan 16, 2008
 */
public interface RoutingPropertyFormatStrategy extends FormatStrategy
{
    String FULL_NAME_TYPE_STYLE_NAME = "Full Name Type";
    String FULL_NAME_TYPE_STYLE_DESCRIPTION = "Full Name of Property Type";

    String NAME_VALUE_PAIR_STYLE_NAME = "Name Value Pair";
    String NAME_VALUE_PAIR_STYLE_DESCRIPTION = "Name Value Pair. Name is NOT Proper style.";
    
    String LIST_STYLE_NAME = "List Style name";
    String LIST_STYLE_DESCRIPTION = "List all values separated with comma";
    

    String format(BasePropertyGroup basePropertyGroup);

    String format(BasePropertyGroup basePropertyGroup, String styleName);

    String format(BaseProperty baseProperty);

    String format(BaseProperty baseProperty, String styleName);

    
}