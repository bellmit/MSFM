package com.cboe.delegates.application;

import com.cboe.interfaces.application.UserSessionManager;

public class UserSessionManagerV3Delegate extends com.cboe.idl.cmiV3.POA_UserSessionManagerV3_tie {
    public UserSessionManagerV3Delegate(UserSessionManager delegate) {
        super(delegate);
    }
}
