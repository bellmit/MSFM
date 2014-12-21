//
// -----------------------------------------------------------------------------------
// Source file: BooleanFormatter.java
//
// PACKAGE: com.cboe.presentation.common.formatters
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.formatters;

import com.cboe.interfaces.presentation.common.formatters.ErrorCodeFormatStrategy;

/**
 * @author Thomas Morrow
 * @since Sep 21, 2007
 */
public class ErrorCodeFormatter extends Formatter implements ErrorCodeFormatStrategy
{
    public ErrorCodeFormatter()
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
        String string = Short.toString(aShort);
        validateStyle(style);
        if (style.equals(CAPITALIZED_FORMAT))
        {
            if (aShort == 0)
            {
                string = SUCCESS_STRING;
            }
            else
            {
                string = FAILURE_STRING;
            }
        }
        else if (style.equals(UPPER_CASE_FORMAT))
        {
            if (aShort == 0)
            {
                string = SUCCESS_STRING.toUpperCase();
            }
            else
            {
                string = FAILURE_STRING.toUpperCase();
            }
        }
        return string;
    }
}