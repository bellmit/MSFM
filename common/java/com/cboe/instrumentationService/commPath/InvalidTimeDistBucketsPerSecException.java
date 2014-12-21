
package com.cboe.instrumentationService.commPath;

/**
 * Exception for reporting an invalid buckets per second value for the 
 * transaction time distribution array.
 * 
 * @author Kevin Yaussy
 */
public class InvalidTimeDistBucketsPerSecException extends Exception {

/**
 * InvalidTimeDistBucketsPerSecException constructor comment.
 */
public InvalidTimeDistBucketsPerSecException() {
	super();
}
/**
 * InvalidTimeDistBucketsPerSecException constructor comment.
 * @param s java.lang.String
 */
public InvalidTimeDistBucketsPerSecException(String s) {
	super(s);
}
}
