package com.cboe.instrumentationService;

import java.util.concurrent.TimeUnit;

/**
 * Defines a service for recording the duration of a method
 * 
 */
public interface Stopwatch
{

	/**
	 * Save an instance of the time measured.
	 * 
	 * @param before
	 *            Time before a block of code
	 * @param after
	 *            Time after a block of code
	 * @param units
	 *            The unit of time recorded in both <code>before</code> and
	 *            <code>after</code>
	 */
	public void record(long before, long after, TimeUnit units);

	/**
	 * Indicates that records should be reset and new timings recorded. This
	 * allows segmenting measures.
	 */
	public void mark();

	/**
	 * Indicates that records should be reset and new timings recorded with
	 * <code>name</code> as a grouping. This allows segmenting measures.
	 * 
	 * @param name
	 *            A name to give the new segment.
	 */
	public void mark(String name);

}
