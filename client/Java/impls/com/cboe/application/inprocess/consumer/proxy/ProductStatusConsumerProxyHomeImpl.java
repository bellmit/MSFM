package com.cboe.application.inprocess.consumer.proxy;

import com.cboe.infrastructureServices.foundationFramework.BOHome;
import com.cboe.interfaces.application.inprocess.ProductStatusConsumerProxyHome;
import com.cboe.interfaces.callback.ProductStatusConsumer;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.util.channel.ChannelListener;

/**
 * @author Jing Chen
 */
public class ProductStatusConsumerProxyHomeImpl extends BOHome implements ProductStatusConsumerProxyHome
{
    /** constructor. **/
    public ProductStatusConsumerProxyHomeImpl()
    {
        super();
    }

    public ChannelListener create(ProductStatusConsumer consumer, BaseSessionManager sessionManager)
    {
        ProductStatusConsumerProxy bo = new ProductStatusConsumerProxy(consumer, sessionManager);

        //Every bo object must be added to the container BEFORE anything else.
        addToContainer(bo);
        //Every BOObject create MUST have a name...if the object is to be a managed object.
        bo.create(String.valueOf(bo.hashCode()));
        return bo;
    }
}
