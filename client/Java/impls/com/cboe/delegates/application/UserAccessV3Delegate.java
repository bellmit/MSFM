package com.cboe.delegates.application;

import com.cboe.interfaces.application.UserAccessV3;

public class UserAccessV3Delegate extends com.cboe.idl.cmiV3.POA_UserAccessV3_tie {
    public UserAccessV3Delegate(UserAccessV3 delegate) {
        super(delegate);
    }
}
