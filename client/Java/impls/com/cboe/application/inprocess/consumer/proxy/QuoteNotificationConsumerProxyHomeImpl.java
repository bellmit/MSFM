package com.cboe.application.inprocess.consumer.proxy;

import com.cboe.domain.supplier.proxy.BaseConsumerProxyHomeImpl;
import com.cboe.interfaces.application.inprocess.LockedQuoteStatusConsumer;
import com.cboe.interfaces.application.inprocess.QuoteNotificationConsumerProxyHome;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.util.channel.ChannelListener;
/**
 * @author Jing Chen
 */
public class QuoteNotificationConsumerProxyHomeImpl extends BaseConsumerProxyHomeImpl implements QuoteNotificationConsumerProxyHome
{
    /** constructor. **/
    public QuoteNotificationConsumerProxyHomeImpl()
    {
        super();
    }

    public ChannelListener create(LockedQuoteStatusConsumer consumer, BaseSessionManager sessionManager)
    {
        QuoteNotificationConsumerProxy bo = new QuoteNotificationConsumerProxy(consumer, sessionManager);

        //Every bo object must be added to the container BEFORE anything else.
        addToContainer(bo);
        //Every BOObject create MUST have a name...if the object is to be a managed object.
        bo.create(String.valueOf(bo.hashCode()));
        return bo;
    }
}
