package com.cboe.delegates.application;

import com.cboe.interfaces.application.Quote;

public class QuoteDelegate extends com.cboe.idl.cmi.POA_Quote_tie {
    public QuoteDelegate(Quote delegate) {
        super(delegate);
    }
}
