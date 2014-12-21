package com.cboe.interfaces.application.inprocess;

import com.cboe.interfaces.callback.StrategyStatusConsumer;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.util.channel.ChannelListener;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;

/**
 * @author Jing Chen
 */
public interface StrategyStatusConsumerProxyHome
{
    public final static String HOME_NAME = "StrategyStatusConsumerProxyHome";

    public ChannelListener create(StrategyStatusConsumer consumer, BaseSessionManager sessionManager)
        throws DataValidationException, SystemException, CommunicationException, AuthorizationException;

}
