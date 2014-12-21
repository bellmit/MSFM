package com.cboe.delegates.application;

import com.cboe.interfaces.application.UserPreferenceQuery;

public class UserPreferenceQueryDelegate extends com.cboe.idl.cmi.POA_UserPreferenceQuery_tie {
    public UserPreferenceQueryDelegate(UserPreferenceQuery delegate) {
        super(delegate);
    }
}
