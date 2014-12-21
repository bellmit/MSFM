//
// -----------------------------------------------------------------------------------
// Source file: StrategyTypeFormatter.java
//
// PACKAGE: com.cboe.presentation.common.formatters
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.formatters;

import com.cboe.idl.cmiConstants.StrategyTypes;
import com.cboe.interfaces.presentation.common.formatters.StrategyTypeFormatStrategy;

/**
 * @author Thomas Morrow
 * @since Sep 24, 2007
 */
public class StrategyTypeFormatter extends Formatter implements StrategyTypeFormatStrategy
{

    public StrategyTypeFormatter()
    {
        addStyle(UPPER_CASE_FORMAT, UPPER_CASE_FORMAT_DESC);
        addStyle(CAPITALIZED_FORMAT, CAPITALIZED_FORMAT_DESC);

        setDefaultStyle(CAPITALIZED_FORMAT);
    }

    public String format(Short aShort)
    {
        return format(aShort, CAPITALIZED_FORMAT);
    }

    public String format(Short aShort, String style)
    {
        String str = null;
        validateStyle(style);
        if (style.equals(CAPITALIZED_FORMAT))
        {
            str = getStrategyTypeString(aShort);
        }
        else if (style.equals(UPPER_CASE_FORMAT))
        {
            str = getStrategyTypeString(aShort).toUpperCase();
        }
        return str;
    }

    private String getStrategyTypeString(Short aShort)
    {
        String str;
        switch (aShort)
        {
            case StrategyTypes.UNKNOWN:
                str = UNKNOWN_STRING;
                break;

            case StrategyTypes.STRADDLE:
                str = STRADDLE_STRING;
                break;

            case StrategyTypes.PSEUDO_STRADDLE:
                str = PSEUDO_STRADDLE_STRING;
                break;

            case StrategyTypes.VERTICAL:
                str = VERTICAL_STRING;
                break;

            case StrategyTypes.RATIO:
                str = RATIO_STRING;
                break;

            case StrategyTypes.TIME:
                str = TIME_STRING;
                break;

            case StrategyTypes.DIAGONAL:
                str = DIAGONAL_STRING;
                break;

            case StrategyTypes.COMBO:
                str = COMBO_STRING;
                break;

            case StrategyTypes.BUY_WRITE:
                str = BUY_WRITE_STRING;
                break;

            default:
                str = UNDEFINED_STRING;
                break;

        }
        return str;
    }
}
