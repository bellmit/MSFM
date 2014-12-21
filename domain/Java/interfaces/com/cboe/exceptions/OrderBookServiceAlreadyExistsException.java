package com.cboe.exceptions;

/**
 * This exception is thrown when an attempt is made to create
 * an order book service when it already exists.
 *
 * @version 0.50
 * @author Kevin Park
 */
public class OrderBookServiceAlreadyExistsException extends OrderBookException {
/**
 * OrderBookServiceAlreadyExistsException default constructor.
 */
public OrderBookServiceAlreadyExistsException() {
	super();
}
/**
 * @param s java.lang.String
 */
public OrderBookServiceAlreadyExistsException(String s) {
	super(s);
}
}
