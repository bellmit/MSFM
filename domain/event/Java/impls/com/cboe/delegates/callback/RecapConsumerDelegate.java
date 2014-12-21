package com.cboe.delegates.callback;

import com.cboe.interfaces.callback.*;

public class RecapConsumerDelegate extends com.cboe.idl.cmiCallback.POA_CMIRecapConsumer_tie {

    public RecapConsumerDelegate(RecapConsumer delegate) {
        super(delegate);
    }
}
