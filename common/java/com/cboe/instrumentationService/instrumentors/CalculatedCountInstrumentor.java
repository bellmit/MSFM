package com.cboe.instrumentationService.instrumentors;

/**
 * CalculatedCountInstrumentor.java
 *
 *
 * Created: Mon Oct  6 10:05:33 2003
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
public interface CalculatedCountInstrumentor extends CalculatedInstrumentor {

	public long getPeakCount();
	public long getIntervalCount();

	public double getPeakCountRate();
	public double getAvgCountRate();

} // CalculatedCountInstrumentor
