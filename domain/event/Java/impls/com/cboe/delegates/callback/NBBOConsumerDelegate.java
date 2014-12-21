package com.cboe.delegates.callback;

import com.cboe.interfaces.callback.*;

public class NBBOConsumerDelegate extends com.cboe.idl.cmiCallback.POA_CMINBBOConsumer_tie {

    public NBBOConsumerDelegate(NBBOConsumer delegate) {
        super(delegate);
    }
}