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

import com.cboe.idl.session.TradingSessionEventDescriptionStruct;
import com.cboe.idl.session.TradingSessionEventHistoryStructV2;

import com.cboe.interfaces.internalPresentation.tradingSession.TradingSessionEvent;

public abstract class TradingSessionEventFactory
{
    public static TradingSessionEvent createTradingSessionEvent(TradingSessionEventDescriptionStruct struct)
    {
        return new TradingSessionEventImpl(struct);
    }

    public static TradingSessionEvent createTradingSessionEvent(TradingSessionEventHistoryStructV2 struct)
    {
        return new TradingSessionEventImpl(struct);
    }
}
