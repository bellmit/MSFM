package com.cboe.interfaces.application;

import com.cboe.idl.cmiCallback.*;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.util.channel.ChannelListener;

/**
 * This is the common interface for the OrderQueryCollectorProxyHome
 * @author Jimmy Wang
 */
public interface TickerCollectorProxyHome extends BaseProxyHome
{
    /**
     * Name that will be used for this home.
     */
    public final static String HOME_NAME = "TickerCollectorProxyHome";

  /**
   * Creates an instance of the market data generator home.
   *
   * @return reference to ChannelListener -- > TickerCollectorProxy
   *
   * @author Jimmy Wang
   */
  public ChannelListener create(TickerCollector consumer, BaseSessionManager sessionManager);
}
