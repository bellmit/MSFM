package com.cboe.application.supplier.proxy;

import com.cboe.domain.supplier.proxy.BaseConsumerProxyHomeImpl;
import com.cboe.idl.cmiCallback.CMINBBOConsumer;
import com.cboe.interfaces.application.NBBOConsumerProxyHome;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.util.channel.ChannelListener;


/**
 * NBBOConsumerProxyHomeImpl.
 * @author Jimmy Wang
 */
public class NBBOConsumerProxyHomeImpl extends BaseConsumerProxyHomeImpl implements NBBOConsumerProxyHome
{
    /** constructor. **/
    public NBBOConsumerProxyHomeImpl()
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
    public ChannelListener create(CMINBBOConsumer consumer, BaseSessionManager sessionManager)
    {
        NBBOConsumerProxy bo = new NBBOConsumerProxy(consumer, sessionManager);

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
