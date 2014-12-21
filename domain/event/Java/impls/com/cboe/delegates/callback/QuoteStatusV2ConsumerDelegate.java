package com.cboe.delegates.callback;

import com.cboe.interfaces.callback.QuoteStatusV2Consumer;

public class QuoteStatusV2ConsumerDelegate extends com.cboe.idl.cmiCallbackV2.POA_CMIQuoteStatusConsumer_tie {

    public QuoteStatusV2ConsumerDelegate(QuoteStatusV2Consumer delegate) {
        super(delegate);
    }
}
