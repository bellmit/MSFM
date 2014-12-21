package com.cboe.instrumentationService.impls;

import java.util.concurrent.atomic.AtomicInteger;

import com.cboe.instrumentationService.instrumentors.ThreadPoolInstrumentor;
import com.cboe.instrumentationService.factories.InstrumentorFactory;

/**
 * ThreadPoolInstrumentorImpl.java
 *
 *
 * Created: Wed Sep  3 15:54:04 2003
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
class ThreadPoolInstrumentorImpl implements ThreadPoolInstrumentor {

	private String name;
	private Object userData;
	private Object lock;
	private AtomicInteger currentlyExecutingThreads = new AtomicInteger();
	private AtomicInteger startedThreads = new AtomicInteger();
	private AtomicInteger pendingThreads = new AtomicInteger();
	private AtomicInteger startedThreadsHighWaterMark = new AtomicInteger();
	private AtomicInteger pendingTaskCount = new AtomicInteger();
	private AtomicInteger pendingTaskCountHighWaterMark = new AtomicInteger();
	private byte[] key = null;
	private boolean privateMode = false;
	private InstrumentorFactory factory = null;

	public ThreadPoolInstrumentorImpl( String name, Object userData ) {
		this.name = name;
		this.userData = userData;
		lock = this; // Not really used anymore.
	} // ThreadPoolInstrumentorImpl constructor

	public void setLockObject( Object newLockObject ) {
		lock = newLockObject; // Not really used anymore.
	}

	/**
	 * Sets new value to privateMode.  This flag can control whether this
	 * instrumentor is exposed to the outside via any output
	 * mechanism.
	 *
	 * @param newValue a <code>boolean</code> value
	 */
	public void setPrivate( boolean newValue ) {
		privateMode = newValue;
	}

	/**
	 * Returns value of privateMode.  This flag can control whether this
	 * instrumentor is exposed to the outside via any output
	 * mechanism.
	 *
	 * @return a <code>boolean</code> value
	 */
	public boolean isPrivate() {
		return privateMode;
	}

	/**
	 * Gets the value of key
	 *
	 * @return the value of key
	 */
	public byte[] getKey()  {
		return this.key;
	}

	/**
	 * Sets the value of key
	 *
	 * @param argKey Value to assign to this.key
	 */
	public void setKey(byte[] argKey) {
		this.key = argKey;
	}

	/**
	 * Gets the value of name
	 *
	 * @return the value of name
	 */
	public String getName()  {
		return this.name;
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
		this.name = newName;
	}

	/**
	 * Gets the value of userData
	 *
	 * @return the value of userData
	 */
	public Object getUserData()  {
		return this.userData;
	}

	/**
	 * Sets the value of userData
	 *
	 * @param argUserObject Value to assign to this.userData
	 */
	public void setUserData(Object argUserData) {
		this.userData = argUserData;
	}

	public void setFactory( InstrumentorFactory factory ) {
		this.factory = factory;
	}

	public InstrumentorFactory getFactory() {
		return factory;
	}

	/**
	 * Gets the value of currentlyExecutingThreads
	 *
	 * @return the value of currentlyExecutingThreads
	 */
	public int getCurrentlyExecutingThreads()  {
		return currentlyExecutingThreads.get();
	}

	/**
	 * Sets the value of currentlyExecutingThreads
	 *
	 * @param argCurrentlyExecutingThreads Value to assign to this.currentlyExecutingThreads
	 */
	public void setCurrentlyExecutingThreads(int argCurrentlyExecutingThreads) {
		currentlyExecutingThreads.set( argCurrentlyExecutingThreads );
	}

	/**
	 * Sets the value of currentlyExecutingThreads
	 *
	 * @param argCurrentlyExecutingThreads Value to assign to this.currentlyExecutingThreads
	 */
	public void incCurrentlyExecutingThreads(int incAmount ) {
		currentlyExecutingThreads.addAndGet( incAmount );
	}

	/**
	 * Gets the value of startedThreads
	 *
	 * @return the value of startedThreads
	 */
	public int getStartedThreads()  {
		return startedThreads.get();
	}

	/**
	 * Sets the value of startedThreads
	 *
	 * @param argStartedThreads Value to assign to this.startedThreads
	 */
	public void setStartedThreads(int argStartedThreads) {
		startedThreads.set( argStartedThreads );
	}

	/**
	 * Sets the value of startedThreads
	 *
	 * @param argStartedThreads Value to assign to this.startedThreads
	 */
	public void incStartedThreads(int incAmount) {
		startedThreads.addAndGet( incAmount );
	}

	/**
	 * Gets the value of pendingThreads
	 *
	 * @return the value of pendingThreads
	 */
	public int getPendingThreads()  {
		return pendingThreads.get();
	}

	/**
	 * Sets the value of pendingThreads
	 *
	 * @param argPendingThreads Value to assign to this.pendingThreads
	 */
	public void setPendingThreads(int argPendingThreads) {
		pendingThreads.set( argPendingThreads );
	}

	/**
	 * Gets the value of startedThreadsHighWaterMark
	 *
	 * @return the value of startedThreadsHighWaterMark
	 */
	public int getStartedThreadsHighWaterMark()  {
		return startedThreadsHighWaterMark.get();
	}

	/**
	 * Sets the value of startedThreadsHighWaterMark
	 *
	 * @param argStartedThreadsHighWaterMark Value to assign to this.startedThreadsHighWaterMark
	 */
	public void setStartedThreadsHighWaterMark(int argStartedThreadsHighWaterMark) {
		startedThreadsHighWaterMark.set( argStartedThreadsHighWaterMark );
	}

	/**
	 * Gets the value of pendingTaskCount
	 *
	 * @return the value of pendingTaskCount
	 */
	public int getPendingTaskCount()  {
		return pendingTaskCount.get();
	}

	/**
	 * Sets the value of pendingTaskCount
	 *
	 * @param argPendingTaskCount Value to assign to this.pendingTaskCount
	 */
	public void setPendingTaskCount(int argPendingTaskCount) {
		pendingTaskCount.set( argPendingTaskCount );
	}

	/**
	 * Sets the value of pendingTaskCount
	 *
	 * @param argPendingTaskCount Value to assign to this.pendingTaskCount
	 */
	public void incPendingTaskCount(int incAmount ) {
		pendingTaskCount.addAndGet( incAmount );
	}

	/**
	 * Gets the value of pendingTaskCountHighWaterMark
	 *
	 * @return the value of pendingTaskCountHighWaterMark
	 */
	public int getPendingTaskCountHighWaterMark()  {
		return pendingTaskCountHighWaterMark.get();
	}

	/**
	 * Sets the value of pendingTaskCountHighWaterMark
	 *
	 * @param argPendingTaskCountHighWaterMark Value to assign to this.pendingTaskCountHighWaterMark
	 */
	public void setPendingTaskCountHighWaterMark(int argPendingTaskCountHighWaterMark) {
		pendingTaskCountHighWaterMark.set( argPendingTaskCountHighWaterMark );
	}

	/**
	 * Copies this TPI to the given TPI.
	 *
	 * @param tpi a <code>ThreadPoolInstrumentor</code> value
	 */
	public void get( ThreadPoolInstrumentor tpi ) {
		if ( tpi != null ) {
			tpi.setCurrentlyExecutingThreads( currentlyExecutingThreads.get() );
			tpi.setStartedThreads( startedThreads.get() );
			tpi.setPendingThreads( pendingThreads.get() );
			tpi.setStartedThreadsHighWaterMark( startedThreadsHighWaterMark.get() );
			tpi.setPendingTaskCount( pendingTaskCount.get() );
			tpi.setPendingTaskCountHighWaterMark( pendingTaskCountHighWaterMark.get() );
		}
	}

	public String getToStringHeader( boolean showUserData, boolean showPrivateFlag ) {
		return "name," + (showPrivateFlag ? "private," : "") + "execthr,startthr,pendthr,starthwm,pendtask,pendtaskhw" + (showUserData ? ",userdata" : "");
	}

    public String toString(boolean showUserData, boolean showPrivateFlag, String instNameToUse) {
        String userDataStr = "";
        if (showUserData) {
            userDataStr = "," + getUserData();
        }

        return instNameToUse + "," +
			(showPrivateFlag ? (isPrivate() + ",") : "") +
            getCurrentlyExecutingThreads() + "," +
            getStartedThreads() + "," +
            getPendingThreads() + "," +
            getStartedThreadsHighWaterMark() + "," +
            getPendingTaskCount() + "," +
            getPendingTaskCountHighWaterMark() + 
            userDataStr;
    }

	public String toString() {
		return toString( true, true, getName() );
	}

} // ThreadPoolInstrumentorImpl
