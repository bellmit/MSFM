package com.cboe.interfaces.domain;

import com.cboe.util.*;

/**
 * This is the common interface for the GlobalThreadPoolHome
 * @author Connie Feng
 */
public interface GlobalThreadPoolHome
{
	/**
	 * Name that will be used for this home.
	 */
	public final static String HOME_NAME = "GlobalThreadPoolHome";

	/**
	* Creates an instance of the ThreadPool.
	*
	* @return reference to ThreadPool
	*
	* @author Connie Feng
	*/
	public ThreadPool create();
  
	/**
	 * Finds an instance of the ThreadPool.
	 *
	 * @return reference to ThreadPool
	 *
	 * @author Connie Feng
	 */
	public ThreadPool find();

}
