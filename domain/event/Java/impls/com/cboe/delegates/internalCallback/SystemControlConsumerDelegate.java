package com.cboe.delegates.internalCallback;

import com.cboe.interfaces.callback.SystemControlCallbackConsumer;

public class SystemControlConsumerDelegate
    extends com.cboe.idl.consumers.POA_SystemControlConsumer_tie {
    public SystemControlConsumerDelegate(SystemControlCallbackConsumer delegate) {
        super(delegate);
    }
}