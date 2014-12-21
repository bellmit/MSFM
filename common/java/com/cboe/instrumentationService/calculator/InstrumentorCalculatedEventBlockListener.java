package com.cboe.instrumentationService.calculator;

import java.util.ArrayList;

/**
 * InstrumentorCalculatedEventBlockListener.java
 * This interface represents the mechanism by which a user can be given
 * calculated instrumentor information.  For a given dispatching mechanism,
 * there can be a receiver implementation that can derive Calculated
 * instrumentors.  The receiver implementation can allow a listener
 * following this interface to register with it, and then call the
 * listener when calculations have been done.
 *
 * The interface allows for seeing the so-called "raw" instrumentor,
 * which contain actual / current values from the originator.  The
 * Calculated instrumentors contain information derived from the
 * time interval / delta between updates of the raw instrumentors.
 * For example, a MethodInstrumentor was received twice, spanning
 * a ten second interval.  The calculator implementation can derive
 * deltas for #calls, methodTime, etc., for the 10 second interval.
 * The CalculatedMethodInstrumentor will contain delta values for
 * #calls, methodTime, along with a rate calculation for
 * #calls, and a responseTime calculation based upon the
 * delta method time and delta calls.
 *
 * Created: Fri Nov 28 2003
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
public interface InstrumentorCalculatedEventBlockListener {

	// These methods allow receiving the events in a block, if applicable.
	public void acceptCalculatedCountInstrumentorsEvent( long eventTimeMillis, ArrayList totalList, ArrayList calculatedList, String clusterName, String orbName );
	public void acceptCalculatedEventChannelInstrumentorsEvent( long eventTimeMillis, ArrayList totalList, ArrayList calculatedList, String clusterName, String orbName );
	public void acceptCalculatedHeapInstrumentorsEvent( long eventTimeMillis, ArrayList totalList, ArrayList calculatedList, String clusterName, String orbName );
	public void acceptCalculatedMethodInstrumentorsEvent( long eventTimeMillis, ArrayList totalList, ArrayList calculatedList, String clusterName, String orbName );
	public void acceptCalculatedNetworkConnectionInstrumentorsEvent( long eventTimeMillis, ArrayList totalList, ArrayList calculatedList, String clusterName, String orbName );
	public void acceptCalculatedQueueInstrumentorsEvent( long eventTimeMillis, ArrayList totalList, ArrayList calculatedList, String clusterName, String orbName );
    public void acceptCalculatedThreadPoolInstrumentorsEvent( long eventTimeMillis, ArrayList totalList, ArrayList calculatedList, String clusterName, String orbName );
    public void acceptCalculatedJmxInstrumentorsEvent( long eventTimeMillis, ArrayList totalList, ArrayList calculatedList, String clusterName, String orbName );
    public void acceptCalculatedJstatInstrumentorsEvent( long eventTimeMillis, ArrayList totalList, ArrayList calculatedList, String clusterName, String orbName );

} // InstrumentorCalculatedEventBlockListener
