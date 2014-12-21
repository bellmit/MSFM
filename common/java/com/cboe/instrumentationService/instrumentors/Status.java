package com.cboe.instrumentationService.instrumentors;

/**
 * Status.java
 *
 *
 * Created: Mon Sep 15 16:08:48 2003
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
public interface Status {

	public static final short INIT = 1;
	public static final String INIT_S = "INIT";
	public static final short UP = 2;
	public static final String UP_S = "UP";
	public static final short DOWN = 3;
	public static final String DOWN_S = "DOWN";
	public static final short THREAD_RUNNING = 4;
	public static final String THREAD_RUNNING_S = "THREAD_RUNNING";
	public static final short THREAD_EXITED = 5;
	public static final String THREAD_EXITED_S = "THREAD_EXITED";
	public static final short THREAD_NOT_STARTED = 6;
	public static final String THREAD_NOT_STARTED_S = "THREAD_NOT_STARTED";
	public static final short THREAD_WAITING = 7;
	public static final String THREAD_WAITING_S = "THREAD_WAITING";

} // Status
