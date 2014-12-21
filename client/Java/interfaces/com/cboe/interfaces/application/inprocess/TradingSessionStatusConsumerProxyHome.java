package com.cboe.interfaces.application.inprocess;

import com.cboe.util.channel.ChannelListener;
import com.cboe.interfaces.callback.TradingSessionStatusConsumer;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.AuthorizationException;

/**
 * @author Jing Chen
 */
public interface TradingSessionStatusConsumerProxyHome
{
    public final static String HOME_NAME = "TradingSessionStatusConsumerProxyHome";

    public ChannelListener create(TradingSessionStatusConsumer consumer, BaseSessionManager sessionManager)
        throws  DataValidationException, SystemException, CommunicationException, AuthorizationException;
}
