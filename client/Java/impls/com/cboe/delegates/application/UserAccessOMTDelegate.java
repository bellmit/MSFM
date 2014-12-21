package com.cboe.delegates.application;

import com.cboe.interfaces.application.UserAccessOMT;

public class UserAccessOMTDelegate extends com.cboe.idl.omt.POA_UserAccessOMT_tie
{
    public UserAccessOMTDelegate(UserAccessOMT delegate)
    {
        super(delegate);
    }
}
