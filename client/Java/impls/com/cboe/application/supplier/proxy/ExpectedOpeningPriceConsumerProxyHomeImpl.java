package com.cboe.application.supplier.proxy;

import com.cboe.domain.supplier.proxy.BaseConsumerProxyHomeImpl;
import com.cboe.idl.cmiCallback.CMIExpectedOpeningPriceConsumer;
import com.cboe.interfaces.application.ExpectedOpeningPriceConsumerProxyHome;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.util.channel.ChannelListener;

/**
 * ExpectedOpeningPriceConsumerProxyHomeImpl.
 * @author Jimmy Wang
 */
public class ExpectedOpeningPriceConsumerProxyHomeImpl extends BaseConsumerProxyHomeImpl implements ExpectedOpeningPriceConsumerProxyHome
{
    /** constructor. **/
    public ExpectedOpeningPriceConsumerProxyHomeImpl()
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
    public ChannelListener create(CMIExpectedOpeningPriceConsumer consumer, BaseSessionManager sessionManager)
    {
        ExpectedOpeningPriceConsumerProxy bo = new ExpectedOpeningPriceConsumerProxy(consumer, sessionManager);

        //Every bo object must be added to the container BEFORE anything else.
        addToContainer(bo);
        //Every BOObject create MUST have a name...if the object is to be a managed object.
        bo.create(String.valueOf(bo.hashCode()));
        if(getInstrumentationEnablementProperty())
        {
            bo.startMethodInstrumentation(getInstrumentationProperty());
        }
        bo.initConnectionProperty(getConnectionProperty(sessionManager));
        bo.initFlushProxyQueueDepthProperty(getFlushQueueDepth(sessionManager));
        bo.initNoActionProxyQueueDepthProperty(getNoActionQueueDepth(sessionManager));
        return bo;
    }
}
