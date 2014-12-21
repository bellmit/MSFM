package com.cboe.application.heartBeat;

import java.util.*;

import com.cboe.application.shared.*;
import com.cboe.application.supplier.*;

import com.cboe.interfaces.application.*;

import com.cboe.idl.events.*;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiUser.*;
import com.cboe.idl.user.*;
import com.cboe.idl.cmiCallback.*;
import com.cboe.idl.cmiConstants.*;

import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.*;
import com.cboe.infrastructureServices.loggingService.*;
import com.cboe.infrastructureServices.securityService.*;
import com.cboe.infrastructureServices.systemsManagementService.*;
import com.cboe.util.event.EventChannelAdapter;
import com.cboe.util.event.EventChannelAdapterFactory;

import com.cboe.util.*;
import com.cboe.util.event.*;

/**
 *
 * HeartBeatImpl generates a regular interval, timed, heartBeat for the CAS
 * by periodically publishing on the CBOE event channel
 *
 * @author Keith A. Korecky
 *
 */
public class HeartBeatImpl extends BObject implements HeartBeat
{
   protected HeartBeatThread       heartBeat                      = null;
   private EventChannelAdapter     internalEventChannel           = null;
   private long                    heartBeatInterval;
   private boolean                 threadSuspended                = false;
  /**
   * HeartBeatImpl constructor comment.
   */
   public HeartBeatImpl(int interval)
   {
      super();
      heartBeatInterval = interval;
   }

   public void initialize() throws Exception
   {
      internalEventChannel = EventChannelAdapterFactory.find();

      heartBeat = new HeartBeatThread(internalEventChannel, heartBeatInterval);
      heartBeat.start();
   }

   public void start()
   {
       if (Log.isDebugOn()) { 
           Log.debug(this, "HeartBeat start in progress..." );
       } 
       heartBeat.resume();
       if (Log.isDebugOn()) { 
           Log.debug(this, "HeartBeat start complete." );
       }
   }

   public void shutdown()
   {
       if (Log.isDebugOn()) { 
           Log.debug(this, "HeartBeat shutdown in progress..." );
       } 
       heartBeat.suspend();
       if (Log.isDebugOn()) { 
           Log.debug(this, "HeartBeat shutdown complete." );
       }
   }
}
