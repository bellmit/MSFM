package com.cboe.delegates.application;

import com.cboe.interfaces.application.UserTradingParameters;

public class UserTradingParametersDelegate extends com.cboe.idl.cmi.POA_UserTradingParameters_tie {
    public UserTradingParametersDelegate(UserTradingParameters delegate) {
        super(delegate);
    }
}
