package com.cboe.interfaces.application;

import com.cboe.exceptions.DataValidationException;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.util.channel.ChannelListener;

/**
 * This is the common interface for the RecapConsumerProxyHome
 * @author Jimmy Wang
 */
public interface RecapV2ConsumerProxyHome extends BaseProxyHome
{
    /**
     * Name that will be used for this home.
     */
    public final static String HOME_NAME = "RecapV2ConsumerProxyHome";

  /**
   * Creates an instance of the market data generator home.
   *
   * @return reference to ChannelListener -- > RecapConsumerProxy
   *
   * @author Jimmy Wang
   */
  public ChannelListener create(com.cboe.idl.cmiCallbackV2.CMIRecapConsumer consumer, BaseSessionManager sessionManager, short queuePolicy)
          throws DataValidationException;
}
