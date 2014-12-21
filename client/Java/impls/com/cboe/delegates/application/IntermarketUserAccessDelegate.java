package com.cboe.delegates.application;

import com.cboe.interfaces.application.IntermarketUserAccess;

public class IntermarketUserAccessDelegate extends com.cboe.idl.cmiIntermarket.POA_IntermarketUserAccess_tie {
    public IntermarketUserAccessDelegate(IntermarketUserAccess delegate) {
        super(delegate);
    }
}
