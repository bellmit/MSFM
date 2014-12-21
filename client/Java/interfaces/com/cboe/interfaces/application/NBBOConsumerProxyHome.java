package com.cboe.interfaces.application;

import com.cboe.idl.cmiCallback.CMINBBOConsumer;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.util.channel.ChannelListener;

/**
 * This is the common interface for the CurrentMarketConsumerProxyHome
 * @author Jimmy Wang
 */
public interface NBBOConsumerProxyHome extends BaseProxyHome
{
    /**
     * Name that will be used for this home.
     */
    public final static String HOME_NAME = "NBBOConsumerProxyHome";

  /**
   * Creates an instance of the market data generator home.
   *
   * @return reference to ChannelListener -- > NBBOConsumerProxy
   *
   * @author Jimmy Wang
   */
  public ChannelListener create(CMINBBOConsumer consumer, BaseSessionManager sessionManager);
}
