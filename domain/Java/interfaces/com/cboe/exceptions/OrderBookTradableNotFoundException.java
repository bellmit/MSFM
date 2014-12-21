package com.cboe.exceptions;

/**
 * Exception that is raised when an action is attempted that requires an order
 * to be in the book, but that order is not present.
 *
 * @version 0.50
 * @author Kevin Park
 */
public class OrderBookTradableNotFoundException extends OrderBookException {
/**
 * Constructor.
 */
public OrderBookTradableNotFoundException() {
	super();
}
/**
 * Constructor with string input.
 * @param s java.lang.String
 */
public OrderBookTradableNotFoundException(String s) {
	super(s);
}
}
