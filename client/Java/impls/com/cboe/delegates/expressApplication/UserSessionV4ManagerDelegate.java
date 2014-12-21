package com.cboe.delegates.expressApplication;

import com.cboe.interfaces.expressApplication.SessionManagerV4;

public class UserSessionV4ManagerDelegate extends com.cboe.idl.cmiV4.POA_UserSessionManagerV4_tie
{
    public UserSessionV4ManagerDelegate(SessionManagerV4 delegate)
    {
        super(delegate);
    }
}
