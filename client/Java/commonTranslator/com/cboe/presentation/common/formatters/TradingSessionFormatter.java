//
// -----------------------------------------------------------------------------------
// Source file: Formatter.java
//
// PACKAGE: com.cboe.presentation.common.formatters;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2001 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.formatters;

import com.cboe.interfaces.presentation.common.formatters.TradingSessionFormatStrategy;
import com.cboe.interfaces.presentation.tradingSession.DefaultTradingSession;
import com.cboe.interfaces.presentation.tradingSession.TradingSession;

import com.cboe.idl.cmiSession.TradingSessionStruct;
import com.cboe.presentation.common.formatters.Formatter;

/**
 * Implements the TradingSessionFormatStrategy
 */
class TradingSessionFormatter extends Formatter implements TradingSessionFormatStrategy
{
    /**
     * Constructor, defines styles and sets initial default style
     */
    public TradingSessionFormatter()
    {
        super();

        addStyle(PLAIN_TRADING_SESSION_NAME, PLAIN_TRADING_SESSION_NAME_DESCRIPTION);
        addStyle(TRADING_SESSION_NAME_AND_STATE, TRADING_SESSION_NAME_AND_STATE_DESCRIPTION);
        addStyle(TRADING_SESSION_STATE, TRADING_SESSION_STATE_DESCRIPTION);

        setDefaultStyle(PLAIN_TRADING_SESSION_NAME);
    }

    public String format(TradingSession tradingSession)
    {
        return format(tradingSession, getDefaultStyle());
    }

    public String format(TradingSession tradingSession, String styleName)
    {
        validateStyle(styleName);
        StringBuffer sessionText = new StringBuffer();

        if( styleName.equals(PLAIN_TRADING_SESSION_NAME) )
        {
            sessionText.append(getTradingSessionName(tradingSession));
        }
        else if( styleName.equals(TRADING_SESSION_STATE) )
        {
            sessionText.append(getTradingSessionState(tradingSession));
        }
        else if( styleName.equals(TRADING_SESSION_NAME_AND_STATE) )
        {
            sessionText.append(getTradingSessionName(tradingSession));
            sessionText.append(getTradingSessionState(tradingSession));
        }

        return sessionText.toString();
    }

    public String format(TradingSessionStruct tradingSessionStruct)
    {
        return format(tradingSessionStruct, getDefaultStyle());
    }

    public String format(TradingSessionStruct tradingSessionStruct, String styleName)
    {
        validateStyle(styleName);
        StringBuffer sessionText = new StringBuffer();

        if(styleName.equals(PLAIN_TRADING_SESSION_NAME))
        {
            sessionText.append(getTradingSessionName(tradingSessionStruct));
        }
        else if(styleName.equals(TRADING_SESSION_STATE))
        {
            sessionText.append(getTradingSessionState(tradingSessionStruct));
        }
        else if(styleName.equals(TRADING_SESSION_NAME_AND_STATE))
        {
            sessionText.append(getTradingSessionName(tradingSessionStruct));
            sessionText.append(getTradingSessionState(tradingSessionStruct));
        }

        return sessionText.toString();
    }

    private String getTradingSessionName(TradingSessionStruct tradingSessionStruct)
    {
        if( tradingSessionStruct.sessionName.equals(DefaultTradingSession.DEFAULT) )
        {
            return ALL_SESSIONS_FORMATTED_NAME;
        }
        else
        {
            return tradingSessionStruct.sessionName;
        }
    }

    private String getTradingSessionName(TradingSession tradingSession)
    {
        if( tradingSession.isDefaultTradingSession() )
        {
            return ALL_SESSIONS_FORMATTED_NAME;
        }
        else
        {
            return tradingSession.getTradingSessionName();
        }
    }

    private String getTradingSessionState(TradingSessionStruct tradingSessionStruct)
    {
        return buildStateString(tradingSessionStruct.state);

    }

    private String getTradingSessionState(TradingSession tradingSession)
    {
        return buildStateString(tradingSession.getTradingSessionState());
    }

    private String buildStateString(short state)
    {
        StringBuffer stateBuffer = new StringBuffer();
        stateBuffer.append('(');
        stateBuffer.append(TradingSessionStates.toString(state));
        stateBuffer.append(')');

        return stateBuffer.toString();
    }
}
