package com.cboe.instrumentationService.instrumentors;

/**
 * ThreadPoolInstrumentor.java
 *
 *
 * Created: Wed Jul 23 16:07:46 2003
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
public interface ThreadPoolInstrumentor extends Instrumentor {

	public static final String INSTRUMENTOR_TYPE_NAME = "ThreadPoolInstrumentor";

	public void incCurrentlyExecutingThreads( int incAmount );
	public void setCurrentlyExecutingThreads( int newAmount );
	public int getCurrentlyExecutingThreads();

	public void incStartedThreads( int incAmount );
	public void setStartedThreads( int newAmount );
	public int getStartedThreads();

	public void setPendingThreads( int newAmount );
	public int getPendingThreads();

	public void setStartedThreadsHighWaterMark( int newAmount );
	public int getStartedThreadsHighWaterMark();

	public void incPendingTaskCount( int incAmount );
	public void setPendingTaskCount( int newAmount );
	public int getPendingTaskCount();

	public void setPendingTaskCountHighWaterMark( int newAmount );
	public int getPendingTaskCountHighWaterMark();

	public void get( ThreadPoolInstrumentor tpi );

} // ThreadPoolInstrumentor
