package com.cboe.delegates.application;

import com.cboe.interfaces.application.UserAccessV2;

public class UserAccessV2Delegate extends com.cboe.idl.cmiV2.POA_UserAccessV2_tie {
    public UserAccessV2Delegate(UserAccessV2 delegate) {
        super(delegate);
    }
}
