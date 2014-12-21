package com.cboe.interfaces.cfix;

import com.cboe.idl.cmiUser.UserLogonStruct;
import com.cboe.idl.cmiCallback.CMIUserSessionAdmin;
import com.cboe.exceptions.*;

/**
 * @author Jing Chen
 */
public interface CfixUserAccess {
    CfixSessionManager logon(UserLogonStruct userLogonStruct, short i, CfixUserSessionAdminConsumer cmiUserSessionAdmin, boolean b) throws SystemException, CommunicationException, AuthorizationException, AuthenticationException, DataValidationException;
}
