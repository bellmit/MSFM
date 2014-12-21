package com.cboe.instrumentationService.monitor;

/**
 * InstrumentorMonitorDefaults.java
 *
 *
 * Created: Fri Sep 19 13:56:04 2003
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
public class InstrumentorMonitorDefaults {

	private InstrumentorMonitorDefaults() {
	}

	public static final String MONITOR_NAME = "DefaultMonitor";
	public static final long SAMPLE_INTERVAL = Long.getLong( "InstrumentationService.InstrumentorMonitor.SampleInterval", 10000 ).longValue(); // Ten seconds.
	public static final long MIN_SAMPLE_INTERVAL = Long.getLong( "InstrumentationService.InstrumentorMonitor.MinSampleInterval", 5000 ).longValue(); // Five seconds.

} // InstrumentorMonitorDefaults
