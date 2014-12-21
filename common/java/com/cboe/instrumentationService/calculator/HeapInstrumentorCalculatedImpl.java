package com.cboe.instrumentationService.calculator;

import com.cboe.instrumentationService.instrumentors.HeapInstrumentor;
import com.cboe.instrumentationService.instrumentors.CalculatedHeapInstrumentor;
import com.cboe.instrumentationService.factories.InstrumentorFactory;

/**
 * HeapInstrumentorCalculatedImpl.java
 *
 *
 * Created: Thu Sep 18 11:16:33 2003
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
class HeapInstrumentorCalculatedImpl implements HeapInstrumentor, CalculatedHeapInstrumentor {

	private HeapInstrumentor raw;
	private Object lock;
	private long numSamples = 0;
	// These are set at the beginning of a stats interval.
	private long curIntervalMaxMemory = 0;
	private long curIntervalTotalMemory = 0;
	private long curIntervalFreeMemory = 0;

	// Calculated values.
	private long intervalMaxMemory = 0;
	private long intervalTotalMemory = 0;
	private long intervalFreeMemory = 0;
	private InstrumentorFactory factory = null;

	public HeapInstrumentorCalculatedImpl( HeapInstrumentor rawInst ) {
		raw = rawInst;
		lock = this;
	} // HeapInstrumentorCalculatedImpl constructor

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
		// Not used here.
	}

	public void calculate( short calcToSampleFactor ) {
		synchronized( lock ) {
			intervalMaxMemory = raw.getMaxMemory() - curIntervalMaxMemory;
			if ( intervalMaxMemory < 0 ) {
				intervalMaxMemory = 0;
			}
			intervalTotalMemory = raw.getTotalMemory() - curIntervalTotalMemory;
			if ( intervalTotalMemory < 0 ) {
				intervalTotalMemory = 0;
			}
			intervalFreeMemory = raw.getFreeMemory() - curIntervalFreeMemory;
			if ( intervalFreeMemory < 0 ) {
				intervalFreeMemory = 0;
			}

			curIntervalMaxMemory = 0;
			curIntervalTotalMemory = 0;
			curIntervalFreeMemory = 0;
		}
	}

	public long incSamples() {
		numSamples++;
		return numSamples;
	}

	public long getIntervalMaxMemory() {
		synchronized( lock ) {
			return intervalMaxMemory;
		}
	}

	public long getIntervalTotalMemory() {
		synchronized( lock ) {
			return intervalTotalMemory;
		}
	}

	public long getIntervalFreeMemory() {
		synchronized( lock ) {
			return intervalFreeMemory;
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
	 * @param argUserObject Value to assign to this.userData
	 */
	public void setUserData(Object argUserData) {
		raw.setUserData( argUserData );
	}

	/**
	 * Gets the value of maxMemory
	 *
	 * @return the value of maxMemory
	 */
	public long getMaxMemory()  {
		return raw.getMaxMemory();
	}

	/**
	 * Sets the value of maxMemory
	 *
	 * @param argMaxMemory Value to assign to this.maxMemory
	 */
	public void setMaxMemory(long argMaxMemory) {
		synchronized( lock ) {
			if ( curIntervalMaxMemory == 0 ) {
				curIntervalMaxMemory = raw.getMaxMemory();
			}
		}

		raw.setMaxMemory( argMaxMemory );
	}

	/**
	 * Gets the value of totalMemory
	 *
	 * @return the value of totalMemory
	 */
	public long getTotalMemory()  {
		return raw.getTotalMemory();
	}

	/**
	 * Sets the value of totalMemory
	 *
	 * @param argTotalMemory Value to assign to this.totalMemory
	 */
	public void setTotalMemory(long argTotalMemory) {
		synchronized( lock ) {
			if ( curIntervalTotalMemory == 0 ) {
				curIntervalTotalMemory = raw.getTotalMemory();
			}
		}

		raw.setTotalMemory( argTotalMemory );
	}

	/**
	 * Gets the value of freeMemory
	 *
	 * @return the value of freeMemory
	 */
	public long getFreeMemory()  {
		return raw.getFreeMemory();
	}

	/**
	 * Sets the value of freeMemory
	 *
	 * @param argFreeMemory Value to assign to this.freeMemory
	 */
	public void setFreeMemory(long argFreeMemory) {
		synchronized( lock ) {
			if ( curIntervalFreeMemory == 0 ) {
				curIntervalFreeMemory = raw.getFreeMemory();
			}
		}

		raw.setFreeMemory( argFreeMemory );
	}

	/**
	 * Copies this HI to the given HI.
	 *
	 * @param hi a <code>HeapInstrumentor</code> value
	 */
	public void get( HeapInstrumentor hi ) {
		raw.get( hi );
	}

	public String getToStringHeader( boolean showUserData, boolean showPrivate ) {
		return "THIS getToStringHeader NOT IMPLEMENTED";
	}

	public String getToStringHeader() {
		return "Name,IntMaxMemory,IntTotalMemory,IntFreeMemory";
	}

	public String toString( boolean showUserData, boolean showPrivate, String instNameToUse ) {
		return raw.toString( showUserData, showPrivate, instNameToUse );
	}

	public String toString( String instNameToUse ) {
		return instNameToUse + "," +
			getIntervalMaxMemory() + "," +
			getIntervalTotalMemory() + "," +
			getIntervalFreeMemory();
	}

	public String toString() {
		return toString( getName() );
	}

} // HeapInstrumentorCalculatedImpl
