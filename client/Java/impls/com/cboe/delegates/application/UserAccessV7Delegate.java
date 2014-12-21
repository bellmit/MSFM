package com.cboe.delegates.application;

import com.cboe.interfaces.application.UserAccessV7;

public class UserAccessV7Delegate extends com.cboe.idl.cmiV7.POA_UserAccessV7_tie
{
    public UserAccessV7Delegate(UserAccessV7 delegate)
    {
        super(delegate);
    }
}
