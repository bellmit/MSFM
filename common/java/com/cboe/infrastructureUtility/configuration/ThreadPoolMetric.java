package com.cboe.infrastructureUtility.configuration;

/**
 * Contains information about the performance of the pool
 */
public final class ThreadPoolMetric
{
	private final int globalHWM;
	private final int recentHWM;
	private final int totalThreads;
	private final int outStandingRequests;
	private final long totalCalls;
	private final Rating rating;
	private final long capturedTimeMS;

	private static final ThreadPoolMetric undefined = new ThreadPoolMetric(Rating.Unknown, -1, -1, -1, -1, -1);

	public static ThreadPoolMetric getUndefined()
	{
		return undefined;
	}

	public ThreadPoolMetric(Rating rating, int globalHWM, int recentHWM, int totalThreads, int outStandingRequests, long totalCalls)
	{
		this.rating = rating;

		this.globalHWM = globalHWM;
		this.recentHWM = recentHWM;
		this.totalThreads = totalThreads;
		this.outStandingRequests = outStandingRequests;
		this.totalCalls = totalCalls;
		this.capturedTimeMS = System.currentTimeMillis();
	}

	public long getCapturedTimeMS()
	{
		return capturedTimeMS;
	}

	public long getTotalCalls()
	{
		return totalCalls;
	}

	public int getGlobalHWM()
	{
		return globalHWM;
	}

	public int getRecentHWM()
	{
		return recentHWM;
	}

	public int getTotalThreads()
	{
		return totalThreads;
	}

	public int getOutStandingRequests()
	{
		return outStandingRequests;
	}

	public Rating getRating()
	{
		return rating;
	}

}
