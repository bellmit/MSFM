//
// -----------------------------------------------------------------------------------
// Source file: TradingSessionModelFactory.java
//
// PACKAGE: com.cboe.internalPresentation.tradingSession
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.tradingSession;

import com.cboe.idl.session.TradingSessionStruct;

import com.cboe.interfaces.internalPresentation.tradingSession.TradingSessionModel;

public abstract class TradingSessionModelFactory
{
    public static TradingSessionModel createTradingSessionModel(TradingSessionStruct struct)
    {
        return new TradingSessionModelImpl(struct);
    }
}
