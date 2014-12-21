package com.cboe.interfaces.application;

import com.cboe.interfaces.domain.session.SessionBasedCollector;

/**
 *
 * @author Connie Feng
 *
 */
public interface TradingSessionStatusCollector extends SessionBasedCollector
{
    public void acceptTradingSessionState(com.cboe.idl.cmiSession.TradingSessionStateStruct sessionState);
}
