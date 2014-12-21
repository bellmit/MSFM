package com.cboe.exceptions;

import com.cboe.util.CboeException;

/**
 * This exception is thrown when There is no NBBO agent available for the given class
 *
 * @author Ravi Nagayach
 */
public class NBBOAgentNotAvailableException extends CboeException {
    public com.cboe.exceptions.ExceptionDetails details;
/**
 * NBBOAgentNotAvailableException default constructor.
 */
public NBBOAgentNotAvailableException() {
	super();
	details = new com.cboe.exceptions.ExceptionDetails();
	details.message = "";
        details.dateTime = "";
        details.severity = 0; 
        details.error = 0; 
}
/**
 * @param s java.lang.String
 */
public NBBOAgentNotAvailableException(String s) {
	super(s);
	details = new com.cboe.exceptions.ExceptionDetails();
	details.message = s;
        details.dateTime = "";
        details.severity = 0; 
        details.error = 0; 
}

public NBBOAgentNotAvailableException(com.cboe.exceptions.ExceptionDetails details) {
    this.details = details;
}
}
