package com.cboe.instrumentationService.calculator;

import com.cboe.instrumentationService.instrumentors.CountInstrumentor;
import com.cboe.instrumentationService.instrumentors.CalculatedCountInstrumentor;
import com.cboe.instrumentationService.factories.InstrumentorFactory;

/**
 * CountInstrumentorCalculatedImpl.java
 *
 *
 * Created: Mon Oct  6 13:40:18 2003
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
class CountInstrumentorCalculatedImpl implements CountInstrumentor, CalculatedCountInstrumentor {

	private CountInstrumentor raw;
	private Object lock;
	private long numSamples = 0;
	private long lastSampleTime;
	// These are set before every time new values are given to raw via interface set methods,
	// if this is the best sample so far.
	private long curPeakCount = 0;
	// These are set at the beginning of a stats interval.
	private long curIntervalCount = 0;
	private long intervalTimeMillis = 0;

	// Calculated values.
	private long peakCount = 0;
	private long intervalCount = 0;
	private double peakCountRate = 0.0;
	private double avgCountRate = 0.0;
	private InstrumentorFactory factory = null;

	public CountInstrumentorCalculatedImpl( CountInstrumentor rawInst ) {
		raw = rawInst;
		lock = this;
		lastSampleTime = System.currentTimeMillis();
	} // CountInstrumentorCalculatedImpl constructor

	public void setLockObject( Object newLockObject ) {
		lock = newLockObject;
		raw.setLockObject( lock );
	}

	public void setFactory( InstrumentorFactory factory ) {
		this.factory = factory;
	}

	public InstrumentorFactory getFactory() {
		return factory;
	}

	public void sumIntervalTime( long timestamp ) {
		intervalTimeMillis += (timestamp - lastSampleTime);
		lastSampleTime = timestamp;
	}

	public void calculate( short calcToSampleFactor ) {
		synchronized( lock ) {
			peakCount = curPeakCount;
			intervalCount = raw.getCount() - curIntervalCount;
			if ( intervalCount < 0 ) {
				intervalCount = 0;
			}

			long sampleTimeMillis = intervalTimeMillis / calcToSampleFactor;
			peakCountRate = peakCount / (sampleTimeMillis / 1000.0);

			avgCountRate = intervalCount / (intervalTimeMillis / 1000.0);

			// Zero-out cur values.
			curPeakCount = 0;
			curIntervalCount = 0;
			intervalTimeMillis = 0;
		}
	}

	public long incSamples() {
		numSamples++;
		return numSamples;
	}

	public long getPeakCount() {
		synchronized( lock ) {
			return peakCount;
		}
	}

	public long getIntervalCount() {
		synchronized( lock ) {
			return intervalCount;
		}
	}

	public double getPeakCountRate() {
		synchronized( lock ) {
			return peakCountRate;
		}
	}

	public double getAvgCountRate() {
		synchronized( lock ) {
			return avgCountRate;
		}
	}


	// Provide impls for interface, delegate everything to raw.

	/**
	 * Sets new value to privateMode.  This flag can control whether this
	 * instrumentor is exposed to the outside via any output
	 * mechanism.
	 *
	 * @param newValue a <code>boolean</code> value
	 */
	public void setPrivate( boolean newValue ) {
		raw.setPrivate( newValue );
	}

	/**
	 * Returns value of privateMode.  This flag can control whether this
	 * instrumentor is exposed to the outside via any output
	 * mechanism.
	 *
	 * @return a <code>boolean</code> value
	 */
	public boolean isPrivate() {
		return raw.isPrivate();
	}

	/**
	 * Gets the value of name
	 *
	 * @return the value of name
	 */
	public String getName()  {
		return raw.getName();
	}

	/**
	 * Renames the instrumentor.  Do not call this method
	 * if the instrumentor is currently registered with
	 * its factory.  A rename without first unregistering
	 * the instrumentor will make a subsequent unregister
	 * call fail (it won't find the instrumentor, so the
	 * instrumentor won't be unregistered).
	 *
	 * So, before calling this method, unregister this
	 * instrumentor.  After the rename, the instrumentor
	 * can be reregistered with the factory under the new
	 * name.
	 *
	 * @param newName a <code>String</code> value
	 */
	public void rename( String newName ) {
		raw.rename( newName );
	}

	/**
	 * Gets the value of userData
	 *
	 * @return the value of userData
	 */
	public Object getUserData()  {
		return raw.getUserData();
	}

	/**
	 * Sets the value of userData
	 *
	 * @param argUserData Value to assign to this.userData
	 */
	public void setUserData(Object argUserData) {
		raw.setUserData( argUserData );
	}

	/**
	 * Gets the value of count
	 *
	 * @return the value of count
	 */
	public long getCount()  {
		return raw.getCount();
	}

	/**
	 * Sets the value of count
	 *
	 * @param argCount Value to assign to this.count
	 */
	public void setCount(long argCount) {
		synchronized( lock ) {
			long deltaCount = argCount - raw.getCount();
			if ( deltaCount > curPeakCount ) {
				curPeakCount = deltaCount;
			}

			if ( curIntervalCount == 0 ) {
				curIntervalCount = raw.getCount();
			}
		}

		raw.setCount( argCount );
	}

	/**
	 * Increments the value of count
	 *
	 * @param argCount Value to increment this.count
	 */
	public void incCount(long argCount) {
		raw.incCount( argCount );
	}

	/**
	 * Gets the value of key
	 *
	 * @return the value of key
	 */
	public byte[] getKey()  {
		return raw.getKey();
	}

	/**
	 * Sets the value of key
	 *
	 * @param argKey Value to assign to this.key
	 */
	public void setKey(byte[] argKey) {
		raw.setKey( argKey );
	}

	public String getToStringHeader( boolean showUserData, boolean showPrivate ) {
		return "THIS getToStringHeader NOT IMPLEMENTED";
	}

	public String getToStringHeader() {
		return "Name,PeakCount,IntervalCount,PeakCountRate,AvgCountRate";
	}

	public String toString( boolean showUserData, boolean showPrivate, String instNameToUse ) {
		return raw.toString( showUserData, showPrivate, instNameToUse );
	}

	public String toString( String instNameToUse ) {
		return instNameToUse + "," +
			getPeakCount() + "," +
			getIntervalCount() + "," +
			getPeakCountRate() + "," +
			getAvgCountRate();
	}

	public String toString() {
		return toString( getName() );
	}

} // CountInstrumentorCalculatedImpl
