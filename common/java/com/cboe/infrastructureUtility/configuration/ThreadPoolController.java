package com.cboe.infrastructureUtility.configuration;

/**
 * Manages the state of a Thread Pool
 */
public interface ThreadPoolController
{
	/**
	 * Fetch the associated identity pool that this object controls
	 */
	public ThreadPoolIdentity getIdentity();

	/**
	 * Return a memento of the current state of the thread pool, this may not be
	 * an immediate copy of the performance. 
	 */
	public ThreadPoolMetric getPerformanceMemento();

	/**
	 * make a request for more threads. This may not be honoured, especially if there are
	 * outstanding requests.
	 */
	public void requestAdditionalThreads(int count);
}
