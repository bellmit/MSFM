package com.cboe.interfaces.application;

import com.cboe.idl.cmiCallback.CMICurrentMarketConsumer;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.util.channel.ChannelListener;

/**
 * This is the common interface for the CurrentMarketConsumerProxyHome
 * @author Jimmy Wang
 */
public interface CurrentMarketConsumerProxyHome extends BaseProxyHome
{
    /**
     * Name that will be used for this home.
     */
    public final static String HOME_NAME = "CurrentMarketConsumerProxyHome";

  /**
   * Creates an instance of the market data generator home.
   *
   * @return reference to ChannelListener -- > CurrentMarketConsumerProxy
   *
   * @author Jimmy Wang
   */
  public ChannelListener create(CMICurrentMarketConsumer consumer, BaseSessionManager sessionManager);
}
