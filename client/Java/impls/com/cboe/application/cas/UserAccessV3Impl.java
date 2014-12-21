package com.cboe.application.cas;

import com.cboe.exceptions.*;
import com.cboe.idl.cmiCallback.CMIUserSessionAdmin;
import com.cboe.idl.cmiUser.UserLogonStruct;
import com.cboe.interfaces.application.UserAccessV3;

public class UserAccessV3Impl extends UserAccessBaseImpl implements UserAccessV3
{
    public UserAccessV3Impl(char sessionMode, int heartbeatTimeout, String cmiVersion)
    {
        super(sessionMode, heartbeatTimeout, cmiVersion) ;
    }

    public com.cboe.idl.cmiV3.UserSessionManagerV3 logon( UserLogonStruct logonStruct, short sessionType, CMIUserSessionAdmin cmiListener, boolean gmdTextMessaging )
        throws  SystemException, CommunicationException,
                AuthorizationException, AuthenticationException,
                DataValidationException
    {
        return super.logonV3(logonStruct, sessionType, cmiListener, gmdTextMessaging);
    }

}
