// Source file: d:/cboe/java/com/cboe/loggingService/LoggingRuntimeException.java

package com.cboe.loggingService;

/**
   An exception generated during the creation, delivery or retrieval of a log message, or configuration of the logging service that does not have to be declared as thrown.
   @author David Houlding
   @version 4.0
 */
public class LoggingRuntimeException extends RuntimeException {
    
    /**
       @roseuid 365C81390388
     */
    public LoggingRuntimeException(String message) {
        super( message );
    }
}
