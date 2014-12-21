package com.cboe.interfaces.application.inprocess;

import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.interfaces.application.BaseProxyHome;
import com.cboe.interfaces.application.inprocess.QuoteStatusConsumer;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.util.channel.ChannelListener;

/**
 * This is the common interface for the QuoteStatusConsumerProxyHome
 * @author Jing Chen
 */
public interface QuoteStatusConsumerProxyHome extends BaseProxyHome
{
    /**
     * Name that will be used for this home.
     */
    public final static String HOME_NAME = "QuoteStatusConsumerProxyHome";

    public ChannelListener create(QuoteStatusConsumer consumer, BaseSessionManager sessionManager)
        throws  DataValidationException, SystemException, CommunicationException, AuthorizationException;
}
