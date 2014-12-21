package com.cboe.presentation.api;

import java.util.*;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiSession.*;
import com.cboe.util.*;
import com.cboe.util.channel.*;
import com.cboe.util.event.*;

public class TradingSessionCache implements EventChannelListener
{
    protected Map tradingSessions;

    public TradingSessionCache() {
        tradingSessions = new HashMap();
    }

    public void channelUpdate(ChannelEvent event)
    {
        ChannelKey key = (ChannelKey)event.getChannel();
        if(key.channelType == ChannelType.CB_TRADING_SESSION_STATE)
        {
            updateTradingSessionState((TradingSessionStateStruct)event.getEventData());
        }
    }

    public void updateTradingSessionState(TradingSessionStateStruct state) {
        TradingSessionStruct session = (TradingSessionStruct)tradingSessions.get(state.sessionName);
        if (session != null) {
            session.state = state.sessionState;
        }
    }

    public void addTradingSessions(TradingSessionStruct[] sessions) {
        for (int i = 0; i < sessions.length; i++) {
            tradingSessions.put(sessions[i].sessionName,sessions[i]);
        }
    }

    public TradingSessionStruct[] getCurrentTradingSessions() {
        TradingSessionStruct[] sessions = new TradingSessionStruct[tradingSessions.size()];
        tradingSessions.values().toArray(sessions);

        return sessions;
    }
}
