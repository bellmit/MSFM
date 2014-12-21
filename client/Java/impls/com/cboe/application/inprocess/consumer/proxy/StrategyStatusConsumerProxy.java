package com.cboe.application.inprocess.consumer.proxy;

import com.cboe.application.supplier.proxy.ProductStatusCollectorProxy;
import com.cboe.idl.cmiSession.SessionStrategyStruct;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.callback.StrategyStatusConsumer;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.util.channel.ChannelEvent;

/**
 * @author Jing Chen
 */

public class StrategyStatusConsumerProxy extends ProductStatusCollectorProxy
{
    protected StrategyStatusConsumer strategyStatusConsumer;

    public StrategyStatusConsumerProxy(StrategyStatusConsumer consumer, BaseSessionManager sessionManager)
    {
        super(null, sessionManager, consumer);
        setHashKey(consumer);
        this.strategyStatusConsumer = consumer;
    }

    public void channelUpdate(ChannelEvent event)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this,"Got channel update " + event);
        }
        SessionStrategyStruct[] sessionStrategyStructs = {(SessionStrategyStruct) event.getEventData()};
        strategyStatusConsumer.updateProductStrategy(sessionStrategyStructs);
    }
}
