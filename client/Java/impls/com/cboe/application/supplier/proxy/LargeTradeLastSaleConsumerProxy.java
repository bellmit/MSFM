package com.cboe.application.supplier.proxy;

import com.cboe.idl.consumers.TickerConsumer;
import com.cboe.idl.marketData.InternalTickerDetailStruct;
import com.cboe.idl.util.RoutingParameterStruct;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.domain.SupplierProxyMessageTypes;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.application.supplier.LargeTradeLastSaleSupplierFactory;

public class LargeTradeLastSaleConsumerProxy extends InstrumentedConsumerProxy {
	private static RoutingParameterStruct defaultRP = new RoutingParameterStruct(com.cboe.client.util.CollectionHelper.EMPTY_int_ARRAY,"",0,(short) 0);
         
	public LargeTradeLastSaleConsumerProxy(TickerConsumer lsConsumer, 
										   BaseSessionManager sessionManager) {
		super(sessionManager, 
			  LargeTradeLastSaleSupplierFactory.find(sessionManager), 
			  lsConsumer);
		interceptor = new LargeTradeLastSaleConsumerInterceptor(lsConsumer);
	}

	@Override
	public String getMessageType() {
		return SupplierProxyMessageTypes.TICKER;
	}

	@Override
	public String getMethodName(ChannelEvent event) {
		return "acceptLargeTradeTickerDetailForClass";
	}
	
	public TickerConsumer getTickerConsumer()
    {
        return ((LargeTradeLastSaleConsumerInterceptor)interceptor).corbaObj;
    }

	@Override
	public void channelUpdate(ChannelEvent event) {
		if (Log.isDebugOn())
        {
            Log.debug(this,"calling channelUpdate for " + getSessionManager());
        }
        if (event != null)
        {
            boolean disconnect = false;
            try
            {
                if (getProxyWrapper().getQueueSize() > this.getNoActionProxyQueueDepthLimit()) // NEED TO PUT THIS IN THE XML
                {
                    String us = this.toString();
                    StringBuilder discon = new StringBuilder(us.length()+40);
                    discon.append("Disconnection consumer for : ").append(us).append(" Q=").append(getProxyWrapper().getQueueSize());
                    Log.information(this, discon.toString());
                    disconnect = true;
                }
                else
                {
                    // Call the proxied method passing the extracted data from the EventChannelEvent.
                    ((LargeTradeLastSaleConsumerInterceptor)interceptor).acceptLargeTradeTickerDetailForClass(
                    		defaultRP,		
                    		(InternalTickerDetailStruct[]) event.getEventData());
                }
            }
            catch (org.omg.CORBA.TIMEOUT toe)
            {
                Log.exception(this, "session:" + getSessionManager(), toe);
            }
            catch(Exception e)
            {
                Log.exception(this, "session:" + getSessionManager(), e);
                disconnect = true;
            }
            if (disconnect)
            {
                // End connection to client, throw exception
                lostConnection(event);
            }
        }
        else
        {
            Log.information(this, "Null event");
        }
	}

}
