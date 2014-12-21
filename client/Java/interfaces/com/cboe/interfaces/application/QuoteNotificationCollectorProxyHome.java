package com.cboe.interfaces.application;

import com.cboe.idl.cmiCallback.*;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.util.channel.ChannelListener;

/**
 * This is the common interface for the QuoteNotificationCollectorProxyHome
 * @author William Wei
 */
public interface QuoteNotificationCollectorProxyHome extends BaseProxyHome
{
    /**
     * Name that will be used for this home.
     */
    public final static String HOME_NAME = "QuoteNotificationCollectorProxyHome";

  /**
   * Creates an instance of the market data generator home.
   *
   * @return reference to ChannelListener -- > QuoteNotificationCollectorProxy
   */
  public ChannelListener create(QuoteNotificationCollector consumer, BaseSessionManager sessionManager);
}
