package com.cboe.application.inprocess.consumer.proxy;

import com.cboe.interfaces.application.inprocess.AuctionConsumerProxyHome;
import com.cboe.interfaces.application.inprocess.AuctionConsumer;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.interfaces.domain.instrumentedChannel.InstrumentedChannelListener;
import com.cboe.domain.supplier.proxy.BaseConsumerProxyHomeImpl;

/**
 * Author: beniwalv
 * Date: Sep 27, 2004
 * Time: 4:13:04 PM
 */
public class AuctionConsumerProxyHomeImpl extends BaseConsumerProxyHomeImpl implements AuctionConsumerProxyHome{
    /**
     * Constructor
     */
    public AuctionConsumerProxyHomeImpl()
    {
        super();
    }
    /**
     *
     * @param consumer
     * @param sessionManager
     * @return
     */

    public InstrumentedChannelListener create(AuctionConsumer consumer, BaseSessionManager sessionManager) {
        AuctionConsumerProxy bo = new AuctionConsumerProxy(consumer, sessionManager);
        //Every bo object must be added to the container BEFORE anything else.
        addToContainer(bo);
        //Every BOObject create MUST have a name...if the object is to be a managed object.
        bo.create(String.valueOf(bo.hashCode()));
        return bo;
    }
}
