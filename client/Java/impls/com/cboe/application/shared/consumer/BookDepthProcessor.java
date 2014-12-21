package com.cboe.application.shared.consumer;

import com.cboe.idl.cmiMarketData.BookDepthStruct;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.BookDepthCollector;
import com.cboe.interfaces.domain.SupplierProxyMessageTypes;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.channel.ChannelEvent;
//import com.cboe.util.event.EventChannelAdapterFactory;
//import com.cboe.domain.instrumentedChannel.event.InstrumentedEventChannelAdapter;
//import com.cboe.domain.instrumentedChannel.event.InstrumentedEventChannelAdapterFactory;

public class BookDepthProcessor extends InstrumentedProcessor {
    private BookDepthCollector parent = null;
//    private InstrumentedEventChannelAdapter internalEventChannel = null;

  public BookDepthProcessor(BookDepthCollector parent) {
    super(parent);
    this.parent = parent;
//    internalEventChannel = InstrumentedEventChannelAdapterFactory.find();
  }

  public void setParent(BookDepthCollector parent) {
    this.parent = parent;
  }

  public BookDepthCollector getParent() {
    return parent;
  }

  public void channelUpdate(ChannelEvent event) {
    ChannelKey channelKey = (ChannelKey)event.getChannel();
    if (channelKey.channelType == ChannelType.BOOK_DEPTH_BY_CLASS && parent != null) {
      parent.acceptBookDepthsForClass((BookDepthStruct[])event.getEventData());
    }
    else {
        if (Log.isDebugOn())
        {
            Log.debug("BookDepthProcessor -> Wrong Channel : " + channelKey.channelType);
        }
    }
  }
    
  public String getMessageType()
  {
        return SupplierProxyMessageTypes.BOOK_DEPTH;
  }
}
