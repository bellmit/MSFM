//
// ------------------------------------------------------------------------
// FILE: AbstractCommonStylesFormatter.java
// 
// PACKAGE: com.cboe.presentation.common.formatters
// 
// ------------------------------------------------------------------------
// Copyright (c) 1999-2003 The Chicago Board Options Exchange.  All Rights Reserved.
// 
// ------------------------------------------------------------------------
//

package com.cboe.presentation.common.formatters;

import com.cboe.interfaces.presentation.common.formatters.CommonFormatStrategyStyles;

/**
 * @author torresl@cboe.com
 */
abstract public class AbstractCommonStylesFormatter extends Formatter implements CommonFormatStrategyStyles
{
    public AbstractCommonStylesFormatter()
    {
        super();
        initialize();
    }

    private void initialize()
    {
        addStyle(BRIEF_STYLE_NAME, BRIEF_STYLE_DESCRIPTION);
        addStyle(FULL_STYLE_NAME, FULL_STYLE_DESCRIPTION);
        setDefaultStyle(FULL_STYLE_NAME);
    }

    public boolean isBrief(String styleName)
    {
        return BRIEF_STYLE_NAME.equals(styleName);
    }

    public String getDelimiterForStyle(String style)
    {
        if(isBrief(style))
        {
            return BRIEF_STYLE_DELIMITER;
        }
        return FULL_STYLE_DELIMITER;
    }
}
