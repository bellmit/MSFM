package com.cboe.application.inprocess.consumer.proxy;

import com.cboe.domain.supplier.proxy.BaseConsumerProxyHomeImpl;
import com.cboe.exceptions.DataValidationException;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.interfaces.application.inprocess.QuoteStatusConsumer;
import com.cboe.interfaces.application.inprocess.QuoteStatusConsumerProxyHome;
import com.cboe.util.channel.ChannelListener;
import com.cboe.application.inprocess.consumer.proxy.QuoteStatusConsumerProxy;
import com.cboe.infrastructureServices.foundationFramework.BOHome;
/**
 * @author Jing Chen
 */
public class QuoteStatusConsumerProxyHomeImpl extends BOHome implements QuoteStatusConsumerProxyHome
{
    /** constructor. **/
    public QuoteStatusConsumerProxyHomeImpl()
    {
        super();
    }

    public ChannelListener create(QuoteStatusConsumer consumer, BaseSessionManager sessionManager)
        throws DataValidationException
    {
        QuoteStatusConsumerProxy bo = new QuoteStatusConsumerProxy(consumer, sessionManager);
        //Every bo object must be added to the container BEFORE anything else.
        addToContainer(bo);
        //Every BOObject create MUST have a name...if the object is to be a managed object.
        bo.create(String.valueOf(bo.hashCode()));
        return bo;
    }
}
