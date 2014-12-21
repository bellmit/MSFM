package com.cboe.interfaces.application;

import com.cboe.idl.cmiCallback.CMIOrderBookConsumer;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.util.channel.ChannelListener;

/**
 * This is the common interface for the BookDepthConsumerProxyHome
 * @author William Wei
 */
public interface BookDepthConsumerProxyHome extends BaseProxyHome
{
    /**
     * Name that will be used for this home.
     */
    public final static String HOME_NAME = "BookDepthConsumerProxyHome";

  /**
   * Creates an instance of the market data generator home.
   *
   * @return reference to ChannelListener -- > BookDepthConsumerProxy
   */
  public ChannelListener create(CMIOrderBookConsumer consumer, BaseSessionManager sessionManager);
}
