package com.cboe.interfaces.application;

import com.cboe.exceptions.DataValidationException;
import com.cboe.idl.cmiCallbackV2.CMIExpectedOpeningPriceConsumer;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.util.channel.ChannelListener;

/**
 * This is the common interface for the ExpectedOpeningPriceConsumerProxyHome
 * @author Jimmy Wang
 */
public interface ExpectedOpeningPriceV2ConsumerProxyHome extends BaseProxyHome
{
    /**
     * Name that will be used for this home.
     */
    public final static String HOME_NAME = "ExpectedOpeningPriceV2ConsumerProxyHome";

  /**
   * Creates an instance of the market data generator home.
   *
   * @return reference to ChannelListener -- > ExpectedOpeningPriceConsumerProxy
   *
   * @author Jimmy Wang
   */
  public ChannelListener create(CMIExpectedOpeningPriceConsumer consumer, BaseSessionManager sessionManager, short queuePolicy)
          throws DataValidationException;
}
