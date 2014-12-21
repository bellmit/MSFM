// Source file: d:/cboe/java/com/cboe/loggingService/InvalidMessageException.java

package com.cboe.loggingService;

/**
   An attempt was made to create an invalid log message.
   @author David Houlding
   @version 4.0
 */
public class InvalidMessageException extends LoggingRuntimeException {
    
    /**
       @roseuid 36641BE4005F
     */
    public InvalidMessageException(String message) {
        super( message );
    }
}
