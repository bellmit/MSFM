package com.cboe.application.supplier.proxy;

import com.cboe.interfaces.application.*;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.util.channel.ChannelListener;
import com.cboe.domain.supplier.proxy.BaseConsumerProxyHomeImpl;
/**
 * QuoteStatusConsumerProxyHomeImpl.
 * @author Jimmy Wang
 */
public class ProductStatusCollectorProxyHomeImpl extends BaseConsumerProxyHomeImpl implements ProductStatusCollectorProxyHome
{
    /** constructor. **/
    public ProductStatusCollectorProxyHomeImpl()
    {
        super();
    }

    /**
      * Follows the prescribed method for creating and generating a impl class.
      * Sets the Session Manager parent class and initializes the Order Query.
      * @param sessionManager user session
      * @return proxy object for the consumer
      */
    public ChannelListener create(ProductStatusCollector consumer, BaseSessionManager sessionManager)
    {
        ProductStatusCollectorProxy bo = new ProductStatusCollectorProxy(consumer, sessionManager, consumer);

        //Every bo object must be added to the container BEFORE anything else.
        addToContainer(bo);
        //Every BOObject create MUST have a name...if the object is to be a managed object.
        bo.create(String.valueOf(bo.hashCode()));
        bo.initConnectionProperty(getConnectionProperty(sessionManager));
        return bo;
    }
}
