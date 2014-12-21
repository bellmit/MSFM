package com.cboe.infrastructureUtility.configuration;

/**
 * Builds {@link ThreadPoolController} objects to the specification.
 */
public interface ThreadPoolControllerBuilder
{
	/**
	 * Set the size of the pool, a default of 1 will be used otherwise
	 */
	public ThreadPoolControllerBuilder setInitialThreadCount(int count);


	/**
	 * Creates a thread pool controller with the supplied builder values applied.
	 */
	public ThreadPoolController build();
}
