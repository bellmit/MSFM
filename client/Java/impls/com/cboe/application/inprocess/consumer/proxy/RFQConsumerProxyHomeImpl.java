package com.cboe.application.inprocess.consumer.proxy;

import com.cboe.domain.supplier.proxy.BaseConsumerProxyHomeImpl;
import com.cboe.interfaces.application.inprocess.RFQConsumer;
import com.cboe.interfaces.application.inprocess.RFQConsumerProxyHome;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.util.channel.ChannelListener;

/**
 * @author Jing Chen
 */
public class RFQConsumerProxyHomeImpl extends BaseConsumerProxyHomeImpl implements RFQConsumerProxyHome
{
    /** constructor. **/
    public RFQConsumerProxyHomeImpl()
    {
        super();
    }

    public ChannelListener create(RFQConsumer consumer, BaseSessionManager sessionManager)
    {
        RFQConsumerProxy bo = new RFQConsumerProxy(consumer, sessionManager);

        //Every bo object must be added to the container BEFORE anything else.
        addToContainer(bo);
        //Every BOObject create MUST have a name...if the object is to be a managed object.
        bo.create(String.valueOf(bo.hashCode()));
        return bo;
    }
}
