package com.cboe.application.shared.consumer;

import com.cboe.interfaces.application.*;

/**
 * @author Mike Pyatetsky
 */

public class ForcedLogoutProcessorFactory
{

   /**
    * ForcedLogoutProcessorFactory constructor comment.
    */
   public ForcedLogoutProcessorFactory()
   {
      super();
   }

   /**
    * @author Mike Pyatetsky
    */
   public static ForcedLogoutProcessor create( ForcedLogoutCollector parent )
   {
      ForcedLogoutProcessor processor = new ForcedLogoutProcessor();
      processor.setParent(parent);
      return processor;
   }
}
