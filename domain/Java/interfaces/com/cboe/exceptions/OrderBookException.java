package com.cboe.exceptions;

import com.cboe.util.CboeException;

/**
 * This class is the parent of all exceptions raised within the order book package.
 *
 * @version 0.50
 * @author Kevin Park
 */
public class OrderBookException extends CboeException {
/**
 * OrderBookException default constructor.
 */
public OrderBookException() {
	super();
}
/**
 * @param s java.lang.String
 */
public OrderBookException(String s) {
	super(s);
}
}
