package com.cboe.application.inprocess.consumer.proxy;

import com.cboe.infrastructureServices.foundationFramework.BOHome;
import com.cboe.interfaces.application.inprocess.ProductStatusConsumerProxyHome;
import com.cboe.interfaces.application.inprocess.ClassStatusConsumerProxyHome;
import com.cboe.interfaces.callback.ProductStatusConsumer;
import com.cboe.interfaces.callback.ClassStatusConsumer;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.util.channel.ChannelListener;

/**
 * @author Jing Chen
 */
public class ClassStatusConsumerProxyHomeImpl extends BOHome implements ClassStatusConsumerProxyHome
{
    /** constructor. **/
    public ClassStatusConsumerProxyHomeImpl()
    {
        super();
    }

    public ChannelListener create(ClassStatusConsumer consumer, BaseSessionManager sessionManager)
    {
        ClassStatusConsumerProxy bo = new ClassStatusConsumerProxy(consumer, sessionManager);

        //Every bo object must be added to the container BEFORE anything else.
        addToContainer(bo);
        //Every BOObject create MUST have a name...if the object is to be a managed object.
        bo.create(String.valueOf(bo.hashCode()));
        return bo;
    }
}
