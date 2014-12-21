package com.cboe.interfaces.application;

import com.cboe.exceptions.DataValidationException;
import com.cboe.idl.cmiCallbackV2.CMITickerConsumer;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.util.channel.ChannelListener;

/**
 * This is the common interface for the TickerConsumerProxyHome
 * @author Jimmy Wang
 */
public interface TickerV2ConsumerProxyHome extends BaseProxyHome
{
    /**
     * Name that will be used for this home.
     */
    public final static String HOME_NAME = "TickerV2ConsumerProxyHome";

  /**
   * Creates an instance of the market data generator home.
   *
   * @return reference to ChannelListener -- > TickerConsumerProxy
   *
   * @author Jimmy Wang
   */
  public ChannelListener create(CMITickerConsumer consumer, BaseSessionManager sessionManager, short queuePolicy)
          throws DataValidationException;
}
