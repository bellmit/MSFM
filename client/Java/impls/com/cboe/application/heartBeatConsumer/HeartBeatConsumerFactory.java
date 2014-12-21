package com.cboe.application.heartBeatConsumer;

import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.idl.clientCallbackServices.HeartBeatConsumer;
import com.cboe.exceptions.SystemException;

/**
 *
 * HeartBeatConsumerFactory
 *
 * @author Magic Magee
 * @author Gijo Joseph
 *
 */
public class HeartBeatConsumerFactory
{
    private static HeartBeatConsumer heartBeatConsumer = null;

  /**
   * HeartBeatConsumerFactory constructor comment.
   */
   protected HeartBeatConsumerFactory()
   {
      super();
   }

   public static HeartBeatConsumer getHeartBeatConsumerCallback() throws SystemException {
       if (Log.isDebugOn())
       { 
           Log.debug("HeartBeatConsumerFactory.getHeartBeatConsumerCallback invoke"); 
       }

       if ( heartBeatConsumer == null )
       { 
           try 
           { 
               org.omg.CORBA.Object obj = 
                   POAHelper.connect( new com.cboe.idl.clientCallbackServices.POA_HeartBeatConsumer_tie( new HeartBeatConsumerImpl()), null); 
               heartBeatConsumer = com.cboe.idl.clientCallbackServices.HeartBeatConsumerHelper.narrow(obj); 
           } 
           catch (Exception e) 
           { 
               Log.exception("Exception while creating heartbeat callback", e); throw new SystemException();
           } 
       }
       return heartBeatConsumer;
    }
}
