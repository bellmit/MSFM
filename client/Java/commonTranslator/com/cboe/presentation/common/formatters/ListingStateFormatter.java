// -----------------------------------------------------------------------------------
// Source file: ProductTypeFormatter.java
//
// PACKAGE: com.cboe.presentation.common.formatters;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2001 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.formatters;

import com.cboe.idl.cmiConstants.ListingStates;
import com.cboe.interfaces.presentation.common.formatters.ListingStateFormatStrategy;
import com.cboe.presentation.common.formatters.Formatter;

/**
 * Formats com.cboe.idl.cmiConstants.ListingState as a String
 */
class ListingStateFormatter extends Formatter implements ListingStateFormatStrategy
{
    public final static String ACTIVE_STRING = "Active";
    public final static String INACTIVE_STRING = "Inactive";
    public final static String UNLISTED_STRING = "Unlisted";
    public final static String OBSOLETE_STRING = "Obsolete";
    public final static String UNKNOWN_STRING = "Unknown";

    /**
     * Constructor
     */
    public ListingStateFormatter()
    {
        super();

        addStyle(FULL_LISTING_STATE_NAME, FULL_LISTING_STATE_NAME_DESCRIPTION);

        setDefaultStyle(FULL_LISTING_STATE_NAME);
    }

    /**
     * Formats a ListingState
     * @param short listingState to format
     */
    public String format(short listingState)
    {
        return format(listingState, this.getDefaultStyle());
    }

    /**
     * Defines a method for formatting Listing States
     * @param short listingState to format
     * @param format style to use
     * @return formatted string
     */
    public String format(short listingState, String style)
    {
        String retVal = "";

        if ( ! this.containsStyle(style) )
        {
            throw new IllegalArgumentException("ListingStateFormatter - Unknown Style: '"+style+"'");
        }

        if(style.equals(FULL_LISTING_STATE_NAME))
        {
            switch(listingState)
            {
                case ListingStates.ACTIVE:
                    retVal = ACTIVE_STRING;
                    break;
                case ListingStates.INACTIVE:
                    retVal = INACTIVE_STRING;
                    break;
                case ListingStates.UNLISTED:
                    retVal = UNLISTED_STRING;
                    break;
                case ListingStates.OBSOLETE:
                    retVal = OBSOLETE_STRING;
                    break;
                default:
                    retVal = new StringBuffer(20).append("[ ").append(listingState).append(" ]").toString();
            }
        }

        return retVal;
    }

}
