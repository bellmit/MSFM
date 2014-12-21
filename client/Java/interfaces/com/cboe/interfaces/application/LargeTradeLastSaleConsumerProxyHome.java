package com.cboe.interfaces.application;

import com.cboe.exceptions.DataValidationException;
import com.cboe.idl.consumers.TickerConsumer;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.util.channel.ChannelListener;

public interface LargeTradeLastSaleConsumerProxyHome extends BaseProxyHome {
	/**
     * Name that will be used for this home.
     */
    public final static String HOME_NAME = "LargeTradeLastSaleConsumerProxyHome";

  /**
   * Creates an instance of the market data generator home.
   * @return reference to ChannelListener -- > LastSaleConsumerProxy
   */
  public ChannelListener create(TickerConsumer consumer, BaseSessionManager sessionManager, short queuePolicy)
          throws DataValidationException;
}
