//
// -----------------------------------------------------------------------------------
// Source file: TradingSessionImpl.java
//
// PACKAGE: com.cboe.presentation.tradingSession;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.tradingSession;

import com.cboe.idl.cmiSession.TradingSessionStruct;

import com.cboe.interfaces.domain.dateTime.Time;
import com.cboe.interfaces.presentation.tradingSession.DefaultTradingSession;
import com.cboe.interfaces.presentation.tradingSession.TradingSession;

import com.cboe.presentation.common.dateTime.TimeImpl;

public class TradingSessionImpl implements TradingSession
{
    protected TradingSessionStruct tradingSessionStruct;

    public TradingSessionImpl(TradingSessionStruct struct)
    {
        if(struct == null)
        {
            throw new IllegalArgumentException("TradingSessionStruct may not be null.");
        }
        setTradingSessionStruct(struct);
    }

    public boolean isDefaultTradingSession()
    {
        return getTradingSessionName().equals(DefaultTradingSession.DEFAULT);
    }

    public int compareTo(Object other)
    {
        int retVal = -1;
        TradingSession otherSession = ( TradingSession ) other;
        retVal = getTradingSessionName().compareTo(otherSession.getTradingSessionName());
        return retVal;
    }

    public boolean equals(Object other)
    {
        boolean isEqual = false;

        if( this == other )
        {
            isEqual = true;
        }
        else if( other == null)
        {
            isEqual = false;
        }
        else
        {
            //Compare the unique session name of each struct for equivalence.
            TradingSession otherSession = ( TradingSession ) other;
            isEqual = getTradingSessionName().equals(otherSession.getTradingSessionName());
        }
        return isEqual;
    }

    public void setTradingSessionStruct(TradingSessionStruct struct)
    {
        if( struct != null )
        {
            tradingSessionStruct = struct;
        }
    }

    public String getTradingSessionName()
    {
        return tradingSessionStruct.sessionName;
    }

    public Time getStartTime()
    {
        return new TimeImpl(tradingSessionStruct.startTime);
    }

    public Time getEndTime()
    {
        return new TimeImpl(tradingSessionStruct.endTime);
    }

    public short getTradingSessionState()
    {
        return tradingSessionStruct.state;
    }

    public long getSequenceNumber()
    {
        return tradingSessionStruct.sequenceNumber;
    }
}