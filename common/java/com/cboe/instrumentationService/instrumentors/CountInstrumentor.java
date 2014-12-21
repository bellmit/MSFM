package com.cboe.instrumentationService.instrumentors;

/**
 * CountInstrumentor.java
 *
 *
 * Created: Mon Oct  6 09:49:25 2003
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
public interface CountInstrumentor extends Instrumentor {

	public static final String INSTRUMENTOR_TYPE_NAME = "CountInstrumentor";

	public void setCount( long newValue );
	public void incCount( long incAmount );
	public long getCount();

} // CountInstrumentor
