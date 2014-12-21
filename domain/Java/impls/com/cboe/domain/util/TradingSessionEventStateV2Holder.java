package com.cboe.domain.util;

import com.cboe.idl.session.TradingSessionEventHistoryStructV2;

public final class TradingSessionEventStateV2Holder
{
    
    private TradingSessionEventHistoryStructV2 eventState;
    private boolean allServersIncluded;
    
    public TradingSessionEventStateV2Holder(TradingSessionEventHistoryStructV2 eventState, boolean allServersIncluded)
    {
        this.eventState = eventState;
        this. allServersIncluded = allServersIncluded;
    }
    
    public TradingSessionEventHistoryStructV2 getEventState() {return eventState; }
    public boolean allServersIncluded() { return allServersIncluded; }
}
