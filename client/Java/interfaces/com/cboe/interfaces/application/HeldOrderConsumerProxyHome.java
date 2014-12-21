/*
 * Created by IntelliJ IDEA.
 * User: huange
 * Date: Oct 4, 2002
 * Time: 5:07:04 PM
 * To change template for new interface use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.cboe.interfaces.application;

import com.cboe.util.channel.ChannelListener;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.exceptions.DataValidationException;
import com.cboe.idl.cmiIntermarketCallback.CMIIntermarketOrderStatusConsumer;

public interface HeldOrderConsumerProxyHome extends BaseProxyHome
{
    /**
     * Name that will be used for this home.
     */
    public final static String HOME_NAME = "HeldOrderConsumerProxyHome";

  /**
   * Creates an instance of the market data generator home.
   *
   * @return reference to ChannelListener -- > OrderStatusConsumerProxy
   *
   */
  public ChannelListener create(CMIIntermarketOrderStatusConsumer consumer, BaseSessionManager sessionManager)
            throws DataValidationException;
}
