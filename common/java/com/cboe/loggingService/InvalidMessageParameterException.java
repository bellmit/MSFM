// Source file: d:/cboe/java/com/cboe/loggingService/InvalidMessageParameterException.java

package com.cboe.loggingService;

/**
   An attempt was made to create a log message with parameters and the parameters were invalid.
   @author David Houlding
   @version 4.0
 */
public class InvalidMessageParameterException extends InvalidMessageException {
    
    /**
       @roseuid 365C81280008
     */
    public InvalidMessageParameterException(String message) {
        super( message );
    }
}
