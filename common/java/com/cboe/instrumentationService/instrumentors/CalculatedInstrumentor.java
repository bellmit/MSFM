package com.cboe.instrumentationService.instrumentors;

/**
 * CalculatedInstrumentor.java
 *
 *
 * Created: Mon Sep 22 12:45:41 2003
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
public interface CalculatedInstrumentor {

	public void calculate( short calcToSampleFactor );
	public void sumIntervalTime( long sampleTime );
	public long incSamples();

	public String getToStringHeader();
    public String toString(String instrName);
    
} // CalculatedInstrumentor
