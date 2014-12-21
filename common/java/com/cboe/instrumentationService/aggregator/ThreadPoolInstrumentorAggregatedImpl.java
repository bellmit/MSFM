package com.cboe.instrumentationService.aggregator;

import java.util.*;
import com.cboe.instrumentationService.instrumentors.Instrumentor;
import com.cboe.instrumentationService.instrumentors.ThreadPoolInstrumentor;
import com.cboe.instrumentationService.instrumentors.AggregatedThreadPoolInstrumentor;
import com.cboe.instrumentationService.factories.InstrumentorFactory;
import com.cboe.instrumentationService.factories.ThreadPoolInstrumentorFactory;
import com.cboe.instrumentationService.factories.InstrumentorFactoryVisitor;
import com.cboe.instrumentationService.factories.ThreadPoolInstrumentorFactoryVisitor;

/**
 * ThreadPoolInstrumentorAggregatedImpl.java
 *
 *
 * Created: Wed Sep  3 15:54:04 2003
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
class ThreadPoolInstrumentorAggregatedImpl implements ThreadPoolInstrumentor, AggregatedThreadPoolInstrumentor {

	private String name;
	private Object userData;
	private byte[] key = null;
	private boolean privateMode = false;
	private ArrayList instrumentors = new ArrayList();
	private InstrumentorFactory factory = null;

	public ThreadPoolInstrumentorAggregatedImpl( String name, Object userData ) {
		this.name = name;
		this.userData = userData;
	} // ThreadPoolInstrumentorAggregatedImpl constructor

	public void setLockObject( Object newLockObject ) {
	}

	public synchronized void addInstrumentor( ThreadPoolInstrumentor tpi ) {
		if ( instrumentors.indexOf( tpi ) < 0 ) {
			instrumentors.add( tpi );
		}
	}

	public synchronized void removeInstrumentor( ThreadPoolInstrumentor tpi ) {
		instrumentors.remove( tpi );
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
	 * Iterates through the member instrumentors for this Aggregator and
	 * sets the private flag on each to the new value.
	 *
	 * @param newPrivateValue a <code>boolean</code> value
	 */
	public synchronized void setPrivateOnMembers( boolean newPrivateValue ) {
		Iterator iter = instrumentors.iterator();
		while( iter.hasNext() ) {
			Instrumentor inst = (Instrumentor)iter.next();
			inst.setPrivate( newPrivateValue );
		}
	}

	/**
	 * This method can be called to unregister / remove all the
	 * member instrumentors from their respective factories.
	 *
	 */
	public synchronized void removeMembersFromFactories() {
		Iterator iter = instrumentors.iterator();
		while( iter.hasNext() ) {
			ThreadPoolInstrumentor inst = (ThreadPoolInstrumentor)iter.next();
			ThreadPoolInstrumentorFactory ciFactory = (ThreadPoolInstrumentorFactory)inst.getFactory();
			ciFactory.unregister( inst );
		}
	}

	/**
	 * Visit all members of this aggregator.  Uses the existing
	 * InstrumentorFactoryVisitor interfaces.
	 *
	 * @param visitor an <code>InstrumentorFactoryVisitor</code> value
	 */
	public synchronized void visitMembers( InstrumentorFactoryVisitor visitor ) {
		Iterator iter = instrumentors.iterator();
		while( iter.hasNext() ) {
			ThreadPoolInstrumentor inst = (ThreadPoolInstrumentor)iter.next();
			if ( !((ThreadPoolInstrumentorFactoryVisitor)visitor).visit( inst ) ) {
				return;
			}
		}
	}

	public void setFactory( InstrumentorFactory factory ) {
		this.factory = factory;
	}

	public InstrumentorFactory getFactory() {
		return factory;
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

	/**
	 * Gets the value of currentlyExecutingThreads
	 *
	 * @return the value of currentlyExecutingThreads
	 */
	public synchronized int getCurrentlyExecutingThreads()  {
		int total = 0;
		Iterator iter = instrumentors.iterator();
		while( iter.hasNext() ) {
			total += ((ThreadPoolInstrumentor)iter.next()).getCurrentlyExecutingThreads();
		}
		return total;
	}

	/**
	 * Empty for this impl.
	 *
	 * @param argCurrentlyExecutingThreads
	 */
	public void setCurrentlyExecutingThreads(int argCurrentlyExecutingThreads) {
	}

	/**
	 * Empty for this impl.
	 *
	 * @param argCurrentlyExecutingThreads
	 */
	public void incCurrentlyExecutingThreads(int incAmount) {
	}

	/**
	 * Gets the value of startedThreads
	 *
	 * @return the value of startedThreads
	 */
	public synchronized int getStartedThreads()  {
		int total = 0;
		Iterator iter = instrumentors.iterator();
		while( iter.hasNext() ) {
			total += ((ThreadPoolInstrumentor)iter.next()).getStartedThreads();
		}
		return total;
	}

	/**
	 * Empty for this impl
	 *
	 * @param argStartedThreads
	 */
	public void setStartedThreads(int argStartedThreads) {
	}

	/**
	 * Empty for this impl
	 *
	 * @param argStartedThreads
	 */
	public void incStartedThreads(int incAmount) {
	}

	/**
	 * Gets the value of pendingThreads
	 *
	 * @return the value of pendingThreads
	 */
	public synchronized int getPendingThreads()  {
		int total = 0;
		Iterator iter = instrumentors.iterator();
		while( iter.hasNext() ) {
			total += ((ThreadPoolInstrumentor)iter.next()).getPendingThreads();
		}
		return total;
	}

	/**
	 * Empty for this impl
	 *
	 * @param argPendingThreads
	 */
	public void setPendingThreads(int argPendingThreads) {
	}

	/**
	 * Gets the value of startedThreadsHighWaterMark
	 *
	 * @return the value of startedThreadsHighWaterMark
	 */
	public synchronized int getStartedThreadsHighWaterMark()  {
		int total = 0;
		Iterator iter = instrumentors.iterator();
		while( iter.hasNext() ) {
			total += ((ThreadPoolInstrumentor)iter.next()).getStartedThreadsHighWaterMark();
		}
		return total;
	}

	/**
	 * Empty for this impl
	 *
	 * @param argStartedThreadsHighWaterMark
	 */
	public void setStartedThreadsHighWaterMark(int argStartedThreadsHighWaterMark) {
	}

	/**
	 * Gets the value of pendingTaskCount
	 *
	 * @return the value of pendingTaskCount
	 */
	public synchronized int getPendingTaskCount()  {
		int total = 0;
		Iterator iter = instrumentors.iterator();
		while( iter.hasNext() ) {
			total += ((ThreadPoolInstrumentor)iter.next()).getPendingTaskCount();
		}
		return total;
	}

	/**
	 * Empty for this impl
	 *
	 * @param argPendingTaskCount
	 */
	public void setPendingTaskCount(int argPendingTaskCount) {
	}

	/**
	 * Empty for this impl.
	 *
	 * @param argCurrentlyExecutingThreads
	 */
	public void incPendingTaskCount(int incAmount) {
	}

	/**
	 * Gets the value of pendingTaskCountHighWaterMark
	 *
	 * @return the value of pendingTaskCountHighWaterMark
	 */
	public synchronized int getPendingTaskCountHighWaterMark()  {
		int total = 0;
		Iterator iter = instrumentors.iterator();
		while( iter.hasNext() ) {
			total += ((ThreadPoolInstrumentor)iter.next()).getPendingTaskCountHighWaterMark();
		}
		return total;
	}

	/**
	 * Empty for this impl
	 *
	 * @param argPendingTaskCountHighWaterMark
	 */
	public void setPendingTaskCountHighWaterMark(int argPendingTaskCountHighWaterMark) {
	}

	/**
	 * Copies this TPI to the given TPI.
	 *
	 * @param tpi a <code>ThreadPoolInstrumentor</code> value
	 */
	public void get( ThreadPoolInstrumentor tpi ) {
		if ( tpi != null ) {
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

} // ThreadPoolInstrumentorAggregatedImpl
