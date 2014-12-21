package com.cboe.delegates.application;

import com.cboe.interfaces.application.QuoteV3;

public class QuoteV3Delegate extends com.cboe.idl.cmiV3.POA_Quote_tie {
    public QuoteV3Delegate(QuoteV3 delegate) {
        super(delegate);
    }
}
