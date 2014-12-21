package com.cboe.application.shared.consumer;

import com.cboe.interfaces.application.*;
/**
 * @author Jeff Illian
 */
import com.cboe.util.*;
import com.cboe.util.event.*;
import com.cboe.util.channel.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

public class UserTimeoutWarningProcessor implements EventChannelListener {
    private UserTimeoutWarningCollector parent = null;
    private EventChannelAdapter internalEventChannel = null;

  /**
   * @author Jeff Illian
   */
  public UserTimeoutWarningProcessor() {
    super();
    internalEventChannel = EventChannelAdapterFactory.find();
  }

  /**
   * @author Jeff Illian
   */
  public void setParent(UserTimeoutWarningCollector parent) {
    this.parent = parent;
  }

  /**
   * @author Jeff Illian
   */
  public UserTimeoutWarningCollector getParent() {
    return parent;
  }

  /**
   * @author Jeff Illian
   */
  public void channelUpdate(ChannelEvent event) {
    ChannelKey channelKey = (ChannelKey)event.getChannel();
    if (channelKey.channelType == ChannelType.USER_SECURITY_TIMEOUT && parent != null) {
      parent.acceptUserTimeoutWarning((String)event.getEventData());
    }
    else {
      if (Log.isDebugOn())
      {
          Log.debug("UserTimeoutWarningProcessor -> Wrong Channel : " + channelKey.channelType);
      }
    }
  }
}
