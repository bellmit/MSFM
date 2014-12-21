
package com.cboe.instrumentationService.commPath;

/**
 * Exception for reporting that the CommPath could not be found in the master
 * CommPath collection.
 * 
 * @author Kevin Yaussy
 */
public class CommPathNotFoundException extends Exception {

/**
 * CommPathNotFoundException constructor comment.
 */
public CommPathNotFoundException() {
	super();
}
/**
 * CommPathNotFoundException constructor comment.
 * @param s java.lang.String
 */
public CommPathNotFoundException(String s) {
	super(s);
}
}
