package com.cboe.interfaces.application.inprocess;

import com.cboe.util.channel.ChannelListener;
import com.cboe.interfaces.callback.ClassStatusConsumer;
import com.cboe.interfaces.callback.ProductStatusConsumer;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.AuthorizationException;

/**
 * @author Jing Chen
 */
public interface ProductStatusConsumerProxyHome
{
    public final static String HOME_NAME = "ProductStatusConsumerProxyHome";

    public ChannelListener create(ProductStatusConsumer consumer, BaseSessionManager sessionManager)
        throws DataValidationException, SystemException, CommunicationException, AuthorizationException;

}
