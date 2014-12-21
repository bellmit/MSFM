package com.cboe.interfaces.domain;

import com.cboe.util.ThreadPool;

/**
 * This is the common interface for the GlobalThreadPoolHome
 * @author Jing Chen
 */
public interface InstrumentedGlobalThreadPoolHome
{
	/**
	 * Name that will be used for this home.
	 */
	public final static String HOME_NAME = "InstrumentedGlobalThreadPoolHome";

	public ThreadPool create();
  
	public ThreadPool find();

}
