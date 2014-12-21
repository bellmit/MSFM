package com.cboe.delegates.application;

import com.cboe.interfaces.application.IntermarketQuery;

public class IntermarketQueryDelegate extends com.cboe.idl.cmiIntermarket.POA_IntermarketQuery_tie {
    public IntermarketQueryDelegate(IntermarketQuery delegate) {
        super(delegate);
    }
}
