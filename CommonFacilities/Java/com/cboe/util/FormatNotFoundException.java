package com.cboe.util;

/**
 * Exception thrown by DateWrapper when requested date format is not found.
 * 
 * @author John Wickberg
 */
public class FormatNotFoundException extends CboeException
{
/**
 * FormatNotFoundException constructor comment.
 */
public FormatNotFoundException() {
	super();
}
/**
 * FormatNotFoundException constructor comment.
 * @param s java.lang.String
 */
public FormatNotFoundException(String s) {
	super(s);
}
}
