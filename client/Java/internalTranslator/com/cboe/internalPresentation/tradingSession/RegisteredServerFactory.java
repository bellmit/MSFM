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

import com.cboe.idl.session.TradingSessionStruct;
import com.cboe.idl.session.RegisteredServerNameDetailStruct;
import com.cboe.idl.session.TradingSessionRegistrationStruct;

import com.cboe.interfaces.internalPresentation.tradingSession.RegisteredServer;

public abstract class RegisteredServerFactory
{
    public static RegisteredServer createRegisteredServer(TradingSessionStruct sessionStruct,
                                                          RegisteredServerNameDetailStruct serverStruct)
    {
        return new RegisteredServerImpl(sessionStruct, serverStruct);
    }

    public static RegisteredServer[] createRegisteredServers(TradingSessionRegistrationStruct struct)
    {
        RegisteredServerNameDetailStruct[] detailStructs = struct.registeredServers;
        TradingSessionStruct sessionStruct = struct.session;
        RegisteredServer[] servers = new RegisteredServer[detailStructs.length];
        for( int i = 0; i < detailStructs.length; i++ )
        {
            RegisteredServerNameDetailStruct detailStruct = detailStructs[i];
            servers[i] = createRegisteredServer(sessionStruct, detailStruct);
        }
        return servers;
    }
}
