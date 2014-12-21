package com.cboe.delegates.application;

import com.cboe.interfaces.application.QuoteV7;

public class QuoteV7Delegate extends com.cboe.idl.cmiV7.POA_Quote_tie {
    public QuoteV7Delegate(QuoteV7 delegate) {
        super(delegate);
    }
}

