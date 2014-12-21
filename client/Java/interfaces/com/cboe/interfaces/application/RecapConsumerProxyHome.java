package com.cboe.interfaces.application;

import com.cboe.idl.cmiCallback.CMIRecapConsumer;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.util.channel.ChannelListener;

/**
 * This is the common interface for the RecapConsumerProxyHome
 * @author Jimmy Wang
 */
public interface RecapConsumerProxyHome extends BaseProxyHome
{
    /**
     * Name that will be used for this home.
     */
    public final static String HOME_NAME = "RecapConsumerProxyHome";

  /**
   * Creates an instance of the market data generator home.
   *
   * @return reference to ChannelListener -- > RecapConsumerProxy
   *
   * @author Jimmy Wang
   */
  public ChannelListener create(CMIRecapConsumer consumer, BaseSessionManager sessionManager);
}
