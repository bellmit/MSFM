//
// -----------------------------------------------------------------------------------
// Source file: FormatStrategy.java
//
// PACKAGE: com.cboe.interfaces.presentation.common.formatters;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.common.formatters;

import java.util.*;

/**
 * Defines a contract for a class that provides data formatting.
 */
public interface FormatStrategy
{
    String INVALID_STYLE_STRING = "Invalid Style";

    /**
     * Determines if the passed style is present in the collection of styles.
     * @return true if style is contained in the FormatStrategy.
     */
    boolean containsStyle(String styleName);

    /**
     * Defines a method that should return the current formatting style in use.
     * @return style currently in use.
     */
    String getDefaultStyle();

    /**
     * Defines a method that should return a map of format styles. The map keys will be the format styles, the map
     * values will be descriptions of the format styles.
     * @return java.util.Map
     */
    Map getFormatStyles();
}
