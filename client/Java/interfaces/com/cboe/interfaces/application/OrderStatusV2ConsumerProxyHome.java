package com.cboe.interfaces.application;

import com.cboe.idl.cmiCallbackV2.CMIOrderStatusConsumer;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.util.channel.ChannelListener;
import com.cboe.exceptions.DataValidationException;

/**
 * This is the common interface for the V2 OrderStatusConsumerProxyHome
 * @author Tom Trop
 */
public interface OrderStatusV2ConsumerProxyHome extends BaseProxyHome
{
    /**
     * Name that will be used for this home.
     */
    public final static String HOME_NAME = "OrderStatusV2ConsumerProxyHome";

    /**
     * Creates an instance of the order status consumer proxy.
     *
     * @return reference to ChannelListener --> OrderStatusConsumerProxy
     */
    public ChannelListener create(CMIOrderStatusConsumer consumer,
                                  BaseSessionManager sessionManager,
                                  boolean gmdOrderStatus)
        throws DataValidationException;
}
