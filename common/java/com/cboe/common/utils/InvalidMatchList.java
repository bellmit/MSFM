package com.cboe.common.utils;

/**
 * InvalidMatchList.java
 *
 *
 * Created: Fri Sep 12 15:12:21 2003
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
public class InvalidMatchList extends Exception {
	public InvalidMatchList( String message, Throwable cause ) {
		super( message, cause );
	} // InvalidMatchList constructor
	
	public InvalidMatchList( String message ) {
		super( message );
	}
} // InvalidMatchList
