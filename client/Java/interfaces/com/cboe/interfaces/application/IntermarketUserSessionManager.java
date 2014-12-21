package com.cboe.interfaces.application;

import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.SystemException;

/**
 * This extends the CORBA Interface into a CBOE Common standard
 * @author Emily Huang
 */
public interface IntermarketUserSessionManager extends com.cboe.idl.cmiIntermarket.IntermarketUserSessionManagerOperations {
    public void setRemoteDelegate(com.cboe.idl.cmiIntermarket.IntermarketUserSessionManager remoteDelegate)
    		throws SystemException, CommunicationException, AuthorizationException;	
}
