package com.cboe.delegates.expressApplication;

import com.cboe.interfaces.expressApplication.MarketQueryV4;

public class MarketQueryDelegate extends com.cboe.idl.cmiV4.POA_MarketQuery_tie
{
    public MarketQueryDelegate(MarketQueryV4 delegate)
    {
        super(delegate);
    }
}

