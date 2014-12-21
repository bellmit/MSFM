package com.cboe.delegates.application;

import com.cboe.interfaces.application.UserSessionManagerV2;

public class UserSessionManagerV2Delegate extends com.cboe.idl.cmiV2.POA_UserSessionManagerV2_tie {
    public UserSessionManagerV2Delegate(UserSessionManagerV2 delegate) {
        super(delegate);
    }
}
