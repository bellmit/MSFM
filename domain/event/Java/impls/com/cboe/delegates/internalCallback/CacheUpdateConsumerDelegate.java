package com.cboe.delegates.internalCallback;

import com.cboe.interfaces.callback.*;

public class CacheUpdateConsumerDelegate
    extends com.cboe.idl.consumers.POA_CacheUpdateConsumer_tie {
    public CacheUpdateConsumerDelegate(CacheUpdateCallbackConsumer delegate) {
        super(delegate);
    }
}
