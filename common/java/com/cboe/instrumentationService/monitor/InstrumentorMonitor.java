package com.cboe.instrumentationService.monitor;

/**
 * InstrumentorMonitor.java
 *
 *
 * Created: Thu Sep 11 09:58:29 2003
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
public interface InstrumentorMonitor {
	public void visitInstrumentorFactory( long visitTimeMillis );
	public void stopMonitoring();
	public String info();
} // InstrumentorMonitor
