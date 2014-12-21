package com.cboe.application.heartBeatConsumer;

import com.cboe.idl.cmiAdmin.HeartBeatStruct;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.HeartBeatConsumer;
import com.cboe.domain.util.TimeServiceWrapper;

/**
 *
 * HeartBeatConsumerImpl 
 *
 * @author Magic Magee
 *
 */
public class HeartBeatConsumerImpl extends BObject implements HeartBeatConsumer
{
    protected HeartBeatStruct localHeartBeatStruct = null;

  /**
   * HeartBeatConsumerImpl constructor comment.
   */
   public HeartBeatConsumerImpl()
   {
      super();
   }

    public HeartBeatStruct acceptHeartBeat(HeartBeatStruct heartBeatStruct, String s) {
        if (Log.isDebugOn())
        {
            Log.debug("HeartBeatConsumerImpl.acceptHeartBeat from (" + s + ")");
        }
        if (null == localHeartBeatStruct) {
            localHeartBeatStruct = new HeartBeatStruct();
        }
        localHeartBeatStruct.pulseInterval = 0;             // not really used
        localHeartBeatStruct.requestID = s;                 // pass along our parameter ID
        localHeartBeatStruct.currentDate = TimeServiceWrapper.toDateStruct();
        localHeartBeatStruct.currentTime = TimeServiceWrapper.toTimeStruct();

        return localHeartBeatStruct;
    }
}
