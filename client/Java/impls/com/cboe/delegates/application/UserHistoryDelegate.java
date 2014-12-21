package com.cboe.delegates.application;

import com.cboe.interfaces.application.UserHistory;

public class UserHistoryDelegate extends com.cboe.idl.cmi.POA_UserHistory_tie {
    public UserHistoryDelegate(UserHistory delegate) {
        super(delegate);
    }
}
