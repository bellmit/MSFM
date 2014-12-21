// -----------------------------------------------------------------------------------
// Source file: TradingSessionGroupModelFactory
//
// PACKAGE: com.cboe.internalPresentation.product
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.tradingSession;

import com.cboe.idl.session.TradingSessionGroupStruct;
import com.cboe.internalPresentation.tradingSession.TradingSessionGroupModelImpl;
import com.cboe.interfaces.internalPresentation.tradingSession.TradingSessionGroupModel;

public abstract class TradingSessionGroupModelFactory
{
    public static TradingSessionGroupModel create(TradingSessionGroupStruct struct)
    {
        return new TradingSessionGroupModelImpl(struct);
    }

} // -- end of class TradingSessionGroupModelFactory
