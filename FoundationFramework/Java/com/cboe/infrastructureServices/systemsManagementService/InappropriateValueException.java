package com.cboe.infrastructureServices.systemsManagementService;

/**
 * This exception is thrown when a request is made for a specific
 * type of data from a property and the value is inappropriate.
 * For example, when an integer is requested and the value is "red".
 *
 * @author Craig Murphy
 * @creation date 12/08/1998
 */
public class InappropriateValueException extends Exception {
/**
 * InappropriateValueException constructor comment.
 */
public InappropriateValueException() {
	super();
}
/**
 * InappropriateValueException constructor comment.
 * @param s java.lang.String
 */
public InappropriateValueException(String s) {
	super(s);
}
}