package com.cboe.delegates.application;

import com.cboe.interfaces.application.MarketQuery;

public class MarketQueryDelegate extends com.cboe.idl.cmi.POA_MarketQuery_tie {
    public MarketQueryDelegate(MarketQuery delegate) {
        super(delegate);
    }
}
