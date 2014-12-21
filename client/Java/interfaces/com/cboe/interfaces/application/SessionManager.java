package com.cboe.interfaces.application;

import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.SystemException;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.interfaces.domain.session.AcknowledgeSessionManager;

import java.util.List;
import java.util.Map;

/**
 * This is the interface with implements the LoginSessionMananger and user
 * subscription and cleaning up to the event channel
 * @author Connie Feng
 */
public interface SessionManager extends AcknowledgeSessionManager, UserSessionManager, UserSessionManagerV2, UserSessionManagerV3, BaseSessionManager
{
    /**
     * Returns LoginSessionType of either PRIMARY or SECONDARY
     */
    public short getLoginType()
        throws SystemException, CommunicationException, AuthorizationException;
    /**
    * Gets the sessionId for a currently logged in user of the CAS
    * @return String sessionId
    */
    public String getSessionId()
        throws SystemException, CommunicationException, AuthorizationException;

    public void setRemoteDelegate(Object remoteDelegate)
        throws SystemException, CommunicationException, AuthorizationException;

    public com.cboe.idl.cmiV2.UserSessionManagerV2 getUserSessionManagerV2()
        throws SystemException, CommunicationException, AuthorizationException;

    public List<String> getTradingFirmGroup()
        throws SystemException, CommunicationException, AuthorizationException;

    public boolean isTradingFirmEnabled()
        throws SystemException, CommunicationException, AuthorizationException;

    public Map<String,Integer> dependentSessions()
             throws SystemException, CommunicationException, AuthorizationException;
}
