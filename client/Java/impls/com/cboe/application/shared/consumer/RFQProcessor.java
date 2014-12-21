package com.cboe.application.shared.consumer;

import com.cboe.interfaces.application.*;
import com.cboe.interfaces.domain.SupplierProxyMessageTypes;
import com.cboe.util.*;
import com.cboe.util.channel.*;
import com.cboe.idl.cmiQuote.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

/**
 * @author Jeff Illian
 */
public class RFQProcessor extends InstrumentedProcessor {
    private RFQCollector parent = null;

  public RFQProcessor(RFQCollector parent) {
    super(parent);
    this.parent = parent;
  }

  public void setParent(RFQCollector parent) {
    this.parent = parent;
  }

  public RFQCollector getParent() {
    return parent;
  }

  public void channelUpdate(ChannelEvent event) {
    ChannelKey channelKey = (ChannelKey)event.getChannel();
    if (channelKey.channelType == ChannelType.RFQ && parent != null) {
      parent.acceptRFQ((RFQStruct)event.getEventData());
    }
    else {
      if (Log.isDebugOn())
      {
          Log.debug("RFQProcessor -> Wrong Channel : " + channelKey.channelType);
      }
    }
  }

  public String getMessageType()
  {
    return SupplierProxyMessageTypes.RFQ;
  }
}
