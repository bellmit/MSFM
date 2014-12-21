package com.cboe.application.shared.consumer;

import com.cboe.idl.cmiMarketData.CurrentMarketStruct;
import com.cboe.idl.cmiMarketData.NBBOStruct;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.CurrentMarketCollector;
import com.cboe.interfaces.domain.CurrentMarketContainer;
import com.cboe.interfaces.domain.SupplierProxyMessageTypes;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.channel.ChannelEvent;
//import com.cboe.util.event.EventChannelAdapter;
//import com.cboe.util.event.EventChannelAdapterFactory;

public class CurrentMarketProcessor extends InstrumentedProcessor {
    private CurrentMarketCollector parent = null;
//    private EventChannelAdapter internalEventChannel = null;

  public CurrentMarketProcessor(CurrentMarketCollector parent) {
    super(parent);
    this.parent = parent;
//    internalEventChannel = EventChannelAdapterFactory.find();
  }

  public void setParent(CurrentMarketCollector parent) {
    this.parent = parent;
  }

  public CurrentMarketCollector getParent() {
    return parent;
  }

  public void channelUpdate(ChannelEvent event) {
    ChannelKey channelKey = (ChannelKey)event.getChannel();
    if (Log.isDebugOn())
    {
        Log.debug("CurrentMarketProcessor -> channelUpdate : " +  channelKey.channelType );
    }
    if (channelKey.channelType == ChannelType.CURRENT_MARKET_BY_CLASS && parent != null)
    {
      //parent.acceptCurrentMarketsForClass((CurrentMarketStruct[])event.getEventData());
      parent.acceptCurrentMarketsForClass((CurrentMarketContainer)event.getEventData());
    }
    else if (channelKey.channelType == ChannelType.NBBO_BY_CLASS && parent != null)
    {
      parent.acceptNBBOsForClass((NBBOStruct[])event.getEventData());
    }
    /****
    else if (channelKey.channelType == ChannelType.CURRENT_MARKET_BY_CLASS_V2 && parent != null)
    {
        parent.acceptCurrentMarketsForClassV2((CurrentMarketContainer)event.getEventData());
        
    }
    ****/
    else
    {
      if (Log.isDebugOn())
      {
          Log.debug("CurrentMarketProcessor -> Wrong Channel : " + channelKey.channelType);
      }
    }
  }
    
  public String getMessageType()
  {
      return SupplierProxyMessageTypes.CURRENT_MARKET;
  }
}
