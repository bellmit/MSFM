package com.cboe.delegates.application;

import com.cboe.interfaces.application.QuoteV5;

public class QuoteV5Delegate extends com.cboe.idl.cmiV5.POA_Quote_tie {
    public QuoteV5Delegate(QuoteV5 delegate) {
        super(delegate);
    }
}
