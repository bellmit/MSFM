package com.cboe.application.inprocess.consumer.proxy;

import com.cboe.domain.iec.ClientIECFactory;
import com.cboe.domain.iec.ConcurrentEventChannelAdapter;
import com.cboe.idl.cmiConstants.QueueActions;
import com.cboe.idl.cmiMarketData.BookDepthStruct;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.callback.OrderBookV2Consumer;
import com.cboe.interfaces.domain.SupplierProxyMessageTypes;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.channel.ListenerProxyQueueControl;

/**
 * @author Jing Chen
 */
public class BookDepthConsumerProxy extends InstrumentedConsumerProxy
{
    private OrderBookV2Consumer consumer;
    private ListenerProxyQueueControl proxyWrapper;
    private ConcurrentEventChannelAdapter internalEventChannel; 

    public BookDepthConsumerProxy(OrderBookV2Consumer consumer, BaseSessionManager sessionManager)
    {
        super(consumer, sessionManager);
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

    public OrderBookV2Consumer getConsumer()
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
        ChannelKey channelKey = (ChannelKey)event.getChannel();
        if (Log.isDebugOn())
        {
            Log.debug(this, "BookDepthConsumerProxy -> channelUpdate : " +  channelKey.channelType );
        }
        BookDepthStruct[] bookDepths = (BookDepthStruct[])event.getEventData();
        if (channelKey.channelType == ChannelType.BOOK_DEPTH_BY_CLASS)
        {
            consumer.acceptBookDepth(bookDepths, getProxyWrapper().getQueueSize(), QueueActions.NO_ACTION);
        }
        else
        {
            if (Log.isDebugOn())
            {
                Log.debug(this, "BookDepthConsumerProxy -> Wrong Channel : " + channelKey.channelType);
            }
        }
    }
    public String getMessageType()
    {
        return SupplierProxyMessageTypes.BOOK_DEPTH;
    }
    public void queueInstrumentationInitiated(){};
}
