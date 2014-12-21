package com.cboe.application.heartBeat;

import java.util.*;

import com.cboe.application.shared.*;
import com.cboe.application.supplier.*;

import com.cboe.idl.events.*;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiUser.*;
import com.cboe.idl.user.*;
import com.cboe.idl.cmiCallback.*;
import com.cboe.idl.cmiAdmin.*;

import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.*;
import com.cboe.infrastructureServices.loggingService.*;
import com.cboe.infrastructureServices.securityService.*;
import com.cboe.util.event.EventChannelAdapterFactory;
import com.cboe.util.event.EventChannelAdapter;
import com.cboe.util.*;
import com.cboe.util.channel.*;
import com.cboe.util.event.*;
import com.cboe.domain.util.TimeServiceWrapper;

import com.cboe.application.heartBeat.*;
/**
 *
 * HeartBeatImpl generates a regular interval, timed, heartBeat for the CAS
 * by periodically publishing on the CBOE event channel
 *
 * @author Keith A. Korecky
 *
 */
public class HeartBeatThread extends Thread
{

   private static final String         DEFAULT_THREAD_NAME        = "HeartBeatThread";

   private long                        timerTimeout;
   private UserSessionAdminSupplier    heartBeatSupplier          = null;
   private int                         duration                   = 1;
   private String                      requestId                  = "HB requestId";
   private EventChannelAdapter         internalEventChannel       = null;
   private static final Integer        INT_0                      = 0;



//   public HeartBeatThread( ReferenceCountChannelAdapter internalEventChannel )
//   {
//      this( internalEventChannel, HeartBeatHomeImpl.DEFAULT_TIMER_TIMEOUT_MS );
//   }

   public HeartBeatThread( EventChannelAdapter internalEventChannel, long interval )
   {
      this( internalEventChannel, DEFAULT_THREAD_NAME, interval );
   }

   public HeartBeatThread( EventChannelAdapter internalEventChannel, String threadName, long interval )
   {
      super( threadName );

      timerTimeout               = interval;
      this.internalEventChannel  = internalEventChannel;

   }

   public void run()
   {
      while( true )
      {
         try
         {
            sleep( timerTimeout );
            pulse();
         }
         catch( InterruptedException ie )
         {
             if (Log.isDebugOn()) { 
                 Log.debug( "HeartBeatThread exiting..." ); 
             }
         }
      }
   }

   private void pulse()
   {
      ChannelKey        channelKey;
      ChannelEvent      event;
      HeartBeatStruct   heartBeatStruct = new HeartBeatStruct( duration, requestId,  TimeServiceWrapper.toDateStruct(), TimeServiceWrapper.toTimeStruct() );

      // create channel key for HeartBeat
      channelKey  = new ChannelKey( ChannelType.CB_HEARTBEAT, INT_0 );
      event       = EventChannelAdapterFactory.find().getChannelEvent( this, channelKey, heartBeatStruct );
      internalEventChannel.dispatch(event);
   }

}
