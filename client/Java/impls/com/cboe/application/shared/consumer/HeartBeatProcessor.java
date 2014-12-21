package com.cboe.application.shared.consumer;

import com.cboe.interfaces.application.*;
import com.cboe.util.*;
import com.cboe.util.event.*;
import com.cboe.util.channel.*;
import com.cboe.idl.cmiSession.*;
import com.cboe.idl.cmiAdmin.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

public class HeartBeatProcessor implements EventChannelListener
{
   private HeartBeatCollector parent = null;
    private EventChannelAdapter internalEventChannel = null;

   /**
    * @author Keith A. Korecky
    */
   public HeartBeatProcessor()
   {
      super();
      internalEventChannel = EventChannelAdapterFactory.find();
   }

   /**
    * @author Keith A. Korecky
    */
   public void setParent(HeartBeatCollector parent)
   {
      this.parent = parent;
   }

   /**
    * @author Keith A. Korecky
    */
   public HeartBeatCollector getParent()
   {
      return parent;
   }

   /**
    * @author Keith A. Korecky
    */
   public void channelUpdate(ChannelEvent event)
   {
      ChannelKey channelKey = (ChannelKey)event.getChannel();
      if ( (parent != null) && (channelKey.channelType == ChannelType.CB_HEARTBEAT) )
      {
         parent.acceptHeartBeat( (HeartBeatStruct)event.getEventData() );
      }
      else
      {
          if (Log.isDebugOn())
          {
              Log.debug( "HeartBeat -> Wrong Channel : " + channelKey.channelType );
          }
      }
   }
}
