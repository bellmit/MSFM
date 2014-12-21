package com.cboe.instrumentationService.instrumentors;

/**
 * CalculatedMethodInstrumentor.java
 *
 *
 * Created: Thu Sep 18 09:44:55 2003
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
public interface CalculatedMethodInstrumentor extends CalculatedInstrumentor {

	public long getPeakCalls();
	public long getPeakExceptions();
	public double getPeakMethodTime();
	public long getIntervalCalls();
	public long getIntervalExceptions();
	public double getIntervalMethodTime();

	public double getPeakCallsRate();
	public double getPeakExceptionsRate();
	public double getPeakResponseTime();
	public double getAvgCallsRate();
	public double getAvgExceptionsRate();
	public double getAvgResponseTime();

} // CalculatedMethodInstrumentor
