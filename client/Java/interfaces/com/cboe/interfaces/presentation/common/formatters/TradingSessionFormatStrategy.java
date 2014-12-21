//
// -----------------------------------------------------------------------------------
// Source file: TradingSessionFormatStrategy.java
//
// PACKAGE: com.cboe.interfaces.presentation.common.formatters;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.common.formatters;

import com.cboe.idl.cmiSession.TradingSessionStruct;

import com.cboe.interfaces.presentation.tradingSession.TradingSession;

public interface TradingSessionFormatStrategy extends FormatStrategy
{
    public static final String ALL_SESSIONS_FORMATTED_NAME = "<All Sessions>";

    public static final String PLAIN_TRADING_SESSION_NAME = "Plane Trading Session Name";
    public static final String TRADING_SESSION_STATE = "Trading Session State";
    public static final String TRADING_SESSION_NAME_AND_STATE = "Trading Session Name and State";

    public static final String PLAIN_TRADING_SESSION_NAME_DESCRIPTION = "Plane Trading Session Name";
    public static final String TRADING_SESSION_NAME_AND_STATE_DESCRIPTION = "Trading Session Name with State";
    public static final String TRADING_SESSION_STATE_DESCRIPTION = "Trading Session State";

    /**
     * Defines a method for formatting TradingSessionStruct
     * @param product to format
     * @return formatted string
     */
    public String format(TradingSessionStruct tradingSession);
    /**
     * Defines a method for formatting TradingSessionStruct
     * @param product to format
     * @param styleName to use for formatting
     * @return formatted string
     */
    public String format(TradingSessionStruct tradingSession, String styleName);

    public String format(TradingSession tradingSession);

    public String format(TradingSession tradingSession, String styleName);
}
