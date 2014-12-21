package com.cboe.delegates.intermarketCallback;

import com.cboe.interfaces.intermarketCallback.*;

public class IntermarketOrderStatusConsumerDelegate extends com.cboe.idl.cmiIntermarketCallback.POA_CMIIntermarketOrderStatusConsumer_tie {

    public IntermarketOrderStatusConsumerDelegate(IntermarketOrderStatusConsumer delegate) {
        super(delegate);
    }
}
