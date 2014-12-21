package com.cboe.infrastructureServices.queue;

/**
 *  An exception thrown if an "InterruptedException" occured during an
 *  enqueue or dequeue operation.  If the NO_TIMEOUT value is used for
 *  the timeout period, then this exception will never be thrown.
 *
 * @author Steven Sinclair
 */
public class QueueInterruptedException extends QueueException
{
/**
 * QueueInterruptedException constructor comment.
 */
public QueueInterruptedException() {
	super();
}
/**
 * QueueInterruptedException constructor comment.
 * @param s java.lang.String
 */
public QueueInterruptedException(String s) {
	super(s);
}
}
