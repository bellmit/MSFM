package com.cboe.delegates.application;

import com.cboe.interfaces.application.UserSessionManager;

public class UserSessionManagerDelegate extends com.cboe.idl.cmi.POA_UserSessionManager_tie {
    public UserSessionManagerDelegate(UserSessionManager delegate) {
        super(delegate);
    }
}
