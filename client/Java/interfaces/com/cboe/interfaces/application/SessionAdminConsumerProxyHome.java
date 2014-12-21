package com.cboe.interfaces.application;

import com.cboe.idl.cmiCallback.*;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.util.channel.ChannelListener;
import com.cboe.exceptions.*;

/**
 * This is the common interface for the SessionAdminConsumerProxyHome
 * @author Jimmy Wang
 */
public interface SessionAdminConsumerProxyHome extends BaseProxyHome
{
    /**
     * Name that will be used for this home.
     */
    public final static String HOME_NAME = "SessionAdminConsumerProxyHome";

    /**
     * Creates an instance of the market data generator home.
     *
     * @return reference to ChannelListener -- > SessionAdminConsumerProxy
     */
    public ChannelListener create(CMIUserSessionAdmin consumer,
                                  BaseSessionManager sessionManager,
                                  boolean gmdTextMessaging)
        throws  DataValidationException,
                SystemException,
                CommunicationException,
                AuthorizationException;
}
