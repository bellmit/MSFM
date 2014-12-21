package com.cboe.application.shared.consumer;

import com.cboe.idl.cmiMarketData.RecapStruct;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.RecapCollector;
import com.cboe.interfaces.domain.SupplierProxyMessageTypes;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.channel.ChannelEvent;
//import com.cboe.util.event.EventChannelAdapter;
//import com.cboe.util.event.EventChannelAdapterFactory;
//import com.cboe.util.event.EventChannelListener;

public class RecapProcessor extends InstrumentedProcessor {
    private RecapCollector parent = null;
//    private EventChannelAdapter internalEventChannel = null;

  public RecapProcessor(RecapCollector parent) {
    super(parent);
    this.parent = parent;
//    internalEventChannel = EventChannelAdapterFactory.find();
  }

  public void setParent(RecapCollector parent) {
    this.parent = parent;
  }

  public RecapCollector getParent() {
    return parent;
  }

  public void channelUpdate(ChannelEvent event) {
    ChannelKey channelKey = (ChannelKey)event.getChannel();
    if (channelKey.channelType == ChannelType.RECAP_BY_CLASS && parent != null) {
      parent.acceptRecapsForClass((RecapStruct[])event.getEventData());
    }
    else {
      if (Log.isDebugOn())
      {
          Log.debug("RecapProcessor -> Wrong Channel : " + channelKey.channelType);
      }
    }
  }
    
  public String getMessageType()
  {
      return SupplierProxyMessageTypes.RECAP;
  }
}
