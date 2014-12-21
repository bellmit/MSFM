// -----------------------------------------------------------------------------------
// Source file: ListingStateFormatStrategy.java
//
// PACKAGE: com.cboe.interfaces.presentation.common.formatters;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2001 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.common.formatters;

/**
 * Defines a contract for a class that formats ListingStates
 */
public interface ListingStateFormatStrategy extends FormatStrategy
{
    public static final String FULL_LISTING_STATE_NAME = "Full Listing State Name";

    public static final String FULL_LISTING_STATE_NAME_DESCRIPTION = "Full Listing State Name";

    /**
     * Defines a method for formatting ProductType
     * @param productType to format
     * @return formatted string
     */
    public String format(short listingState);

    /**
     * Defines a method for formatting Listing States
     * @param listingState to format
     * @param format style to use
     * @return formatted string
     */
    public String format(short listingState, String style);

}

