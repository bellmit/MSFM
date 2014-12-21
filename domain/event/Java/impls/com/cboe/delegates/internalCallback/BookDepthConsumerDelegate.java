package com.cboe.delegates.internalCallback;

import com.cboe.interfaces.callback.*;

public class BookDepthConsumerDelegate
    extends com.cboe.idl.consumers.POA_BookDepthConsumer_tie {
    public BookDepthConsumerDelegate(BookDepthCallbackConsumer delegate) {
        super(delegate);
    }
}
