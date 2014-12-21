package com.cboe.application.inprocess.consumer.proxy;

import com.cboe.idl.cmiSession.TradingSessionStateStruct;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.callback.TradingSessionStatusConsumer;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.interfaces.domain.SupplierProxyMessageTypes;
import com.cboe.util.channel.ChannelEvent;

/**
 * @author Jing Chen
 */

public class TradingSessionStatusConsumerProxy extends InstrumentedConsumerProxy
{
    protected TradingSessionStatusConsumer tradingSessionStatusConsumer;
    private BaseSessionManager sessionManager;

    public TradingSessionStatusConsumerProxy(TradingSessionStatusConsumer consumer, BaseSessionManager sessionManager)
    {
        super(consumer, sessionManager);
        this.tradingSessionStatusConsumer = consumer;
        this.sessionManager = sessionManager;
        setHashKey(consumer);
    }

    public void channelUpdate(ChannelEvent event)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this,"calling channelUpdate for  " + sessionManager);
        }
        try
        {
            TradingSessionStateStruct tradingSessionStateStruct = (TradingSessionStateStruct)event.getEventData();
            tradingSessionStatusConsumer.acceptTradingSessionState(tradingSessionStateStruct);
        }
        catch(Exception e)
        {
            Log.exception(this, "session:" + sessionManager, e);
        }
    }

    public String getMessageType()
    {
        return SupplierProxyMessageTypes.TRADING_SESSION_STATUS;
    }

    public void queueInstrumentationInitiated()
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
