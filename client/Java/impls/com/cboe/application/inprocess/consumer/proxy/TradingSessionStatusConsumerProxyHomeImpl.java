package com.cboe.application.inprocess.consumer.proxy;

import com.cboe.domain.supplier.proxy.BaseConsumerProxyHomeImpl;
import com.cboe.interfaces.application.inprocess.RFQConsumer;
import com.cboe.interfaces.application.inprocess.RFQConsumerProxyHome;
import com.cboe.interfaces.application.inprocess.TradingSessionStatusConsumerProxyHome;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.interfaces.callback.TradingSessionStatusConsumer;
import com.cboe.util.channel.ChannelListener;
import com.cboe.infrastructureServices.foundationFramework.BOHome;

/**
 * @author Jing Chen
 */
public class TradingSessionStatusConsumerProxyHomeImpl extends BOHome implements TradingSessionStatusConsumerProxyHome
{
    /** constructor. **/
    public TradingSessionStatusConsumerProxyHomeImpl()
    {
        super();
    }

    public ChannelListener create(TradingSessionStatusConsumer consumer, BaseSessionManager sessionManager)
    {
        TradingSessionStatusConsumerProxy bo = new TradingSessionStatusConsumerProxy(consumer, sessionManager);

        //Every bo object must be added to the container BEFORE anything else.
        addToContainer(bo);
        //Every BOObject create MUST have a name...if the object is to be a managed object.
        bo.create(String.valueOf(bo.hashCode()));
        return bo;
    }
}
