package com.cboe.application.heartBeat;

import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.*;
import com.cboe.infrastructureServices.orbService.*;
import com.cboe.infrastructureServices.loggingService.*;
import com.cboe.infrastructureServices.systemsManagementService.*;
import com.cboe.application.shared.*;
import com.cboe.util.*;
import com.cboe.domain.startup.ClientBOHome;

import com.cboe.interfaces.application.*;

public class HeartBeatHomeImpl extends ClientBOHome implements HeartBeatHome
{
   private HeartBeatHomeImpl              instance                = null;
   private HeartBeat                      heartBeat               = null;
   private HeartBeatImpl                  heartBeatImpl           = null;

   public final static String HEARTBEAT_INTERVAL = "HeartBeatInterval";
   public final static String FAILURE_MESSAGE    = "Failed to create HeartBeat";

//   protected static final int   DEFAULT_TIMER_TIMEOUT_MS         = 10000;
   protected static int         interval;

   public HeartBeatHomeImpl() {
       super();
   }

   public HeartBeat create()
   {
      if ( heartBeat == null )
      {
         heartBeatImpl = new HeartBeatImpl(interval);

         //Every BOObject create MUST have a name...if the object is to be a managed object.
         heartBeatImpl.create( String.valueOf( heartBeatImpl.hashCode() ) );

         //Every bo object must be added to the container.
         addToContainer( heartBeatImpl );

         try
         {
            heartBeatImpl.initialize();
         }
         catch (Exception ex)
         {
            manageFatalException(ex);
         }

         heartBeat = heartBeatImpl;
      }

      return heartBeat;
   }

   public HeartBeat find()
   {
      return create();
   }

    public void clientStart()
    {
        create();
        heartBeatImpl.start();
        if (Log.isDebugOn())
        {
            Log.debug(this, "SMA Type = " + this.getSmaType());
        }
    }

    public void clientInitialize()
        throws Exception
    {
        interval = Integer.parseInt(getProperty(HEARTBEAT_INTERVAL));
    }

   public void clientShutdown()
   {
      // Stop the generator thread before going by-by
      heartBeatImpl.shutdown();
   }
}
