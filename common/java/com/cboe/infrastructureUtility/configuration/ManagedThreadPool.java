package com.cboe.infrastructureUtility.configuration;

/**
 * Responsible for adjusting the thread counts of a thread pool system.
 */
public interface ManagedThreadPool
{
	/**
	 * Computes a metric of quality for this pool.
	 */
	public ThreadPoolMetric captureMetricMemento();

	public void setThreadCount(int count);

	public int getThreadCount();
}
