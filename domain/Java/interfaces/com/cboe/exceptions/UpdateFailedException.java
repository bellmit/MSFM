package com.cboe.exceptions;

import com.cboe.util.CboeException;

/**
 * @author Kevin Park
 */
public class UpdateFailedException extends CboeException {
/**
 * OrderBookException default constructor.
 */
public UpdateFailedException() {
	super();
}
/**
 * @param s java.lang.String
 */
public UpdateFailedException(String s) {
	super(s);
}
}
