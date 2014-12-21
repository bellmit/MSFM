package com.cboe.interfaces.application;

import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.SystemException;

/**
 * Author: mahoney
 * Date: May 1, 2007
 */
public interface OMTSessionManager extends com.cboe.idl.omt.OMTSessionManagerOperations
{
    public void setRemoteDelegate(com.cboe.idl.omt.OMTSessionManager remoteDelegate)
		throws SystemException, CommunicationException, AuthorizationException;
}
