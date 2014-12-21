package com.cboe.delegates.application;

import com.cboe.interfaces.application.UserSessionManagerV7;

public class UserSessionManagerV7Delegate extends com.cboe.idl.cmiV7.POA_UserSessionManagerV7_tie
{
    public UserSessionManagerV7Delegate(UserSessionManagerV7 delegate)
    {
        super(delegate);
    }
}
