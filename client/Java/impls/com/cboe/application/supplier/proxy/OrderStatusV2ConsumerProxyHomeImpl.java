package com.cboe.application.supplier.proxy;

import com.cboe.application.supplier.proxy.GMDConsumerProxyHomeImpl;
import com.cboe.exceptions.DataValidationException;
import com.cboe.idl.cmiCallbackV2.CMIOrderStatusConsumer;
import com.cboe.interfaces.application.OrderStatusV2ConsumerProxyHome;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.util.channel.ChannelListener;

/**
 * OrderStatusV2ConsumerProxyHomeImpl
 * @author Tom Trop
 */
public class OrderStatusV2ConsumerProxyHomeImpl
    extends GMDConsumerProxyHomeImpl
    implements OrderStatusV2ConsumerProxyHome
{
    /**
      * Follows the proscribed method for creating and generating a impl class.
      * Sets the Session Manager parent class and initializes the Order Query.
      */
    public ChannelListener create(CMIOrderStatusConsumer consumer,
                                  BaseSessionManager sessionManager,
                                  boolean gmdProxy)
        throws DataValidationException
    {

        return null;
    }
}
