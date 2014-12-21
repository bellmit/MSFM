package com.cboe.interfaces.application;

import com.cboe.idl.cmiCallback.*;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.util.channel.ChannelListener;

/**
 * This is the common interface for the BookDepthCollectorProxyHome
 * @author William Wei
 */
public interface BookDepthCollectorProxyHome extends BaseProxyHome
{
    /**
     * Name that will be used for this home.
     */
    public final static String HOME_NAME = "BookDepthCollectorProxyHome";

  /**
   * Creates an instance of the market data generator home.
   *
   * @return reference to ChannelListener -- > BookDepthCollectorProxy
   */
  public ChannelListener create(BookDepthCollector consumer, BaseSessionManager sessionManager);
}
