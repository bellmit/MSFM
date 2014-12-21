package com.cboe.application.supplier.proxy.flushProxy;

import com.cboe.application.supplier.proxy.CurrentMarketV3ConsumerProxy;
import com.cboe.idl.cmiCallbackV3.CMICurrentMarketConsumer;
import com.cboe.idl.cmiConstants.QueueActions;
import com.cboe.idl.cmiMarketData.CurrentMarketStruct;
//import com.cboe.idl.cmiMarketData.CurrentMarketStructV2;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.domain.CurrentMarketContainer;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.util.channel.ChannelEvent;
//import com.cboe.domain.util.CurrentMarketContainerImpl;


public class CurrentMarketV3ConsumerFlushProxy extends CurrentMarketV3ConsumerProxy
{
    public CurrentMarketV3ConsumerFlushProxy(CMICurrentMarketConsumer currentMarketConsumer, BaseSessionManager sessionManager)
    {
        super(currentMarketConsumer, sessionManager, QueueActions.FLUSH_QUEUE);
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
                CurrentMarketContainer currentMarketContainer = (CurrentMarketContainer)event.getEventData();
                CurrentMarketStruct[] currentMarkets = currentMarketContainer.getBestMarkets();
                CurrentMarketStruct[] publicBestMarkets = currentMarketContainer.getBestPublicMarketsAtTop();
                if (currentMarkets == null || publicBestMarkets == null)
                {
                    if (Log.isDebugOn())
                    {
                        Log.debug(this, "CurrentMarketV3ConsumerFlushProxy:received null values in currentMarkets/publicBestMarkets");
                    }
                    return;
                }

                getCurrentMarketConsumerInterceptor().acceptCurrentMarket(currentMarkets,
                                                          publicBestMarkets,
                                                          getProxyWrapper().getQueueSize(), action
                                                         );
            }
            catch (org.omg.CORBA.TIMEOUT toe)
            {
                Log.exception(this, "session:" + getSessionManager(), toe);
            }
            catch (Exception e)
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
