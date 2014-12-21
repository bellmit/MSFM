package com.cboe.delegates.internalCallback;

import com.cboe.interfaces.callback.*;

public class GroupElementConsumerDelegate
    extends com.cboe.idl.consumers.POA_GroupElementConsumer_tie {
    public GroupElementConsumerDelegate(GroupElementCallbackConsumer delegate) {
        super(delegate);
    }
}