package com.cboe.interfaces.application.inprocess;

import com.cboe.idl.cmiUser.UserLogonStruct;
import com.cboe.exceptions.*;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.interfaces.application.inprocess.InProcessSessionManager;

/**
 * @author Jing Chen
 */
public interface UserAccess
{
    public InProcessSessionManager logon(UserLogonStruct userLogonStruct, UserSessionAdminConsumer userSessionAdmin)
            throws SystemException, CommunicationException, AuthorizationException, AuthenticationException, DataValidationException;
}
