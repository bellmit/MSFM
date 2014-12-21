package com.cboe.delegates.internalCallback;

import com.cboe.interfaces.events.ProductStatusConsumer;

public class AdminProductStatusConsumerDelegate
    extends com.cboe.idl.consumers.POA_ProductStatusConsumer_tie {
    public AdminProductStatusConsumerDelegate(ProductStatusConsumer delegate) {
        super(delegate);
    }
}
