package com.cboe.application.supplier.proxy;

import com.cboe.domain.supplier.proxy.BaseConsumerProxyHomeImpl;
import com.cboe.idl.cmiCallbackV3.CMIAuctionConsumer;
import com.cboe.interfaces.application.AuctionConsumerProxyHome;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.util.channel.ChannelListener;

/** Create/manage objects to send Auction messages to clients.
 */
public class AuctionConsumerProxyHomeImpl extends BaseConsumerProxyHomeImpl implements AuctionConsumerProxyHome
{
    public AuctionConsumerProxyHomeImpl()
    {
        super();
    }

    /**
     * Follows the prescribed method for creating and generating a impl class.
     * Sets the Session Manager parent class and initializes the Order Query.
     * @param consumer Object to send events to client.
     * @param sessionManager Object that manages subscriptions for this proxy.
     * @return Object to send messages to client callback.
     */
    public ChannelListener create(CMIAuctionConsumer consumer, BaseSessionManager sessionManager)
    {
        AuctionConsumerProxy bo = new AuctionConsumerProxy(consumer, sessionManager);

        // Every bo object must be added to the container BEFORE anything else.
        addToContainer(bo);
        // Every BOObject create MUST have a name...if the object is to be a managed object.
        bo.create(String.valueOf(bo.hashCode()));
        if (getInstrumentationEnablementProperty())
        {
            bo.startMethodInstrumentation(getInstrumentationProperty());
        }
        bo.initConnectionProperty(getConnectionProperty(sessionManager));
        bo.initNoActionProxyQueueDepthProperty(getNoActionQueueDepth(sessionManager));
        return bo;
    }
}
