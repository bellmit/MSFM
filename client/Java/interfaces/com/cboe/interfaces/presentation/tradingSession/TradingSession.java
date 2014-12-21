//
// -----------------------------------------------------------------------------------
// Source file: TradingSession.java
//
// PACKAGE: com.cboe.interfaces.presentation.tradingSession;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.tradingSession;

import com.cboe.idl.cmiSession.TradingSessionStruct;

import com.cboe.interfaces.domain.dateTime.Time;

public interface TradingSession extends Comparable
{
    public boolean isDefaultTradingSession();

    public void setTradingSessionStruct(TradingSessionStruct struct);

    public String getTradingSessionName();

    public Time getStartTime();

    public Time getEndTime();

    public short getTradingSessionState();

    public long getSequenceNumber();
}