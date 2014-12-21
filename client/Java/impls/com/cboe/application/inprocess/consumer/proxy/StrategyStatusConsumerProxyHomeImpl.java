package com.cboe.application.inprocess.consumer.proxy;

import com.cboe.infrastructureServices.foundationFramework.BOHome;
import com.cboe.interfaces.application.inprocess.StrategyStatusConsumerProxyHome;
import com.cboe.interfaces.callback.StrategyStatusConsumer;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.util.channel.ChannelListener;

/**
 * @author Jing Chen
 */
public class StrategyStatusConsumerProxyHomeImpl extends BOHome implements StrategyStatusConsumerProxyHome
{
    /** constructor. **/
    public StrategyStatusConsumerProxyHomeImpl()
    {
        super();
    }

    public ChannelListener create(StrategyStatusConsumer consumer, BaseSessionManager sessionManager)
    {
        StrategyStatusConsumerProxy bo = new StrategyStatusConsumerProxy(consumer, sessionManager);

        //Every bo object must be added to the container BEFORE anything else.
        addToContainer(bo);
        //Every BOObject create MUST have a name...if the object is to be a managed object.
        bo.create(String.valueOf(bo.hashCode()));
        return bo;
    }
}
