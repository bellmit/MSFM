package com.cboe.interfaces.floorApplication;

import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.SystemException;
import com.cboe.idl.floorApplication.FloorSessionManagerOperations;

/**
 * User: mahoney
 * Date: Jul 17, 2007
 */
public interface FloorSessionManager extends FloorSessionManagerOperations
{
    public void setRemoteDelegate(com.cboe.idl.floorApplication.FloorSessionManager remoteDelegate)
			throws SystemException, CommunicationException, AuthorizationException;
}
