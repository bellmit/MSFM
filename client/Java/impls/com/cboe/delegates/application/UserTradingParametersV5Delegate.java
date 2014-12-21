package com.cboe.delegates.application;

import com.cboe.interfaces.application.UserTradingParametersV5;

public class UserTradingParametersV5Delegate extends com.cboe.idl.cmiV5.POA_UserTradingParameters_tie {
    public UserTradingParametersV5Delegate(UserTradingParametersV5 delegate) {
        super(delegate);
    }
}
