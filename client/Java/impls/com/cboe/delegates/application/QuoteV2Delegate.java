package com.cboe.delegates.application;

import com.cboe.interfaces.application.QuoteV2;

public class QuoteV2Delegate extends com.cboe.idl.cmiV2.POA_Quote_tie {
    public QuoteV2Delegate(QuoteV2 delegate) {
        super(delegate);
    }
}
