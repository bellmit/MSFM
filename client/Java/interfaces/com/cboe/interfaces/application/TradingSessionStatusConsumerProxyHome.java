package com.cboe.interfaces.application;

import com.cboe.idl.cmiCallback.*;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.util.channel.ChannelListener;

/**
 * This is the common interface for the TradingSessionStatusConsumerProxyHome
 * @author Jimmy Wang
 */
public interface TradingSessionStatusConsumerProxyHome extends BaseProxyHome
{
    /**
     * Name that will be used for this home.
     */
    public final static String HOME_NAME = "TradingSessionStatusConsumerProxyHome";

  /**
   * Creates an instance of the market data generator home.
   *
   * @return reference to ChannelListener -- > TradingSessionStatusConsumerProxy
   *
   * @author Jimmy Wang
   */
  public ChannelListener create(CMITradingSessionStatusConsumer consumer, BaseSessionManager sessionManager);
}
