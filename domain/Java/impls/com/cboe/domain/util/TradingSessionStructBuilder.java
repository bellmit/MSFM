package com.cboe.domain.util;

import com.cboe.idl.cmiSession.*;

public class TradingSessionStructBuilder
{
private TradingSessionStructBuilder()
{
	super();
}

public static TradingSessionStruct[] convertCMITradingSessionStructs(com.cboe.idl.session.TradingSessionStruct[] sessionStructs)
{
    if (sessionStructs == null )
    {
        return null;
    }
    else
    {
        TradingSessionStruct[] cmiSessions = new TradingSessionStruct[sessionStructs.length];
        for ( int i = 0; i < sessionStructs.length; i++)
        {
            cmiSessions[i] = convertCMITradingSessionStruct(sessionStructs[i]);
        }
        return cmiSessions;
    }
}

public static TradingSessionStruct convertCMITradingSessionStruct(com.cboe.idl.session.TradingSessionStruct sessionStruct)
{
    if (sessionStruct == null )
    {
        return null;
    }
    else
    {
        TradingSessionStruct cmiSessionStruct =  new TradingSessionStruct();
        cmiSessionStruct.endTime = sessionStruct.endTime;
        cmiSessionStruct.sequenceNumber = sessionStruct.sequenceNumber;
        cmiSessionStruct.startTime = sessionStruct.startTime;
        cmiSessionStruct.state = sessionStruct.sessionState;
        cmiSessionStruct.sessionName = sessionStruct.sessionName;
        return cmiSessionStruct;
     }
}
}
