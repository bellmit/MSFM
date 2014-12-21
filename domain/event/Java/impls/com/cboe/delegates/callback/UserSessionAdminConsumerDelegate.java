package com.cboe.delegates.callback;

import com.cboe.interfaces.callback.*;

public class UserSessionAdminConsumerDelegate extends com.cboe.idl.cmiCallback.POA_CMIUserSessionAdmin_tie {

    public UserSessionAdminConsumerDelegate(UserSessionAdminConsumer delegate) {
        super(delegate);
    }
}
