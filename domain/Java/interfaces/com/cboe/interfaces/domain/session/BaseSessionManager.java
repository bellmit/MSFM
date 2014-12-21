package com.cboe.interfaces.domain.session;

import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.SystemException;
import com.cboe.util.channel.ChannelListener;
import com.cboe.idl.user.SessionProfileUserStructV2;

/**
 * This is the base session manager interface
 * @author Connie Feng
 */
public interface BaseSessionManager
{
    /**
    * Handles the channel listener lost of connection
    */
    public void lostConnection(ChannelListener channelListener)
        throws SystemException, CommunicationException, AuthorizationException;
    /**
    * Handles the channel listener lost of connection
    */
    public void unregisterNotification(CallbackDeregistrationInfo deregistrationInfo)
        throws SystemException, CommunicationException, AuthorizationException;

    public String getUserId()
            throws SystemException, CommunicationException, AuthorizationException;

    public SessionProfileUserStructV2 getValidSessionProfileUserV2()
            throws SystemException, CommunicationException, AuthorizationException;

    public String getInstrumentorName()
        throws SystemException, CommunicationException, AuthorizationException;

    public int getSessionKey()
        throws SystemException, CommunicationException, AuthorizationException;
}
