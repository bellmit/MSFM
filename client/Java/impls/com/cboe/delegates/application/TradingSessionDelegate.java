package com.cboe.delegates.application;

import com.cboe.interfaces.application.TradingSession;

public class TradingSessionDelegate extends com.cboe.idl.cmi.POA_TradingSession_tie {
    public TradingSessionDelegate(TradingSession delegate) {
        super(delegate);
    }
}
