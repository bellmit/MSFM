package com.cboe.delegates.application;

import com.cboe.interfaces.application.IntermarketManualHandling;

public class IntermarketManualHandlingDelegate extends com.cboe.idl.cmiIntermarket.POA_IntermarketManualHandling_tie {
    public IntermarketManualHandlingDelegate(IntermarketManualHandling delegate) {
        super(delegate);
    }
}
