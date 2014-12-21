/*
 * Created by IntelliJ IDEA.
 * User: huange
 * Date: Oct 11, 2002
 * Time: 1:43:16 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.cboe.interfaces.application;

import com.cboe.util.channel.ChannelListener;
import com.cboe.idl.cmiIntermarketCallback.CMINBBOAgentSessionAdmin;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.exceptions.DataValidationException;

public interface NBBOAgentSessionAdminProxyHome extends BaseProxyHome
{
    /**
     * Name that will be used for this home.
     */
    public final static String HOME_NAME = "NBBOAgentSessionAdminProxyHome";

  /**
   * Creates an instance of the market data generator home.
   *
   * @return reference to ChannelListener -- > OrderStatusConsumerProxy
   *
   * @author Emily Huang
   */
  public ChannelListener create(CMINBBOAgentSessionAdmin consumer, BaseSessionManager sessionManager)
            throws DataValidationException;
}
