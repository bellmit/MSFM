package com.cboe.exceptions;

import com.cboe.util.CboeException;

/**
 * @author Kevin Park
 */
public class DuplicateOrderIDException extends CboeException {
/**
 * OrderBookException default constructor.
 */
public DuplicateOrderIDException() {
	super();
}
/**
 * @param s java.lang.String
 */
public DuplicateOrderIDException(String s) {
	super(s);
}
}
