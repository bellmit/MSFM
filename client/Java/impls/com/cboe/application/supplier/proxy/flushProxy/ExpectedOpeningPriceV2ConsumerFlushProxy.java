package com.cboe.application.supplier.proxy.flushProxy;

import com.cboe.application.supplier.proxy.ExpectedOpeningPriceV2ConsumerProxy;
import com.cboe.idl.cmiCallbackV2.CMIExpectedOpeningPriceConsumer;
import com.cboe.idl.cmiConstants.QueueActions;
import com.cboe.idl.cmiMarketData.ExpectedOpeningPriceStruct;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

public class ExpectedOpeningPriceV2ConsumerFlushProxy extends ExpectedOpeningPriceV2ConsumerProxy
{
    public ExpectedOpeningPriceV2ConsumerFlushProxy(CMIExpectedOpeningPriceConsumer expectedOpeningPriceConsumer, BaseSessionManager sessionManager)
    {
        super(expectedOpeningPriceConsumer, sessionManager, QueueActions.FLUSH_QUEUE);
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
                getExpectedOpeningPriceConsumerInterceptor().acceptExpectedOpeningPrice((ExpectedOpeningPriceStruct[])event.getEventData(),
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