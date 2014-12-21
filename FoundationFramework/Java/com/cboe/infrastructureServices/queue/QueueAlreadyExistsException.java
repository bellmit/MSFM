package com.cboe.infrastructureServices.queue;

/**
 * Thrown if a user attempts to create a named list when that name has already been assigned to a list.
 * 
 * @author Steven Sinclair
 */
public class QueueAlreadyExistsException extends QueueException {
/**
 * QueueAlreadyExistsException constructor comment.
 */
public QueueAlreadyExistsException() {
	super();
}
/**
 * QueueAlreadyExistsException constructor comment.
 * @param s java.lang.String
 */
public QueueAlreadyExistsException(String s) {
	super(s);
}
}
