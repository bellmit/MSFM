package com.cboe.delegates.callback;

import com.cboe.interfaces.callback.*;

public class ProductStatusConsumerDelegate extends com.cboe.idl.cmiCallback.POA_CMIProductStatusConsumer_tie {

    public ProductStatusConsumerDelegate(ProductStatusConsumer delegate) {
        super(delegate);
    }
}
