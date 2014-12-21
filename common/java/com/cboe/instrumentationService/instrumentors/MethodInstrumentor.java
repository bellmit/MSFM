package com.cboe.instrumentationService.instrumentors;

/**
 * MethodInstrumentor.java
 *
 *
 * Created: Tue Sep  9 13:52:42 2003
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
public interface MethodInstrumentor extends Instrumentor {

	public static final String INSTRUMENTOR_TYPE_NAME = "MethodInstrumentor";
	
	public void incCalls( long incAmount );
	public void setCalls( long newAmount );
	public long getCalls();
	public void incExceptions( long incAmount );
	public void setExceptions( long newAmount );
	public long getExceptions();
	public void beforeMethodCall();
	public void afterMethodCall();
	public void afterMethodCall( Throwable t );
	public void incMethodTime( long incAmount );
	public void setMethodTime( double newAmount );
	public double getMethodTime();
	public void setSumOfSquareMethodTime( double newAmount );
	public double getSumOfSquareMethodTime();
	public void setMaxMethodTime( long newAmount );
	public long getMaxMethodTime();

	public void get( MethodInstrumentor mi );

} // MethodInstrumentor
