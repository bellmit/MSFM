package com.cboe.interfaces.presentation.api;

import com.cboe.idl.cmiSession.TradingSessionStruct;

import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;

import com.cboe.util.event.EventChannelListener;

public interface TradingSessionAPI
{
    public void subscribeTradingSessions(EventChannelListener clientListener)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    public TradingSessionStruct[] getCurrentTradingSessions(EventChannelListener clientListener)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    public TradingSessionStruct getAllSessionsTradingSession();

}
