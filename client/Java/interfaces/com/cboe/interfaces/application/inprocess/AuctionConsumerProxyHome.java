package com.cboe.interfaces.application.inprocess;


import com.cboe.interfaces.application.BaseProxyHome;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.interfaces.domain.instrumentedChannel.InstrumentedChannelListener;

/**
 * Author: beniwalv
 * Date: Sep 27, 2004
 * Time: 3:23:28 PM
 */
public interface AuctionConsumerProxyHome extends BaseProxyHome {
    /**
     * Name that will be used for this home.
     */
    public final static String HOME_NAME = "AuctionConsumerProxyHome";
    public InstrumentedChannelListener create(AuctionConsumer consumer, BaseSessionManager sessionManager);
}
