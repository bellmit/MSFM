package com.cboe.instrumentationService.calculator;

import com.cboe.instrumentationService.instrumentors.ThreadPoolInstrumentor;
import com.cboe.instrumentationService.instrumentors.CalculatedThreadPoolInstrumentor;
import com.cboe.instrumentationService.factories.InstrumentorFactory;

/**
 * ThreadPoolInstrumentorCalculatedImpl.java
 *
 *
 * Created: Thu Sep 18 13:47:11 2003
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
class ThreadPoolInstrumentorCalculatedImpl implements ThreadPoolInstrumentor, CalculatedThreadPoolInstrumentor {

	private ThreadPoolInstrumentor raw;
	private Object lock;
	private long numSamples = 0;
	// These are set at the beginning of a stats interval.
	private long curIntervalExecutingThreads = 0;
	private long curIntervalStartedThreads = 0;
	private long curIntervalPendingThreads = 0;
	private long curIntervalPendingTaskCount = 0;

	// Calculated values.
	private long intervalExecutingThreads = 0;
	private long intervalStartedThreads = 0;
	private long intervalPendingThreads = 0;
	private long intervalPendingTaskCount = 0;
	private InstrumentorFactory factory = null;

	public ThreadPoolInstrumentorCalculatedImpl( ThreadPoolInstrumentor rawInst ) {
		raw = rawInst;
		lock = this;
	} // ThreadPoolInstrumentorCalculatedImpl constructor

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
			intervalExecutingThreads = raw.getCurrentlyExecutingThreads() - curIntervalExecutingThreads;
			if ( intervalExecutingThreads < 0 ) {
				intervalExecutingThreads = 0;
			}
			intervalStartedThreads = raw.getStartedThreads() - curIntervalStartedThreads;
			if ( intervalStartedThreads < 0 ) {
				intervalStartedThreads = 0;
			}
			intervalPendingThreads = raw.getPendingThreads() - curIntervalPendingThreads;
			if ( intervalPendingThreads < 0 ) {
				intervalPendingThreads = 0;
			}
			intervalPendingTaskCount = raw.getPendingTaskCount() - curIntervalPendingTaskCount;
			if ( intervalPendingTaskCount < 0 ) {
				intervalPendingTaskCount = 0;
			}

			curIntervalExecutingThreads = 0;
			curIntervalStartedThreads = 0;
			curIntervalPendingThreads = 0;
			curIntervalPendingTaskCount = 0;
		}
	}

	public long incSamples() {
		numSamples++;
		return numSamples;
	}

	public long getIntervalExecutingThreads() {
		synchronized( lock ) {
			return intervalExecutingThreads;
		}
	}

	public long getIntervalStartedThreads() {
		synchronized( lock ) {
			return intervalStartedThreads;
		}
	}

	public long getIntervalPendingThreads() {
		synchronized( lock ) {
			return intervalPendingThreads;
		}
	}

	public long getIntervalPendingTaskCount() {
		synchronized( lock ) {
			return intervalPendingTaskCount;
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
	 * Gets the value of currentlyExecutingThreads
	 *
	 * @return the value of currentlyExecutingThreads
	 */
	public int getCurrentlyExecutingThreads()  {
		return raw.getCurrentlyExecutingThreads();
	}

	/**
	 * Sets the value of currentlyExecutingThreads
	 *
	 * @param argCurrentlyExecutingThreads Value to assign to this.currentlyExecutingThreads
	 */
	public void setCurrentlyExecutingThreads(int argCurrentlyExecutingThreads) {
		synchronized( lock ) {
			if ( curIntervalExecutingThreads == 0 ) {
				curIntervalExecutingThreads = raw.getCurrentlyExecutingThreads();
			}
		}

		raw.setCurrentlyExecutingThreads( argCurrentlyExecutingThreads );
	}

	/**
	 * Sets the value of currentlyExecutingThreads
	 *
	 * @param argCurrentlyExecutingThreads Value to assign to this.currentlyExecutingThreads
	 */
	public void incCurrentlyExecutingThreads(int incAmount ) {
		raw.incCurrentlyExecutingThreads( incAmount );
	}

	/**
	 * Gets the value of startedThreads
	 *
	 * @return the value of startedThreads
	 */
	public int getStartedThreads()  {
		return raw.getStartedThreads();
	}

	/**
	 * Sets the value of startedThreads
	 *
	 * @param argStartedThreads Value to assign to this.startedThreads
	 */
	public void setStartedThreads(int argStartedThreads) {
		synchronized( lock ) {
			if ( curIntervalStartedThreads == 0 ) {
				curIntervalStartedThreads = raw.getStartedThreads();
			}
		}

		raw.setStartedThreads( argStartedThreads );
	}

	/**
	 * Sets the value of startedThreads
	 *
	 * @param argStartedThreads Value to assign to this.startedThreads
	 */
	public void incStartedThreads(int incAmount ) {
		synchronized( lock ) {
			if ( curIntervalStartedThreads == 0 ) {
				curIntervalStartedThreads = raw.getStartedThreads();
			}
		}

		raw.incStartedThreads( incAmount );
	}

	/**
	 * Gets the value of pendingThreads
	 *
	 * @return the value of pendingThreads
	 */
	public int getPendingThreads()  {
		return raw.getPendingThreads();
	}

	/**
	 * Sets the value of pendingThreads
	 *
	 * @param argPendingThreads Value to assign to this.pendingThreads
	 */
	public void setPendingThreads(int argPendingThreads) {
		synchronized( lock ) {
			if ( curIntervalPendingThreads == 0 ) {
				curIntervalPendingThreads = raw.getPendingThreads();
			}
		}

		raw.setPendingThreads( argPendingThreads );
	}

	/**
	 * Gets the value of startedThreadsHighWaterMark
	 *
	 * @return the value of startedThreadsHighWaterMark
	 */
	public int getStartedThreadsHighWaterMark()  {
		return raw.getStartedThreadsHighWaterMark();
	}

	/**
	 * Sets the value of startedThreadsHighWaterMark
	 *
	 * @param argStartedThreadsHighWaterMark Value to assign to this.startedThreadsHighWaterMark
	 */
	public void setStartedThreadsHighWaterMark(int argStartedThreadsHighWaterMark) {
		raw.setStartedThreadsHighWaterMark( argStartedThreadsHighWaterMark );
	}

	/**
	 * Gets the value of pendingTaskCount
	 *
	 * @return the value of pendingTaskCount
	 */
	public int getPendingTaskCount()  {
		return raw.getPendingTaskCount();
	}

	/**
	 * Sets the value of pendingTaskCount
	 *
	 * @param argPendingTaskCount Value to assign to this.pendingTaskCount
	 */
	public void setPendingTaskCount(int argPendingTaskCount) {
		synchronized( lock ) {
			if ( curIntervalPendingTaskCount == 0 ) {
				curIntervalPendingTaskCount = raw.getPendingTaskCount();
			}
		}

		raw.setPendingTaskCount( argPendingTaskCount );
	}

	/**
	 * Sets the value of currentlyExecutingThreads
	 *
	 * @param argCurrentlyExecutingThreads Value to assign to this.currentlyExecutingThreads
	 */
	public void incPendingTaskCount(int incAmount ) {
		raw.incPendingTaskCount( incAmount );
	}

	/**
	 * Gets the value of pendingTaskCountHighWaterMark
	 *
	 * @return the value of pendingTaskCountHighWaterMark
	 */
	public int getPendingTaskCountHighWaterMark()  {
		return raw.getPendingTaskCountHighWaterMark();
	}

	/**
	 * Sets the value of pendingTaskCountHighWaterMark
	 *
	 * @param argPendingTaskCountHighWaterMark Value to assign to this.pendingTaskCountHighWaterMark
	 */
	public void setPendingTaskCountHighWaterMark(int argPendingTaskCountHighWaterMark) {
		raw.setPendingTaskCountHighWaterMark( argPendingTaskCountHighWaterMark );
	}

	/**
	 * Copies this TPI to the given TPI.
	 *
	 * @param tpi a <code>ThreadPoolInstrumentor</code> value
	 */
	public void get( ThreadPoolInstrumentor tpi ) {
		raw.get( tpi );
	}

	public String getToStringHeader( boolean showUserData, boolean showPrivate ) {
		return "THIS getToStringHeader NOT IMPLEMENTED";
	}

	public String getToStringHeader() {
		return "Name,ExecThreads,StartThreads,PendThreads,PendTaskCount";
	}

	public String toString( boolean showUserData, boolean showPrivate, String instNameToUse ) {
		return raw.toString( showUserData, showPrivate, instNameToUse );
	}

	public String toString( String instNameToUse ) {
		return instNameToUse +
			getIntervalExecutingThreads() + "," +
			getIntervalStartedThreads() + "," +
			getIntervalPendingThreads() + "," +
			getIntervalPendingTaskCount();
	}
    
	public String toString() {
		return toString( getName() );
	}

} // ThreadPoolInstrumentorCalculatedImpl
