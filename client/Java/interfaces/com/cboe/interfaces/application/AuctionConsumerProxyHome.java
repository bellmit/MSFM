package com.cboe.interfaces.application;

import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.util.channel.ChannelListener;

/**
 * Common interface for the AuctionConsumerProxyHome
 */
public interface AuctionConsumerProxyHome extends BaseProxyHome
{
    /** Name that will be used for this home. */
    public final static String HOME_NAME = "AuctionConsumerProxyHome";

    /**
     * Create an instance of the market data generator home.
     * @param consumer Object to send messages to the client.
     * @param sessionManager Object to manage subscriptions.
     * @return reference to ChannelListener -- > AuctionConsumerProxy
     */
    public ChannelListener create(com.cboe.idl.cmiCallbackV3.CMIAuctionConsumer consumer, BaseSessionManager sessionManager);
}
