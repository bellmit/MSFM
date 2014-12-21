package com.cboe.interfaces.application;

import com.cboe.idl.cmiCallback.*;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.util.channel.ChannelListener;

/**
 * This is the common interface for the QuoteQueryCollectorProxyHome
 * @author Jimmy Wang
 */
public interface QuoteStatusCollectorProxyHome extends BaseProxyHome
{
    /**
     * Name that will be used for this home.
     */
    public final static String HOME_NAME = "QuoteStatusCollectorProxyHome";

  /**
   * Creates an instance of the market data generator home.
   *
   * @return reference to ChannelListener -- > QuoteStatusCollectorProxy
   *
   * @author Jimmy Wang
   */
  public ChannelListener create(QuoteStatusCollector consumer, BaseSessionManager sessionManager);
}
