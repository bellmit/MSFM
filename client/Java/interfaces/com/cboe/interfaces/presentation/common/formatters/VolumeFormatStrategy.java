//
// -----------------------------------------------------------------------------------
// Source file: VolumeFormatStrategy.java
//
// PACKAGE: com.cboe.interfaces.presentation.common.formatters;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2001 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.common.formatters;

/**
 * Defines a contract for class that formats volumes.
 * @author Nick DePasquale
 */
public interface VolumeFormatStrategy extends FormatStrategy
{


    public static final String STANDARD_VOLUME_NAME = "Std Number with Grouping";
    public static final String TICKER_VOLUME_NAME = "Ticker Volume Fromatting";

    public static final String STANDARD_VOLUME_DESCRIPTION = "Standard number with decimals and grouping enabled.";
    public static final String TICKER_VOLUME_DESCRIPTION = "Ticker Volume Formatting.";

    /**
    * Defines a method for formatting volumes.
    * @param volume to format.
    * @return formatted string
    * @author Nick DePasquale
    */
    public String format(int volume);
    /**
    * Defines a method for formatting volumes.
    * @param volume to format.
    * @param styleName to use
    * @return formatted string
    * @author Nick DePasquale
    */
    public String format(int volume, String styleName);
}
