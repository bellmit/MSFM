package com.cboe.application.shared.consumer;

import com.cboe.interfaces.application.*;

/**
 * @author Keith A. Korecky
 */

public class HeartBeatProcessorFactory
{

   /**
    * HeartBeatProcessorFactory constructor comment.
    */
   public HeartBeatProcessorFactory()
   {
      super();
   }

   /**
    * @author Keith A. Korecky
    */
   public static HeartBeatProcessor create( HeartBeatCollector parent )
   {
      HeartBeatProcessor processor = new HeartBeatProcessor();
      processor.setParent(parent);
      return processor;
   }
}
