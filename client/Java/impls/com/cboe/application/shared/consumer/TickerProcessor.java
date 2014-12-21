package com.cboe.application.shared.consumer;

import com.cboe.idl.cmiMarketData.TickerStruct;
import com.cboe.idl.marketData.InternalTickerDetailStruct;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.TickerCollector;
import com.cboe.interfaces.domain.SupplierProxyMessageTypes;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.channel.ChannelEvent;
//import com.cboe.util.event.EventChannelAdapter;
//import com.cboe.util.event.EventChannelAdapterFactory;
//import com.cboe.util.event.EventChannelListener;

public class TickerProcessor extends InstrumentedProcessor {
    private TickerCollector parent = null;
//    private EventChannelAdapter internalEventChannel = null;

  public TickerProcessor(TickerCollector parent) {
    super(parent);
    this.parent = parent;
//    internalEventChannel = EventChannelAdapterFactory.find();
  }

  public void setParent(TickerCollector parent) {
    this.parent = parent;
  }

  public TickerCollector getParent() {
    return parent;
  }

  public void channelUpdate(ChannelEvent event) {
    ChannelKey channelKey = (ChannelKey)event.getChannel();
    if (Log.isDebugOn())
    {
        Log.debug("TickerProcessor -> channelUpdate : " + channelKey.channelType);
    }
    if (channelKey.channelType == ChannelType.TICKER_BY_CLASS && parent != null) {
      parent.acceptTickersForClass((TickerStruct[])event.getEventData());
    } else if(channelKey.channelType == ChannelType.LARGE_TRADE_LAST_SALE_BY_CLASS && parent != null) {
      parent.acceptLargeTradeLastSaleForClass((InternalTickerDetailStruct[]) event.getEventData());  
    } else {
      if (Log.isDebugOn())
      {
          Log.debug("TickerProcessor -> Wrong Channel : " + channelKey.channelType);
      }
    }
  }
    
  public String getMessageType()
  {
      return SupplierProxyMessageTypes.TICKER;
  }
}
