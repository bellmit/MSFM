package com.cboe.interfaces.application.inprocess;

import com.cboe.interfaces.application.BaseProxyHome;
import com.cboe.interfaces.application.inprocess.RFQConsumer;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.util.channel.ChannelListener;

/**
 * @author Jing Chen
 */
public interface RFQConsumerProxyHome extends BaseProxyHome
{
    /**
     * Name that will be used for this home.
     */
    public final static String HOME_NAME = "RFQConsumerProxyHome";

    public ChannelListener create(RFQConsumer consumer, BaseSessionManager sessionManager);
}
