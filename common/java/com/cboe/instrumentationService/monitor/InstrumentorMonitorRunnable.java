package com.cboe.instrumentationService.monitor;

import java.util.Date;
import java.util.ResourceBundle;

import com.cboe.common.log.InfraLoggingRb;
import com.cboe.common.log.Logger;

/**
 * InstrumentorMonitorRunnable.java
 *
 *
 * Created: Thu Sep 11 10:28:11 2003
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
class InstrumentorMonitorRunnable implements Runnable {

	private boolean exitRun = false;
	private InstrumentorMonitor monitor;
	private long sampleInterval;

	public InstrumentorMonitorRunnable( String monitorName, InstrumentorMonitor p_monitor, long p_sampleInterval ) {
		this.monitor = p_monitor;
		this.sampleInterval = p_sampleInterval;
	} // InstrumentorMonitorRunnable constructor

	public InstrumentorMonitor getMonitor() {
		return monitor;
	}

	public void run() {
		long visitTimeMills, waitTime;
		Date reportTime = new Date();

		// Surround the main loop with a try block.  If anything happens
		// to me, cleanup.  This should catch the ThreadDeath error.
		try {

			ResourceBundle rb = null;
			try {
				rb = ResourceBundle.getBundle( InfraLoggingRb.class.getName() );
			} catch( Exception e ) {
				Logger.sysAlarm( Logger.createLogMessageId( Logger.getDefaultLoggerName(),
															"Unable to set Logging ResourceBundle({0}).",
															"InstrumentorMonitorRunnable", "run" ),
								 new Object[] {InfraLoggingRb.class.getName()} );
			}
			java.lang.Object[] params = new java.lang.Object[4];
			params[0] = "InstrumentorMonitorRunnable";
			params[1] = "run";

			waitTime = sampleInterval;
			while ( !getExitRun() )	{
				if ( waitTime > 0 ) {
					params[2] = "waiting(ms)";
					params[3] = new Long( waitTime );
					Logger.traceEntry( rb, InfraLoggingRb.METHOD_ENTRY_1, params );
					try {
						waitForInterval( waitTime );
					}
					catch ( InterruptedException e ) { break; }
					if ( getExitRun() )
						break;
				}

				visitTimeMills = System.currentTimeMillis();
				reportTime.setTime( visitTimeMills );
				params[2] = "Obtaining samples for time";
				params[3] = reportTime;
				Logger.traceEntry( rb, InfraLoggingRb.METHOD_ENTRY_1, params );
				monitor.visitInstrumentorFactory( reportTime.getTime() );
				waitTime = sampleInterval - (System.currentTimeMillis() - visitTimeMills);
			} // while

		} // end of try block.

		finally {
			monitor.stopMonitoring();
		}

	}

	public synchronized void setExitRun( boolean newValue ) {
		exitRun = newValue;
	}

	public synchronized boolean getExitRun() {
		return exitRun;
	}

	/**
	 * This method handles waiting for the given interval.
	 * @param intervalSecs int
	 */
	private void waitForInterval( long intervalSecs ) throws InterruptedException {
		Thread.sleep( intervalSecs );
	}

} // InstrumentorMonitorRunnable
