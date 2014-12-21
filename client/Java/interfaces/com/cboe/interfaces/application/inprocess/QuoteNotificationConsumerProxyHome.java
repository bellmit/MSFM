package com.cboe.interfaces.application.inprocess;

import com.cboe.interfaces.application.BaseProxyHome;
import com.cboe.interfaces.application.inprocess.LockedQuoteStatusConsumer;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.util.channel.ChannelListener;

/**
 * @author Jing Chen
 */
public interface QuoteNotificationConsumerProxyHome extends BaseProxyHome
{
    /**
     * Name that will be used for this home.
     */
    public final static String HOME_NAME = "QuoteNotificationConsumerProxyHome";

  /**
   * Creates an instance of the market data generator home.
   *
   * @return reference to ChannelListener -- > QuoteNotificationConsumerProxy
   */
    public ChannelListener create(LockedQuoteStatusConsumer consumer, BaseSessionManager sessionManager);
}
