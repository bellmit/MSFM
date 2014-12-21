//
// -----------------------------------------------------------------------------------
// Source file: FormatFactory.java
//
// PACKAGE: com.cboe.internalPresentation.common.formatters;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.common.formatters;

import com.cboe.interfaces.internalPresentation.common.formatters.GroupErrorCodeResultFormatStrategy;
import com.cboe.interfaces.internalPresentation.common.formatters.TradingSessionEventStateFormatStrategy;

/**
 * Provides a factory for creating strategies that will format objects.
 */
public class FormatFactory
{
    private static TradingSessionEventStateFormatter tradingSessionEventStateFormatter = null;
    private static GroupErrorCodeResultFormatter groupErrorCodeResultFormatter = null;
    private static UserAccountModelFormatter userAccountModelFormatter = null;

    private FormatFactory()
    {}

    public static UserAccountModelFormatter getUserAccountModelFormatter()
    {
        if(userAccountModelFormatter == null)
        {
            userAccountModelFormatter = new UserAccountModelFormatter();
        }
        return userAccountModelFormatter;
    }

    /**
     * Gets a singleton instance of the TradingSessionEventStateFormatStrategy
     * @return implementation of TradingSessionEventStateFormatStrategy
     */
    public static TradingSessionEventStateFormatStrategy getTradingSessionEventStateFormatStrategy()
    {
        if(tradingSessionEventStateFormatter == null)
        {
            tradingSessionEventStateFormatter = new TradingSessionEventStateFormatter();
        }
        return tradingSessionEventStateFormatter;
    }

    /**
     * Gets a singleton instance of the GroupErrorCodeResultFormatStrategy
     * @return implementation of GroupErrorCodeResultFormatStrategy
     */
    public static GroupErrorCodeResultFormatStrategy getGroupErrorCodeResultFormatStrategy()
    {
        if(groupErrorCodeResultFormatter == null)
        {
            groupErrorCodeResultFormatter = new GroupErrorCodeResultFormatter();
        }
        return groupErrorCodeResultFormatter;
    }
}