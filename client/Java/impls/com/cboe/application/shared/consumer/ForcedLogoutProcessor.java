package com.cboe.application.shared.consumer;

import com.cboe.interfaces.application.*;
import com.cboe.util.*;
import com.cboe.domain.util.LogoutMessage;
import com.cboe.util.event.*;
import com.cboe.util.channel.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

public class ForcedLogoutProcessor implements EventChannelListener
{
   private ForcedLogoutCollector parent = null;

   /**
    * @author Mike Pyatetsky
    */
   public ForcedLogoutProcessor()
   {
      super();
   }

   /**
    * @author Mike Pyatetsky
    */
   public void setParent(ForcedLogoutCollector  parent)
   {
      this.parent = parent;
   }

   /**
    * @author Mike Pyatetsky
    */
   public ForcedLogoutCollector getParent()
   {
      return parent;
   }

   /**
    * @author Mike Pyatetsky
    */
   public void channelUpdate(ChannelEvent event)
   {
      ChannelKey channelKey = (ChannelKey)event.getChannel();
      if ( (parent != null) && (channelKey.channelType == ChannelType.CB_LOGOUT) )
      {
         LogoutMessage message = (LogoutMessage)event.getEventData();
         parent.acceptForcedLogout(message.getSessionKey(), message.getMessage());
      }
      else
      {
          if (Log.isDebugOn())
          {
              Log.debug( "ForcedLogout -> Wrong Channel : " + channelKey.channelType );
          }
      }
   }
}
