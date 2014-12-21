package com.cboe.exceptions;

/**
 * This exception is thrown when an attempt is made to create an order book
 * for a product for which an order book already exists.
 *
 * @version 0.50
 * @author Kevin Park
 */
public class OrderBookAlreadyExistsException extends OrderBookException {
/**
 * OrderBookAlreadyExistsException default constructor.
 */
public OrderBookAlreadyExistsException() {
	super();
}
/**
 * @param s java.lang.String
 */
public OrderBookAlreadyExistsException(String s) {
	super(s);
}
}
