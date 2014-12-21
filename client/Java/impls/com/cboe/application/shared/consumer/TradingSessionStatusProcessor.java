package com.cboe.application.shared.consumer;

import com.cboe.interfaces.application.*;
import com.cboe.interfaces.domain.SupplierProxyMessageTypes;
import com.cboe.util.*;
import com.cboe.util.channel.*;
import com.cboe.idl.cmiSession.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

/**
 * @author Connie Feng
 */
public class TradingSessionStatusProcessor extends InstrumentedProcessor {
    private TradingSessionStatusCollector parent = null;

  public TradingSessionStatusProcessor(TradingSessionStatusCollector parent) {
    super(parent);
    this.parent = parent;
  }

  public void setParent(TradingSessionStatusCollector parent) {
    this.parent = parent;
  }

  public TradingSessionStatusCollector getParent() {
    return parent;
  }

  public void channelUpdate(ChannelEvent event)
  {
    ChannelKey channelKey = (ChannelKey)event.getChannel();
    if (parent != null)
    {
        switch (channelKey.channelType)
        {
            case ChannelType.TRADING_SESSION:
              parent.acceptTradingSessionState((TradingSessionStateStruct)event.getEventData());
            break;

            default:
              if (Log.isDebugOn())
              {
                  Log.debug("TradingSessionStatusProcessor -> Wrong Channel : " + channelKey.channelType);
              }
            break;
        }
    }
  }

  public String getMessageType()
  {
    return SupplierProxyMessageTypes.TRADING_SESSION_STATUS;
  }
}
