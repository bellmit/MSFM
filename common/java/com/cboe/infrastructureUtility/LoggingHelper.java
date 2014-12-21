package com.cboe.infrastructureUtility;

import com.cboe.common.log.Logger;

/**
 * LoggingHelper
 */
public class LoggingHelper {
    

   static public void logDebugMessage( String msgSource, String msg) {
    Logger.sysNotify(msgSource + ": " + msg);
   }
    
    
   static public void logCriticalError( String msgSource, String msg) {
    Logger.sysAlarm(msgSource + ": " + msg);
   }
    
   static public void logShutDown( boolean s) {

	}

   static public void setTraceOn( boolean traceOn) {
   }

   static public void setDebugOn( boolean debugOn) {
   }

}

