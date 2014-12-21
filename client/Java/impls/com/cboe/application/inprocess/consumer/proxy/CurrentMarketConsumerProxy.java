package com.cboe.application.inprocess.consumer.proxy;

import com.cboe.domain.iec.ClientIECFactory;
import com.cboe.domain.iec.ConcurrentEventChannelAdapter;
import com.cboe.idl.cmiConstants.QueueActions;
import com.cboe.idl.cmiMarketData.CurrentMarketStruct;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.callback.CurrentMarketV2Consumer;
import com.cboe.interfaces.domain.CurrentMarketContainer;
import com.cboe.interfaces.domain.SupplierProxyMessageTypes;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.channel.ListenerProxyQueueControl;

/**
 * @author Jing Chen
 */
public class CurrentMarketConsumerProxy extends InstrumentedConsumerProxy
{
    private CurrentMarketV2Consumer consumer;
    private ListenerProxyQueueControl proxyWrapper;
    private ConcurrentEventChannelAdapter internalEventChannel; 

    public CurrentMarketConsumerProxy(CurrentMarketV2Consumer consumer, BaseSessionManager sessionManger)
    {
        super(consumer, sessionManger);
        this.consumer = consumer;
        try
        {
        	internalEventChannel = ClientIECFactory.findConcurrentEventChannelAdapter(ClientIECFactory.MARKETDATA_INSTRUMENTED_IEC);
        }
        catch (Exception e)
        {
        	Log.exception(this, "Exception getting MARKETDATA_INSTRUMENTED_IEC!", e);
        }        
    }

    public CurrentMarketV2Consumer getConsumer()
    {
        return consumer;
    }
    public ListenerProxyQueueControl getProxyWrapper()
    {
        if (proxyWrapper == null)
        {
            proxyWrapper = internalEventChannel.getProxyForDelegate(this);
        }
        return proxyWrapper;
    }

    public void channelUpdate(ChannelEvent event)
    {
        try{
            ChannelKey channelKey = (ChannelKey)event.getChannel();
            if (Log.isDebugOn())
            {
                Log.debug(this, "CurrentMarketConsumerProxy -> channelUpdate : " +  channelKey.channelType );
            }
            //CurrentMarketStruct[] currentMarkets = (CurrentMarketStruct[])event.getEventData();
            CurrentMarketContainer currentMarketsContainer = (CurrentMarketContainer)event.getEventData();
            CurrentMarketStruct[] currentMarkets = currentMarketsContainer.getBestMarkets();

            if (channelKey.channelType == ChannelType.CURRENT_MARKET_BY_CLASS)
            {
                consumer.acceptCurrentMarket(currentMarkets, getProxyWrapper().getQueueSize(), QueueActions.NO_ACTION);
            }
            else
            {
                if (Log.isDebugOn())
                {
                    Log.debug(this, "CurrentMarketConsumerProxy -> Wrong Channel : " + channelKey.channelType);
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    public void queueInstrumentationInitiated(){};
    public String getMessageType()
    {
        return SupplierProxyMessageTypes.CURRENT_MARKET;
    }
}
