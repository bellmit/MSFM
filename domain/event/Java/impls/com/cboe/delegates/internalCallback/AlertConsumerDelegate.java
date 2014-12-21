package com.cboe.delegates.internalCallback;

import com.cboe.interfaces.events.AlertConsumer;

public class AlertConsumerDelegate
    extends com.cboe.idl.internalConsumers.POA_AlertConsumer_tie {
    public AlertConsumerDelegate(AlertConsumer delegate) {
        super(delegate);
    }
}
