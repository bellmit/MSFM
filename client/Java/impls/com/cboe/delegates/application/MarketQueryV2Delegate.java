package com.cboe.delegates.application;

import com.cboe.interfaces.application.MarketQueryV2;

public class MarketQueryV2Delegate extends com.cboe.idl.cmiV2.POA_MarketQuery_tie {
    public MarketQueryV2Delegate(MarketQueryV2 delegate) {
        super(delegate);
    }
}
