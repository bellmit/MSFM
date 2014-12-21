// -----------------------------------------------------------------------------------
// Source file: OverrideIndicatorFormatter
//
// PACKAGE: com.cboe.presentation.common.formatters
// 
// Created: Jul 21, 2004 1:30:06 PM
// -----------------------------------------------------------------------------------
// Copyright (c) 2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.formatters;

import com.cboe.interfaces.presentation.common.formatters.OverrideIndicatorFormatStrategy;

public class OverrideIndicatorFormatter extends Formatter implements OverrideIndicatorFormatStrategy
{
    public static final char NONE                 = com.cboe.idl.cmiConstants.OverrideIndicatorTypes.NONE;
    public static final char LINKAGE              = com.cboe.idl.cmiConstants.OverrideIndicatorTypes.LINKAGE;
    public static final char BOOK_OVERRIDE        = com.cboe.idl.cmiConstants.OverrideIndicatorTypes.BOOK_OVERRIDE;
    public static final char OFFER_OVERRIDE       = com.cboe.idl.cmiConstants.OverrideIndicatorTypes.OFFER_OVERRIDE;
    public static final char SUPERVISORY_OVERRIDE = com.cboe.idl.cmiConstants.OverrideIndicatorTypes.SUPERVISORY_OVERRIDE;

    public static final String NONE_STRING_FULL                 = "None";
    public static final String LINKAGE_STRING_FULL              = "Linkage";
    public static final String BOOK_OVERRIDE_STRING_FULL        = "Book";
    public static final String OFFER_OVERRIDE_STRING_FULL       = "Offer";
    public static final String SUPERVISORY_OVERRIDE_STRING_FULL = "Supervisory";
    public static final String UNKNOWN_STRING_FULL              = "Unknown";

    public static final String NONE_STRING_BRIEF                 = " ";
    public static final String LINKAGE_STRING_BRIEF              = "L";
    public static final String BOOK_OVERRIDE_STRING_BRIEF        = "B";
    public static final String OFFER_OVERRIDE_STRING_BRIEF       = "O";
    public static final String SUPERVISORY_OVERRIDE_STRING_BRIEF = "X";


    public OverrideIndicatorFormatter()
    {
        super();
        addStyle(FULL, FULL_DESC);
        addStyle(BRIEF, BRIEF_DESC);

        setDefaultStyle(FULL);
    }

    public String format(char indicator)
    {
        return format(indicator, getDefaultStyle());
    }

    public String format(char indicator, String styleName)
    {
        validateStyle(styleName);

        String retValue = "";

        if (styleName.equalsIgnoreCase(BRIEF))
        {
            retValue = formatBrief(indicator);
        }
        else if (styleName.equalsIgnoreCase(FULL))
        {
            retValue = formatFull(indicator);
        }

        return retValue;
    }

    private String formatFull(char indicator)
    {
        switch (indicator)
        {
            case NONE:
                return NONE_STRING_FULL;
            case LINKAGE:
                return LINKAGE_STRING_FULL;
            case BOOK_OVERRIDE:
                return BOOK_OVERRIDE_STRING_FULL;
            case OFFER_OVERRIDE:
                return OFFER_OVERRIDE_STRING_FULL;
            case SUPERVISORY_OVERRIDE:
                return SUPERVISORY_OVERRIDE_STRING_FULL;
            default:
                return new StringBuffer().append(UNKNOWN_STRING_FULL).append("[ ").append(indicator).append(" ]").toString();
        }
    }

    private String formatBrief(char indicator)
    {
        switch (indicator)
        {
            case NONE:
                return NONE_STRING_BRIEF;
            case LINKAGE:
                return LINKAGE_STRING_BRIEF;
            case BOOK_OVERRIDE:
                return BOOK_OVERRIDE_STRING_BRIEF;
            case OFFER_OVERRIDE:
                return OFFER_OVERRIDE_STRING_BRIEF;
            case SUPERVISORY_OVERRIDE:
                return SUPERVISORY_OVERRIDE_STRING_BRIEF;
            default:
                return new StringBuffer().append("[ ").append(indicator).append(" ]").toString();
        }
    }

}
