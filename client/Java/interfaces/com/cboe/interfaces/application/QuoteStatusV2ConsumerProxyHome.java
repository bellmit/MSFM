package com.cboe.interfaces.application;

import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.interfaces.application.BaseProxyHome;
import com.cboe.util.channel.ChannelListener;
import com.cboe.exceptions.*;


/**
 * This is the common interface for the QuoteStatusConsumerProxyHome
 * @author Jimmy Wang
 */
public interface QuoteStatusV2ConsumerProxyHome extends BaseProxyHome
{
    /**
     * Name that will be used for this home.
     */
    public final static String HOME_NAME = "QuoteStatusV2ConsumerProxyHome";

  /**
   * Creates an instance of the market data generator home.
   *
   * @return reference to ChannelListener -- > QuoteStatusConsumerProxy
   *
   * @author Jimmy Wang
   */
  public ChannelListener create(com.cboe.idl.cmiCallbackV2.CMIQuoteStatusConsumer consumer, BaseSessionManager sessionManager, boolean gmdQuoteStatus)
            throws DataValidationException;
}
