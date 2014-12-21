/*
 * Created by IntelliJ IDEA.
 * User: torresl
 * Date: Oct 2, 2002
 * Time: 9:27:52 AM
 */
package com.cboe.interfaces.intermarketPresentation.api;

import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.TransactionFailedException;
import com.cboe.util.event.EventChannelListener;

public interface NBBOAgentAPI
{
    IntermarketHeldOrderAPI register(
        int classKey,
        String session,
        boolean forceOverride,
        EventChannelListener imOrderStatusListener,
        EventChannelListener nbboAgentSessionListener )
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, TransactionFailedException;


    void unregister(
        int classKey,
        String session,
        EventChannelListener imOrderStatusListener,
        EventChannelListener nbboAgentSessionListener )
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, TransactionFailedException;

    boolean isRegistered(int classKey, String session);
}
