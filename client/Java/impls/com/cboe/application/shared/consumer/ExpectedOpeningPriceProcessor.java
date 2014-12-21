package com.cboe.application.shared.consumer;

import com.cboe.interfaces.application.*;
import com.cboe.interfaces.domain.SupplierProxyMessageTypes;

/**
 * @author Jeff Illian
 */
import com.cboe.util.*;
//import com.cboe.util.event.*;
import com.cboe.util.channel.*;
import com.cboe.idl.cmiMarketData.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

public class ExpectedOpeningPriceProcessor extends InstrumentedProcessor {
    private ExpectedOpeningPriceCollector parent = null;
//    private EventChannelAdapter internalEventChannel = null;

  /**
   * @author Jeff Illian
   */
  public ExpectedOpeningPriceProcessor(ExpectedOpeningPriceCollector parent) {
    super(parent);
    this.parent = parent;
//    internalEventChannel = EventChannelAdapterFactory.find();
  }

  /**
   * @author Jeff Illian
   */
  public void setParent(ExpectedOpeningPriceCollector parent) {
    this.parent = parent;
  }

  /**
   * @author Jeff Illian
   */
  public ExpectedOpeningPriceCollector getParent() {
    return parent;
  }

  /**
   * @author Jeff Illian
   */
  public void channelUpdate(ChannelEvent event) {
    ChannelKey channelKey = (ChannelKey)event.getChannel();
    if (channelKey.channelType == ChannelType.OPENING_PRICE_BY_CLASS && parent != null) {
      parent.acceptExpectedOpeningPricesForClass((ExpectedOpeningPriceStruct[])event.getEventData());
    }
    else {
      if (Log.isDebugOn())
      {
          Log.debug("ExpectedOpeningPriceProcessor -> Wrong Channel : " + channelKey.channelType);
      }
    }
  }
    
  public String getMessageType()
  {
      return SupplierProxyMessageTypes.EXPECTED_OPENING_PRICE;
  }
}
