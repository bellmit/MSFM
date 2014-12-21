package com.cboe.instrumentationService;

import java.util.concurrent.TimeUnit;

/**
 * The default implementation of the stopwatch interface that does absolutely
 * nothing.
 * 
 * This implementation is to allow measuring code to potentially sit in
 * production code.
 */
final class NOOPStopwatch implements Stopwatch
{

	@Override
	public void record(long before, long after, TimeUnit units)
	{
		// noop
	}

	@Override
	public void mark()
	{
		// noop
	}

	@Override
	public void mark(String name)
	{
		// noop
	}

}
