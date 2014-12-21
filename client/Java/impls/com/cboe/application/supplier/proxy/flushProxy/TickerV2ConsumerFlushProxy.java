package com.cboe.application.supplier.proxy.flushProxy;

import com.cboe.application.supplier.proxy.TickerV2ConsumerProxy;
import com.cboe.idl.cmiCallbackV2.CMITickerConsumer;
import com.cboe.idl.cmiConstants.QueueActions;
import com.cboe.idl.cmiMarketData.TickerStruct;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

public class TickerV2ConsumerFlushProxy extends TickerV2ConsumerProxy
{
    public TickerV2ConsumerFlushProxy(CMITickerConsumer tickerConsumer, BaseSessionManager sessionManager)
    {
        super(tickerConsumer, sessionManager, QueueActions.FLUSH_QUEUE);
    }

    public void channelUpdate(ChannelEvent event)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this,"calling channelUpdate for " + getSessionManager());
        }
        if (event != null)
        {
            boolean releaseEvent = false;
            try
            {
                short action = QueueActions.NO_ACTION;
                if (getProxyWrapper().getQueueSize() > this.getFlushProxyQueueDepthLimit())
                {
                    action = QueueActions.FLUSH_QUEUE;
                    String us = this.toString();
                    StringBuilder flushing = new StringBuilder(us.length()+32);
                    flushing.append("Flushing queue for : ").append(us).append(" Q=").append(getProxyWrapper().getQueueSize());
                    Log.information(this, flushing.toString());
                    event = getProxyWrapper().flushQueue();
                    releaseEvent = true; // flushQueue doesn't release() what it returns to us
                }
                getTickerConsumerInterceptor().acceptTicker((TickerStruct[])event.getEventData(),
                        getProxyWrapper().getQueueSize(), action);
            }
            catch (org.omg.CORBA.TIMEOUT toe)
            {
                Log.exception(this, "session:" + getSessionManager(), toe);
            }
            catch(Exception e)
            {
                Log.exception(this, "session:" + getSessionManager(), e);
                lostConnection(event);
            }
            finally
            {
                if (releaseEvent)
                {
                    event.release();
                }
            }
        }
    }
}