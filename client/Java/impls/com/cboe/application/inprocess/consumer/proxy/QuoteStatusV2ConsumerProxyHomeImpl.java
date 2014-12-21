package com.cboe.application.inprocess.consumer.proxy;

import com.cboe.domain.supplier.proxy.BaseConsumerProxyHomeImpl;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.interfaces.application.inprocess.QuoteStatusV2Consumer;
import com.cboe.interfaces.application.inprocess.QuoteStatusV2ConsumerProxyHome;
import com.cboe.util.channel.ChannelListener;
import com.cboe.application.inprocess.consumer.proxy.QuoteStatusV2ConsumerProxy;
import com.cboe.infrastructureServices.foundationFramework.BOHome;
/**
 * @author Jing Chen
 */
public class QuoteStatusV2ConsumerProxyHomeImpl extends BOHome implements QuoteStatusV2ConsumerProxyHome
{
    /** constructor. **/
    public QuoteStatusV2ConsumerProxyHomeImpl()
    {
        super();
    }

    public ChannelListener create(QuoteStatusV2Consumer consumer, BaseSessionManager sessionManager)
        throws DataValidationException
    {
        QuoteStatusV2ConsumerProxy bo = new QuoteStatusV2ConsumerProxy(consumer, sessionManager);
        //Every bo object must be added to the container BEFORE anything else.
        addToContainer(bo);
        //Every BOObject create MUST have a name...if the object is to be a managed object.
        bo.create(String.valueOf(bo.hashCode()));
        return bo;
    }
}
