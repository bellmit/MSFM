package com.cboe.interfaces.application;

import com.cboe.idl.cmiCallback.*;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.util.channel.ChannelListener;

/**
 * This is the common interface for the ProductStatusConsumerProxyHome
 * @author Jimmy Wang
 */
public interface ProductStatusConsumerProxyHome extends BaseProxyHome
{
    /**
     * Name that will be used for this home.
     */
    public final static String HOME_NAME = "ProductStatusConsumerProxyHome";

  /**
   * Creates an instance of the market data generator home.
   *
   * @return reference to ChannelListener -- > ProductStatusConsumerProxy
   *
   * @author Jimmy Wang
   */
  public ChannelListener create(CMIProductStatusConsumer consumer, BaseSessionManager sessionManager);
}
