//
// -----------------------------------------------------------------------------------
// Source file: RegisteredServerFactory.java
//
// PACKAGE: com.cboe.internalPresentation.tradingSession
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.tradingSession;

import com.cboe.idl.session.TradingSessionServerEventStateStruct;

import com.cboe.interfaces.internalPresentation.tradingSession.RegisteredServerEvent;
import com.cboe.interfaces.internalPresentation.tradingSession.TradingSessionEvent;

public abstract class RegisteredServerEventFactory
{
    public static RegisteredServerEvent createRegisteredServer(TradingSessionServerEventStateStruct struct)
    {
        return new RegisteredServerEventImpl(struct);
    }

    public static RegisteredServerEvent createRegisteredServer(TradingSessionServerEventStateStruct struct, TradingSessionEvent tradingSessionEvent)
    {
        return new RegisteredServerEventImpl(struct, tradingSessionEvent);
    }
}
