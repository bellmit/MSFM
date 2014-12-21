package com.cboe.delegates.callback;

import com.cboe.interfaces.callback.*;

public class ClassStatusConsumerDelegate extends com.cboe.idl.cmiCallback.POA_CMIClassStatusConsumer_tie {

    public ClassStatusConsumerDelegate(ClassStatusConsumer delegate) {
        super(delegate);
    }
}
