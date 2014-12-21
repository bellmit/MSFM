package com.cboe.exceptions;

/**
 * This exception is thrown when there are no orders present on the entire
 * side of a book.
 *
 * @version 0.50
 * @author Kevin Park
 */
public class OrderBookSideEmptyException extends OrderBookException {
/**
 * OrderBookSideEmptyException default constructor.
 */
public OrderBookSideEmptyException() {
	super();
}
/**
 * @param s java.lang.String
 */
public OrderBookSideEmptyException(String s) {
	super(s);
}
}
