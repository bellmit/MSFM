package com.cboe.delegates.callback;

import com.cboe.interfaces.callback.*;

public class RFQConsumerDelegate extends com.cboe.idl.cmiCallback.POA_CMIRFQConsumer_tie {

    public RFQConsumerDelegate(RFQConsumer delegate) {
        super(delegate);
    }
}
