package com.cboe.application.supplier.proxy;

import com.cboe.interfaces.application.*;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.util.channel.ChannelListener;
import com.cboe.domain.supplier.proxy.BaseConsumerProxyHomeImpl;

/**
 * RFQConsumerProxyHomeImpl.
 * @author Jimmy Wang
 */
public class RFQV2ConsumerProxyHomeImpl extends BaseConsumerProxyHomeImpl implements RFQV2ConsumerProxyHome
{
    /** constructor. **/
    public RFQV2ConsumerProxyHomeImpl()
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
    public ChannelListener create(com.cboe.idl.cmiCallbackV2.CMIRFQConsumer consumer, BaseSessionManager sessionManager)
    {
        RFQV2ConsumerProxy bo = new RFQV2ConsumerProxy(consumer, sessionManager);

        //Every bo object must be added to the container BEFORE anything else.
        addToContainer(bo);
        //Every BOObject create MUST have a name...if the object is to be a managed object.
        bo.create(String.valueOf(bo.hashCode()));
        if(getInstrumentationEnablementProperty())
        {
            bo.startMethodInstrumentation(getInstrumentationProperty());
        }
        bo.initConnectionProperty(getConnectionProperty(sessionManager));
        bo.initNoActionProxyQueueDepthProperty(getNoActionQueueDepth(sessionManager));
        return bo;
    }
}
