package com.cboe.delegates.callback;

import com.cboe.interfaces.callback.*;

public class QuoteStatusConsumerDelegate extends com.cboe.idl.cmiCallback.POA_CMIQuoteStatusConsumer_tie {

    public QuoteStatusConsumerDelegate(QuoteStatusConsumer delegate) {
        super(delegate);
    }
}
