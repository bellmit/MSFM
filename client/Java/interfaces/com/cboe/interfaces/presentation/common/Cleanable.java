package com.cboe.interfaces.presentation.common;

/**
 * Interface that makes a contract with the class that implements it to 
 * provide a cleanup method.
 * 
 * @author Eric Maheo
 * @since 03/05/2009
 */
public interface Cleanable {
	/**
	 * Method that contains the cleanup part of a class. 
	 * in general to remove listeners, clean collections...
	 */
	public void cleanup();
	
}
