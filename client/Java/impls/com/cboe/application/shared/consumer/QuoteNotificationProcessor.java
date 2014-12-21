package com.cboe.application.shared.consumer;

import com.cboe.interfaces.application.*;
import com.cboe.interfaces.domain.SupplierProxyMessageTypes;
import com.cboe.util.channel.*;
import com.cboe.util.ChannelType;
import com.cboe.util.ChannelKey;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

/**
 * @author Jeff Illian
 */
public class QuoteNotificationProcessor extends InstrumentedProcessor {
    private QuoteNotificationCollector parent = null;

  public QuoteNotificationProcessor(QuoteNotificationCollector parent) {
    super(parent);
    this.parent = parent;
  }

  public void setParent(QuoteNotificationCollector parent) {
    this.parent = parent;
  }

  public QuoteNotificationCollector getParent() {
    return parent;
  }

  public void channelUpdate(ChannelEvent event)
  {
    ChannelKey channelKey = (ChannelKey)event.getChannel();
    if (Log.isDebugOn())
    {
        Log.debug("QuoteNotificationProcessor -> Channel : " + channelKey.channelType);
    }

    Object eventData = event.getEventData();

    if (parent != null)
    {
        switch (channelKey.channelType)
        {
            case ChannelType.QUOTE_LOCKED_NOTIFICATION :
            case ChannelType.QUOTE_LOCKED_NOTIFICATION_BY_CLASS:
                //com.cboe.idl.cmiQuote.LockNotificationStruct lockNotification = (com.cboe.idl.cmiQuote.LockNotificationStruct) eventData;
                //build a a quote lock sequence.
                com.cboe.idl.cmiQuote.LockNotificationStruct lockNotification = (com.cboe.idl.cmiQuote.LockNotificationStruct) eventData;
                com.cboe.idl.cmiQuote.LockNotificationStruct [] lockNotifications = new com.cboe.idl.cmiQuote.LockNotificationStruct [1];
		        lockNotifications[0] = lockNotification;
                parent.acceptQuoteNotification(lockNotifications);
            break;

            /*case ChannelType.QUOTE_LOCKED_NOTIFICATION_BY_CLASS:
                UserClassContainer newContainer = (UserClassContainer)eventData;
                parent.acceptQuoteNotification(newContainer.getClassKey());
            break;
*/
            default:
                if (Log.isDebugOn())
                {
                    Log.debug("QuoteNotificationProcessor -> Wrong Channel : " + channelKey.channelType);
                }
      }
    }
  }

  public String getMessageType()
  {
    return SupplierProxyMessageTypes.QUOTE_LOCK;
  }
}
