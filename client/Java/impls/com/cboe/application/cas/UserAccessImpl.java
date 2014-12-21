package com.cboe.application.cas;


import com.cboe.exceptions.*;
import com.cboe.idl.cmiCallback.CMIUserSessionAdmin;
import com.cboe.idl.cmiUser.UserLogonStruct;
import com.cboe.interfaces.application.UserAccess;

public class UserAccessImpl extends UserAccessBaseImpl implements UserAccess
{
    public UserAccessImpl(char sessionMode, int heartbeatTimeout, String cmiVersion) {
        super(sessionMode, heartbeatTimeout, cmiVersion);
    }

    public com.cboe.idl.cmi.UserSessionManager logon( UserLogonStruct logonStruct, short sessionType, CMIUserSessionAdmin cmiListener, boolean gmdTextMessaging )
        throws  SystemException, CommunicationException,
                AuthorizationException, AuthenticationException,
                DataValidationException
    {
        return super.logonV1(logonStruct, sessionType, cmiListener, gmdTextMessaging);
    }

}
