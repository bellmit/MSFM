package com.cboe.delegates.application;

import com.cboe.interfaces.application.UserAccess;

public class UserAccessDelegate extends com.cboe.idl.cmi.POA_UserAccess_tie {
    public UserAccessDelegate(UserAccess delegate) {
        super(delegate);
    }
}
